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

import com.craftfire.bifrost.Bifrost;
import com.craftfire.bifrost.ScriptAPI;
import com.craftfire.bifrost.classes.general.ScriptHandle;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.UnsupportedScript;
import com.craftfire.bifrost.exceptions.UnsupportedVersion;
import com.craftfire.commons.enums.DataType;
import com.craftfire.commons.managers.DataManager;
import com.craftfire.commons.managers.LoggingManager;
import com.craftfire.commons.managers.YamlManager;
import com.craftfire.odin.util.MainUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class OdinManager {
    private static Bifrost bifrost;
    private static StorageManager storageManager;
    private static ConfigurationManager configurationManager;
    private static CommandManager commandManager;
    private static InventoryManager inventoryManager;
    private static MessageManager messageManager;
    private static LoggingHandler loggingHandler;

    private static String pluginName, pluginVersion;

    private static Map<String, Long> userSessions = new HashMap<String, Long>();
    private static HashSet<String> userAuthenticated = new HashSet<String>();
    private static HashSet<String> userTimeouts = new HashSet<String>();
    private static Map<String, String> userLinkedNames = new HashMap<String, String>();
    private static Map<String, OdinUser> userStorage = new HashMap<String, OdinUser>();
    private static Map<String, Integer> userPasswordAttempts = new HashMap<String, Integer>();

    private static Map<String, Long> playerJoin = new HashMap<String, Long>();

    public OdinManager(File directory) {
        configurationManager = new ConfigurationManager();
        commandManager = new CommandManager();
        inventoryManager = new InventoryManager();
        messageManager = new MessageManager();
        loadConfiguration(directory);
        loadAuthAPI(directory);
    }

    public static String getPluginName() {
        return pluginName;
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }

    public static Bifrost getBifrost() {
        return bifrost;
    }

    public static ScriptAPI getScriptAPI() {
        return getBifrost().getScriptAPI();
    }

    public static ScriptHandle getScript() {
        return getScriptAPI().getHandle();
    }

    public static DataManager getDataManager() {
        return getScript().getDataManager();
    }

    public static StorageManager getStorage() {
        return storageManager;
    }

    public static ConfigurationManager getConfig() {
        return configurationManager;
    }

    public static CommandManager getCommands() {
        return commandManager;
    }

    public static InventoryManager getInventories() {
        return inventoryManager;
    }

    public static MessageManager getMessages() {
        return messageManager;
    }

    public static LoggingManager getLogging() {
        return loggingHandler;
    }

    public static Map<String, Long> getUserSessions() {
        return userSessions;
    }

    public static HashSet<String> getAuthenticatedUsers() {
        return userAuthenticated;
    }

    public static HashSet<String> getUserTimeouts() {
        return userTimeouts;
    }

    public static Map<String, String> getLinkedUsernames() {
        return userLinkedNames;
    }

    public static Map<String, OdinUser> getUserStorage() {
        return userStorage;
    }

    public static Map<String, Integer> getUserPasswordAttempts() {
        return userPasswordAttempts;
    }

    public static Map<String, Long> getPlayerJoins() {
        return playerJoin;
    }

    public void clean() {
        userSessions.clear();
        userAuthenticated.clear();
        userStorage.clear();
        userTimeouts.clear();
        userPasswordAttempts.clear();
        getInventories().clear();
        playerJoin.clear();
    }

    protected void loadAuthAPI(File directory) {
        DataManager scriptDataManager = new DataManager(DataType.MYSQL,
                                                        getConfig().getString("database.username"),
                                                        getConfig().getString("database.password"));
        scriptDataManager.setHost(getConfig().getString("database.host"));
        scriptDataManager.setPort(getConfig().getInt("database.port"));
        scriptDataManager.setDatabase(getConfig().getString("database.name"));
        scriptDataManager.setPrefix(getConfig().getString("script.tableprefix"));
        scriptDataManager.setTimeout(getConfig().getInt("database.timeout"));
        scriptDataManager.setKeepAlive(getConfig().getBoolean("database.keepalive"));
        DataManager storageDataManager = new DataManager(DataType.H2,
                                                         getConfig().getString("database.username"),
                                                         getConfig().getString("database.password"));
        storageDataManager.setDirectory(directory + "/data/Odin");
        getLogging().debug("Storage data manager has been loaded.");
        try {
            bifrost = new Bifrost();
            getScriptAPI().addHandle(Scripts.stringToScript(getConfig().getString("script.name")),
                                                            getConfig().getString("script.version"),
                                                            scriptDataManager);
            getLogging().debug("Bifrost has been loaded.");
        } catch (UnsupportedScript e) {
            getLogging().stackTrace(e);
        } catch (UnsupportedVersion e) {
            getLogging().stackTrace(e);
        }
        storageManager = new StorageManager(storageDataManager);
    }

    protected void loadConfiguration(File directory) {
        try {
            loggingHandler = new LoggingHandler("Minecraft.Odin", "[Odin]");
            getConfig().load(new YamlManager(new File(directory + "/config/basic.yml")),
                                    new YamlManager("files/config/basic.yml"));
            getConfig().load(new YamlManager(new File(directory + "/config/advanced.yml")),
                                    new YamlManager("files/config/advanced.yml"));
            loggingHandler.setDirectory(directory + "/logs/");
            loggingHandler.setFormat(getConfig().getString("plugin.logformat"));
            loggingHandler.setDebug(getConfig().getBoolean("plugin.debugmode"));
            loggingHandler.setLogging(getConfig().getBoolean("plugin.logging"));
            MainUtils util = new MainUtils();
            util.loadLanguage(directory.toString() + "\\translations\\", "commands");
            util.loadLanguage(directory.toString() + "\\translations\\", "messages");
        } catch (IOException e) {
            loggingHandler.stackTrace(e);
        }
    }
}
