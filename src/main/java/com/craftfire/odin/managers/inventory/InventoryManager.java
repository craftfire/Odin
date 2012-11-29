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
package com.craftfire.odin.managers.inventory;

import com.craftfire.odin.managers.OdinUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InventoryManager {
    private Map<String, Set<InventoryItem>> inventories = new HashMap<String, Set<InventoryItem>>();
    private Map<String, Set<InventoryItem>> armor = new HashMap<String, Set<InventoryItem>>();

    public void clear() {
        this.inventories.clear();
        this.armor.clear();
    }

    public Map<String, Set<InventoryItem>> getInventories() {
        return this.inventories;
    }

    public Map<String, Set<InventoryItem>> getArmor() {
        return this.armor;
    }

    public void setInventory(OdinUser user, Set<InventoryItem> inventory) {
        setInventory(user.getUsername(), inventory);
    }

    public void setInventory(String username, Set<InventoryItem> inventory) {
        this.inventories.put(username, inventory);
    }

    public Set<InventoryItem> getInventory(OdinUser user) {
        return getInventory(user.getUsername());
    }

    public Set<InventoryItem> getInventory(String username) {
        if (hasInventory(username)) {
            return this.inventories.get(username);
        }
        return null;
    }

    public String getInventoryString(OdinUser user) {
        return getInventoryString(user.getUsername());
    }

    public String getInventoryString(String username) {
        if (hasInventory(username)) {
            StringBuilder itemsString = new StringBuilder();
            Set<InventoryItem> items = getInventory(username);
            for (InventoryItem item : items) {
                String enchantments = "0";
                if (item.getEnchantments().size() > 0) {
                   enchantments = "";
                   for (ItemEnchantment enchantment : item.getEnchantments()) {
                       enchantments += enchantment.getID() + "=" + enchantment.getLevel() + "-";
                   }
                }
                itemsString.append(item.getID() + ":" + item.getAmount() + ":" + item.getMaterial() + ":" + item.getDurability() + ":" + enchantments + ",");
            }
            return itemsString.toString();
        }
        return null;
    }

    public boolean hasInventory(OdinUser user) {
        return hasInventory(user.getUsername());
    }

    public boolean hasInventory(String username) {
        return this.inventories.containsKey(username);
    }

    public void setArmor(OdinUser user, Set<InventoryItem> armor) {
        setArmor(user.getUsername(), armor);
    }

    public void setArmor(String username, Set<InventoryItem> armor) {
        this.armor.put(username, armor);
    }

    public Set<InventoryItem> getArmor(OdinUser user) {
        return getArmor(user.getUsername());
    }

    public Set<InventoryItem> getArmor(String username) {
        if (hasArmor(username)) {
            return this.armor.get(username);
        }
        return null;
    }

    public String getArmorString(OdinUser user) {
        return getInventoryString(user.getUsername());
    }

    public String getArmorString(String username) {
        if (hasArmor(username)) {
            StringBuilder itemsString = new StringBuilder();
            Set<InventoryItem> items = getArmor(username);
            for (InventoryItem item : items) {
                String enchantments = "0";
                if (item.getEnchantments().size() > 0) {
                    enchantments = "";
                    for (ItemEnchantment enchantment : item.getEnchantments()) {
                        enchantments += enchantment.getID() + "=" + enchantment.getLevel() + "-";
                    }
                }
                itemsString.append(item.getID() + ":" + item.getAmount() + ":" + item.getMaterial() + ":" + item.getDurability() + ":" + enchantments + ",");
            }
            return itemsString.toString();
        }
        return null;
    }

    public boolean hasArmor(OdinUser user) {
        return hasArmor(user.getUsername());
    }

    public boolean hasArmor(String username) {
        return this.armor.containsKey(username);
    }
}
