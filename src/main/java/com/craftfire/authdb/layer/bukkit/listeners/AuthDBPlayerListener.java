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
import com.craftfire.authdb.layer.bukkit.util.Event;
import com.craftfire.authdb.layer.bukkit.util.Util;
import com.craftfire.authdb.managers.AuthDBManager;
import com.craftfire.authdb.managers.permissions.Permissions;
import com.craftfire.authdb.util.MainUtils;
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
        AuthDBPlayer player =  Util.getPlayer(event.getPlayer());
        if (! AuthDBManager.dataManager.isConnected()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    "You cannot join when the server has no database connection.");
            return;
        }

        if (AuthDBManager.cfgMgr.getBoolean("session.protect") && player.hasSession()) {
            player.sendMessage("session.protected", event);
            return;
        }

        if (AuthDBManager.cfgMgr.getBoolean("join.restrict") && ! player.isRegistered()) {
            player.sendMessage("join.restricted", event);
            return;
        }

        if (AuthDBManager.cfgMgr.getString("filter.action").equalsIgnoreCase("kick") && player.hasBadCharacters() &&
            ! player.isFilterWhitelisted()) {
            AuthDBManager.logMgr.debug(player.getUsername() +
                                           " is not in the filter whitelist and has bad characters in his/her name.");
            player.sendMessage("filter.username", event);
            return;
        }

        if (! player.hasMinLength()) {
            player.sendMessage("username.minimum", event);
            return;
        } else if (! player.hasMaxLength()) {
            player.sendMessage("username.maximum", event);
            return;
        }

        if (AuthDBManager.cfgMgr.getBoolean("link.rename") && player.isLinked()) {
            player.setLinkedName();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean allow = false;
        final AuthDBPlayer player =  Util.getPlayer(event.getPlayer());
        player.setJoinTime();

        if (AuthDBManager.cfgMgr.getBoolean("link.rename") && player.isLinked()) {
            event.setJoinMessage(event.getJoinMessage().replaceAll(player.getName(), player.getDisplayName()));
        }

        player.clearPasswordAttempts();

        if (AuthDBManager.cfgMgr.getBoolean("session.enabled") &&
                AuthDBManager.cfgMgr.getInteger("session.length") != 0) {
            if (player.hasSession()) {
                AuthDBManager.logMgr.debug("Found session for " + player.getName() + ", timestamp: " +
                        player.getSessionTime());
                long diff = System.currentTimeMillis() / 1000 - player.getSessionTime();
                AuthDBManager.logMgr.debug("Difference: " + diff);
                AuthDBManager.logMgr.debug("Session in config: " +
                                        MainUtils.stringToSeconds(AuthDBManager.cfgMgr.getString("session.length")));
                if (diff < MainUtils.stringToSeconds(AuthDBManager.cfgMgr.getString("session.length"))) {
                    allow = true;
                }
            }
        }

        if (! allow) {
            int time = 0;
            if (MainUtils.stringToTicks(AuthDBManager.cfgMgr.getString("login.timeout")) > 0 && player.isRegistered()) {
                time =  MainUtils.stringToTicks(AuthDBManager.cfgMgr.getString("login.timeout"));
                AuthDBManager.logMgr.debug("Login timeout time is: " + time + " ticks.");
            } else if (MainUtils.stringToTicks(AuthDBManager.cfgMgr.getString("register.timeout")) > 0 &&
                       ! player.isRegistered()) {
                time =  MainUtils.stringToTicks(AuthDBManager.cfgMgr.getString("register.timeout"));
                AuthDBManager.logMgr.debug("Register timeout time is: " + time + " ticks.");
            }
            if (time > 0) {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.checkTimeout();
                    }
                }, time);
                player.setTimeout();
                AuthDBManager.logMgr.debug("Added timeout for " + player.getName() + ".");
            }
        }

        if (AuthDBManager.cfgMgr.getBoolean("customdb.enabled") &&
                AuthDBManager.cfgMgr.getString("customdb.encryption").isEmpty()) {
            player.sendMessage("§4YOUR PASSWORD WILL NOT BE ENCRYPTED," +
                               "PLEASE BE ADWARE THAT THIS SERVER STORES THE PASSWORDS IN PLAINTEXT.");
        }

        if (AuthDBManager.cfgMgr.getBoolean("session.enabled") /* TODO: Reload time*/) {
            allow = true;
        }

        if (AuthDB.instance.getServer().getOnlineMode() && player.isRegistered()) {
            allow = true;
        }

        if (allow) {
            player.setAuthenticated(true);
            /* TODO */
        } else if (player.isRegistered()) {
            player.storeInventory();
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (! player.isAuthenticated()) {
                        player.clearInventory();
                    }
                }
            } , 20);
            if (AuthDBManager.cfgMgr.getString("login.method").equalsIgnoreCase("prompt")) {
                player.sendMessage("login.prompt");
            } else {
                player.sendMessage("login.normal");
            }
        } else if (AuthDBManager.cfgMgr.getBoolean("register.force")) {
            player.storeInventory();
            player.sendMessage("register.welcome");
        } else if (! AuthDBManager.cfgMgr.getBoolean("register.force") &&
                   AuthDBManager.cfgMgr.getBoolean("register.enabled")) {
            player.sendMessage("register.welcome");
        } else {
            //Authenticate?
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        AuthDBPlayer player =  Util.getPlayer(event.getPlayer());

        if (AuthDBManager.cfgMgr.getBoolean("link.rename") && player.isLinked()) {
            event.setQuitMessage(event.getQuitMessage().replaceAll(player.getName(), player.getDisplayName()));
        }

        if (AuthDBManager.cfgMgr.getBoolean("session.enabled") &&
                AuthDBManager.cfgMgr.getString("session.start").equalsIgnoreCase("logoff") &&
                player.isAuthenticated()) {
            /* TODO */
            //this.plugin.AuthDB_Sessions.put(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)), thetimestamp);
            //EBean EBeanClass = EBean.checkPlayer(player, true);
           // EBeanClass.setSessiontime(thetimestamp);
            //this.plugin.AuthDB_AuthTime.put(player.getName(), thetimestamp);
        }

        if (! AuthDBManager.cfgMgr.getBoolean("guest.inventory") && ! player.isRegistered()) {
            player.getInventory().setContents(new ItemStack[36]);
        }

        player.callEvent(Event.QUIT);
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

        AuthDBPlayer player =  Util.getPlayer(event.getPlayer());

        /* TODO */
        if (! player.isAuthenticated() &&
                player.getJoinTime() != 0 &&
                ! AuthDBManager.cfgMgr.getBoolean("guest.movement")) {
            if (AuthDBManager.cfgMgr.getBoolean("protection.freeze.enabled") &&
                    (player.getJoinTime() + AuthDBManager.cfgMgr.getInteger("protection.freeze.delay")) <
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

        AuthDBPlayer player =  Util.getPlayer(event.getPlayer());

        if (! player.isAuthenticated()) {
            if (AuthDBManager.cfgMgr.getString("login.method").equalsIgnoreCase("prompt")) {
                if (player.isRegistered() && player.hasPermissions(Permissions.command_login)) {
                    /* TODO */
                }
            }

            if (player.isGuest() && ! AuthDBManager.cfgMgr.getBoolean("guest.chat")) {
                event.setCancelled(true);
            }
        }

        /* TODO */
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        AuthDBPlayer player =  Util.getPlayer(event.getPlayer());
        if (! player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && ! AuthDBManager.cfgMgr.getBoolean("guest.interactions")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        AuthDBPlayer player =  Util.getPlayer(event.getPlayer());
        if (! player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && ! AuthDBManager.cfgMgr.getBoolean("guest.pickup")) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        AuthDBPlayer player =  Util.getPlayer(event.getPlayer());
        if (! player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && ! AuthDBManager.cfgMgr.getBoolean("guest.drop")) {
                event.setCancelled(true);
            }
        }
    }
}