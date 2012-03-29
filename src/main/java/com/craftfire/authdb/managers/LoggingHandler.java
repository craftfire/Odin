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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

public class LoggingHandler {

    public static void stackTrace(final Exception e, final Thread t) {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "AuthDB version: " + AuthDBManager.pluginVersion);
        map.put(1, "MySQL keep alive: " + AuthDBManager.scriptDataManager.isKeepAlive());
        map.put(2, "MySQL connection: " + AuthDBManager.scriptDataManager.isConnected());
        map.put(3, "MySQL last query: " + AuthDBManager.scriptDataManager.getLastQuery());
        if (AuthDBManager.cfgMgr.getBoolean("customdb.enabled")) {
            map.put(4, "Script: Custom");
            map.put(5, "Custom table: " + AuthDBManager.cfgMgr.getString("customdb.table"));
            if (AuthDBManager.cfgMgr.getBoolean("customdb.emailrequired")) {
                map.put(6, "Custom email field: " + AuthDBManager.cfgMgr.getString("customdb.emailfield"));
            }
            map.put(7, "Custom password field: " + AuthDBManager.cfgMgr.getString("customdb.passfield"));
            map.put(8, "Custom username field: " + AuthDBManager.cfgMgr.getString("customdb.userfield"));
            map.put(9, "Custom encryption: " + AuthDBManager.cfgMgr.getString("customdb.encryption"));
            map.put(10, "Custom table schema:");
            try {
                ResultSet rs = AuthDBManager.scriptDataManager.getResultSet(
                                    "SELECT * FROM `" + AuthDBManager.cfgMgr.getString("customdb.table") + "` LIMIT 1");
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
                AuthDBManager.logMgr.error("Failed while getting MySQL table schema.");
            }
        } else {
            if (AuthDBManager.authAPI != null) {
                map.put(14, "Script chosen: " + AuthDBManager.authAPI.getScript().getScriptName());
                map.put(15, "Script version: " + AuthDBManager.authAPI.getScript().getVersion());
                map.put(16, "Table prefix: " + AuthDBManager.cfgMgr.getString("script.tableprefix"));
            } else {
                map.put(17, "AuthDB will not work because you've set the wrong script name in basic.yml, " +
                        "please correct this node (script.name).");
                map.put(18, "Script in config: " + AuthDBManager.cfgMgr.getString("script.name"));
                map.put(19, "Script version in config: " +  AuthDBManager.cfgMgr.getString("script.version"));
                map.put(20, "Table prefix in config: " +  AuthDBManager.cfgMgr.getString("script.tableprefix"));
            }
        }
        AuthDBManager.logMgr.stackTrace(e, t, map);
    }
}
