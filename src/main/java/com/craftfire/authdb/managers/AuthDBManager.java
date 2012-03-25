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
import com.craftfire.commons.LoggingManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class AuthDBManager {
    public static AuthAPI authAPI;
    public static DataManager dataManager;
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
        loadAuthAPI();
    }

    protected void setClasses() {
        AuthDBManager.cfgMgr = new ConfigurationManager();
        AuthDBManager.prmMgr = new PermissionsManager();
        AuthDBManager.cmdMgr = new CommandManager();
        AuthDBManager.msgMgr = new MessageManager();
        AuthDBManager.craftCommons = new CraftCommons();
    }

    protected void loadAuthAPI() {
        AuthDBManager.dataManager = new DataManager(
                AuthDBManager.cfgMgr.getBoolean("database.keepalive"),
                AuthDBManager.cfgMgr.getInteger("database.timeout"),
                AuthDBManager.cfgMgr.getString("database.host"),
                AuthDBManager.cfgMgr.getInteger("database.port"),
                AuthDBManager.cfgMgr.getString("database.name"),
                AuthDBManager.cfgMgr.getString("database.username"),
                AuthDBManager.cfgMgr.getString("database.password"),
                AuthDBManager.cfgMgr.getString("script.tableprefix"));
        try {
            AuthDBManager.authAPI = new AuthAPI(
                    AuthDBManager.cfgMgr.getString("script.name"),
                    AuthDBManager.cfgMgr.getString("script.version"),
                    AuthDBManager.dataManager);
        } catch (UnsupportedScript u) {
            final Thread t = Thread.currentThread();
            LoggingHandler.stackTrace(u, t);
        } catch (UnsupportedVersion u) {
            final Thread t = Thread.currentThread();
            LoggingHandler.stackTrace(u, t);
        }
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
