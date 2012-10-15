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

import com.craftfire.commons.managers.DataManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StorageManager {
    private final DataManager dataManager;
    private Map<Table, String> primaries = new HashMap<Table, String>();

    public StorageManager(DataManager dataManager) {
        this.dataManager = dataManager;
        checkDatabases();
        setupPrimaries();
    }

    public enum Table {
        INVENTORY("ODIN_INVENTORIES");

        private String name;
        Table(String name) {
            this.name = name;
        }
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }
    
    public boolean exists(Table table, String value) {
        return getDataManager().exist(table.name, this.primaries.get(table), value);
    }

    public void checkDatabases() {
        checkInventoryDatabase();
    }

    private void setupPrimaries() {
        this.primaries.put(Table.INVENTORY, "PLAYERNAME");
    }

    private void checkInventoryDatabase() {
        try {
            getDataManager().executeQuery("CREATE TABLE IF NOT EXISTS ODIN_INVENTORIES(" +
                                          "ID INT PRIMARY KEY, " +
                                          "PLAYERNAME VARCHAR(50), " +
                                          "INVENTORY TEXT," +
                                           "ARMOR TEXT)");
        } catch (SQLException e) {
            OdinManager.getLogging().error("Failed while creating odin_inventories table for H2 database.");
            OdinManager.getLogging().stackTrace(e);
            return;
        }
        OdinManager.getLogging().debug("Successfully created odin_inventories table for H2 database.");
    }
}
