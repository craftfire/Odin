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
package com.craftfire.odin.layer.bukkit.managers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.inventory.InventoryItem;
import com.craftfire.odin.managers.inventory.InventoryType;
import com.craftfire.odin.managers.inventory.ItemEnchantment;

public class InventoryManager {
    public void setInventoryFromStorage(Player player) {
        ItemStack[] inv = getInventory(player);
        if (inv != null) {
            player.getInventory().setContents(inv);
        }
        inv = getArmorInventory(player);
        if (inv != null) {
            player.getInventory().setArmorContents(inv);
        }
    }

    public void storeInventory(OdinPlayer player) {
        ItemStack[] inventory = player.getInventory().getContents();
        ItemStack[] armorInventory = player.getInventory().getArmorContents();
        InventoryItem[] inv = new InventoryItem[inventory.length];
        InventoryItem[] armorInv = new InventoryItem[armorInventory.length];

        for (short i = 0; i < inventory.length; i = (short)(i + 1)) {
            if (inventory[i] != null) {
                HashSet<ItemEnchantment> enchantments = new HashSet<ItemEnchantment>();
                Iterator<Map.Entry<Enchantment, Integer>> itemEnchantments =
                        inventory[i].getEnchantments().entrySet().iterator();
                while (itemEnchantments.hasNext()) {
                    Map.Entry<Enchantment, Integer> key = itemEnchantments.next();
                    Enchantment enc = key.getKey();
                    enchantments.add(new ItemEnchantment(enc.getId(), inventory[i].getEnchantmentLevel(enc)));
                }
                byte material = (inventory[i].getData() == null) ? 0 : inventory[i].getData().getData();
                InventoryItem item = new InventoryItem(inventory[i].getTypeId(), material);
                item.setAmount(inventory[i].getAmount());
                item.setDurability(inventory[i].getDurability());
                item.setEnchantments(enchantments);
                inv[i] = item;
            } else {
                inv[i] = new InventoryItem(0, 0);
            }
        }
        for (short i = 0; i < armorInventory.length; i = (short)(i + 1)) {
            if (armorInventory[i] != null) {
                HashSet<ItemEnchantment> enchantments = new HashSet<ItemEnchantment>();
                Iterator<Map.Entry<Enchantment, Integer>> itemEnchantments =
                        armorInventory[i].getEnchantments().entrySet().iterator();
                while (itemEnchantments.hasNext()) {
                    Map.Entry<Enchantment, Integer> key = itemEnchantments.next();
                    Enchantment enc = key.getKey();
                    enchantments.add(new ItemEnchantment(enc.getId(), armorInventory[i].getEnchantmentLevel(enc)));
                }
                byte material = (armorInventory[i].getData() == null) ? 0 : armorInventory[i].getData().getData();
                InventoryItem item = new InventoryItem(armorInventory[i].getTypeId(), material);
                item.setAmount(armorInventory[i].getAmount());
                item.setDurability(armorInventory[i].getDurability());
                item.setEnchantments(enchantments);
                armorInv[i] = item;
            } else {
                armorInv[i] = new InventoryItem(0, 0);
            }
        }
        player.storeInventory(inv, armorInv);
    }

    public ItemStack[] getInventory(Player player) {
        if (OdinManager.getInventories().hasInventory(player.getName())) {
            return getInventory(player, InventoryType.INVENTORY);
        }
        return null;
    }

    public ItemStack[] getArmorInventory(Player player) {
        if (OdinManager.getInventories().hasArmor(player.getName())) {
            return getInventory(player, InventoryType.ARMOR);
        }
        return null;
    }

    private ItemStack[] getInventory(Player player, InventoryType type) {
        String data = null;
        if (type.equals(InventoryType.INVENTORY)) {
            data = OdinManager.getInventories().getInventoryString(player.getName());
        } else if (type.equals(InventoryType.ARMOR)) {
            data = OdinManager.getInventories().getArmorString(player.getName());
        }
        if (data != null && !data.isEmpty()) {
            String[] inv = data.split(",");
            ItemStack[] inventory = new ItemStack[inv.length];
            for (int i = 0; i < inv.length; i++) {
                InventoryItem item = new InventoryItem(inv[i]);
                inventory[i] = new ItemStack(item.getID(), item.getAmount());
                inventory[i].setDurability(item.getDurability());
                Material material = Material.getMaterial(item.getID());
                if (material == null) {
                    inventory[i].setData(new MaterialData(item.getID(), item.getMaterial()));
                } else {
                    inventory[i].setData(material.getNewData(item.getMaterial()));
                }
                for (ItemEnchantment itemEnchantment : item.getEnchantments()) {
                    Enchantment enchantment = Enchantment.getById(itemEnchantment.getID());
                    inventory[i].addEnchantment(enchantment, itemEnchantment.getLevel());
                }
            }
            return inventory;
        }
        return null;
    }
}
