/*
 * This file is part of Odin.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.craftfire.bifrost.Bifrost;
import com.craftfire.bifrost.ScriptAPI;
import com.craftfire.bifrost.classes.general.ScriptHandle;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.analytics.AnalyticsManager;
import com.craftfire.commons.database.DataManager;
import com.craftfire.commons.database.DataType;
import com.craftfire.odin.managers.commands.CommandManager;
import com.craftfire.odin.managers.inventory.InventoryManager;
import com.craftfire.odin.managers.storage.StorageManager;
import com.craftfire.odin.util.MainUtils;

public class OdinManager {
    private static Bifrost bifrost;
    private static StorageManager storageManager;
    private static ConfigurationManager configurationManager;
    private static CommandManager commandManager;
    private static InventoryManager inventoryManager;
    private static MessageManager messageManager;
    private static LoggingHandler loggingHandler;

    private static String pluginVersion = "NULL";

    private OdinManager() {}

    public static boolean init(File directory, String version) {
        pluginVersion = version;
        messageManager = new MessageManager();
        commandManager = new CommandManager();
        configurationManager = new ConfigurationManager();
        inventoryManager = new InventoryManager();
        loadConfiguration(directory);
        submitStats();
        if (OdinManager.getConfig().getString("database.username").equalsIgnoreCase("odin") && OdinManager.getConfig().getString("database.username").equalsIgnoreCase("odin")) {
            OdinManager.getLogger().error("The username and password in basic.yml is default, please change these settings to use Odin.");
            return false;
        } else if (OdinManager.loadDatabases(directory)) {
            if (OdinManager.getDataManager().hasConnection()) {
                // TODO: Custom database
                try {
                    int userCount = OdinManager.getScript().getUserCount();
                    if (userCount > 0) {
                        OdinManager.getLogger().info(OdinManager.getScript().getUserCount() + " user registrations in database.");
                    } else {
                        OdinManager.getLogger().error("Odin " + getPluginVersion() + " could not be enabled"
                                                        + "due to an issue with the configuration: SQL query failed '"
                                                        + OdinManager.getScript().getDataManager().getLastQuery() + "'");
                        return false;
                    }
                } catch (ScriptException e) {
                    OdinManager.getLogger().error("Could not get the amount of registrations from the database, disabling Odin.");
                    OdinManager.getLogger().stackTrace(e);
                    return false;
                }
                OdinManager.getLogger().debug("Debug has been enabled, prepare for loads of information.");
                OdinManager.getLogger().info("Odin " + getPluginVersion() + " enabled.");
                OdinManager.getLogger().info("Developed by CraftFire <dev@craftfire.com>");
                return true;
            } else {
                OdinManager.getLogger().error("Odin " + getPluginVersion() + " could not be enabled due to an issue with the MySQL connection.");
                return false;
            }
        } else {
            OdinManager.getLogger().error("Failed loading Odin databases, check log for more information.");
            return false;
        }
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

    public static LoggingHandler getLogger() {
        if (loggingHandler == null) {
            loggingHandler = new LoggingHandler("Minecraft.Odin", "[Odin]");
        }
        return loggingHandler;
    }

    public static void clear() {
        getInventories().clear();
    }

    public static void disable() {
        if (getStorage() != null && getStorage().getDataManager() != null) {
            getStorage().getDataManager().close(true);
        }
        if (getBifrost() != null && getScript().getDataManager() != null) {
            getScript().getDataManager().close(true);
        }
        clear();
    }

    public static boolean loadDatabases(File directory) {
        DataManager scriptDataManager = new DataManager(DataType.MYSQL,
                                                        getConfig().getString("database.username"),
                                                        getConfig().getString("database.password"));
        scriptDataManager.getLogger().setDebug(getConfig().getBoolean("plugin.debugmode"));
        scriptDataManager.setHost(getConfig().getString("database.host"));
        scriptDataManager.setPort(getConfig().getInt("database.port"));
        scriptDataManager.setDatabase(getConfig().getString("database.name"));
        scriptDataManager.setPrefix(getConfig().getString("script.tableprefix"));
        scriptDataManager.setTimeout(getConfig().getInt("database.timeout"));
        scriptDataManager.setKeepAlive(getConfig().getBoolean("database.keepalive"));
        DataManager storageDataManager = new DataManager(DataType.H2,
                                                         "odin",
                                                         "odin");
        storageDataManager.setLoggingManager(getLogger());
        // TODO: storageDataManager.getLogging().setPrefix("[Odin] [Storage]");
        storageDataManager.setKeepAlive(true);
        storageDataManager.setDirectory(directory + File.separator + "data" + File.separator);
        storageDataManager.setDatabase("OdinStorage");

        getLogger().debug("Storage data manager has been loaded.");
        try {
            bifrost = new Bifrost();
            getBifrost().getLogger().setDebug(getConfig().getBoolean("plugin.debugmode"));
            getScriptAPI().addHandle(Scripts.stringToScript(getConfig().getString("script.name")),
                                                            getConfig().getString("script.version"),
                                                            scriptDataManager);
            getLogger().debug("Bifrost has been loaded.");
        } catch (ScriptException e) {
            getLogger().stackTrace(e);
            return false;
        }
        storageManager = new StorageManager(storageDataManager);
        return true;
    }

    private static void loadConfiguration(File directory) {
        try {
            loggingHandler = new LoggingHandler("Minecraft.Odin", "[Odin]");
            /*
            Debugging
            getConfig().setLoggingHandler(getLogger());
            getCommands().setLoggingHandler(getLogger());
            getMessages().setLoggingHandler(getLogger());
            */
            MainUtils util = new MainUtils();
            util.defaultFile(directory.toString() + File.separator + "config", "config", "basic.yml");
            util.defaultFile(directory.toString() + File.separator + "config", "config", "advanced.yml");

            getConfig().getConfig().load(new File(directory + File.separator + "config" + File.separator + "basic.yml"));
            getConfig().getDefaults().load("files/config/basic.yml");
            getConfig().getConfig().load(new File(directory + File.separator + "config" + File.separator + "advanced.yml"));
            getConfig().getDefaults().load("files/config/advanced.yml");

            getLogger().setDirectory(directory + File.separator + "logs" + File.separator);
            getLogger().setFormat(getConfig().getString("plugin.logformat"));
            getLogger().setDebug(getConfig().getBoolean("plugin.debugmode"));
            getLogger().setLogging(getConfig().getBoolean("plugin.logging"));

            util.loadLanguage(directory.toString() + File.separator + "translations" + File.separator, "commands");
            util.loadLanguage(directory.toString() + File.separator + "translations" + File.separator, "messages");
        } catch (IOException e) {
            getLogger().stackTrace(e);
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
            // TODO: Set H2 version dynamically from pom version
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
        AnalyticsManager analyticsManager = new AnalyticsManager("http://stats.craftfire.com",
                                                                 OdinManager.getPluginName(),
                                                                 OdinManager.getPluginVersion());
        analyticsManager.setLoggingManager(OdinManager.getLogger());
        // TODO: analyticsManager.submitVoid();
        //getLogging().info(analyticsManager.getParameters());
    }
}
