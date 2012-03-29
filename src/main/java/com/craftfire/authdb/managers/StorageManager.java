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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StorageManager {
    private Map<Table, String> primaries = new HashMap<Table, String>();

    public enum Table {
        INVENTORY("AUTHDB_INVENTORIES");
        
        private String name;
        Table(String name) {
            this.name = name;
        }
    }

    public StorageManager() {
        checkDatabases();
        setupPrimaries();
    }
    
    public boolean exists(Table table, String value) {
        return AuthDBManager.storageDataManager.exist(table.name, this.primaries.get(table), value);
    }

    public void checkDatabases() {
        checkInventoryDatabase();
    }

    private void setupPrimaries() {
        this.primaries.put(Table.INVENTORY, "PLAYERNAME");
    }

    private void checkInventoryDatabase() {
        try {
            AuthDBManager.storageDataManager.executeQuery("CREATE TABLE IF NOT EXISTS AUTHDB_INVENTORIES(" +
                                                            "ID INT PRIMARY KEY, " +
                                                            "PLAYERNAME VARCHAR(50), " +
                                                            "INVENTORY TEXT," +
                                                            "ARMOR TEXT)");
        } catch (SQLException e) {
            AuthDBManager.logMgr.error("Failed while creating authdb_inventories table for H2 database.");
            LoggingHandler.stackTrace(e, Thread.currentThread());
            return;
        }
        AuthDBManager.logMgr.debug("Successfully created authdb_inventories table for H2 database.");
    }
}
