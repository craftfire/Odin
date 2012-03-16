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

public class InventoryManager {
    public void setInventory(String player, String inventory) {
        AuthDBManager.playerInventory.put(player, inventory);
    }

    public String getInventory(String player) {
        if (AuthDBManager.playerInventory.containsKey(player)) {
            return AuthDBManager.playerInventory.get(player);
        }
        return null;
    }

    public void setArmor(String player, String armor) {
        AuthDBManager.playerArmor.put(player, armor);
    }

    public String getArmor(String player) {
        if (AuthDBManager.playerArmor.containsKey(player)) {
            return AuthDBManager.playerArmor.get(player);
        }
        return null;
    }
}
