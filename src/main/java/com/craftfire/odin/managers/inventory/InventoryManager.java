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

import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinUser;
import com.craftfire.odin.managers.StorageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InventoryManager {
    private Map<String, InventoryItem[]> inventories = new HashMap<String, InventoryItem[]>();
    private Map<String, InventoryItem[]> armor = new HashMap<String, InventoryItem[]>();

    public void clear() {
        this.inventories.clear();
        this.armor.clear();
    }

    public Map<String, InventoryItem[]> getInventories() {
        return this.inventories;
    }

    public Map<String, InventoryItem[]> getStoredInventories() {
        //TODO
        return null;
    }

    public Map<String, InventoryItem[]> getStoredArmor() {
        //TODO
        return null;
    }

    public Map<String, InventoryItem[]> getArmor() {
        return this.armor;
    }

    public void setInventory(OdinUser user, InventoryItem[] inventory) {
        setInventory(user.getUsername(), inventory);
    }

    public void setInventory(String username, InventoryItem[] inventory) {
        this.inventories.put(username, inventory);
        OdinManager.getStorage(); //TODO
    }

    public InventoryItem[] getInventory(OdinUser user) {
        return getInventory(user.getUsername());
    }

    public InventoryItem[] getInventory(String username) {
        if (hasInventory(username)) {
            return this.inventories.get(username);
        } else {
            //TODO: OdinManager.getStorage().getString(StorageManager.Table.INVENTORY);
        }
        return null;
    }

    public String getInventoryString(OdinUser user) {
        return getInventoryString(user.getUsername());
    }

    public String getInventoryString(String username) {
        if (hasInventory(username)) {
            return itemArrayToString(getInventory(username));
        }
        return null;
    }

    public boolean hasInventory(OdinUser user) {
        return hasInventory(user.getUsername());
    }

    public boolean hasInventory(String username) {
        return this.inventories.containsKey(username);
    }

    public void setArmor(OdinUser user, InventoryItem[] armor) {
        setArmor(user.getUsername(), armor);
    }

    public void setArmor(String username, InventoryItem[] armor) {
        this.armor.put(username, armor);
    }

    public InventoryItem[] getArmor(OdinUser user) {
        return getArmor(user.getUsername());
    }

    public InventoryItem[] getArmor(String username) {
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
            return itemArrayToString(getArmor(username));
        }
        return null;
    }

    public boolean hasArmor(OdinUser user) {
        return hasArmor(user.getUsername());
    }

    public boolean hasArmor(String username) {
        return this.armor.containsKey(username);
    }

    private String itemArrayToString(InventoryItem[] items) {
        //TODO: Add a check to see if it's a real set?
        StringBuilder itemsString = new StringBuilder();
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

    private InventoryItem[] itemStringToArray(String items) {
        //TODO: Add a check to see if it's real? Also go through and make sure it works.
        if (items != null && ! items.isEmpty()) {
            String[] inv = items.split(",");
            InventoryItem[] inventory = new InventoryItem[inv.length];
            for (int i = 0; i < inv.length; i++) {
                String line = inv[i];
                String[] split = line.split(":");
                if (split.length == 5) {
                    inventory[i] = new InventoryItem(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
                    inventory[i].setDurability(Short.valueOf(split[3]));
                    inventory[i].setMaterial((split[2].length() == 0) ? 0 : Byte.valueOf(split[2]));
                    if (!split[4].equals("0")) {
                        String[] enchantments = split[4].split("-");
                        for (String enchantment : enchantments) {
                            String[] enchantmentOptions = enchantment.split("=");
                            inventory[i].addEnchantment(Integer.parseInt(enchantmentOptions[0]), Integer.parseInt(enchantmentOptions[1]));
                        }
                    }
                }
            }
            return inventory;
        }
        return null;
    }
}
