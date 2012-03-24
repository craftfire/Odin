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
package com.craftfire.authdb.layer.bukkit.util;

import com.craftfire.authdb.layer.bukkit.api.events.*;
import com.craftfire.authdb.layer.bukkit.managers.AuthDBPlayer;
import com.craftfire.authdb.managers.AuthDBManager;
import org.bukkit.Bukkit;

public class Events {

    public static void quit(AuthDBPlayer player) {
        //TODO: Call event
        player.restoreInventory();
        logout(player, false);
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerQuitEvent(player.getPlayer(), true));
    }

    public static void kick(AuthDBPlayer player, String message) {
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerKickEvent(player.getPlayer(), true, message));
    }

    public static void kick(AuthDBPlayer player) {
        kick(player, null);
    }

    public static void login(AuthDBPlayer player) {
        if (! player.isAuthenticated()) {
            player.login();
            player.restoreInventory();
            Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerLoginEvent(player.getPlayer(), true));
        }
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerLoginEvent(player.getPlayer(), false));
    }

    public static void logout(AuthDBPlayer player, boolean storeInventory) {
        if (player.isAuthenticated()) {
            player.logout();
            if (storeInventory) {
                player.storeInventory();
            }
            Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerLogoutEvent(player.getPlayer(), true));
        }
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerLogoutEvent(player.getPlayer(), false));
    }
    
    public static void link(AuthDBPlayer player, String name) {
        if (! player.isAuthenticated() && ! player.isLinked() && AuthDBManager.cfgMgr.getBoolean("link.enabled")) {
            player.login();
            if (! AuthDBManager.userLinkedNames.containsKey(player.getUsername())) {
                AuthDBManager.userLinkedNames.put(player.getUsername(), name);
            }
            if (AuthDBManager.cfgMgr.getBoolean("link.rename")) {
                player.setDisplayName(name);
            }
            Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerLinkEvent(player.getPlayer(), true, name));
        }
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerLinkEvent(player.getPlayer(), true, name));
    }

    public static void unlink(AuthDBPlayer player) {
        if (player.isAuthenticated() && player.isLinked() && AuthDBManager.cfgMgr.getBoolean("link.enabled")) {
            String name = player.getLinkedName();
            player.unlink();
            logout(player, true);
            if (AuthDBManager.cfgMgr.getBoolean("link.rename")) {
                player.setDisplayName(player.getName());
            }
            Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerUnlinkEvent(player.getPlayer(), true, name));
        }
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBPlayerUnlinkEvent(player.getPlayer(), false, null));
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
