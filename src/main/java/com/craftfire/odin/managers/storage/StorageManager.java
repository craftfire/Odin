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

import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.database.DataManager;
import com.craftfire.commons.encryption.Encryption;
import com.craftfire.commons.ip.IPAddress;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinUser;
import com.craftfire.odin.managers.inventory.InventoryItem;
import com.craftfire.odin.managers.inventory.InventoryManager;

import javax.swing.text.TabExpander;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StorageManager {
    private final DataManager dataManager;
    private Map<String, OdinUser> users = new HashMap<String, OdinUser>();
    private Map<String, String> linkedUsernames = new HashMap<String, String>();
    private Set<String> authenticatedUsers = new HashSet<String>();
    private Map<String, Long> userSessions = new HashMap<String, Long>();
    private Map<String, String> userInventories = new HashMap<String, String>();
    private Map<String, String> userArmor = new HashMap<String, String>();

    //TODO: move inventory methods to StoredOdinUser
    public StorageManager(DataManager dataManager) {
        this.dataManager = dataManager;
        checkDatabases();
    }

    public void clear() {
        this.users.clear();
        this.linkedUsernames.clear();
        this.authenticatedUsers.clear();
        this.userSessions.clear();
    }

    public boolean hasLinkedUsername(String username) {
        return this.linkedUsernames.containsKey(username);
    }

    public void putLinkedUsername(String username, String linkedUsername) {
        this.linkedUsernames.put(username, linkedUsername);
    }

    public void removeLinkedUsername(String username) {
        if (hasLinkedUsername(username)) {
            this.linkedUsernames.remove(username);
        }
    }

    public String getLinkedUsername(String username) {
        return this.linkedUsernames.get(username);
    }

    public boolean isAuthenticated(String username) {
        return this.authenticatedUsers.contains(username);
    }

    public void putAuthenticated(String username) {
        OdinManager.getLogger().debug("Adding '" + username + "' to the authenticated list.");
        this.authenticatedUsers.add(username);
    }

    public void removeAuthenticated(String username) {
        if (isAuthenticated(username)) {
            OdinManager.getLogger().debug("Removing '" + username + "' from the authenticated list.");
            this.authenticatedUsers.remove(username);
        }
    }

    public boolean hasSession(OdinUser user) {
        return this.userSessions.containsKey(CraftCommons.encrypt(Encryption.MD5,
                                                                  user.getUsername() + user.getIP().toString()));
    }

    public void putSession(OdinUser user) {
        this.userSessions.put(CraftCommons.encrypt(Encryption.MD5,
                                                   user.getUsername() + user.getIP().toString()),
                              System.currentTimeMillis() / 1000);
    }

    public long getSessionTime(OdinUser user) {
        if (hasSession(user)) {
            return this.userSessions.get(CraftCommons.encrypt(Encryption.MD5,
                                         user.getUsername() + user.getIP().toString()));
        }
        return 0;
    }

    public void removeSession(OdinUser user) {
        if (hasSession(user)) {
            this.userSessions.remove(CraftCommons.encrypt(Encryption.MD5,
                                     user.getUsername() + user.getIP().toString()));
        }
    }

    public Map<String, OdinUser> getCachedUsers() {
        return this.users;
    }

    public OdinUser getCachedUser(String username) {
        if (isCachedUser(username)) {
            return this.users.get(username);
        } else {
            OdinManager.getLogger().debug("Could not find cached username '" + username + "', returning null.");
            return null;
        }
    }

    public void putCachedUser(OdinUser user) {
        OdinManager.getLogger().debug("Adding username '" + user.getUsername() + "' to cache.");
        this.users.put(user.getUsername(), user);
    }

    public boolean isCachedUser(String username) {
        return this.users.containsKey(username) && this.users.get(username) != null;
    }

    public enum InventoryField {
        INVENTORY, ARMOR
    }

    public enum Table {
        INVENTORY("ODIN_INVENTORIES", "USERNAME"),
        USERS("ODIN_USERS", "USERNAME"),
        INFORMATION("ODIN_INFORMATION", "INFO");

        private String name, primary;
        Table(String name, String primary) {
            this.name = name;
            this.primary = primary;
        }

        public String getName() {
            return this.name;
        }

        public String getPrimary() {
            return this.primary;
        }
    }

    public String getVersion() {
        return "1.0";
    }

    public String getStoredVersion() {
        return getInfo("version");
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public String getString(Table table, String field, String where) {
        return getDataManager().getStringField(table.getName(), field, where);
    }
    
    public boolean exists(Table table, String value) {
        return getDataManager().exist(table.getName(), table.getPrimary(), value);
    }

    public boolean userExists(Table table, OdinUser user) {
        return userExists(table, user.getUsername());
    }

    public boolean userExists(Table table, String username) {
       return exists(table, username);
    }

    public StoredOdinUser getUser(String username) throws SQLException {
        if (userExists(Table.USERS, username)) {
            return new StoredOdinUser(getDataManager().getResults("SELECT * FROM " + Table.USERS.getName() + " WHERE " + Table.USERS.getPrimary() + " = '" + username + "' LIMIT 1"));
        } else {
            return new StoredOdinUser(username);
        }
    }

    public String getInfo(String info) {
        return getDataManager().getStringField(Table.INFORMATION.getName(), "DATA", Table.INFORMATION.getPrimary() + " = '" + info + "'");
    }

    private void setInfo(String info, Object value) throws SQLException {
        if (exists(Table.INFORMATION, info)) {
            getDataManager().updateField(Table.INFORMATION.getName(), "DATA", value, Table.INFORMATION.getPrimary() + " = '" + info + "'");
        } else {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("INFO", info);
            data.put("DATA", value);
            getDataManager().insertFields(data, Table.INFORMATION.getName());
        }
    }

    public String getInventory(String username) {
        if (this.userInventories.containsKey(username)) {
            return this.userInventories.get(username);
        }
        return getDataManager().getStringField(Table.INVENTORY.getName(), "INVENTORY", Table.INVENTORY.getPrimary() + " = '" + username + "'");
    }

    public void setInventory(String username, String inventory) {
        this.userInventories.put(username, inventory);
        if (userExists(Table.INVENTORY, username)) {
            try {
                getDataManager().updateField(Table.INVENTORY.getName(), "INVENTORY", inventory, Table.INVENTORY.getPrimary() + " = '" + username + "'");
            } catch (SQLException e) {
                OdinManager.getLogger().error("Failed updating inventory for username '" + username + "'.");
                OdinManager.getLogger().debug("Tried updating inventory '" + inventory + "'");
                OdinManager.getLogger().stackTrace(e);
            }
        } else {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put(Table.INVENTORY.getPrimary(), username);
            data.put("INVENTORY", inventory);
            try {
                getDataManager().insertFields(data, Table.INVENTORY.getName());
            } catch (SQLException e) {
                OdinManager.getLogger().error("Failed inserting inventory for username '" + username + "'.");
                OdinManager.getLogger().debug("Tried inserting inventory '" + inventory + "'");
                OdinManager.getLogger().stackTrace(e);
            }
            data.clear();
        }
    }

    public String getArmor(String username) {
        if (this.userArmor.containsKey(username)) {
            return this.userArmor.get(username);
        }
        return getDataManager().getStringField(Table.INVENTORY.getName(), "ARMOR", Table.INVENTORY.getPrimary() + "='" + username + "'");
    }

    public void setArmor(String username, String armor) {
        this.userArmor.put(username, armor);
        if (userExists(Table.INVENTORY, username)) {
            try {
                getDataManager().updateField(Table.INVENTORY.getName(), "ARMOR", armor, Table.INVENTORY.getPrimary() + "='" + username + "'");
            } catch (SQLException e) {
                OdinManager.getLogger().error("Failed updating armor for username '" + username + "'.");
                OdinManager.getLogger().debug("Tried updating armor '" + armor + "'");
                OdinManager.getLogger().stackTrace(e);
            }
        } else {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put(Table.INVENTORY.getPrimary(), username);
            data.put("ARMOR", armor);
            try {
                getDataManager().insertFields(data, Table.INVENTORY.getName());
            } catch (SQLException e) {
                OdinManager.getLogger().error("Failed inserting armor for username '" + username + "'.");
                OdinManager.getLogger().debug("Tried inserting armor '" + armor + "'");
                OdinManager.getLogger().stackTrace(e);
            }
            data.clear();
        }
    }

    private void checkDatabases() {
        if (!getDataManager().tableExist(Table.USERS.getName())) {
            try {
                getDataManager().executeQuery("CREATE TABLE IF NOT EXISTS " + Table.USERS.getName() + "(" +
                                              "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                              "USERNAME VARCHAR(50), " +
                                              "LINKED_NAME VARCHAR(50), " +
                                              "PASSWORD VARCHAR(255), " +
                                              "PASSWORD_SALT VARCHAR(255), " +
                                              "EMAIL VARCHAR(255), " +
                                              "IP_ADDRESS VARCHAR(45), " +
                                              "ACTIVATED INT(1), " +
                                              "REGISTERED INT(1), " +
                                              "RELOAD_TIME INT(11), " +
                                              "SESSION_TIME INT(11))");
            } catch (SQLException e) {
                OdinManager.getLogger().error("Failed creating " + Table.USERS.getName() + " table for H2 database.");
                OdinManager.getLogger().stackTrace(e);
                return;
            }
            OdinManager.getLogger().debug("Successfully created " + Table.USERS.getName() + " table for H2 database.");
        }
        if (!getDataManager().tableExist(Table.INVENTORY.getName())) {
            try {
                getDataManager().executeQuery("CREATE TABLE IF NOT EXISTS " + Table.INVENTORY.getName() + "(" +
                                              "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                              "USERNAME VARCHAR(50), " +
                                              "INVENTORY TEXT, " +
                                              "ARMOR TEXT)");
            } catch (SQLException e) {
                OdinManager.getLogger().error("Failed creating " + Table.INVENTORY.getName() + " table for H2 database.");
                OdinManager.getLogger().stackTrace(e);
                return;
            }
            OdinManager.getLogger().debug("Successfully created " + Table.INVENTORY.getName() + " table for H2 database.");
        }
        if (!getDataManager().tableExist(Table.INFORMATION.getName())) {
            try {
                getDataManager().executeQuery("CREATE TABLE IF NOT EXISTS " + Table.INFORMATION.getName() + "(" +
                                              "INFO VARCHAR(50), " +
                                              "DATA VARCHAR(100))");
            } catch (SQLException e) {
                OdinManager.getLogger().error("Failed creating " + Table.INFORMATION.getName() + " table for H2 database.");
                OdinManager.getLogger().stackTrace(e);
                return;
            }
            OdinManager.getLogger().debug("Successfully created " + Table.INFORMATION.getName() + " table for H2 database.");
            try {
                setInfo("version", getVersion());
                setInfo("installed", System.currentTimeMillis() / 1000);
                setInfo("updated", 0);
            } catch (SQLException e) {
                OdinManager.getLogger().error("Failed inserting into the " + Table.INFORMATION.getName() + " table.");
                OdinManager.getLogger().stackTrace(e);
                return;
            }
            OdinManager.getLogger().debug("Successfully inserted data into the " + Table.INFORMATION.getName() + " table.");
        } else {
            String version = getStoredVersion();
            if (version == null || !version.equalsIgnoreCase(getVersion())) {
                try {
                    setInfo("version", getVersion());
                    setInfo("updated", System.currentTimeMillis() / 1000);
                } catch (SQLException e) {
                    OdinManager.getLogger().debug("Failed while updating the version of the StorageDatabase (" + version +
                                                   " -> " + getVersion() + ".");
                    OdinManager.getLogger().stackTrace(e);
                    return;
                }
                OdinManager.getLogger().debug("Successfully updated the version of the StorageDatabase (" + version +
                                               " -> " + getVersion() + ".");
            }
        }
    }
}
