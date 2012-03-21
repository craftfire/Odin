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
import java.util.HashSet;

public class LoggingHandler {

    public static void stackTrace(Exception e, Thread t) {
        HashSet<String> set = new HashSet<String>();
        set.add("AuthDB version: " + AuthDBManager.pluginVersion);
        set.add("MySQL keep alive: " + AuthDBManager.dataManager.isKeepAlive());
        set.add("MySQL connection: " + AuthDBManager.dataManager.isConnected());
        set.add("MySQL last query: " + AuthDBManager.dataManager.getLastQuery());
        if (AuthDBManager.cfgMgr.getBoolean("customdb.enabled")) {
            set.add("Script: Custom");
            set.add("Custom table: " + AuthDBManager.cfgMgr.getString("customdb.table"));
            if (AuthDBManager.cfgMgr.getBoolean("customdb.emailrequired")) {
                set.add("Custom email field: " + AuthDBManager.cfgMgr.getString("customdb.emailfield"));
            }
            set.add("Custom password field: " + AuthDBManager.cfgMgr.getString("customdb.passfield"));
            set.add("Custom username field: " + AuthDBManager.cfgMgr.getString("customdb.userfield"));
            set.add("Custom encryption: " + AuthDBManager.cfgMgr.getString("customdb.encryption"));
            set.add("Custom table schema:");
            try {
                ResultSet rs = AuthDBManager.dataManager.getResultSet(
                                    "SELECT * FROM `" + AuthDBManager.cfgMgr.getString("customdb.table") + "` LIMIT 1");
                ResultSetMetaData metaData = rs.getMetaData();
                int rowCount = metaData.getColumnCount();
                set.add("Table Name : " + metaData.getTableName(2));
                set.add("Column\tType(size)");
                for (int i = 0; i < rowCount; i++) {
                    set.add(metaData.getColumnName(i + 1) + "\t" +
                            metaData.getColumnTypeName(i + 1) + "(" +
                            metaData.getColumnDisplaySize(i + 1) + ")");
                }
            } catch (SQLException a) {
                AuthDBManager.logMgr.error("Failed while getting MySQL table schema.");
            }
        } else {
            set.add("Script chosen: " + AuthDBManager.authAPI.getScript().getScriptName());
            set.add("Script version: " + AuthDBManager.authAPI.getScript().getVersion());
            set.add("Table prefix: " + AuthDBManager.cfgMgr.getString("script.tableprefix"));
        }
        AuthDBManager.logMgr.stackTrace(e, t, set);    
    }
}
