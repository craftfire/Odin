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
import com.craftfire.authdb.managers.configuration.ConfigurationManager;
import com.craftfire.commons.DataManager;

import java.util.HashMap;
import java.util.HashSet;

public class AuthDBManager {
    public static AuthAPI authAPI;
    public static DataManager dataManager;
    public static ConfigurationManager cfgMngr;
    public static InventoryManager invMngr;
    
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
}
