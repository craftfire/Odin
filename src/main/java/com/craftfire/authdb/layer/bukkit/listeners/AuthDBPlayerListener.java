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

import com.craftfire.authdb.layer.bukkit.AuthDB;
import com.craftfire.authdb.layer.bukkit.managers.AuthDBPlayer;
import com.craftfire.authdb.layer.bukkit.util.AuthDBUtil;
import com.craftfire.authdb.managers.AuthDBManager;
import com.craftfire.authdb.managers.configuration.ConfigurationNode;
import com.craftfire.authdb.managers.permissions.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

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
        boolean allow = false;
        AuthDBPlayer player =  AuthDBUtil.getPlayer(event.getPlayer());
        player.setJoinTime();

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.link_rename) && player.isLinked()) {
            /* TODO */
        }

        player.clearPasswordAttempts();

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.session_enabled) &&
            AuthDBManager.cfgMngr.getInteger(ConfigurationNode.session_length) != 0) {
            if (player.hasSession()) {
                /* TODO */
            }
        }

        if (! allow) {
            /* TODO */
        }

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.customdb_enabled) &&
            AuthDBManager.cfgMngr.getString(ConfigurationNode.customdb_encryption).isEmpty()) {
            /* TODO */
        }

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.session_enabled) /* TODO */) {
            allow = true;
        }

        if (AuthDB.instance.getServer().getOnlineMode() && player.isRegistered()) {
            allow = true;
        }

        if (allow) {
            player.setAuthenticated(true);
            /* TODO */
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        AuthDBPlayer player =  AuthDBUtil.getPlayer(event.getPlayer());

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.link_rename) && player.isLinked()) {
            /* TODO */
        }

        if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.session_enabled) &&
            AuthDBManager.cfgMngr.getString(ConfigurationNode.session_start).equalsIgnoreCase("logoff") &&
            player.isAuthenticated()) {
            /* TODO */
        }

        if (! AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.guest_inventory) && ! player.isRegistered()) {
            player.getInventory().setContents(new ItemStack[36]);
        }

        /* TODO */
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        /* TODO */
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        AuthDBPlayer player =  AuthDBUtil.getPlayer(event.getPlayer());

        /* TODO */
        if (! player.isAuthenticated() &&
            player.getJoinTime() != 0 &&
            ! AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.guest_movement)) {
            if (AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.protection_freeze_enabled) &&
               (player.getJoinTime() + AuthDBManager.cfgMngr.getInteger(ConfigurationNode.protection_freeze_delay)) <
               System.currentTimeMillis() / 1000) {
               player.setJoinTime(0);
            }
            event.setTo(event.getFrom());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        AuthDBPlayer player =  AuthDBUtil.getPlayer(event.getPlayer());

        if (! player.isAuthenticated()) {
            if (AuthDBManager.cfgMngr.getString(ConfigurationNode.login_method).equalsIgnoreCase("prompt")) {
                if (player.isRegistered() && player.hasPermissions(Permission.command_login)) {
                    /* TODO */
                }
            }

            if (player.isGuest() && ! AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.guest_chat)) {
                event.setCancelled(true);
            }
        }

        /* TODO */
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        AuthDBPlayer player =  AuthDBUtil.getPlayer(event.getPlayer());
        if (! player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && ! AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.guest_interactions)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
     public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        AuthDBPlayer player =  AuthDBUtil.getPlayer(event.getPlayer());
        if (! player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && ! AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.guest_pickup)) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        AuthDBPlayer player =  AuthDBUtil.getPlayer(event.getPlayer());
        if (! player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && ! AuthDBManager.cfgMngr.getBoolean(ConfigurationNode.guest_drop)) {
                event.setCancelled(true);
            }
        }
    }
}
