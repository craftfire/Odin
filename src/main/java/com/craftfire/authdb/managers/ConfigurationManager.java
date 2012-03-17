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

import java.util.HashMap;

public class ConfigurationManager {
    private HashMap<String, Object> config = new HashMap<String, Object>();
    private HashMap<String, Object> defaults = new HashMap<String, Object>();

    public boolean getBoolean(String node) {
        node = node.toLowerCase();
        if (exist(node) && this.config.get(node) instanceof Boolean) {
            return (Boolean) this.config.get(node);
        } else if (existDefault(node) && this.defaults.get(node) instanceof Boolean) {
            return (Boolean) this.defaults.get(node);
        }
        return false;
    }

    public String getString(String node) {
        node = node.toLowerCase();
        if (exist(node) && this.config.get(node) instanceof String) {
            return (String) this.config.get(node);
        } else if (existDefault(node) && this.defaults.get(node) instanceof String) {
            return (String) this.defaults.get(node);
        }
        return null;
    }

    public int getInteger(String node) {
        node = node.toLowerCase();
        if (exist(node) && this.config.get(node) instanceof Integer) {
            return (Integer) this.config.get(node);
        } else if (existDefault(node) && this.defaults.get(node) instanceof Integer) {
            return (Integer) this.defaults.get(node);
        }
        return 0;
    }

    public Long getLong(String node) {
        node = node.toLowerCase();
        if (exist(node) && this.config.get(node) instanceof Long) {
            return (Long) this.config.get(node);
        } else if (existDefault(node) && this.defaults.get(node) instanceof Long) {
            return (Long) this.defaults.get(node);
        }
        return null;
    }
    
    public void load(HashMap<String, Object> config, HashMap<String, Object> defaults) {
        this.config.putAll(config);
        this.defaults.putAll(defaults);
    }
    
    private boolean exist(String node) {
        return this.config.containsKey(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return this.defaults.containsKey(node.toLowerCase());
    }
}
