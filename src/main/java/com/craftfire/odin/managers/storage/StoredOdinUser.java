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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StoredOdinUser {
    private final String username;
    private Map<OdinUserField, Object> original = new HashMap<OdinUserField, Object>(),
                                       data = new HashMap<OdinUserField, Object>();

    public StoredOdinUser(String username) {
        this.username = username;
    }

    public StoredOdinUser(Results data) {
        DataRow row = data.getFirstResult();
        this.username = row.getStringField("USERNAME");
        for (OdinUserField field : OdinUserField.values()) {
            this.original.put(field, row.getIntField(field.getName()));
        }
        this.data = this.original;
    }

    public void save() {
        // TODO: sync with script database
        Map<String, Object> saveData = new HashMap<String, Object>();
        for (OdinUserField field : OdinUserField.values()) {
            if (!this.original.get(field).equals(this.data.get(field))) {
                saveData.put(field.getName(), this.data.get(field));
            }
        }
        if (!saveData.isEmpty()) {
            OdinManager.getStorage().saveUser(getUsername(), saveData);
        }
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
        return (Integer) this.data.get(OdinUserField.ID);
    }

    public String getUsername() {
        return this.username;
    }

    public String getLinkedName() {
        return (String) this.data.get(OdinUserField.LINKED_NAME);
    }

    public void setLinkedName(String linkedName) {
        this.data.put(OdinUserField.LINKED_NAME, linkedName);
    }

    public String getPassword() {
        return (String) this.data.get(OdinUserField.PASSWORD);
    }

    public void setPassword(String password) {
        try {
            this.data.put(OdinUserField.PASSWORD, OdinManager.getScript().hashPassword(getUsername(), password));
        } catch (ScriptException e) {
            OdinManager.getLogger().error("Failed hashing password for user '" + getUsername() + "'.");
            OdinManager.getLogger().stackTrace(e);
        }
    }

    public String getPasswordSalt() {
        return (String) this.data.get(OdinUserField.PASSWORD_SALT);
    }

    public void setPasswordSalt(String passwordSalt) {
        this.data.put(OdinUserField.PASSWORD_SALT, passwordSalt);
    }

    public String getEmail() {
        return (String) this.data.get(OdinUserField.EMAIL);
    }

    public void setEmail(String email) {
        this.data.put(OdinUserField.EMAIL, email);
    }

    public String getIPAddress() {
        return (String) this.data.get(OdinUserField.IP_ADDRESS);
    }

    public void setIPAddress(IPAddress ipAddress) {
        if (this.data.get(OdinUserField.IP_ADDRESS) == null) {
            this.data.put(OdinUserField.IP_ADDRESS, "");
        } else {
            this.data.put(OdinUserField.IP_ADDRESS, ipAddress.toIPv4().toString());
        }
    }

    public void setIPAddress(String ipAddress) {
        this.data.put(OdinUserField.IP_ADDRESS, ipAddress);
    }

    public boolean isActivated() {
        return ((Integer) this.data.get(OdinUserField.ACTIVATED)) == 1;
    }

    public void setActivated(boolean activated) {
        if (activated) {
            this.data.put(OdinUserField.ACTIVATED, 1);
        } else {
            this.data.put(OdinUserField.ACTIVATED, 0);
        }
    }

    public boolean isRegistered() {
        return ((Integer) this.data.get(OdinUserField.REGISTERED)) == 1;
    }

    public void setRegistered(boolean registered) {
        if (registered) {
            this.data.put(OdinUserField.REGISTERED, 1);
        } else {
            this.data.put(OdinUserField.REGISTERED, 0);
        }
    }

    public long getSessionTime() {
        return (Long) this.data.get(OdinUserField.SESSION_TIME);
    }

    public void setSessionTime() {
        setSessionTime(System.currentTimeMillis() / 1000);
    }

    public void setSessionTime(long sessionTime) {
        this.data.put(OdinUserField.SESSION_TIME, sessionTime);
    }

    public long getReloadTime() {
        return (Long) this.data.get(OdinUserField.RELOAD_TIME);
    }

    public void setReloadTime() {
        setReloadTime(System.currentTimeMillis() / 1000);
    }

    public void setReloadTime(long reloadTime) {
        this.data.put(OdinUserField.RELOAD_TIME, reloadTime);
    }
}
