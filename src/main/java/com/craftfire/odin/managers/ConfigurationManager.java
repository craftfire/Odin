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

import com.craftfire.commons.YamlManager;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {
    private YamlManager config = null;
    private YamlManager defaults = null;

    public boolean isInitialized() {
        return this.config != null && this.defaults != null;
    }

    public boolean getBoolean(String node) {
        if (exist(node)) {
            return this.config.getBoolean(node);
        } else if (existDefault(node)) {
            return this.defaults.getBoolean(node);
        }
        return false;
    }

    public String getString(String node) {
        if (exist(node)) {
            return this.config.getString(node);
        } else if (existDefault(node)) {
            return this.defaults.getString(node);
        }
        return null;
    }

    public int getInt(String node) {
        if (exist(node)) {
            return this.config.getInt(node);
        } else if (existDefault(node)) {
            return this.defaults.getInt(node);
        }
        return 0;
    }

    public Long getLong(String node) {
        if (exist(node)) {
            return this.config.getLong(node);
        } else if (existDefault(node)) {
            return this.defaults.getLong(node);
        }
        return null;
    }
    
    public Map<String, Object> getNodes() {
        return this.config.getNodes();
    }
    
    public Map<String, Object> getDefaults() {
        return this.defaults.getNodes();
    }
    
    public void load(YamlManager config, YamlManager defaults) {
        if (this.config == null) {
            this.config = config;
        } else {
            this.config.addNodes(config);
        }
        if (this.defaults == null) {
            this.defaults = defaults;
        } else {
            this.defaults.addNodes(defaults);
        }
    }
    
    private boolean exist(String node) {
        return node != null && this.config.exist(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return node != null && this.defaults.exist(node.toLowerCase());
    }
}
