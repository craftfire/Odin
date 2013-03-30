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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.PlayerInventory;

import com.craftfire.odin.layer.bukkit.Odin;
import com.craftfire.odin.layer.bukkit.util.event.Event;
import com.craftfire.odin.layer.bukkit.util.event.Events;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinPermission;
import com.craftfire.odin.managers.OdinUser;
import com.craftfire.odin.util.PermissionType;

public class OdinPlayer extends OdinUser {
    private Player player;

    /**
     * Default constructor for the object.
     *
     * @param player player object.
     */
    public OdinPlayer(Player player) {
        super(player.getName(), player.getAddress());
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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

    public boolean isLoggedIn() {
        for (Player p : this.player.getServer().getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(this.player.getName()) && isAuthenticated()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermissions(CommandSender sender, String permission) {
        return Odin.getPermissions().has(sender, permission);
    }

    public boolean hasPermissions(OdinPermission permission) {
        return Odin.getPermissions().has(this.player, permission.getNode());
    }

    public boolean hasPermissions(PermissionType type) {
       return Odin.getPermissions().has(this.player, OdinPermission.getNode(type));
    }

    public boolean hasAdminPermissions() {
        return hasPermissions(PermissionType.ADMIN);
    }

    public PlayerInventory getInventory() {
        return this.player.getInventory();
    }

    public void storeInventory() {
        Odin.getInstance().getInventoryManager().storeInventory(this);
        clearInventory();
    }

    public void clearInventory() {
        this.player.getInventory().setHelmet(null);
        this.player.getInventory().setChestplate(null);
        this.player.getInventory().setLeggings(null);
        this.player.getInventory().setBoots(null);
        this.player.getInventory().clear();
    }

    public void restoreInventory() {
        Odin.getInstance().getInventoryManager().setInventoryFromStorage(this.player);
    }

    public void kickPlayer(String node) {
        OdinManager.getLogger().debug("Kicking player '" + getName() + "': '" + node + "'.");
        if (isNode(node)) {
            callEvent(Event.KICK, OdinManager.getMessages().getMessage(node, this));
        } else {
            callEvent(Event.KICK, OdinManager.getMessages().replace(node, this));
        }
    }

    public void sendMessage(String node) {
        if (isNode(node)) {
            String message = OdinManager.getMessages().getMessage(node, this);
            OdinManager.getLogger().debug("Sending message to '" + getName() + "': '" + node + "' = '" + message + "'");
            callEvent(Event.MESSAGE, message);
        } else {
            String message = OdinManager.getMessages().replace(node, this);
            OdinManager.getLogger().debug("Sending message to '" + getName() + "': '" + node + "' = '" + message + "'");
            callEvent(Event.MESSAGE, message);
        }
    }

    public void sendMessage(String node, PlayerLoginEvent event) {
        // TODO: Call event?
        if (isNode(node)) {
            String message = OdinManager.getMessages().getMessage(node, this);
            OdinManager.getLogger().debug("Sending disallow message to '" + getName() + "': '" + node + "' = '" + message + "'");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, message);
        } else {
            String message = OdinManager.getMessages().replace(node, this);
            OdinManager.getLogger().debug("Sending disallow message to '" + getName() + "': '" + node + "' = '" + message + "'");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, message);
        }
    }

    public void checkTimeout() {
        if (!isAuthenticated() && getTimeout() > 0) {
            if (isRegistered()) {
                kickPlayer("login.timeout");
            } else {
                kickPlayer("register.timeout");
            }
        } else if (getTimeout() > 0) {
            setTimeout(0);
        }
    }

    public boolean callEventResults(Event event, String message) {
        switch (event) {
            case KICK:
                return Events.kick(this, message);
            case LINK:
                return Events.link(this, getLinkedName());
            case LOGIN:
                return Events.login(this);
            case LOGOUT:
                return Events.logout(this, false);
            case UNLINK:
                return Events.unlink(this);
            case QUIT:
                Events.quit(this);
                return true;
            case MESSAGE:
                return Events.message(this, message);
        }
        return false;
    }

    public boolean callEventResults(Event event) {
        return callEventResults(event, null);
    }

    public void callEvent(Event event, String message) {
        switch (event) {
            case KICK:
                Events.kick(this, message);
                break;
            case LINK:
                Events.link(this, getLinkedName());
                break;
            case LOGIN:
                Events.login(this);
                break;
            case LOGOUT:
                Events.logout(this, false);
                break;
            case UNLINK:
                Events.unlink(this);
                break;
            case QUIT:
                Events.quit(this);
                break;
            case MESSAGE:
                Events.message(this, message);
                break;
        }
    }

    public void callEvent(Event event) {
        callEvent(event, null);
    }

    private boolean isNode(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return string.contains(".");
    }
}
