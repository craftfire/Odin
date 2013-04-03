/*
 * This file is part of Odin.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.craftfire.commons.yaml.YamlCombiner;
import com.craftfire.commons.yaml.YamlManager;

public class ConfigurationManager {
    private YamlCombiner config = new YamlCombiner();
    private YamlCombiner defaults = new YamlCombiner();
    private final Set<String> ignoredNodes = new HashSet<String>(Arrays.asList(new String[] { "protection.freeze.enabled",
            "protection.freeze.delay",
            "guest.movement" }));

    public boolean isInitialized() {
        return (this.config.getFinalNodeCount() != 0 && this.defaults.getFinalNodeCount() != 0);
    }

    public void setLoggingHandler(LoggingHandler loggingHandler) {
        getConfig().setLoggingManager(loggingHandler);
        getDefaults().setLoggingManager(loggingHandler);
    }

    private void debug(String node, String string, boolean ignoreLogging) {
        if (!this.ignoredNodes.contains(node) && !node.startsWith("database.") && !ignoreLogging) {
            OdinManager.getLogger().debug(string);
        }
    }

    private void error(String node, String string, boolean ignoreLogging) {
        if (!this.ignoredNodes.contains(node) && !node.startsWith("database.") && !ignoreLogging) {
            OdinManager.getLogger().error(string);
        }
    }

    public boolean getBoolean(String node) {
        return getBoolean(node, false);
    }

    public boolean getBoolean(String node, boolean ignoreLogging) {
        if (!this.ignoredNodes.contains(node)) {
            debug(node, "Getting Boolean from config node: '" + node + "'.", ignoreLogging);
        }
        if (exist(node)) {
            boolean value = this.config.getBoolean(node);
            debug(node, "Found Boolean config value for node '" + node + "' = '" + value + "'.", ignoreLogging);
            return value;
        } else if (existDefault(node)) {
            boolean value = this.defaults.getBoolean(node);
            error(node, "Could not find a custom config node '" + node + "', using default Boolean instead = '" + value + "'.", ignoreLogging);
            return value;
        }
        OdinManager.getLogger().error("Could not find config node '" + node + "', returning false.");
        OdinManager.getLogger().debug("Custom config size: " + this.config.getFinalNodeCount());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getFinalNodeCount());
        return false;
    }

    public String getString(String node) {
        return getString(node, false);
    }

    public String getString(String node, boolean ignoreLogging) {
        debug(node, "Getting String from config node: '" + node + "'.", ignoreLogging);
        if (exist(node)) {
            String value = this.config.getString(node);
            debug(node, "Found String config value for node '" + node + "' = '" + value + "'.", ignoreLogging);
            return value;
        } else if (existDefault(node)) {
            String value = this.defaults.getString(node);
            error(node, "Could not find a custom config node '" + node + "', using default String instead = '" + value + "'.", ignoreLogging);
            return value;
        }
        OdinManager.getLogger().error("Could not find config node '" + node + "', returning null.");
        OdinManager.getLogger().debug("Custom config size: " + this.config.getFinalNodeCount());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getFinalNodeCount());
        return null;
    }

    public int getInt(String node) {
        return getInt(node, false);
    }

    public int getInt(String node, boolean ignoreLogging) {
        debug(node, "Getting Integer from config node: '" + node + "'.", ignoreLogging);
        if (exist(node)) {
            int value = this.config.getInt(node);
            debug(node, "Found Integer config value for node '" + node + "' = '" + value + "'.", ignoreLogging);
            return value;
        } else if (existDefault(node)) {
            int value = this.defaults.getInt(node);
            error(node, "Could not find a custom config node '" + node + "', using default Integer instead = '" + value + "'.", ignoreLogging);
            return value;
        }
        OdinManager.getLogger().error("Could not find config node '" + node + "', returning 0.");
        OdinManager.getLogger().debug("Custom config size: " + this.config.getFinalNodeCount());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getFinalNodeCount());
        return 0;
    }

    public Long getLong(String node) {
        return getLong(node, false);
    }

    public Long getLong(String node, boolean ignoreLogging) {
        debug(node, "Getting Long from config node: '" + node + "'.", ignoreLogging);
        if (exist(node)) {
            long value = this.config.getLong(node);
            debug(node, "Found Long config value for node '" + node + "' = '" + value + "'.", ignoreLogging);
            return value;
        } else if (existDefault(node)) {
            long value = this.defaults.getLong(node);
            error(node, "Could not find a custom config node '" + node + "', using default Long instead = '" + value + "'.", ignoreLogging);
            return value;
        }
        OdinManager.getLogger().error("Could not find config node '" + node + "', returning null.");
        OdinManager.getLogger().debug("Custom config size: " + this.config.getFinalNodeCount());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getFinalNodeCount());
        return null;
    }

    public Map<String, Object> getNodes() {
        return this.config.getNodes();
    }

    public Map<String, Object> getDefaultNodes() {
        return this.defaults.getNodes();
    }

    public YamlCombiner getConfig() {
        return this.config;
    }

    public YamlCombiner getDefaults() {
        return this.defaults;
    }

    public void load(YamlManager config, YamlManager defaults) {
        this.config.addYamlManager(config);
        this.defaults.addYamlManager(defaults);
        OdinManager.getLogger().debug("Custom config size: " + this.config.getFinalNodeCount());
        OdinManager.getLogger().debug("Default config size: " + this.defaults.getFinalNodeCount());
        if (this.config.getFinalNodeCount() == 0) {
            OdinManager.getLogger().error("Failed loading custom config!");
        }
        if (this.defaults.getFinalNodeCount() == 0) {
            OdinManager.getLogger().error("Failed loading default config!");
        }
    }

    private boolean exist(String node) {
        return node != null && this.config.exist(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return node != null && this.defaults.exist(node.toLowerCase());
    }
}
