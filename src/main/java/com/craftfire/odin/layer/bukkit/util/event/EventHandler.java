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
package com.craftfire.odin.layer.bukkit.util.event;

import org.bukkit.Bukkit;

import com.craftfire.odin.layer.bukkit.api.events.player.*;
import com.craftfire.odin.layer.bukkit.api.events.plugin.OdinMessageEvent;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;

public class EventHandler {
    public static OdinPlayerQuitEvent onOdinPlayerQuit(OdinPlayer player) {
        OdinPlayerQuitEvent event = new OdinPlayerQuitEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static OdinPlayerKickEvent onOdinPlayerKick(OdinPlayer player, String message) {
        OdinPlayerKickEvent event = new OdinPlayerKickEvent(player, message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static OdinMessageEvent onOdinMessage(OdinPlayer player, String message) {
        OdinMessageEvent event = new OdinMessageEvent(player, message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static OdinPlayerLoginEvent onOdinPlayerLogin(OdinPlayer player) {
        OdinPlayerLoginEvent event = new OdinPlayerLoginEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static OdinPlayerLogoutEvent onOdinPlayerLogout(OdinPlayer player) {
        OdinPlayerLogoutEvent event = new OdinPlayerLogoutEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static OdinPlayerLinkEvent onOdinPlayerLink(OdinPlayer player, String name) {
        OdinPlayerLinkEvent event = new OdinPlayerLinkEvent(player, name);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static OdinPlayerUnlinkEvent onOdinPlayerUnlink(OdinPlayer player) {
        OdinPlayerUnlinkEvent event = new OdinPlayerUnlinkEvent(player, player.getLinkedName());
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
}
