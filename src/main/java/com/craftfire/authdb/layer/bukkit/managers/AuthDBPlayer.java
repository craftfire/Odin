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

import com.craftfire.authdb.layer.bukkit.AuthDB;
import com.craftfire.authdb.layer.bukkit.api.events.AuthDBPlayerKickEvent;
import com.craftfire.authdb.managers.AuthDBManager;
import com.craftfire.authdb.managers.AuthDBUser;
import com.craftfire.authdb.managers.LoggingHandler;
import com.craftfire.authdb.managers.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;

public class AuthDBPlayer extends AuthDBUser {
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
    
    public boolean hasPermissions(Permissions permission) {
        return AuthDB.permission.has(this.player, permission.permission);
    }

    public PlayerInventory getInventory() {
        return this.player.getInventory();
    }

    public void storeInventory() {
        try {
            AuthDB.inventoryManager.storeInventory(this.player,
                                                         this.player.getInventory().getContents(),
                                                         this.player.getInventory().getArmorContents());
            clearInventory();
        } catch (IOException e) {
            LoggingHandler.stackTrace(e, Thread.currentThread());
        }
    }

    public void clearInventory() {
        this.player.getInventory().setHelmet(null);
        this.player.getInventory().setChestplate(null);
        this.player.getInventory().setLeggings(null);
        this.player.getInventory().setBoots(null);
        this.player.getInventory().clear();
    }

    public void restoreInventory() {
        AuthDB.inventoryManager.setInventoryFromStorage(this.player);
    }
    
    public void kickPlayer(String node) {
        if (isNode(node)) {
            this.player.kickPlayer(AuthDBManager.msgMgr.getMessage(node, this));
        } else {
            this.player.kickPlayer(AuthDBManager.msgMgr.replace(node, this));
        }
    }
    
    public void sendMessage(String node) {
        if (isNode(node)) {
            this.player.sendMessage(AuthDBManager.msgMgr.getMessage(node, this));
        } else {
            this.player.sendMessage(AuthDBManager.msgMgr.replace(node, this));
        }
    }

    public void sendMessage(String node, PlayerLoginEvent event) {
        if (isNode(node)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, AuthDBManager.msgMgr.getMessage(node, this));
            Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerKickEvent(
                                                                        event.getPlayer(),
                                                                        true,
                                                                        AuthDBManager.msgMgr.getMessage(node, this)));
        } else {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, AuthDBManager.msgMgr.replace(node, this));
            Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerKickEvent(
                    event.getPlayer(),
                    true,
                    AuthDBManager.msgMgr.replace(node, this)));
        }
    }

    public void checkTimeout() {
        if (! isAuthenticated() && AuthDBManager.userTimeouts.contains(this.username)) {
            if (isRegistered()) {
                kickPlayer("login.timeout");
            } else {
                kickPlayer("register.timeout");
            }
        } else if (AuthDBManager.userTimeouts.contains(this.username)) {
            AuthDBManager.userTimeouts.remove(this.username);
        }
    }
    
    private boolean isNode(String string) {
        for (int i=0; i < string.length(); i++) {
            if (Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return string.contains(".");
    }
}
