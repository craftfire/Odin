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
package com.craftfire.odin.managers.commands;

import com.craftfire.commons.yaml.YamlManager;
import com.craftfire.odin.managers.LoggingHandler;
import com.craftfire.odin.managers.OdinManager;

import java.util.Map;

public class CommandManager {
    private YamlManager commands = new YamlManager();
    private YamlManager defaults = new YamlManager();

    public boolean isInitialized() {
        return (!commands.getNodes().isEmpty() && !defaults.getNodes().isEmpty());
    }

    public void setLoggingHandler(LoggingHandler loggingHandler) {
        getCommands().setLoggingManager(loggingHandler);
        getDefaults().setLoggingManager(loggingHandler);
    }

    public String getCommand(String node) {
        String newNode = "commands." + node.toLowerCase();
        OdinManager.getLogger().debug("Getting command from commands node: '" + newNode + "'.");
        if (exist(newNode)) {
            String value = this.commands.getString(newNode);
            OdinManager.getLogger().debug("Found command value for node '" + newNode + "' = '" + value + "'.");
            return value;
        } else if (existDefault(newNode)) {
            String value = this.defaults.getString(newNode);
            OdinManager.getLogger().debug("Could not find a custom command node '" + newNode + "', using default command value instead = '" + value + "'.");
            return value;
        }
        OdinManager.getLogger().error("Could not find command node '" + newNode + "', returning null.");
        OdinManager.getLogger().debug("Custom commands size: " + this.commands.getNodes().size());
        OdinManager.getLogger().debug("Default commands size: " + this.defaults.getNodes().size());
        return null;
    }

    public String getAlias(String node) {
        String newNode = "aliases." + node.toLowerCase();
        OdinManager.getLogger().debug("Getting alias from alias node: '" + newNode + "'.");
        if (exist(newNode)) {
            String value = this.commands.getString(newNode);
            OdinManager.getLogger().debug("Found alias value for node '" + newNode + "' = '" + value + "'.");
            return value;
        } else if (existDefault(newNode)) {
            String value = this.defaults.getString(newNode);
            OdinManager.getLogger().debug("Could not find a custom alias node '" + newNode + "', using default alias value instead = '" + value + "'.");
            return value;
        }
        OdinManager.getLogger().error("Could not find alias node '" + newNode + "', returning null.");
        OdinManager.getLogger().debug("Custom alias size: " + this.commands.getNodes().size());
        OdinManager.getLogger().debug("Default alias size: " + this.defaults.getNodes().size());
        return null;
    }

    public Map<String, Object> getNodes() {
        return this.commands.getNodes();
    }

    public Map<String, Object> getDefaultNodes() {
        return this.defaults.getNodes();
    }

    public YamlManager getCommands() {
        return this.commands;
    }

    public YamlManager getDefaults() {
        return this.defaults;
    }
    
    public boolean equals(String command, String node) {
        return command.equalsIgnoreCase(getCommand(node)) || command.equalsIgnoreCase(getAlias(node));
    }

    public void load(YamlManager config, YamlManager defaults) {
        if (this.commands == null) {
            this.commands = config;
        } else {
            this.commands.addNodes(config);
        }
        if (this.defaults == null) {
            this.defaults = defaults;
        } else {
            this.defaults.addNodes(defaults);
        }
    }

    private boolean exist(String node) {
        return node != null && this.commands.exist(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return node != null && this.defaults.exist(node.toLowerCase());
    }
}
