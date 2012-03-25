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
import org.bukkit.Bukkit;

public class EventHandler {
    public static AuthDBPlayerQuitEvent onAuthDBPlayerQuit(AuthDBPlayer player) {
        AuthDBPlayerQuitEvent event = new AuthDBPlayerQuitEvent(player.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static AuthDBPlayerKickEvent onAuthDBPlayerKick(AuthDBPlayer player, String message) {
        AuthDBPlayerKickEvent event = new AuthDBPlayerKickEvent(player.getPlayer(), message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static AuthDBMessageEvent onAuthDBMessage(AuthDBPlayer player, String message) {
        AuthDBMessageEvent event = new AuthDBMessageEvent(player.getPlayer(), message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static AuthDBPlayerLoginEvent onAuthDBPlayerLogin(AuthDBPlayer player) {
        AuthDBPlayerLoginEvent event = new AuthDBPlayerLoginEvent(player.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static AuthDBPlayerLogoutEvent onAuthDBPlayerLogout(AuthDBPlayer player) {
        AuthDBPlayerLogoutEvent event = new AuthDBPlayerLogoutEvent(player.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
    
    public static AuthDBPlayerLinkEvent onAuthDBPlayerLink(AuthDBPlayer player, String name) {
        AuthDBPlayerLinkEvent event = new AuthDBPlayerLinkEvent(player.getPlayer(), name);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static AuthDBPlayerUnlinkEvent onAuthDBPlayerUnlink(AuthDBPlayer player) {
        AuthDBPlayerUnlinkEvent event = new AuthDBPlayerUnlinkEvent(player.getPlayer(), player.getLinkedName());
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
}
