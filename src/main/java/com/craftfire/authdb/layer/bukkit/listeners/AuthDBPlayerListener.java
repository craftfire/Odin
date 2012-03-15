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
package com.craftfire.authdb.layer.bukkit.listeners;

import com.craftfire.authdb.managers.AuthDBManager;
import com.craftfire.authdb.managers.configuration.ConfigurationNode;
import com.craftfire.authdb.layer.bukkit.AuthDB;
import com.craftfire.authdb.layer.bukkit.managers.AuthDBPlayer;
import com.craftfire.authdb.layer.bukkit.util.AuthDBUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class AuthDBPlayerListener implements Listener {
    private AuthDB plugin;

    public AuthDBPlayerListener(AuthDB plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        AuthDBPlayer player =  AuthDBUtil.getPlayer(event.getPlayer());
        if (! AuthDBManager.dataManager.isConnected()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                           "You cannot join when the server has no database connection.");
            return;
        }

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.session_protect) && player.hasSession()) {
            /* TODO */
        }

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.join_restrict) && ! player.isRegistered()) {
            /* TODO */
        }

        if (AuthDBManager.cfgMngr.getString(ConfigurationNode.filter_action).equalsIgnoreCase("kick") ||
            AuthDBManager.cfgMngr.getString(ConfigurationNode.filter_action).equalsIgnoreCase("rename")) {
            /* TODO */
        }

        if (! player.hasMinLength()) {
            /* TODO */
        } else if (! player.hasMaxLength()) {
            /* TODO */
        }

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.link_rename) && player.isLinked()) {
            player.setLinkedName();
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
