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
package com.craftfire.authdb.layer.bukkit.managers;

import com.craftfire.authdb.managers.AuthDBPlayerBase;
import com.craftfire.authdb.layer.bukkit.AuthDB;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;

public class AuthDBPlayer extends AuthDBPlayerBase {
    protected Player player;
    /**
     * Default constructor for the object.
     *
     * @param player player object.
     */
    public AuthDBPlayer(Player player) {
        super(player.getName());
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }

    public String getDisplayName() {
        return this.player.getDisplayName();
    }
    
    public void setDisplayName(String name) {
        this.player.setDisplayName(name);
    }

    public void setLinkedName() {
        if (isLinked()) {
            setDisplayName(getLinkedName());
        }
    }

    public boolean hasPermissions(String permission) {
        return AuthDB.permission.has(this.player, permission);
    }

    public boolean hasPermissions(CommandSender sender, String permission) {
        return AuthDB.permission.has(sender, permission);
    }

    public PlayerInventory getInventory() {
        return this.player.getInventory();
    }

    public void storeInventory() {
        try {
            AuthDB.inventoryManager.storeInventory(this.player,
                                                         this.player.getInventory().getContents(),
                                                         this.player.getInventory().getArmorContents());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreInventory() {
        AuthDB.inventoryManager.setInventoryFromStorage(this.player);
    }
}
