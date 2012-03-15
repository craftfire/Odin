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
package com.craftfire.authdb.authdb.managers;

import com.craftfire.authapi.AuthAPI;
import com.craftfire.authdb.authdb.managers.configuration.ConfigurationManager;
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
    public static HashMap<String, AuthDBPlayerBase> userStorage = new HashMap<String, AuthDBPlayerBase>();
}
