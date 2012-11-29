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
package com.craftfire.odin.layer.bukkit.managers;

import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.inventory.InventoryItem;

import com.craftfire.odin.managers.inventory.ItemEnchantment;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

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
        HashSet<InventoryItem> inv = new HashSet<InventoryItem>();
        HashSet<InventoryItem> armorinv = new HashSet<InventoryItem>();

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
                String material = (inventory[i].getData() == null) ? "0" : "" + Byte.valueOf(inventory[i].getData().getData());
                InventoryItem item = new InventoryItem(inventory[i].getTypeId(), material);
                item.setAmount(inventory[i].getAmount());
                item.setDurability(inventory[i].getDurability());
                item.setEnchantments(enchantments);
                inv.add(item);
            } else {
                inv.add(new InventoryItem(0, null));
            }
        }
        for (short i = 0; i < armorInventory.length; i = (short)(i + 1)) {
            if (armorInventory[i] != null) {
                HashSet<ItemEnchantment> enchantments = new HashSet<ItemEnchantment>();
                Iterator<Map.Entry<Enchantment, Integer>> itemEnchantments =
                        inventory[i].getEnchantments().entrySet().iterator();
                while (itemEnchantments.hasNext()) {
                    Map.Entry<Enchantment, Integer> key = itemEnchantments.next();
                    Enchantment enc = key.getKey();
                    enchantments.add(new ItemEnchantment(enc.getId(), inventory[i].getEnchantmentLevel(enc)));
                }
                String material = (inventory[i].getData() == null) ? "0" : "" + Byte.valueOf(inventory[i].getData().getData());
                InventoryItem item = new InventoryItem(armorInventory[i].getTypeId(), material);
                item.setAmount(armorInventory[i].getAmount());
                item.setDurability(armorInventory[i].getDurability());
                item.setEnchantments(enchantments);
                armorinv.add(item);
            } else {
                inv.add(new InventoryItem(0, null));
            }
        }
        player.storeInventory(inv, armorinv);
    }

    public ItemStack[] getInventory(Player player) {
        if (OdinManager.getInventories().hasInventory(player.getName())) {
            String data = OdinManager.getInventories().getInventory(player.getName());
            if (data != null && ! data.isEmpty()) {
                String[] inv = data.split(",");
                ItemStack[] inventory;
                inventory = new ItemStack[36];
                for (int i=0; i<inv.length; i++) {
                    String line = inv[i];
                    String[] split = line.split(":");
                    if (split.length == 5) {
                        int type = Integer.valueOf(split[0]).intValue();
                        inventory[i] = new ItemStack(type, Integer.valueOf(split[1]).intValue());
                        short dur = Short.valueOf(split[3]).shortValue();
                        if (dur > 0) {
                            inventory[i].setDurability(dur);
                        }
                        byte dd;
                        if (split[2].length() == 0) {
                            dd = 0;
                        } else {
                            dd = Byte.valueOf(split[2]).byteValue();
                        }
                        Material mat = Material.getMaterial(type);
                        if (mat == null) {
                            inventory[i].setData(new MaterialData(type, dd));
                        } else {
                            inventory[i].setData(mat.getNewData(dd));
                        }
                        if (!split[4].equals("0")) {
                            String[] enchatments = split[4].split("-");
                            for (int a=0; a<enchatments.length; a++) {
                                String[] enchOptions = enchatments[a].split("=");
                                Enchantment ench = Enchantment.getById(Integer.parseInt(enchOptions[0]));
                                int level = Integer.parseInt(enchOptions[1]);
                                inventory[i].addEnchantment(ench, level);
                            }
                        }
                    }
                }
                return inventory;
            }
        }
        return null;
    }

    public ItemStack[] getArmorInventory(Player player) {
        if (OdinManager.getInventories().hasArmor(player.getName())) {
            String data = OdinManager.getInventories().getArmor(player.getName());
            if (data != null && ! data.isEmpty()) {
                String[] inv = data.split(",");
                ItemStack[] inventory = new ItemStack[4];
                for (int i=0; i<inv.length; i++) {
                    String line = inv[i];
                    String[] split = line.split(":");
                    if (split.length == 5) {
                        int type = Integer.valueOf(split[0]).intValue();
                        inventory[i] = new ItemStack(type, Integer.valueOf(split[1]).intValue());
                        short dur = Short.valueOf(split[3]).shortValue();
                        if (dur > 0) {
                            inventory[i].setDurability(dur);
                        }
                        byte dd;
                        if (split[2].length() == 0) {
                            dd = 0;
                        } else {
                            dd = Byte.valueOf(split[2]).byteValue();
                        }
                        Material mat = Material.getMaterial(type);
                        if (mat == null) {
                            inventory[i].setData(new MaterialData(type, dd));
                        } else {
                            inventory[i].setData(mat.getNewData(dd));
                        }
                        if (!split[4].equals("0")) {
                            String[] enchatments = split[4].split("-");
                            for (int a=0; a<enchatments.length; a++) {
                                String[] enchOptions = enchatments[a].split("=");
                                Enchantment ench = Enchantment.getById(Integer.parseInt(enchOptions[0]));
                                int level = Integer.parseInt(enchOptions[1]);
                                inventory[i].addEnchantment(ench, level);
                            }
                        }
                    }
                }
                return inventory;
            }
        }
        return null;
    }
}
