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
    public static Bifrost bifrost;
    public static ScriptAPI scriptAPI;
    public static ScriptHandle scriptHandle;
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
    
    public static Map<String, Long> userSessions = new HashMap<String, Long>();
    public static HashSet<String> userAuthenticated = new HashSet<String>();
    public static HashSet<String> userTimeouts = new HashSet<String>();
    public static Map<String, String> userLinkedNames = new HashMap<String, String>();
    public static Map<String, OdinUser> userStorage = new HashMap<String, OdinUser>();
    public static Map<String, Integer> userPasswordAttempts = new HashMap<String, Integer>();

    public static Map<String, String> playerInventory = new HashMap<String, String>();
    public static Map<String, String> playerArmor = new HashMap<String, String>();
    public static Map<String, Long> playerJoin = new HashMap<String, Long>();

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
        OdinManager.cfgMgr = new ConfigurationManager();
        OdinManager.prmMgr = new PermissionsManager();
        OdinManager.cmdMgr = new CommandManager();
        OdinManager.msgMgr = new MessageManager();
        OdinManager.craftCommons = new CraftCommons();
    }

    protected void loadAuthAPI(File directory) {
        OdinManager.scriptDataManager = new DataManager(DataType.MYSQL,
                                                          OdinManager.cfgMgr.getString("database.username"),
                                                          OdinManager.cfgMgr.getString("database.password"));
        OdinManager.scriptDataManager.setHost(OdinManager.cfgMgr.getString("database.host"));
        OdinManager.scriptDataManager.setPort(OdinManager.cfgMgr.getInt("database.port"));
        OdinManager.scriptDataManager.setDatabase(OdinManager.cfgMgr.getString("database.name"));
        OdinManager.scriptDataManager.setPrefix(OdinManager.cfgMgr.getString("script.tableprefix"));
        OdinManager.scriptDataManager.setTimeout(OdinManager.cfgMgr.getInt("database.timeout"));
        OdinManager.scriptDataManager.setKeepAlive(OdinManager.cfgMgr.getBoolean("database.keepalive"));
        OdinManager.storageDataManager = new DataManager(DataType.H2,
                                                          OdinManager.cfgMgr.getString("database.username"),
                                                          OdinManager.cfgMgr.getString("database.password"));
        OdinManager.storageDataManager.setDirectory(directory + "/data/Odin");
        OdinManager.logMgr.debug("Storage data manager has been loaded.");
        try {
            OdinManager.bifrost = new Bifrost();
            OdinManager.scriptAPI = OdinManager.bifrost.getScriptAPI();
            OdinManager.scriptAPI.addHandle(Scripts.stringToScript(OdinManager.cfgMgr.getString("script.name")),
                                            OdinManager.cfgMgr.getString("script.version"),
                                            OdinManager.scriptDataManager);
            OdinManager.scriptHandle = OdinManager.scriptAPI.getHandle();
            OdinManager.logMgr.debug("Bifrost has been loaded.");
        } catch (UnsupportedScript e) {
            LoggingHandler.stackTrace(e);
        } catch (UnsupportedVersion e) {
            LoggingHandler.stackTrace(e);
        }
        OdinManager.storageManager = new StorageManager();
    }

    protected void loadConfiguration(File directory) {
        try {
            OdinManager.cfgMgr.load(new YamlManager(new File(directory + "/config/basic.yml")),
                                    new YamlManager("files/config/basic.yml"));
            OdinManager.cfgMgr.load(new YamlManager(new File(directory + "/config/advanced.yml")),
                                    new YamlManager("files/config/advanced.yml"));
            OdinManager.logMgr = new LoggingManager("Minecraft.Odin", "[Odin]");
            OdinManager.logMgr.setDirectory(directory + "/logs/");
            OdinManager.logMgr.setFormat(OdinManager.cfgMgr.getString("plugin.logformat"));
            OdinManager.logMgr.setDebug(OdinManager.cfgMgr.getBoolean("plugin.debugmode"));
            OdinManager.logMgr.setLogging(OdinManager.cfgMgr.getBoolean("plugin.logging"));
            MainUtils util = new MainUtils();
            util.loadLanguage(directory.toString() + "\\translations\\", "commands");
            util.loadLanguage(directory.toString() + "\\translations\\", "messages");
        } catch (IOException e) {
            /* TODO */
            e.printStackTrace();
        }
    }
}
