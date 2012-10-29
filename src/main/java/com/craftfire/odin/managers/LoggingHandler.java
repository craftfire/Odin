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

import com.craftfire.commons.util.LoggingManager;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

public class LoggingHandler extends LoggingManager {

    public LoggingHandler(String logger, String prefix) {
        super(logger, prefix);
    }

    @Override
    public void stackTrace(final Exception e) {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "Odin version: " + OdinManager.getPluginVersion());
        if (OdinManager.getBifrost() != null) {
            map.put(1, "MySQL keep alive: " + OdinManager.getScript().getDataManager().isKeepAlive());
            map.put(2, "MySQL connection: " + OdinManager.getScript().getDataManager().isConnected());
            map.put(3, "MySQL last query: " + OdinManager.getScript().getDataManager().getLastQuery());
        }
        if (OdinManager.getConfig().isInitialized() && OdinManager.getConfig().getBoolean("customdb.enabled")) {
            map.put(4, "Script: Custom");
            map.put(5, "Custom table: " + OdinManager.getConfig().getString("customdb.table"));
            if (OdinManager.getConfig().getBoolean("customdb.emailrequired")) {
                map.put(6, "Custom email field: " + OdinManager.getConfig().getString("customdb.emailfield"));
            }
            map.put(7, "Custom password field: " + OdinManager.getConfig().getString("customdb.passfield"));
            map.put(8, "Custom username field: " + OdinManager.getConfig().getString("customdb.userfield"));
            map.put(9, "Custom encryption: " + OdinManager.getConfig().getString("customdb.encryption"));
            map.put(10, "Custom table schema:");
            try {
                ResultSet rs = OdinManager.getScript().getDataManager().getResultSet(
                                    "SELECT * FROM `" + OdinManager.getConfig().getString("customdb.table") + "` LIMIT 1");
                ResultSetMetaData metaData = rs.getMetaData();
                int rowCount = metaData.getColumnCount();
                map.put(11, "Table Name : " + metaData.getTableName(2));
                map.put(12, "Column\tType(size)");
                for (int i = 0; i < rowCount; i++) {
                    map.put(13,
                            metaData.getColumnName(i + 1) + "\t" +
                            metaData.getColumnTypeName(i + 1) + "(" +
                            metaData.getColumnDisplaySize(i + 1) + ")");
                }
            } catch (SQLException a) {
                error("Failed while getting MySQL table schema.");
            }
        } else {
            if (OdinManager.getBifrost() != null && OdinManager.getScript() != null) {
                map.put(14, "Script chosen: " + OdinManager.getScript().getScriptName());
                map.put(15, "Script version: " + OdinManager.getScript().getVersion());
                map.put(16, "Table prefix: " + OdinManager.getConfig().getString("script.tableprefix"));
            } else if (OdinManager.getConfig().isInitialized() ) {
                map.put(17, "Odin will not work because you've set the wrong script name in basic.yml, " +
                            "please correct this node (script.name).");
                map.put(18, "Script in config: " + OdinManager.getConfig().getString("script.name"));
                map.put(19, "Script version in config: " +  OdinManager.getConfig().getString("script.version"));
                map.put(20, "Table prefix in config: " +  OdinManager.getConfig().getString("script.tableprefix"));
            }
        }
        stackTrace(e, map);
    }
}
