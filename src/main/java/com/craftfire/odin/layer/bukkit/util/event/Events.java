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
        Bukkit.getServer().getPluginManager().callEvent(new OdinPlayerQuitEvent(player.getPlayer()));
        player.restoreInventory();
        player.save();
        logout(player, false);
    }

    public static boolean kick(OdinPlayer player, String message) {
        OdinPlayerKickEvent event = new OdinPlayerKickEvent(player.getPlayer(), message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            event.getPlayer().kickPlayer(event.getReason());
            return true;
        }
        return false;
    }

    public static boolean message(OdinPlayer player, String message) {
        OdinMessageEvent event = new OdinMessageEvent(player, message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            event.getPlayer().sendMessage(event.getMessage());
            return true;
        }
        return false;
    }

    public static boolean login(OdinPlayer player) {
        OdinPlayerLoginEvent event = new OdinPlayerLoginEvent(player.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && !player.isAuthenticated()) {
            player.login();
            player.restoreInventory();
            return true;
        }
        return false;
    }

    public static boolean logout(OdinPlayer player, boolean storeInventory) {
        OdinPlayerLogoutEvent event = new OdinPlayerLogoutEvent(player.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && player.isAuthenticated()) {
            player.logout();
            if (storeInventory) {
                player.storeInventory();
            }
            return true;
        }
        return false;
    }

    public static boolean link(OdinPlayer player, String name) {
        OdinPlayerLinkEvent event = new OdinPlayerLinkEvent(player.getPlayer(), name);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && !player.isAuthenticated() && !player.isLinked()) {
            player.login();
            if (!OdinManager.getStorage().hasLinkedUsername(player.getUsername())) {
                OdinManager.getStorage().putLinkedUsername(player.getUsername(), name);
            }
            if (OdinManager.getConfig().getBoolean("link.rename")) {
                player.setDisplayName(name);
            }
            return true;
        }
        return false;
    }

    public static boolean unlink(OdinPlayer player) {
        OdinPlayerLinkEvent event = new OdinPlayerLinkEvent(player.getPlayer(), player.getLinkedName());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && player.isAuthenticated() && player.isLinked()) {
            if (logout(player, true)) {
                player.unlink();
                if (OdinManager.getConfig().getBoolean("link.rename")) {
                    player.setDisplayName(player.getName());
                }
                return true;
            }
        }
        return false;
    }
}
