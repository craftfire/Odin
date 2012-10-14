/*
 * This file is part of Odin <http://www.odin.com/>.
 *
 * Odin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Odin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.odin.managers;

import com.craftfire.bifrost.classes.general.ScriptUser;
import com.craftfire.bifrost.exceptions.UnsupportedMethod;
import com.craftfire.commons.enums.Encryption;
import com.craftfire.odin.util.MainUtils;
import com.craftfire.commons.CraftCommons;

import java.sql.SQLException;
import java.util.Date;

public class OdinUser {
    protected String username;
    private ScriptUser user = null;
    private Status status;
    private String password, ip;
    private boolean badcharacters;

    /**
     * Default constructor for the object.
     *
     * @param username name of the player.
     */
    public OdinUser(final String username) {
        this.username = username;
        this.badcharacters = MainUtils.hasBadCharacters(username, OdinManager.cfgMgr.getString("filter.username"));
        load();
    }
    
    public void save() {
        OdinManager.userStorage.put(this.username, this);
    }

    public void load() {
        if (OdinManager.userStorage.containsKey(this.username)) {
            /* TODO */
            OdinUser temp = OdinManager.userStorage.get(this.username);
            this.username = temp.getUsername();
            this.user = temp.getUser();
            this.status = temp.getStatus();
            this.ip = temp.getIP();
        }
    }
    
    public String getUsername() {
        return this.username;
    }

    public String getName() {
        return this.username;
    }
    
    public ScriptUser getUser() {
        return this.user;
    }
    
    public void setUser() {
        try {
            this.user = OdinManager.scriptHandle.getUser(this.username);
        } catch (UnsupportedMethod e) {
            OdinManager.logMgr.stackTrace(e);
        } catch (SQLException e) {
            OdinManager.logMgr.stackTrace(e);
        }
    }

    public boolean isLinked() {
        return OdinManager.userLinkedNames.containsKey(this.username);
    }

    public String getLinkedName() {
        if (OdinManager.userLinkedNames.containsKey(this.username)) {
            return OdinManager.userLinkedNames.get(this.username);
        }
        return null;
    }

    public boolean isAuthenticated() {
        return OdinManager.userAuthenticated.contains(this.username);
    }

    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            OdinManager.userAuthenticated.add(this.username);
            if (OdinManager.cfgMgr.getBoolean("session.enabled")) {
                setSession();
            }
        } else {
            logout();
        }
    }

    public boolean logout() {
        if (OdinManager.userAuthenticated.contains(this.username)) {
            OdinManager.userAuthenticated.remove(this.username);
            if (OdinManager.userPasswordAttempts.containsKey(this.username)) {
                OdinManager.userPasswordAttempts.remove(this.username);
            }
            if (OdinManager.userTimeouts.contains(this.username)) {
                OdinManager.userTimeouts.remove(this.username);
            }
            return true;
        }
        return false;
    }

    public boolean login(String password) throws UnsupportedMethod {
        if (!OdinManager.userAuthenticated.contains(this.username)) {
            if (isLinked()) {
                if (OdinManager.scriptHandle.authenticate(getLinkedName(), password)) {
                    OdinManager.userAuthenticated.add(this.username);
                    return true;
                }
            } else {
                if (OdinManager.scriptHandle.authenticate(this.username, password)) {
                    OdinManager.userAuthenticated.add(this.username);
                    return true;
                }
            }
        }
        return false;
    }

    public void login() {
        if (! OdinManager.userAuthenticated.contains(this.username)) {
            OdinManager.userAuthenticated.add(this.username);
        }
    }

    public boolean unlink() {
        if (OdinManager.userLinkedNames.containsKey(this.username)) {
            OdinManager.userLinkedNames.remove(this.username);
            return true;
        }
        return false;
    }

    public String getIP() {
        return ip;
    }
    
    public void setIP(String ip) {
        this.ip = ip;
    }

    public boolean hasSession() {
        //TODO: Check storage for session.
        return OdinManager.userSessions.containsKey(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip));
    }
    
    public long getSessionTime() {
        if (OdinManager.cfgMgr.getBoolean("session.enabled") && hasSession()) {
            return OdinManager.userSessions.get(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip));
        }
        return 0;
    }
    
    public void setSession() {
        if (OdinManager.cfgMgr.getBoolean("session.enabled")) {
            OdinManager.userSessions.put(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip), new Date().getTime() / 1000);
        }
    }

    public boolean isRegistered() throws UnsupportedMethod {
        if (this.status != null) {
            switch (this.status) {
                case Authenticated: return true;
                case Registered: return true;
                case Guest: return false;
            }
        } else if (OdinManager.scriptHandle.getScript().isRegistered(this.username)) {
            this.status = Status.Registered;
            return true;
        }
        this.status = Status.Guest;
        return false;
    }

    public boolean isGuest() throws UnsupportedMethod {
        if (this.status != null) {
            return this.status.equals(Status.Guest);
        } else {
            if (!OdinManager.scriptHandle.getScript().isRegistered(this.username)) {
                this.status = Status.Guest;
                return true;
            } else {
                this.status = Status.Registered;
            }
        }
        return false;
    }

    public Status getStatus() {
        return this.status;
    }

    public enum Status {
        Guest,
        Registered,
        Authenticated
    }

    public boolean hasMinLength() {
        return this.username.length() < OdinManager.cfgMgr.getInt("username.minimum");
    }

    public boolean hasMaxLength() {
        return this.username.length() > OdinManager.cfgMgr.getInt("username.maximum");
    }
    
    public long getJoinTime() {
        if (OdinManager.playerJoin.containsKey(this.username)) {
            return OdinManager.playerJoin.get(this.username);
        }
        return 0;
    }
    
    public void setJoinTime() {
        OdinManager.playerJoin.put(this.username, System.currentTimeMillis());
    }

    public void setJoinTime(long time) {
        OdinManager.playerJoin.put(this.username, time);
    }
    
    public int getPasswordAttempts() {
        if (OdinManager.userPasswordAttempts.containsKey(this.username)) {
            return OdinManager.userPasswordAttempts.get(this.username);
        }
        return 0;
    }

    public void setPasswordAttempts(int attempts) {
        OdinManager.userPasswordAttempts.put(this.username, attempts);
    }

    public void clearPasswordAttempts() {
        OdinManager.userPasswordAttempts.put(this.username, 0);
    }

    public void increasePasswordAttempts() {
        if (OdinManager.userPasswordAttempts.containsKey(this.username)) {
            OdinManager.userPasswordAttempts.put(this.username,
                                                   OdinManager.userPasswordAttempts.get(this.username) + 1);
        } else {
            OdinManager.userPasswordAttempts.put(this.username, 1);
        }
    }
    
    public boolean hasBadCharacters() {
        return this.badcharacters;
    }
    
    public boolean isFilterWhitelisted() {
        return OdinManager.cfgMgr.getString("filter.whitelist").contains(this.username);
    }

    public void setTimeout() {
        OdinManager.userTimeouts.add(this.username);
    }
}
