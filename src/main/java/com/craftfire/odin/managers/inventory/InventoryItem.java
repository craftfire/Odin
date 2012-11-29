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

import java.util.HashSet;
import java.util.Set;

public class InventoryItem {
    private int id;
    private int amount = 1;
    private String material = "0";
    private Set<ItemEnchantment> enchantments = new HashSet<ItemEnchantment>();
    private short durability = 0;

    public InventoryItem(int id, String material) {
        this.id = id;
        this.material = material;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMaterial() {
        return this.material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public short getDurability() {
        return this.durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }

    public Set<ItemEnchantment> getEnchantments() {
        return this.enchantments;
    }

    public void setEnchantments(HashSet<ItemEnchantment> enchantments) {
        this.enchantments = enchantments;
    }
}
