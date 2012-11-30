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
package com.craftfire.odin.managers;

import com.craftfire.commons.database.DataManager;

import javax.swing.text.TabExpander;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StorageManager {
    private final DataManager dataManager;

    public StorageManager(DataManager dataManager) {
        this.dataManager = dataManager;
        checkDatabases();
    }

    public enum InventoryField {
        INVENTORY, ARMOR
    }

    public enum Table {
        INVENTORY("ODIN_INVENTORIES", "PLAYERNAME"),
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
        return (getInfo("version") == null) ? getVersion() : getInfo("version");
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

    private void checkDatabases() {
        checkInventoryDatabase();
    }

    public String getInfo(String info) {
        return getDataManager().getStringField(Table.INFORMATION.getName(), "DATA", Table.INFORMATION.getPrimary() + "='" + info + "'");
    }

    private void setInfo(String info, Object value) throws SQLException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("INFO", info);
        data.put("DATA", value);
        getDataManager().insertFields(data, Table.INFORMATION.getName());
    }

    private void checkInventoryDatabase() {
        if (!getDataManager().tableExist(Table.INVENTORY.getName())) {
            try {
                getDataManager().executeQuery("CREATE TABLE IF NOT EXISTS " + Table.INVENTORY.getName() + "(" +
                                              "ID INT PRIMARY KEY, " +
                                              "PLAYERNAME VARCHAR(50), " +
                                              "INVENTORY TEXT," +
                                              "ARMOR TEXT)");
            } catch (SQLException e) {
                OdinManager.getLogging().error("Failed while creating " + Table.INVENTORY.getName() + " table for H2 database.");
                OdinManager.getLogging().stackTrace(e);
                return;
            }
            OdinManager.getLogging().debug("Successfully created " + Table.INVENTORY.getName() + " table for H2 database.");
        }
        if (!getDataManager().tableExist(Table.INFORMATION.getName())) {
            try {
                getDataManager().executeQuery("CREATE TABLE IF NOT EXISTS " + Table.INFORMATION.getName() + "(" +
                                              "INFO VARCHAR(50), " +
                                              "VALUE VARCHAR(100))");
            } catch (SQLException e) {
                OdinManager.getLogging().error("Failed while creating " + Table.INFORMATION.getName() + " table for H2 database.");
                OdinManager.getLogging().stackTrace(e);
                return;
            }
            OdinManager.getLogging().debug("Successfully created " + Table.INFORMATION.getName() + " table for H2 database.");
            try {
                setInfo("version", getVersion());
                setInfo("installed", System.currentTimeMillis() / 1000);
                setInfo("updated", 0);
            } catch (SQLException e) {
                OdinManager.getLogging().error("Failed while inserting into the " + Table.INFORMATION.getName() + " table.");
                OdinManager.getLogging().stackTrace(e);
                return;
            }

            OdinManager.getLogging().debug("Successfully inserted data into the " + Table.INFORMATION.getName() + " table.");
        }
    }
}
