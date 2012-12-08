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

import com.craftfire.commons.yaml.YamlManager;

import java.util.Map;

public class ConfigurationManager {
    private YamlManager config = new YamlManager();
    private YamlManager defaults = new YamlManager();

    public boolean isInitialized() {
        return (!config.getNodes().isEmpty() && !defaults.getNodes().isEmpty());
    }

    public void setLoggingHandler(LoggingHandler loggingHandler) {
        getConfig().setLoggingManager(loggingHandler);
        getDefaults().setLoggingManager(loggingHandler);
    }

    public boolean getBoolean(String node) {
        OdinManager.getLogger().debug("Getting Boolean from config node: '" + node + "'.");
        if (exist(node)) {
            boolean value = this.config.getBoolean(node);
            OdinManager.getLogger().debug("Found Boolean config value for node '" + node + "' = '" + value + "'.");
            return value;
        } else if (existDefault(node)) {
            boolean value = this.defaults.getBoolean(node);
            OdinManager.getLogger().debug("Could not find a custom config node '" + node + "', using default Boolean instead = '" + value + "'.");
            return value;
        }
        OdinManager.getLogger().error("Could not find config node '" + node + "', returning false.");
        OdinManager.getLogger().debug("Custom config size: " + this.config.getNodes().size());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getNodes().size());
        return false;
    }

    public String getString(String node) {
        OdinManager.getLogger().debug("Getting String from config node: '" + node + "'.");
        if (exist(node)) {
            String value = this.config.getString(node);
            OdinManager.getLogger().debug("Found String config value for node '" + node + "' = '" + value + "'.");
            return value;
        } else if (existDefault(node)) {
            String value = this.defaults.getString(node);
            OdinManager.getLogger().debug("Could not find a custom config node '" + node + "', using default String instead = '" + value + "'.");
            return value;
        }
        OdinManager.getLogger().error("Could not find config node '" + node + "', returning null.");
        OdinManager.getLogger().debug("Custom config size: " + this.config.getNodes().size());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getNodes().size());
        return null;
    }

    public int getInt(String node) {
        OdinManager.getLogger().debug("Getting Integer from config node: '" + node + "'.");
        if (exist(node)) {
            int value = this.config.getInt(node);
            OdinManager.getLogger().debug("Found Integer config value for node '" + node + "' = '" + value + "'.");
            return value;
        } else if (existDefault(node)) {
            int value = this.defaults.getInt(node);
            OdinManager.getLogger().debug("Could not find a custom config node '" + node + "', using default Integer instead = '" + value + "'.");
            return value;
        }
        OdinManager.getLogger().error("Could not find config node '" + node + "', returning 0.");
        OdinManager.getLogger().debug("Custom config size: " + this.config.getNodes().size());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getNodes().size());
        return 0;
    }

    public Long getLong(String node) {
        OdinManager.getLogger().debug("Getting Long from config node: '" + node + "'.");
        if (exist(node)) {
            long value = this.config.getLong(node);
            OdinManager.getLogger().debug("Found Long config value for node '" + node + "' = '" + value + "'.");
            return value;
        } else if (existDefault(node)) {
            long value = this.defaults.getLong(node);
            OdinManager.getLogger().debug("Could not find a custom config node '" + node + "', using default Long instead = '" + value + "'.");
            return value;
        }
        OdinManager.getLogger().error("Could not find config node '" + node + "', returning null.");
        OdinManager.getLogger().debug("Custom config size: " + this.config.getNodes().size());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getNodes().size());
        return null;
    }
    
    public Map<String, Object> getNodes() {
        return this.config.getNodes();
    }
    
    public Map<String, Object> getDefaultNodes() {
        return this.defaults.getNodes();
    }

    public YamlManager getConfig() {
        return this.config;
    }

    public YamlManager getDefaults() {
        return this.defaults;
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
