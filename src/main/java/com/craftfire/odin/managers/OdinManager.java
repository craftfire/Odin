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

import com.craftfire.bifrost.Bifrost;
import com.craftfire.bifrost.ScriptAPI;
import com.craftfire.bifrost.classes.general.ScriptHandle;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.YamlManager;
import com.craftfire.commons.analytics.AnalyticsManager;
import com.craftfire.commons.database.DataManager;
import com.craftfire.commons.database.DataType;
import com.craftfire.commons.util.LoggingManager;
import com.craftfire.odin.managers.inventory.InventoryManager;
import com.craftfire.odin.util.MainUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OdinManager {
    private static Bifrost bifrost;
    private static StorageManager storageManager;
    private static ConfigurationManager configurationManager;
    private static CommandManager commandManager;
    private static InventoryManager inventoryManager;
    private static MessageManager messageManager;
    private static LoggingHandler loggingHandler;

    private static String pluginVersion = "NULL";

    private static Map<String, Long> userSessions = new HashMap<String, Long>();
    private static Set<String> userAuthenticated = new HashSet<String>();
    private static Set<String> userTimeouts = new HashSet<String>();
    private static Map<String, String> userLinkedNames = new HashMap<String, String>();
    private static Map<String, OdinUser> userStorage = new HashMap<String, OdinUser>();
    private static Map<String, Integer> userPasswordAttempts = new HashMap<String, Integer>();
    private static Map<String, Long> playerJoin = new HashMap<String, Long>();

    private OdinManager() {}

    public static void init(File directory, String version) {
        pluginVersion = version;
        messageManager = new MessageManager();
        commandManager = new CommandManager();
        configurationManager = new ConfigurationManager();
        inventoryManager = new InventoryManager();
        loadConfiguration(directory);
        submitStats();
    }

    public static String getPluginName() {
        return "Odin";
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

    public static Set<String> getAuthenticatedUsers() {
        return userAuthenticated;
    }

    public static Set<String> getUserTimeouts() {
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

    public static void clean() {
        userSessions.clear();
        userAuthenticated.clear();
        userStorage.clear();
        userTimeouts.clear();
        userPasswordAttempts.clear();
        getInventories().clear();
        playerJoin.clear();
    }

    public static boolean loadDatabases(File directory) {
        DataManager scriptDataManager = new DataManager(DataType.MYSQL,
                                                        getConfig().getString("database.username"),
                                                        getConfig().getString("database.password"));
        scriptDataManager.setHost(getConfig().getString("database.host"));
        scriptDataManager.setPort(getConfig().getInt("database.port"));
        scriptDataManager.setDatabase(getConfig().getString("database.name"));
        scriptDataManager.setPrefix(getConfig().getString("script.tableprefix"));
        scriptDataManager.setTimeout(getConfig().getInt("database.timeout"));
        scriptDataManager.setKeepAlive(getConfig().getBoolean("database.keepalive"));
        scriptDataManager.getLogging().setDebug(getConfig().getBoolean("plugin.debugmode"));
        DataManager storageDataManager = new DataManager(DataType.H2,
                                                         getConfig().getString("database.username"),
                                                         getConfig().getString("database.password"));
        storageDataManager.setLoggingManager(getLogging());
        storageDataManager.setKeepAlive(true);
        storageDataManager.setDirectory(directory + File.separator + "data" + File.separator);
        storageDataManager.setDatabase("OdinStorage");

        getLogging().debug("Storage data manager has been loaded.");
        try {
            bifrost = new Bifrost();
            getScriptAPI().addHandle(Scripts.stringToScript(getConfig().getString("script.name")),
                                                            getConfig().getString("script.version"),
                                                            scriptDataManager);
            getLogging().debug("Bifrost has been loaded.");
        } catch (ScriptException e) {
            getLogging().stackTrace(e);
            return false;
        }
        storageManager = new StorageManager(storageDataManager);
        return true;
    }

    private static void loadConfiguration(File directory) {
        try {
            loggingHandler = new LoggingHandler("Minecraft.Odin", "[Odin]");
            MainUtils util = new MainUtils();
            util.defaultFile(directory.toString() + File.separator + "config", "config", "basic.yml");
            util.defaultFile(directory.toString() + File.separator + "config", "config", "advanced.yml");
            getConfig().load(new YamlManager(new File(directory + File.separator + "config" + File.separator + "basic.yml")),
                    new YamlManager("files" + File.separator + "config" + File.separator + "basic.yml"));
            getConfig().load(new YamlManager(new File(directory + File.separator + "config" + File.separator + "advanced.yml")),
                             new YamlManager("files" + File.separator + "config" + File.separator + "advanced.yml"));
            loggingHandler.setDirectory(directory + File.separator + "logs" + File.separator);
            loggingHandler.setFormat(getConfig().getString("plugin.logformat"));
            loggingHandler.setDebug(getConfig().getBoolean("plugin.debugmode"));
            loggingHandler.setLogging(getConfig().getBoolean("plugin.logging"));
            util.loadLanguage(directory.toString() + File.separator + "translations" + File.separator, "commands");
            util.loadLanguage(directory.toString() + File.separator + "translations" + File.separator, "messages");
        } catch (IOException e) {
            loggingHandler.stackTrace(e);
        }
    }

    public static boolean loadLibraries(File directory) {
        File outputDirectory = new File(directory.toString() + File.separator + "lib");
        if (!outputDirectory.exists() && !outputDirectory.mkdir()) {
            System.out.println("Could not create " + outputDirectory.toString());
        }
        File h2Driver = new File(outputDirectory.toString() + File.separator + "h2.jar");
        if (!CraftCommons.hasClass("org.h2.Driver") && !h2Driver.exists()) {
            System.out.println("Could not find required H2 driver.");
            System.out.println("Starting download for the H2 driver. Please wait...");
            Set<String> urls = new HashSet<String>();
            urls.add("http://hsql.sourceforge.net/m2-repo/com/h2database/h2/1.3.169/h2-1.3.169.jar");
            urls.add("http://repo2.maven.org/maven2/com/h2database/h2/1.3.169/h2-1.3.169.jar");
            if (!MainUtils.downloadLibrary(h2Driver, urls)) {
                System.out.println("Could not download H2 driver, see log for more information.");
                return false;
            }
        }
        return true;
    }

    protected static void submitStats() {
        try {
            AnalyticsManager analyticsManager = new AnalyticsManager("http://stats.craftfire.com",
                                                                     OdinManager.getPluginName(),
                                                                     OdinManager.getPluginVersion());
            analyticsManager.setLoggingManager(OdinManager.getLogging());
            getLogging().info(analyticsManager.getParameters());
        } catch (MalformedURLException ignore) {}
    }
}