/*
 * This file is part of Odin.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * Odin is licensed under the GNU Lesser General Public License.
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
package com.craftfire.odin.managers.storage;

import com.craftfire.commons.database.DataRow;
import com.craftfire.commons.database.Results;

public class StoredOdinUser {
    private final int id;
    private final String username;
    private String linkedName, password, passwordSalt, email, ipAddress;
    private int activated, registered;
    private long sessionTime, reloadTime;

    public StoredOdinUser(Results data) {
        DataRow row = data.getFirstResult();
        this.id = row.getIntField("ID");
        this.username = row.getStringField("USERNAME");
        this.linkedName = row.getStringField("LINKED_NAME");
        this.password = row.getStringField("PASSWORD");
        this.passwordSalt = row.getStringField("PASSWORD_SALT");
        this.email = row.getStringField("EMAIL");
        this.ipAddress = row.getStringField("IP_ADDRESS");
        this.activated = row.getIntField("ACTIVATED");
        this.registered = row.getIntField("REGISTERED");
        this.sessionTime = row.getLongField("SESSION_TIME");
        this.reloadTime = row.getLongField("RELOAD_TIME");
    }

    public void save() {
        //TODO
    }

    public int getID() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getLinkedName() {
        return this.linkedName;
    }

    public void setLinkedName(String linkedName) {
        this.linkedName = linkedName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return this.passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    public void setIPAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isActivated() {
        return this.activated == 1;
    }

    public void setActivated(boolean activated) {
        if (activated) {
            this.activated = 1;
        } else {
            this.activated = 0;
        }
    }

    public boolean isRegistered() {
        return this.registered == 1;
    }

    public void setRegistered(boolean registered) {
        if (registered) {
            this.registered = 1;
        } else {
            this.registered = 0;
        }
    }

    public long getSessionTime() {
        return this.sessionTime;
    }

    public void setSessionTime(long sessionTime) {
        this.sessionTime = sessionTime;
    }

    public long getReloadTime() {
        return this.reloadTime;
    }

    public void setReloadTime(long reloadTime) {
        this.reloadTime = reloadTime;
    }
}