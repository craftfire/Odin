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
        String newNode = "Core.commands." + node.toLowerCase();
        if (exist(newNode)) {
            return this.commands.getString(newNode);
        } else if (existDefault(newNode)) {
            return this.defaults.getString(newNode);
        }
        return null;
    }

    public String getAlias(String node) {
        String newNode = "Core.aliases." + node.toLowerCase();
        if (exist(newNode)) {
            return this.commands.getString(newNode);
        } else if (existDefault(newNode)) {
            return this.defaults.getString(newNode);
        }
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
