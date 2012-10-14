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

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private Map<String, String> inventories = new HashMap<String, String>();
    private Map<String, String> armor = new HashMap<String, String>();

    public void clear() {
        this.inventories.clear();
        this.armor.clear();
    }

    public Map<String, String> getInventories() {
        return this.inventories;
    }

    public Map<String, String> getArmor() {
        return this.armor;
    }

    public void setInventory(String player, String inventory) {
        this.inventories.put(player, inventory);
    }

    public String getInventory(String player) {
        if (this.inventories.containsKey(player)) {
            return this.inventories.get(player);
        }
        return null;
    }

    public boolean hasInventory(String player) {
        return this.inventories.containsKey(player);
    }

    public void setArmor(String player, String armor) {
        this.armor.put(player, armor);
    }

    public String getArmor(String player) {
        if (this.armor.containsKey(player)) {
            return this.armor.get(player);
        }
        return null;
    }

    public boolean hasArmor(String player) {
        return this.armor.containsKey(player);
    }
}
