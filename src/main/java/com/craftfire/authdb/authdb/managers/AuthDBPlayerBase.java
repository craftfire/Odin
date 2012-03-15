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
package com.craftfire.authdb.authdb.managers;

import com.craftfire.authapi.classes.ScriptUser;

public class AuthDBPlayerBase {
    protected String username;
    private ScriptUser user = null;
    private Status status;
    private String password, ip;

    /**
     * Default constructor for the object.
     *
     * @param username name of the player.
     */
    public AuthDBPlayerBase(final String username) {
        this.username = username;
    }
    
    public String getUsername() {
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
        /* TODO */
        return false;
    }

    public void setAuthenticated(boolean authenticated) {
        /* TODO */
    }

    public boolean logout() {
        /* TODO */
        return false;
    }

    public boolean login(String password) {
        /* TODO */
        return false;
    }

    public String getIP() {
        /* TODO */
        return null;
    }

    public boolean isRegistered() {
        if (this.status != null && this.status.equals(Status.Registered)) {
            return true;
        } else {
            if (AuthDBManager.authAPI.getScript().isRegistered(this.username)) {
                this.status = Status.Registered;
                return true;
            } else {
                this.status = Status.Guest;
            }
        }
    }

    public boolean isGuest() {
        if (this.status != null && this.status.equals(Status.Guest)) {
            return true;
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
}
