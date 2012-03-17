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
    private HashMap<String, Object> defaults = new HashMap<String, Object>();

    public boolean getBoolean(ConfigurationNode config) {
        String configNode = config.node.toLowerCase();
        if (exist(config) && this.config.get(configNode) instanceof Boolean) {
            return (Boolean) this.config.get(configNode);
        } else if (existDefault(config) && this.defaults.get(configNode) instanceof Boolean) {
            return (Boolean) this.defaults.get(configNode);
        }
        return false;
    }

    public String getString(ConfigurationNode config) {
        String configNode = config.node.toLowerCase();
        if (exist(config) && this.config.get(configNode) instanceof String) {
            return (String) this.config.get(configNode);
        } else if (existDefault(config) && this.defaults.get(configNode) instanceof Boolean) {
            return (String) this.defaults.get(configNode);
        }
        return null;
    }

    public int getInteger(ConfigurationNode config) {
        String configNode = config.node.toLowerCase();
        if (exist(config) && this.config.get(configNode) instanceof Integer) {
            return (Integer) this.config.get(configNode);
        } else if (existDefault(config) && this.defaults.get(configNode) instanceof Boolean) {
            return (Integer) this.defaults.get(configNode);
        }
        return 0;
    }

    public Long getLong(ConfigurationNode config) {
        String configNode = config.node.toLowerCase();
        if (exist(config) && this.config.get(configNode) instanceof Integer) {
            return (Long) this.config.get(configNode);
        } else if (existDefault(config) && this.defaults.get(configNode) instanceof Boolean) {
            return (Long) this.defaults.get(configNode);
        }
        return null;
    }
    
    public void load(HashMap<String, Object> config, HashMap<String, Object> defaults) {
        this.config = config;
        this.defaults = defaults;
    }
    
    private boolean exist(ConfigurationNode conf) {
        if (this.config.containsKey(conf.node.toLowerCase())) {
            return true;
        }
        return false;
    }

    private boolean existDefault(ConfigurationNode conf) {
        if (this.defaults.containsKey(conf.node.toLowerCase())) {
            return true;
        }
        return false;
    }
}
