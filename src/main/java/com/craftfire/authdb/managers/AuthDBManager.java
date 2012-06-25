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

import com.craftfire.authapi.AuthAPI;
import com.craftfire.authapi.exceptions.UnsupportedScript;
import com.craftfire.authapi.exceptions.UnsupportedVersion;
import com.craftfire.authdb.managers.permissions.PermissionsManager;
import com.craftfire.authdb.util.MainUtils;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.DataManager;
import com.craftfire.commons.DataType;
import com.craftfire.commons.LoggingManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class AuthDBManager {
    public static AuthAPI authAPI;
    public static DataManager scriptDataManager;
    public static DataManager storageDataManager;
    public static StorageManager storageManager;
    public static ConfigurationManager cfgMgr;
    public static CommandManager cmdMgr;
    public static InventoryManager invMgr;
    public static PermissionsManager prmMgr;
    public static MessageManager msgMgr;
    public static LoggingManager logMgr;
    public static CraftCommons craftCommons;
    
    public static String pluginName, pluginVersion;
    
    public static HashMap<String, Long> userSessions = new HashMap<String, Long>();
    public static HashSet<String> userAuthenticated = new HashSet<String>();
    public static HashSet<String> userTimeouts = new HashSet<String>();
    public static HashMap<String, String> userLinkedNames = new HashMap<String, String>();
    public static HashMap<String, AuthDBUser> userStorage = new HashMap<String, AuthDBUser>();
    public static HashMap<String, Integer> userPasswordAttempts = new HashMap<String, Integer>();

    public static HashMap<String, String> playerInventory = new HashMap<String, String>();
    public static HashMap<String, String> playerArmor = new HashMap<String, String>();
    public static HashMap<String, Long> playerJoin = new HashMap<String, Long>();

    public void clean() {
        userSessions.clear();
        userAuthenticated.clear();
        userStorage.clear();
        userTimeouts.clear();
        userPasswordAttempts.clear();
        playerInventory.clear();
        playerArmor.clear();
        playerJoin.clear();
    }

    public void load(File directory) {
        setClasses();
        loadConfiguration(directory);
        loadAuthAPI(directory);
    }

    protected void setClasses() {
        AuthDBManager.cfgMgr = new ConfigurationManager();
        AuthDBManager.prmMgr = new PermissionsManager();
        AuthDBManager.cmdMgr = new CommandManager();
        AuthDBManager.msgMgr = new MessageManager();
        AuthDBManager.craftCommons = new CraftCommons();
    }

    protected void loadAuthAPI(File directory) {
        AuthDBManager.scriptDataManager = new DataManager(DataType.MYSQL,
                                                          AuthDBManager.cfgMgr.getString("database.username"),
                                                          AuthDBManager.cfgMgr.getString("database.password"));
        AuthDBManager.scriptDataManager.setHost(AuthDBManager.cfgMgr.getString("database.host"));
        AuthDBManager.scriptDataManager.setPort(AuthDBManager.cfgMgr.getInteger("database.port"));
        AuthDBManager.scriptDataManager.setDatabase(AuthDBManager.cfgMgr.getString("database.name"));
        AuthDBManager.scriptDataManager.setPrefix(AuthDBManager.cfgMgr.getString("script.tableprefix"));
        AuthDBManager.scriptDataManager.setTimeout(AuthDBManager.cfgMgr.getInteger("database.timeout"));
        AuthDBManager.scriptDataManager.setKeepAlive(AuthDBManager.cfgMgr.getBoolean("database.keepalive"));
        AuthDBManager.storageDataManager = new DataManager(DataType.H2,
                                                          AuthDBManager.cfgMgr.getString("database.username"),
                                                          AuthDBManager.cfgMgr.getString("database.password"));
        AuthDBManager.storageDataManager.setDirectory(directory + "/data/AuthDB");
        AuthDBManager.logMgr.debug("Storage data manager has been loaded.");
        try {
            AuthDBManager.authAPI = new AuthAPI(
                    AuthDBManager.cfgMgr.getString("script.name"),
                    AuthDBManager.cfgMgr.getString("script.version"),
                    AuthDBManager.scriptDataManager);
            AuthDBManager.logMgr.debug("AuthAPI has been loaded.");
        } catch (UnsupportedScript u) {
            final Thread t = Thread.currentThread();
            //TODO
            LoggingHandler.stackTrace(u, t);
        } catch (UnsupportedVersion u) {
            final Thread t = Thread.currentThread();
            //TODO
            LoggingHandler.stackTrace(u, t);
        }
        AuthDBManager.storageManager = new StorageManager();
    }

    protected void loadConfiguration(File directory) {
        try {
            AuthDBManager.cfgMgr.load(CraftCommons.loadYaml(new File(directory + "/config/basic.yml")),
                    AuthDBManager.craftCommons.loadLocalYaml("files/config/basic.yml"));
            AuthDBManager.cfgMgr.load(CraftCommons.loadYaml(new File(directory + "/config/advanced.yml")),
                    AuthDBManager.craftCommons.loadLocalYaml("files/config/advanced.yml"));
            AuthDBManager.logMgr = new LoggingManager(
                                                    "Minecraft.AuthDB",
                                                    directory + "/logs/",
                                                    "[AuthDB]",
                                                    AuthDBManager.cfgMgr.getString("plugin.logformat"));
            AuthDBManager.logMgr.setDebug(AuthDBManager.cfgMgr.getBoolean("plugin.debugmode"));
            AuthDBManager.logMgr.setLogging(AuthDBManager.cfgMgr.getBoolean("plugin.logging"));
            MainUtils util = new MainUtils();
            util.loadLanguage(directory.toString() + "\\translations\\", "commands");
            util.loadLanguage(directory.toString() + "\\translations\\", "messages");
        } catch (IOException e) {
            /* TODO */
            e.printStackTrace();
        }
    }
}
