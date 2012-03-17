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
import com.craftfire.authapi.ScriptAPI;
import com.craftfire.authdb.managers.permissions.PermissionsManager;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.DataManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class AuthDBManager {
    public static AuthAPI authAPI;
    public static DataManager dataManager;
    public static ConfigurationManager cfgMngr;
    public static InventoryManager invMngr;
    public static PermissionsManager prmMngr;
    public static CraftCommons craftCommons;
    
    public static HashSet<String> userSessions = new HashSet<String>();
    public static HashSet<String> userAuthenticated = new HashSet<String>();
    public static HashMap<String, AuthDBUser> userStorage = new HashMap<String, AuthDBUser>();
    public static HashMap<String, Integer> userPasswordAttempts = new HashMap<String, Integer>();

    public static HashMap<String, String> playerInventory = new HashMap<String, String>();
    public static HashMap<String, String> playerArmor = new HashMap<String, String>();
    public static HashMap<String, Long> playerJoin = new HashMap<String, Long>();

    public void clean() {
        userSessions.clear();
        userAuthenticated.clear();
        userStorage.clear();
        playerInventory.clear();
        playerArmor.clear();
    }

    public void load(File directory) {
        setClasses();
        loadConfiguration(directory);
        loadAuthAPI();
    }

    protected void setClasses() {
        AuthDBManager.cfgMngr = new ConfigurationManager();
        AuthDBManager.prmMngr = new PermissionsManager();
        AuthDBManager.craftCommons = new CraftCommons();
    }

    protected void loadAuthAPI() {
        AuthDBManager.dataManager = new DataManager(
                AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.database_keepalive),
                AuthDBManager.cfgMngr.getInteger(ConfigurationNode.database_timeout),
                AuthDBManager.cfgMngr.getString(ConfigurationNode.database_host),
                AuthDBManager.cfgMngr.getInteger(ConfigurationNode.database_port),
                AuthDBManager.cfgMngr.getString(ConfigurationNode.database_name),
                AuthDBManager.cfgMngr.getString(ConfigurationNode.database_username),
                AuthDBManager.cfgMngr.getString(ConfigurationNode.database_password),
                AuthDBManager.cfgMngr.getString(ConfigurationNode.script_tableprefix));
        AuthDBManager.authAPI = new AuthAPI(
                ScriptAPI.Scripts.XF,
                AuthDBManager.cfgMngr.getString(ConfigurationNode.script_version),
                AuthDBManager.dataManager);
    }

    protected void loadConfiguration(File directory) {
        try {
            AuthDBManager.cfgMngr.load(CraftCommons.loadYaml(new File(directory + "/config/basic.yml")),
                    AuthDBManager.craftCommons.loadLocalYaml("/files/config/basic.yml"));
            AuthDBManager.cfgMngr.load(CraftCommons.loadYaml(new File(directory + "/config/advanced.yml")),
                    AuthDBManager.craftCommons.loadLocalYaml("/files/config/advanced.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
