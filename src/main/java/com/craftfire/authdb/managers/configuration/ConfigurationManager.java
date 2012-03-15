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
package com.craftfire.authdb.managers.configuration;

import java.util.HashMap;

public class ConfigurationManager {
    private HashMap<String, Object> config = new HashMap<String, Object>();

    public boolean getBoolean(ConfigurationNode config) {
        if (exist(config) && config.data instanceof Boolean) {
            return (Boolean) this.config.get(config.node.toLowerCase());
        }
        return false;
    }

    public String getString(ConfigurationNode config) {
        if (exist(config) && config.data instanceof String) {
            return (String) this.config.get(config.node.toLowerCase());
        }
        return null;
    }

    public int getInteger(ConfigurationNode config) {
        if (exist(config) && config.data instanceof Integer) {
            return (Integer) this.config.get(config.node.toLowerCase());
        }
        return 0;
    }
    
    public void load(HashMap<String, Object> config) {
        this.config = config;
    }
    
    private boolean exist(ConfigurationNode config) {
        if (this.config.containsKey(config.node.toLowerCase())) {
            return true;
        }
        return false;
    }
}
