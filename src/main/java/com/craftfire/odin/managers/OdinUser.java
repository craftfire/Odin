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
        this.badcharacters = MainUtils.hasBadCharacters(username, OdinManager.getConfig().getString("filter.username"));
        load();
    }
    
    public void save() {
        OdinManager.getUserStorage().put(this.username, this);
    }

    public void load() {
        if (OdinManager.getUserStorage().containsKey(this.username)) {
            /* TODO */
            OdinUser temp = OdinManager.getUserStorage().get(this.username);
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
            this.user = OdinManager.getScript().getUser(this.username);
        } catch (UnsupportedMethod e) {
            OdinManager.getLogging().stackTrace(e);
        } catch (SQLException e) {
            OdinManager.getLogging().stackTrace(e);
        }
    }

    public boolean isLinked() {
        return OdinManager.getLinkedUsernames().containsKey(this.username);
    }

    public String getLinkedName() {
        if (OdinManager.getLinkedUsernames().containsKey(this.username)) {
            return OdinManager.getLinkedUsernames().get(this.username);
        }
        return null;
    }

    public boolean isAuthenticated() {
        return OdinManager.getAuthenticatedUsers().contains(this.username);
    }

    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            OdinManager.getAuthenticatedUsers().add(this.username);
            if (OdinManager.getConfig().getBoolean("session.enabled")) {
                setSession();
            }
        } else {
            logout();
        }
    }

    public boolean logout() {
        if (OdinManager.getAuthenticatedUsers().contains(this.username)) {
            OdinManager.getAuthenticatedUsers().remove(this.username);
            if (OdinManager.getUserPasswordAttempts().containsKey(this.username)) {
                OdinManager.getUserPasswordAttempts().remove(this.username);
            }
            if (OdinManager.getUserTimeouts().contains(this.username)) {
                OdinManager.getUserTimeouts().remove(this.username);
            }
            return true;
        }
        return false;
    }

    public boolean login(String password) {
        try {
            if (!OdinManager.getAuthenticatedUsers().contains(this.username)) {
                if (isLinked()) {
                    if (OdinManager.getScript().authenticate(getLinkedName(), password)) {
                        OdinManager.getAuthenticatedUsers().add(this.username);
                        return true;
                    }
                } else {
                    if (OdinManager.getScript().authenticate(this.username, password)) {
                        OdinManager.getAuthenticatedUsers().add(this.username);
                        return true;
                    }
                }
            }
        } catch (UnsupportedMethod e) {
            OdinManager.getLogging().stackTrace(e);
        }
        return false;
    }

    public void login() {
        if (!OdinManager.getAuthenticatedUsers().contains(this.username)) {
            OdinManager.getAuthenticatedUsers().add(this.username);
        }
    }

    public boolean unlink() {
        if (OdinManager.getLinkedUsernames().containsKey(this.username)) {
            OdinManager.getLinkedUsernames().remove(this.username);
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
        return OdinManager.getUserSessions().containsKey(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip));
    }
    
    public long getSessionTime() {
        if (OdinManager.getConfig().getBoolean("session.enabled") && hasSession()) {
            return OdinManager.getUserSessions().get(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip));
        }
        return 0;
    }
    
    public void setSession() {
        if (OdinManager.getConfig().getBoolean("session.enabled")) {
            OdinManager.getUserSessions().put(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip), new Date().getTime() / 1000);
        }
    }

    public boolean isRegistered() {
        if (this.status != null) {
            switch (this.status) {
                case Authenticated: return true;
                case Registered: return true;
                case Guest: return false;
            }
        } else try {
            if (OdinManager.getScript().getScript().isRegistered(this.username)) {
                this.status = Status.Registered;
                return true;
            }
        } catch (UnsupportedMethod e) {
            OdinManager.getLogging().stackTrace(e);
            return false;
        }
        this.status = Status.Guest;
        return false;
    }

    public boolean isGuest() {
        if (this.status != null) {
            return this.status.equals(Status.Guest);
        } else {
            if (isRegistered()) {
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
        return this.username.length() < OdinManager.getConfig().getInt("username.minimum");
    }

    public boolean hasMaxLength() {
        return this.username.length() > OdinManager.getConfig().getInt("username.maximum");
    }
    
    public long getJoinTime() {
        if (OdinManager.getPlayerJoins().containsKey(this.username)) {
            return OdinManager.getPlayerJoins().get(this.username);
        }
        return 0;
    }
    
    public void setJoinTime() {
        OdinManager.getPlayerJoins().put(this.username, System.currentTimeMillis());
    }

    public void setJoinTime(long time) {
        OdinManager.getPlayerJoins().put(this.username, time);
    }
    
    public int getPasswordAttempts() {
        if (OdinManager.getUserPasswordAttempts().containsKey(this.username)) {
            return OdinManager.getUserPasswordAttempts().get(this.username);
        }
        return 0;
    }

    public void setPasswordAttempts(int attempts) {
        OdinManager.getUserPasswordAttempts().put(this.username, attempts);
    }

    public void clearPasswordAttempts() {
        OdinManager.getUserPasswordAttempts().put(this.username, 0);
    }

    public void increasePasswordAttempts() {
        if (OdinManager.getUserPasswordAttempts().containsKey(this.username)) {
            OdinManager.getUserPasswordAttempts().put(this.username,
                                                   OdinManager.getUserPasswordAttempts().get(this.username) + 1);
        } else {
            OdinManager.getUserPasswordAttempts().put(this.username, 1);
        }
    }
    
    public boolean hasBadCharacters() {
        return this.badcharacters;
    }
    
    public boolean isFilterWhitelisted() {
        return OdinManager.getConfig().getString("filter.whitelist").contains(this.username);
    }

    public void setTimeout() {
        OdinManager.getUserTimeouts().add(this.username);
    }
}
