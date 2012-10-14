/*
 * This file is part of Odin <http://www.odin.com/>.
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

import com.craftfire.commons.managers.YamlManager;

import java.util.Map;

public class CommandManager {
    private YamlManager config = null;
    private YamlManager defaults = null;

    public String getCommand(String node) {
        node = "Core.commands." + node.toLowerCase();
        if (exist(node)) {
            return this.config.getString(node);
        } else if (existDefault(node)) {
            return this.defaults.getString(node);
        }
        return null;
    }

    public String getAlias(String node) {
        node = "Core.aliases." + node.toLowerCase();
        if (exist(node)) {
            return this.config.getString(node);
        } else if (existDefault(node)) {
            return this.defaults.getString(node);
        }
        return null;
    }

    public Map<String, Object> getNodes() {
        return this.config.getNodes();
    }

    public Map<String, Object> getDefaults() {
        return this.defaults.getNodes();
    }
    
    public boolean equals(String command, String node) {
        return command.equalsIgnoreCase(getCommand(node)) || command.equalsIgnoreCase(getAlias(node));
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
        return this.config.exist(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return this.defaults.exist(node.toLowerCase());
    }
}
