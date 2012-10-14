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
package com.craftfire.odin.layer.bukkit.managers;

import com.craftfire.bifrost.exceptions.UnsupportedMethod;
import com.craftfire.odin.layer.bukkit.Odin;
import com.craftfire.odin.layer.bukkit.util.event.Event;
import com.craftfire.odin.layer.bukkit.util.event.Events;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinUser;
import com.craftfire.odin.managers.LoggingHandler;
import com.craftfire.odin.managers.permissions.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;

public class OdinPlayer extends OdinUser {
    protected Player player;
    /**
     * Default constructor for the object.
     *
     * @param player player object.
     */
    public OdinPlayer(Player player) {
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
        return Odin.permission.has(this.player, permission);
    }

    public boolean hasPermissions(CommandSender sender, String permission) {
        return Odin.permission.has(sender, permission);
    }
    
    public boolean hasPermissions(Permissions permission) {
        return Odin.permission.has(this.player, permission.permission);
    }

    public PlayerInventory getInventory() {
        return this.player.getInventory();
    }

    public void storeInventory() {
        try {
            Odin.inventoryManager.storeInventory(this.player,
                                                         this.player.getInventory().getContents(),
                                                         this.player.getInventory().getArmorContents());
            clearInventory();
        } catch (IOException e) {
            LoggingHandler.stackTrace(e);
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
        Odin.inventoryManager.setInventoryFromStorage(this.player);
    }
    
    public void kickPlayer(String node) {
        if (isNode(node)) {
            callEvent(Event.KICK, OdinManager.msgMgr.getMessage(node, this));
        } else {
            callEvent(Event.KICK, OdinManager.msgMgr.replace(node, this));
        }
    }
    
    public void sendMessage(String node) {
        if (isNode(node)) {
            callEvent(Event.MESSAGE, OdinManager.msgMgr.getMessage(node, this));
        } else {
            callEvent(Event.MESSAGE, OdinManager.msgMgr.replace(node, this));
        }
    }

    public void sendMessage(String node, PlayerLoginEvent event) {
        //TODO: Call event?
        if (isNode(node)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, OdinManager.msgMgr.getMessage(node, this));
        } else {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, OdinManager.msgMgr.replace(node, this));
        }
    }

    public void checkTimeout() {
        if (!isAuthenticated() && OdinManager.userTimeouts.contains(this.username)) {
            if (isRegistered()) {
                kickPlayer("login.timeout");
            } else {
                kickPlayer("register.timeout");
            }
        } else if (OdinManager.userTimeouts.contains(this.username)) {
            OdinManager.userTimeouts.remove(this.username);
        }
    }

    public boolean callEventResults(Event event, String message) {
        switch (event) {
            case KICK:      return Events.kick(this, message);
            case LINK:      return Events.link(this, getLinkedName());
            case LOGIN:     return Events.login(this);
            case LOGOUT:    return Events.logout(this, false);
            case UNLINK:    return Events.unlink(this);
            case QUIT:      Events.quit(this);
                            return true;
            case MESSAGE:   return Events.message(this, message);
        }
        return false;
    }

    public boolean callEventResults(Event event) {
        return callEventResults(event, null);
    }

    public void callEvent(Event event, String message) {
        switch (event) {
            case KICK:      Events.kick(this, message);
                            break;
            case LINK:      Events.link(this, getLinkedName());
                            break;
            case LOGIN:     Events.login(this);
                            break;
            case LOGOUT:    Events.logout(this, false);
                            break;
            case UNLINK:    Events.unlink(this);
                            break;
            case QUIT:      Events.quit(this);
                            break;
            case MESSAGE:   Events.message(this, message);
                            break;
        }
    }

    public void callEvent(Event event) {
        switch (event) {
            case KICK:      Events.kick(this, null);
                break;
            case LINK:      Events.link(this, getLinkedName());
                break;
            case LOGIN:     Events.login(this);
                break;
            case LOGOUT:    Events.logout(this, false);
                break;
            case UNLINK:    Events.unlink(this);
                break;
            case QUIT:      Events.quit(this);
                break;
            case MESSAGE:   Events.message(this, null);
                break;
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
