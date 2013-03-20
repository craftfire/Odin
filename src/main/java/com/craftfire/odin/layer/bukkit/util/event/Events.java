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
package com.craftfire.odin.layer.bukkit.util.event;

import com.craftfire.odin.layer.bukkit.api.events.player.*;
import com.craftfire.odin.layer.bukkit.api.events.plugin.OdinMessageEvent;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.managers.OdinManager;
import org.bukkit.Bukkit;

public class Events {
    public static void quit(OdinPlayer player) {
        OdinManager.getLogger().debug("Calling the 'QUIT' event for user '" + player.getName() + "'.");
        Bukkit.getServer().getPluginManager().callEvent(new OdinPlayerQuitEvent(player));
        OdinManager.getLogger().debug("Successfully executed the 'QUIT' event for user '" + player.getName() + "'.");
        player.restoreInventory();
        player.save();
        logout(player, false);
    }

    public static boolean kick(OdinPlayer player, String message) {
        OdinPlayerKickEvent event = new OdinPlayerKickEvent(player, message);
        OdinManager.getLogger().debug("Calling the 'KICK' event for user '" + player.getName()
                                    + "', message is '" + message + "'.");
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            event.getUser().getPlayer().kickPlayer(event.getReason());
            OdinManager.getLogger().debug("Successfully executed the 'KICK' event for user '" + player.getName() + "'.");
            return true;
        }
        OdinManager.getLogger().debug("The 'KICK' event for user '" + player.getName()  + "', was cancelled.");
        return false;
    }

    public static boolean message(OdinPlayer player, String message) {
        OdinMessageEvent event = new OdinMessageEvent(player, message);
        OdinManager.getLogger().debug("Calling the 'MESSAGE' event for user '" + player.getName()
                                    + "', message is '" + message + "'.");
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            event.getUser().getPlayer().sendMessage(event.getMessage());
            OdinManager.getLogger().debug("Successfully executed the 'MESSAGE' event for user '" + event.getUser().getName()
                                        + "': '" + event.getMessage() + "'.");
            return true;
        }
        OdinManager.getLogger().debug("The 'MESSAGE' event for user '" + player.getName()  + "', was cancelled.");
        return false;
    }

    public static boolean login(OdinPlayer player) {
        OdinPlayerLoginEvent event = new OdinPlayerLoginEvent(player);
        OdinManager.getLogger().debug("Calling the 'LOGIN' event for user '" + player.getName() + "'.");
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && !player.isAuthenticated()) {
            player.login();
            player.restoreInventory();
            OdinManager.getLogger().debug("Successfully executed the 'LOGIN' event for user '" + player.getName()  + "'.");
            return true;
        }
        OdinManager.getLogger().debug("The 'LOGIN' event for user '" + player.getName()
                                    + "', was cancelled or the user was already logged in.");
        return false;
    }

    public static boolean logout(OdinPlayer player, boolean storeInventory) {
        OdinPlayerLogoutEvent event = new OdinPlayerLogoutEvent(player);
        OdinManager.getLogger().debug("Calling the 'LOGOUT' event for user '" + player.getName()
                                    + "', storing inventory: '" + storeInventory + "'.");
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && player.isAuthenticated()) {
            player.logout();
            if (storeInventory) {
                player.storeInventory();
            }
            OdinManager.getLogger().debug("Successfully executed the 'LOGOUT' event for user '" + player.getName() + "'.");
            return true;
        }
        OdinManager.getLogger().debug("The 'LOGOUT' event for user '" + player.getName()
                                    + "', was cancelled or the user was not logged in.");
        return false;
    }

    public static boolean link(OdinPlayer player, String name) {
        OdinPlayerLinkEvent event = new OdinPlayerLinkEvent(player, name);
        OdinManager.getLogger().debug("Calling the 'LINK' event for user '" + player.getName()
                                    + "', linked name is '" + name + "'.");
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && !player.isAuthenticated() && !player.isLinked()) {
            player.login();
            if (!OdinManager.getStorage().hasLinkedUsername(player.getUsername())) {
                OdinManager.getStorage().putLinkedUsername(player.getUsername(), name);
            }
            if (OdinManager.getConfig().getBoolean("link.rename")) {
                player.setDisplayName(name);
            }
            OdinManager.getLogger().debug("Successfully executed the 'LINK' event for user '" + player.getName() + "'.");
            return true;
        }
        OdinManager.getLogger().debug("The 'LINK' event for user '" + player.getName()
                                    + "', was cancelled or the user was already logged in or already linked.");
        return false;
    }

    public static boolean unlink(OdinPlayer player) {
        OdinPlayerLinkEvent event = new OdinPlayerLinkEvent(player, player.getLinkedName());
        OdinManager.getLogger().debug("Calling the 'UNLINK' event for user '" + player.getName() + "'.");
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && player.isAuthenticated() && player.isLinked()) {
            if (logout(player, true)) {
                player.unlink();
                if (OdinManager.getConfig().getBoolean("link.rename")) {
                    player.setDisplayName(player.getName());
                }
                OdinManager.getLogger().debug("Successfully executed the 'UNLINK' event for user '" + player.getName() + "'.");
                return true;
            }
        }
        OdinManager.getLogger().debug("The 'UNLINK' event for user '" + player.getName()
                                    + "', was cancelled or the user was not logged in or already unlinked.");
        return false;
    }
}
