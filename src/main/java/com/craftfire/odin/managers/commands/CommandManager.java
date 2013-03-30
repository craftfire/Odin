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
package com.craftfire.odin.managers.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.craftfire.commons.yaml.YamlManager;
import com.craftfire.odin.managers.LoggingHandler;
import com.craftfire.odin.managers.OdinManager;

public class CommandManager {
    private YamlManager commands = new YamlManager();
    private YamlManager defaults = new YamlManager();
    private Map<String, String> customValueNodes = new HashMap<String, String>();
    private Map<String, String> defaultValueNodes = new HashMap<String, String>();

    public boolean isInitialized() {
        return (!commands.getNodes().isEmpty() && !defaults.getNodes().isEmpty());
    }

    public void initialize() throws IllegalAccessException {
        for (Map.Entry<String, Object> stringObjectEntry : getDefaultNodes().entrySet()) {
            Map.Entry pairs = (Map.Entry) stringObjectEntry;
            if (pairs.getValue() instanceof String && pairs.getKey() instanceof String) {
                if (this.defaultValueNodes.containsKey(pairs.getValue())) {
                    throw new IllegalAccessException("The command/alias '" + pairs.getValue() + "' (" + pairs.getKey() + ") "
                                                    + "already exists under the '" + this.defaultValueNodes.get(pairs.getKey()) + "' "
                                                    + "node and cannot be used twice.");
                } else {
                    this.defaultValueNodes.put((String) pairs.getValue(), (String) pairs.getKey());
                }
            }
        }
        for (Map.Entry<String, Object> stringObjectEntry : getNodes().entrySet()) {
            Map.Entry pairs = (Map.Entry) stringObjectEntry;
            if (pairs.getValue() instanceof String && pairs.getKey() instanceof String) {
                if (this.customValueNodes.containsKey(pairs.getValue())) {
                    throw new IllegalAccessException("The command/alias '" + pairs.getValue() + "' (" + pairs.getKey() + ") "
                                                   + "already exists under the '" + this.customValueNodes.get(pairs.getKey()) + "' "
                                                   + "node and cannot be used twice.");
                } else {
                    this.customValueNodes.put((String) pairs.getValue(), (String) pairs.getKey());
                }
            }
        }
    }

    public void setLoggingHandler(LoggingHandler loggingHandler) {
        getCommands().setLoggingManager(loggingHandler);
        getDefaults().setLoggingManager(loggingHandler);
    }

    public String getCommand(String node) {
        return getCommand(node, false);
    }

    public String getCommand(String node, boolean ignoreLogging) {
        String newNode = "commands." + node.toLowerCase();
        if (!ignoreLogging) {
            OdinManager.getLogger().debug("Getting command from commands node: '" + newNode + "'.");
        }
        if (exist(newNode)) {
            String value = this.commands.getString(newNode);
            if (!ignoreLogging) {
                OdinManager.getLogger().debug("Found command value for node '" + newNode + "' = '" + value + "'.");
            }
            return value;
        } else if (existDefault(newNode)) {
            String value = this.defaults.getString(newNode);
            if (!ignoreLogging) {
                OdinManager.getLogger().error("Could not find a custom command node '" + newNode + "', using default command value instead = '" + value + "'.");
            }
            return value;
        }
        OdinManager.getLogger().error("Could not find command node '" + newNode + "', returning null.");
        OdinManager.getLogger().debug("Custom commands size: " + this.commands.getNodes().size());
        OdinManager.getLogger().debug("Default commands size: " + this.defaults.getNodes().size());
        return null;
    }

    public String getAlias(String node) {
        return getAlias(node, false);
    }

    public String getAlias(String node, boolean ignoreLogging) {
        String newNode = "aliases." + node.toLowerCase();
        if (!ignoreLogging) {
            OdinManager.getLogger().debug("Getting alias from alias node: '" + newNode + "'.");
        }
        if (exist(newNode)) {
            String value = this.commands.getString(newNode);
            if (!ignoreLogging) {
                OdinManager.getLogger().debug("Found alias value for node '" + newNode + "' = '" + value + "'.");
            }
            return value;
        } else if (existDefault(newNode)) {
            String value = this.defaults.getString(newNode);
            if (!ignoreLogging) {
                OdinManager.getLogger().error("Could not find a custom alias node '" + newNode + "', using default alias value instead = '" + value + "'.");
            }
            return value;
        }
        OdinManager.getLogger().error("Could not find alias node '" + newNode + "', returning null.");
        OdinManager.getLogger().debug("Custom alias size: " + this.commands.getNodes().size());
        OdinManager.getLogger().debug("Default alias size: " + this.defaults.getNodes().size());
        return null;
    }

    public String getNode(String value) {
        String node = this.commands.getNode(value, true);
        if (node == null) {
            node = this.defaults.getNode(value, true);
        }
        OdinManager.getLogger().debug("Returning node for value '" + value + "' = '" + node + "'.");
        return node;
    }

    public String getCommandName(String input) {
        String[] array = input.split("\\s+");
        String node = null;
        String checkString = null;
        int maxWords = 3;
        for (int i = 0; array.length > i && maxWords > i; i++) {
            if (checkString == null) {
                checkString = array[i];
            } else {
                checkString += " " + array[i];
            }
            if (getNodes().containsValue(checkString)) {
                node = getValueNodes().get(checkString);
                OdinManager.getLogger().debug("Found command '" + checkString + "' in node: '" + node + "'.");
                break;
            } else if (getDefaultNodes().containsValue(checkString)) {
                node = getDefaultValueNodes().get(checkString);
                OdinManager.getLogger().error("Could not find a custom node for '" + checkString + "', using default node instead: '" + node + "'.");
                break;
            }
        }
        if (node != null) {
            node = node.replaceAll("aliases.", "")
                       .replaceAll("commands.", "");
        }
        OdinManager.getLogger().debug("Returning command name for input '" + input + "' = '" + node + "'.");
        return node;
    }

    public Map<String, Object> getNodes() {
        return this.commands.getNodes();
    }

    public Map<String, Object> getDefaultNodes() {
        return this.defaults.getNodes();
    }

    public Map<String, String> getValueNodes() {
        return this.customValueNodes;
    }

    public Map<String, String> getDefaultValueNodes() {
        return this.defaultValueNodes;
    }

    public YamlManager getCommands() {
        return this.commands;
    }

    public YamlManager getDefaults() {
        return this.defaults;
    }

    public boolean isCommand(String input) {
        String command = getCommandName(input);
        if (command == null) {
            OdinManager.getLogger().debug("Could not find '" + input + "' in the command list.");
            return false;
        } else {
            return true;
        }
        // TODO: Do something with this
        /*if (getNode(input) == null) {
            OdinManager.getLogger().debug("Could not find '" + input + "' in the command list.");
            return false;
        } else {
            return true;
        }*/
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
        OdinManager.getLogger().debug("Custom commands size: " + this.commands.getNodes().size());
        OdinManager.getLogger().debug("Default commands size: " + this.defaults.getNodes().size());
        if (this.commands.getNodes().size() == 0) {
            OdinManager.getLogger().error("Failed loading custom commands!");
        }
        if (this.defaults.getNodes().size() == 0) {
            OdinManager.getLogger().error("Failed loading default commands!");
        }
    }

    private boolean exist(String node) {
        return node != null && this.commands.exist(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return node != null && this.defaults.exist(node.toLowerCase());
    }
}
