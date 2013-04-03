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

import java.util.HashMap;
import java.util.Map;

import com.craftfire.commons.yaml.YamlCombiner;
import com.craftfire.commons.yaml.YamlManager;

import com.craftfire.odin.managers.LoggingHandler;
import com.craftfire.odin.managers.OdinManager;

public class CommandManager {
    private YamlCombiner commands = new YamlCombiner();
    private YamlCombiner defaults = new YamlCombiner();
    private Map<String, String> customValueNodes = new HashMap<String, String>();
    private Map<String, String> defaultValueNodes = new HashMap<String, String>();

    public boolean isInitialized() {
        return (this.commands.getFinalNodeCount() != 0 && this.defaults.getFinalNodeCount() != 0);
    }

    public void initialize() throws IllegalAccessException {
        for (Map.Entry<String, Object> stringObjectEntry : getDefaultNodes().entrySet()) {
            Map.Entry pairs = stringObjectEntry;
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
            Map.Entry pairs = stringObjectEntry;
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
        // TODO: Ignore logging if the string contains private information, ie. password.
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
        OdinManager.getLogger().debug("Custom commands size: " + this.commands.getFinalNodeCount());
        OdinManager.getLogger().debug("Default commands size: " + this.defaults.getFinalNodeCount());
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
        OdinManager.getLogger().debug("Custom alias size: " + this.commands.getFinalNodeCount());
        OdinManager.getLogger().debug("Default alias size: " + this.defaults.getFinalNodeCount());
        return null;
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

    public YamlCombiner getCommands() {
        return this.commands;
    }

    public YamlCombiner getDefaults() {
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
        this.commands.addYamlManager(config);
        this.defaults.addYamlManager(defaults);
        OdinManager.getLogger().debug("Custom commands size: " + this.commands.getFinalNodeCount());
        OdinManager.getLogger().debug("Default commands size: " + this.defaults.getFinalNodeCount());
        if (this.commands.getFinalNodeCount() == 0) {
            OdinManager.getLogger().error("Failed loading custom commands!");
        }
        if (this.defaults.getFinalNodeCount() == 0) {
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
