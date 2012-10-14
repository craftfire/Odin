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
        this.badcharacters = MainUtils.hasBadCharacters(username, OdinManager.getInstance().getConfig().getString("filter.username"));
        load();
    }
    
    public void save() {
        OdinManager.getInstance().getUserStorage().put(this.username, this);
    }

    public void load() {
        if (OdinManager.getInstance().getUserStorage().containsKey(this.username)) {
            /* TODO */
            OdinUser temp = OdinManager.getInstance().getUserStorage().get(this.username);
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
            this.user = OdinManager.getInstance().getScript().getUser(this.username);
        } catch (UnsupportedMethod e) {
            OdinManager.getInstance().getLogging().stackTrace(e);
        } catch (SQLException e) {
            OdinManager.getInstance().getLogging().stackTrace(e);
        }
    }

    public boolean isLinked() {
        return OdinManager.getInstance().getLinkedUsernames().containsKey(this.username);
    }

    public String getLinkedName() {
        if (OdinManager.getInstance().getLinkedUsernames().containsKey(this.username)) {
            return OdinManager.getInstance().getLinkedUsernames().get(this.username);
        }
        return null;
    }

    public boolean isAuthenticated() {
        return OdinManager.getInstance().getAuthenticatedUsers().contains(this.username);
    }

    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            OdinManager.getInstance().getAuthenticatedUsers().add(this.username);
            if (OdinManager.getInstance().getConfig().getBoolean("session.enabled")) {
                setSession();
            }
        } else {
            logout();
        }
    }

    public boolean logout() {
        if (OdinManager.getInstance().getAuthenticatedUsers().contains(this.username)) {
            OdinManager.getInstance().getAuthenticatedUsers().remove(this.username);
            if (OdinManager.getInstance().getUserPasswordAttempts().containsKey(this.username)) {
                OdinManager.getInstance().getUserPasswordAttempts().remove(this.username);
            }
            if (OdinManager.getInstance().getUserTimeouts().contains(this.username)) {
                OdinManager.getInstance().getUserTimeouts().remove(this.username);
            }
            return true;
        }
        return false;
    }

    public boolean login(String password) {
        try {
            if (!OdinManager.getInstance().getAuthenticatedUsers().contains(this.username)) {
                if (isLinked()) {
                    if (OdinManager.getInstance().getScript().authenticate(getLinkedName(), password)) {
                        OdinManager.getInstance().getAuthenticatedUsers().add(this.username);
                        return true;
                    }
                } else {
                    if (OdinManager.getInstance().getScript().authenticate(this.username, password)) {
                        OdinManager.getInstance().getAuthenticatedUsers().add(this.username);
                        return true;
                    }
                }
            }
        } catch (UnsupportedMethod e) {
            OdinManager.getInstance().getLogging().stackTrace(e);
        }
        return false;
    }

    public void login() {
        if (!OdinManager.getInstance().getAuthenticatedUsers().contains(this.username)) {
            OdinManager.getInstance().getAuthenticatedUsers().add(this.username);
        }
    }

    public boolean unlink() {
        if (OdinManager.getInstance().getLinkedUsernames().containsKey(this.username)) {
            OdinManager.getInstance().getLinkedUsernames().remove(this.username);
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
        return OdinManager.getInstance().getUserSessions().containsKey(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip));
    }
    
    public long getSessionTime() {
        if (OdinManager.getInstance().getConfig().getBoolean("session.enabled") && hasSession()) {
            return OdinManager.getInstance().getUserSessions().get(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip));
        }
        return 0;
    }
    
    public void setSession() {
        if (OdinManager.getInstance().getConfig().getBoolean("session.enabled")) {
            OdinManager.getInstance().getUserSessions().put(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip), new Date().getTime() / 1000);
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
            if (OdinManager.getInstance().getScript().getScript().isRegistered(this.username)) {
                this.status = Status.Registered;
                return true;
            }
        } catch (UnsupportedMethod e) {
            OdinManager.getInstance().getLogging().stackTrace(e);
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
        return this.username.length() < OdinManager.getInstance().getConfig().getInt("username.minimum");
    }

    public boolean hasMaxLength() {
        return this.username.length() > OdinManager.getInstance().getConfig().getInt("username.maximum");
    }
    
    public long getJoinTime() {
        if (OdinManager.getInstance().getPlayerJoins().containsKey(this.username)) {
            return OdinManager.getInstance().getPlayerJoins().get(this.username);
        }
        return 0;
    }
    
    public void setJoinTime() {
        OdinManager.getInstance().getPlayerJoins().put(this.username, System.currentTimeMillis());
    }

    public void setJoinTime(long time) {
        OdinManager.getInstance().getPlayerJoins().put(this.username, time);
    }
    
    public int getPasswordAttempts() {
        if (OdinManager.getInstance().getUserPasswordAttempts().containsKey(this.username)) {
            return OdinManager.getInstance().getUserPasswordAttempts().get(this.username);
        }
        return 0;
    }

    public void setPasswordAttempts(int attempts) {
        OdinManager.getInstance().getUserPasswordAttempts().put(this.username, attempts);
    }

    public void clearPasswordAttempts() {
        OdinManager.getInstance().getUserPasswordAttempts().put(this.username, 0);
    }

    public void increasePasswordAttempts() {
        if (OdinManager.getInstance().getUserPasswordAttempts().containsKey(this.username)) {
            OdinManager.getInstance().getUserPasswordAttempts().put(this.username,
                                                   OdinManager.getInstance().getUserPasswordAttempts().get(this.username) + 1);
        } else {
            OdinManager.getInstance().getUserPasswordAttempts().put(this.username, 1);
        }
    }
    
    public boolean hasBadCharacters() {
        return this.badcharacters;
    }
    
    public boolean isFilterWhitelisted() {
        return OdinManager.getInstance().getConfig().getString("filter.whitelist").contains(this.username);
    }

    public void setTimeout() {
        OdinManager.getInstance().getUserTimeouts().add(this.username);
    }
}
