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
package com.craftfire.authdb.managers;

import java.util.HashMap;

public class MessageManager {
    private HashMap<String, String> messages = new HashMap<String, String>();
    private HashMap<String, String> defaults = new HashMap<String, String>();

    public String getString(String node) {
        if (exist(node.toLowerCase())) {
            return this.messages.get(node.toLowerCase());
        } else if (existDefault(node.toLowerCase())) {
            return this.defaults.get(node.toLowerCase());
        }
        return null;
    }

    public void load(HashMap<String, String> defaults, HashMap<String, String> messages) {
        this.defaults.putAll(defaults);
        this.messages.putAll(messages);
    }

    private boolean exist(String node) {
        return this.messages.containsKey(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return this.defaults.containsKey(node.toLowerCase());
    }
}
