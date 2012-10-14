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
import com.craftfire.odin.managers.permissions.PermissionsManager;
import com.craftfire.odin.util.MainUtils;
import com.craftfire.commons.CraftCommons;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class OdinManager {
    private static OdinManager instance;
    private Bifrost bifrost;
    private StorageManager storageManager;
    private ConfigurationManager configurationManager;
    private CommandManager commandManager;
    private InventoryManager inventoryManager;
    private PermissionsManager permissionsManager;
    private MessageManager messageManager;
    private LoggingManager loggingManager;

    public static String pluginName, pluginVersion;

    private Map<String, Long> userSessions = new HashMap<String, Long>();
    private HashSet<String> userAuthenticated = new HashSet<String>();
    private HashSet<String> userTimeouts = new HashSet<String>();
    private Map<String, String> userLinkedNames = new HashMap<String, String>();
    private Map<String, OdinUser> userStorage = new HashMap<String, OdinUser>();
    private Map<String, Integer> userPasswordAttempts = new HashMap<String, Integer>();

    private Map<String, String> playerInventory = new HashMap<String, String>();
    private Map<String, String> playerArmor = new HashMap<String, String>();
    private Map<String, Long> playerJoin = new HashMap<String, Long>();

    public OdinManager(File directory) {
        instance = this;
        this.configurationManager = new ConfigurationManager();
        this.permissionsManager = new PermissionsManager();
        this.commandManager = new CommandManager();
        this.messageManager = new MessageManager();
        loadConfiguration(directory);
        loadAuthAPI(directory);
    }

    public static OdinManager getInstance() {
        return instance;
    }

    public Bifrost getBifrost() {
        return this.bifrost;
    }

    public ScriptAPI getScriptAPI() {
        return this.bifrost.getScriptAPI();
    }

    public StorageManager getStorage() {
        return this.storageManager;
    }

    public ConfigurationManager getConfig() {
        return this.configurationManager;
    }

    public CommandManager getCommand() {
        return this.commandManager;
    }

    public InventoryManager getInventory() {
        return this.inventoryManager;
    }

    public PermissionsManager getPermissions() {
        return this.permissionsManager;
    }

    public MessageManager getMessage() {
        return this.messageManager;
    }

    public LoggingManager getLogging() {
        return this.loggingManager;
    }

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
            this.bifrost = new Bifrost();
            getScriptAPI().addHandle(Scripts.stringToScript(getConfig().getString("script.name")),
                                                            getConfig().getString("script.version"),
                                                            scriptDataManager);
            getLogging().debug("Bifrost has been loaded.");
        } catch (UnsupportedScript e) {
            LoggingHandler.stackTrace(e);
        } catch (UnsupportedVersion e) {
            LoggingHandler.stackTrace(e);
        }
        this.storageManager = new StorageManager(storageDataManager);
    }

    protected void loadConfiguration(File directory) {
        try {
            getConfig().load(new YamlManager(new File(directory + "/config/basic.yml")),
                                    new YamlManager("files/config/basic.yml"));
            getConfig().load(new YamlManager(new File(directory + "/config/advanced.yml")),
                                    new YamlManager("files/config/advanced.yml"));
            this.loggingManager = new LoggingManager("Minecraft.Odin", "[Odin]");
            this.loggingManager.setDirectory(directory + "/logs/");
            this.loggingManager.setFormat(getConfig().getString("plugin.logformat"));
            this.loggingManager.setDebug(getConfig().getBoolean("plugin.debugmode"));
            this.loggingManager.setLogging(getConfig().getBoolean("plugin.logging"));
            MainUtils util = new MainUtils();
            util.loadLanguage(directory.toString() + "\\translations\\", "commands");
            util.loadLanguage(directory.toString() + "\\translations\\", "messages");
        } catch (IOException e) {
            /* TODO */
            e.printStackTrace();
        }
    }
}
