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
package com.craftfire.authdb.layer.bukkit.util.event;

import com.craftfire.authdb.layer.bukkit.api.events.player.*;
import com.craftfire.authdb.layer.bukkit.api.events.plugin.AuthDBMessageEvent;
import com.craftfire.authdb.layer.bukkit.managers.AuthDBPlayer;
import com.craftfire.authdb.managers.AuthDBManager;
import org.bukkit.Bukkit;

public class Events {
    public static void quit(AuthDBPlayer player) {
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerQuitEvent(player.getPlayer()));
        player.restoreInventory();
        logout(player, false);
    }

    public static boolean kick(AuthDBPlayer player, String message) {
        AuthDBPlayerKickEvent event = new AuthDBPlayerKickEvent(player.getPlayer(), message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            event.getPlayer().kickPlayer(event.getReason());
            return true;
        }
        return false;
    }

    public static boolean message(AuthDBPlayer player, String message) {
        AuthDBMessageEvent event = new AuthDBMessageEvent(player.getPlayer(), message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            event.getPlayer().sendMessage(event.getMessage());
            return true;
        }
        return false;
    }

    public static boolean login(AuthDBPlayer player) {
        AuthDBPlayerLoginEvent event = new AuthDBPlayerLoginEvent(player.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && !player.isAuthenticated()) {
            player.login();
            player.restoreInventory();
            return true;
        }
        return false;
    }

    public static boolean logout(AuthDBPlayer player, boolean storeInventory) {
        AuthDBPlayerLogoutEvent event = new AuthDBPlayerLogoutEvent(player.getPlayer());
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

    public static boolean link(AuthDBPlayer player, String name) {
        AuthDBPlayerLinkEvent event = new AuthDBPlayerLinkEvent(player.getPlayer(), name);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && !player.isAuthenticated() && !player.isLinked()) {
            player.login();
            if (!AuthDBManager.userLinkedNames.containsKey(player.getUsername())) {
                AuthDBManager.userLinkedNames.put(player.getUsername(), name);
            }
            if (AuthDBManager.cfgMgr.getBoolean("link.rename")) {
                player.setDisplayName(name);
            }
            return true;
        }
        return false;
    }

    public static boolean unlink(AuthDBPlayer player) {
        AuthDBPlayerLinkEvent event = new AuthDBPlayerLinkEvent(player.getPlayer(), player.getLinkedName());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && player.isAuthenticated() && player.isLinked()) {
            if (logout(player, true)) {
                player.unlink();
                if (AuthDBManager.cfgMgr.getBoolean("link.rename")) {
                    player.setDisplayName(player.getName());
                }
                return true;
            }
        }
        return false;
    }
}
