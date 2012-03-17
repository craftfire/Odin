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
package com.craftfire.authdb.managers.messages;

import com.craftfire.authdb.managers.configuration.ConfigurationNode;

import java.util.HashMap;

public class MessageManager {
    private HashMap<String, String> messages = new HashMap<String, String>();
    private HashMap<String, String> defaults = new HashMap<String, String>();

    public String getString(ConfigurationNode config) {
        if (exist(config)) {
            return this.messages.get(config.node.toLowerCase());
        } else if (existDefault(config)) {
            return this.defaults.get(config.node.toLowerCase());
        }
        return null;
    }

    public void load(HashMap<String, String> defaults, HashMap<String, String> messages) {
        this.defaults = defaults;
        this.messages = messages;
    }

    private boolean exist(ConfigurationNode config) {
        if (this.messages.containsKey(config.node.toLowerCase())) {
            return true;
        }
        return false;
    }

    private boolean existDefault(ConfigurationNode config) {
        if (this.defaults.containsKey(config.node.toLowerCase())) {
            return true;
        }
        return false;
    }
}
