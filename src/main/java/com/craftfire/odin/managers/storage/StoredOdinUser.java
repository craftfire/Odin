/*
 * This file is part of Odin.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
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

import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.commons.database.DataRow;
import com.craftfire.commons.database.Results;
import com.craftfire.commons.ip.IPAddress;
import com.craftfire.odin.managers.OdinManager;

public class StoredOdinUser {
    private final String username;
    private String linkedName, password, passwordSalt, email, ipAddress;
    private int id, activated, registered;
    private long sessionTime, reloadTime;

    public StoredOdinUser(String username) {
        this.username = username;
    }

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
        // TODO: sync with script database
    }

    public boolean authenticate(String password) {
        try {
            return OdinManager.getScript().hashPassword(getUsername(), password).equalsIgnoreCase(getPassword());
        } catch (ScriptException e) {
            OdinManager.getLogger().error("Failed authenticating user '" + getUsername() + "'.");
            OdinManager.getLogger().stackTrace(e);
        }
        return false;
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
        try {
            this.password = OdinManager.getScript().hashPassword(getUsername(), password);
        } catch (ScriptException e) {
            OdinManager.getLogger().error("Failed hashing password for user '" + getUsername() + "'.");
            OdinManager.getLogger().stackTrace(e);
        }
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

    public void setIPAddress(IPAddress ipAddress) {
        if (this.ipAddress == null) {
            this.ipAddress = "";
        } else {
            this.ipAddress = ipAddress.toIPv4().toString();
        }
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

    public void setSessionTime() {
        setSessionTime(System.currentTimeMillis() / 1000);
    }

    public void setSessionTime(long sessionTime) {
        this.sessionTime = sessionTime;
    }

    public long getReloadTime() {
        return this.reloadTime;
    }

    public void setReloadTime() {
        setReloadTime(System.currentTimeMillis() / 1000);
    }

    public void setReloadTime(long reloadTime) {
        this.reloadTime = reloadTime;
    }
}
