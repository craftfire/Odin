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
package com.craftfire.authdb.bukkit.listeners;

import com.craftfire.authdb.authdb.managers.AuthDBManager;
import com.craftfire.authdb.bukkit.AuthDBPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class AuthDBPlayerListener implements Listener {
    private AuthDBPlugin plugin;

    public AuthDBPlayerListener(AuthDBPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (! AuthDBManager.dataManager.isConnected()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                           "You cannot join when the server has no database connection.");
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    @EventHandler
     public void onPlayerPickupItem(PlayerPickupItemEvent event) {

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent event) {

    }

}
