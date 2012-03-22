/*
 * This file is part of AuthDB <http://www.authdb.com/>.
 *
 * AuthDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.authdb.managers;

import com.craftfire.authapi.classes.ScriptUser;
import com.craftfire.authdb.util.MainUtils;
import com.craftfire.commons.CraftCommons;

import java.util.Date;

public class AuthDBUser {
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
    public AuthDBUser(final String username) {
        this.username = username;
        this.badcharacters = MainUtils.hasBadCharacters(username, AuthDBManager.cfgMgr.getString("filter.username"));
        load();
    }
    
    public void save() {
        AuthDBManager.userStorage.put(this.username, this);
    }

    public void load() {
        if (AuthDBManager.userStorage.containsKey(this.username)) {
            /* TODO */
            AuthDBUser temp = AuthDBManager.userStorage.get(this.username);
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
        this.user = AuthDBManager.authAPI.getUser(this.username);
    }

    public boolean isLinked() {
        /* TODO */
        return false;
    }

    public String getLinkedName() {
        /* TODO */
        return null;
    }
    
    public void setLinkedName() {
        /* TODO */
    }

    public boolean isAuthenticated() {
        return AuthDBManager.userAuthenticated.contains(this.username);
    }

    public void setAuthenticated(boolean authenticated) {
        AuthDBManager.userAuthenticated.add(this.username);
        if (AuthDBManager.cfgMgr.getBoolean("session.enabled")) {
            setSession();
        }
    }

    public boolean logout() {
        if (AuthDBManager.userAuthenticated.contains(this.username)) {
            AuthDBManager.userAuthenticated.remove(this.username);
            return true;
        }
        return false;
    }

    public boolean login(String password) {
        if (! AuthDBManager.userAuthenticated.contains(this.username)) {
            if (AuthDBManager.authAPI.authenticate(this.username, password)) {
                AuthDBManager.userAuthenticated.add(this.username);
                return true;
            }
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
        return AuthDBManager.userSessions.containsKey(CraftCommons.md5(this.username + this.ip));
    }
    
    public long getSessionTime() {
        if (AuthDBManager.cfgMgr.getBoolean("session.enabled") && hasSession()) {
            return AuthDBManager.userSessions.get(CraftCommons.md5(this.username + this.ip));
        }
        return 0;
    }
    
    public void setSession() {
        if (AuthDBManager.cfgMgr.getBoolean("session.enabled")) {
            AuthDBManager.userSessions.put(CraftCommons.md5(this.username + this.ip), new Date().getTime() / 1000);
        }
    }

    public boolean isRegistered() {
        if (this.status != null) {
            switch (this.status) {
                case Authenticated: return true;
                case Registered: return true;
                case Guest: return false;
            }
        } else if (AuthDBManager.authAPI.getScript().isRegistered(this.username)) {
            this.status = Status.Registered;
            return true;
        }
        this.status = Status.Guest;
        return false;
    }

    public boolean isGuest() {
        if (this.status != null) {
            return this.status.equals(Status.Guest);
        } else {
            if (! AuthDBManager.authAPI.getScript().isRegistered(this.username)) {
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
        return this.username.length() < AuthDBManager.cfgMgr.getInteger("username.minimum");
    }

    public boolean hasMaxLength() {
        return this.username.length() > AuthDBManager.cfgMgr.getInteger("username.maximum");
    }
    
    public long getJoinTime() {
        if (AuthDBManager.playerJoin.containsKey(this.username)) {
            return AuthDBManager.playerJoin.get(this.username);
        }
        return 0;
    }
    
    public void setJoinTime() {
        AuthDBManager.playerJoin.put(this.username, System.currentTimeMillis());
    }

    public void setJoinTime(long time) {
        AuthDBManager.playerJoin.put(this.username, time);
    }
    
    public int getPasswordAttempts() {
        if (AuthDBManager.userPasswordAttempts.containsKey(this.username)) {
            return AuthDBManager.userPasswordAttempts.get(this.username);
        }
        return 0;
    }

    public void setPasswordAttempts(int attempts) {
        AuthDBManager.userPasswordAttempts.put(this.username, attempts);
    }

    public void clearPasswordAttempts() {
        AuthDBManager.userPasswordAttempts.put(this.username, 0);
    }

    public void increasePasswordAttempts() {
        if (AuthDBManager.userPasswordAttempts.containsKey(this.username)) {
            AuthDBManager.userPasswordAttempts.put(this.username,
                                                   AuthDBManager.userPasswordAttempts.get(this.username) + 1);
        } else {
            AuthDBManager.userPasswordAttempts.put(this.username, 1);
        }
    }
    
    public boolean hasBadCharacters() {
        return this.badcharacters;
    }
    
    public boolean isFilterWhitelisted() {
        return AuthDBManager.cfgMgr.getString("filter.whitelist").contains(this.username);
    }

    public void setTimeout() {
        AuthDBManager.userTimeouts.add(this.username);
    }
}
