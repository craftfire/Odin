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
package com.craftfire.odin.layer.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import com.craftfire.odin.layer.bukkit.Odin;
import com.craftfire.odin.layer.bukkit.commands.BukkitCommandManager;
import com.craftfire.odin.layer.bukkit.commands.OdinBukkitCommand;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.layer.bukkit.util.event.Event;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.permissions.OdinPermission;
import com.craftfire.odin.util.MainUtils;

public class OdinPlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        OdinPlayer player = Odin.getPlayer(event.getPlayer());
        if (!OdinManager.getDataManager().hasConnection() ) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    "You cannot join when the server has no database connection.");
            return;
        }

        if (OdinManager.getConfig().getBoolean("session.protect") && player.isLoggedIn()) {
            player.sendMessage("session.protected", event);
            return;
        } else {
            player.sync();
        }

        if (OdinManager.getConfig().getBoolean("join.restrict") && !player.isRegistered()) {
            player.sendMessage("join.restricted", event);
            return;
        }

        if (OdinManager.getConfig().getString("filter.action").equalsIgnoreCase("kick") && player.hasBadCharacters() &&
            !player.isFilterWhitelisted()) {
            OdinManager.getLogger().debug(player.getUsername() +
                                           " is not in the filter whitelist and has bad characters in his/her name.");
            player.sendMessage("filter.username", event);
            return;
        }

        if (!player.hasMinLength()) {
            player.sendMessage("username.minimum", event);
            return;
        } else if (! player.hasMaxLength()) {
            player.sendMessage("username.maximum", event);
            return;
        }

        if (OdinManager.getConfig().getBoolean("link.rename") && player.isLinked()) {
            player.setLinkedName();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean allow = false;
        final OdinPlayer player = Odin.getPlayer(event.getPlayer());
        player.setJoinTime();

        if (OdinManager.getConfig().getBoolean("link.rename") && player.isLinked()) {
            event.setJoinMessage(event.getJoinMessage().replaceAll(player.getName(), player.getDisplayName()));
        }

        player.clearPasswordAttempts();

        if (OdinManager.getConfig().getBoolean("session.enabled") &&
                OdinManager.getConfig().getInt("session.length") != 0) {
            if (player.hasSession()) {
                OdinManager.getLogger().debug("Found session for " + player.getName() + ", timestamp: " +
                        player.getSessionTime());
                long diff = System.currentTimeMillis() / 1000 - player.getSessionTime();
                OdinManager.getLogger().debug("Difference: " + diff);
                OdinManager.getLogger().debug("Session in config: " +
                                        MainUtils.getTimeUtil(OdinManager.getConfig().getString("session.length")).getSeconds());
                if (diff < MainUtils.getTimeUtil(OdinManager.getConfig().getString("session.length")).getSeconds()) {
                    allow = true;
                }
            }
        }

        if (!allow) {
            int time = 0;
            if (MainUtils.getTimeUtil(OdinManager.getConfig().getString("login.timeout")).getTicks() > 0 && player.isRegistered()) {
                time =  MainUtils.getTimeUtil(OdinManager.getConfig().getString("login.timeout")).getTicks();
                OdinManager.getLogger().debug("Login timeout time is: " + time + " ticks.");
            } else if (MainUtils.getTimeUtil(OdinManager.getConfig().getString("register.timeout")).getTicks() > 0 &&
                       !player.isRegistered()) {
                time =  MainUtils.getTimeUtil(OdinManager.getConfig().getString("register.timeout")).getTicks();
                OdinManager.getLogger().debug("Register timeout time is: " + time + " ticks.");
            }
            if (time > 0) {
                Odin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Odin.getInstance(),
                    new Runnable() {
                        @Override
                        public void run() {
                            player.checkTimeout();
                        }
                    }
                , time);
                player.setTimeout();
                OdinManager.getLogger().debug("Added timeout for " + player.getName() + ".");
            }
        }

        if (OdinManager.getConfig().getBoolean("customdb.enabled") && OdinManager.getConfig().getString("customdb.encryption").isEmpty()) {
            player.sendMessage("YOUR PASSWORD WILL NOT BE ENCRYPTED, " +
                               "PLEASE BE AWARE THAT THIS SERVER STORES THE PASSWORDS IN PLAINTEXT.");
        }

        if (OdinManager.getConfig().getBoolean("session.enabled") &&  (player.getReloadTime() + 30) >= (System.currentTimeMillis() / 1000)) {
            allow = true;
        }

        if (Odin.getInstance().getServer().getOnlineMode() && player.isRegistered()) {
            allow = true;
        }

        if (allow) {
            player.setAuthenticated(true);
            player.sendMessage("session.valid");
        } else if (player.isRegistered()) {
            player.storeInventory();
            Odin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Odin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (!player.isAuthenticated()) {
                        player.clearInventory();
                    }
                }
            } , 20);
            if (OdinManager.getConfig().getString("login.method").equalsIgnoreCase("prompt")) {
                player.sendMessage("login.prompt");
            } else {
                player.sendMessage("login.normal");
            }
        } else if (OdinManager.getConfig().getBoolean("register.force")) {
            player.storeInventory();
            player.sendMessage("register.welcome");
        } else if (!OdinManager.getConfig().getBoolean("register.force") && OdinManager.getConfig().getBoolean("register.enabled")) {
            player.sendMessage("register.welcome");
        } else {
            //TODOL: Authenticate?
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        OdinPlayer player = Odin.getPlayer(event.getPlayer());

        if (OdinManager.getConfig().getBoolean("link.rename") && player.isLinked()) {
            event.setQuitMessage(event.getQuitMessage().replaceAll(player.getName(), player.getDisplayName()));
        }

        if (OdinManager.getConfig().getBoolean("session.enabled") &&
                OdinManager.getConfig().getString("session.start").equalsIgnoreCase("logoff") &&
                player.isAuthenticated()) {
            // TODO
            /*this.plugin.Odin_Sessions.put(Encryption.md5(player.getName() + Util.craftFirePlayer.getIP(player)), thetimestamp);
            EBean EBeanClass = EBean.checkPlayer(player, true);
            EBeanClass.setSessiontime(thetimestamp);
            this.plugin.Odin_AuthTime.put(player.getName(), thetimestamp);*/
        }

        if (!player.hasPermissions(OdinPermission.guest_inventory) && !player.isRegistered()) {
            player.getInventory().setContents(new ItemStack[36]);
        }

        player.callEvent(Event.QUIT);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String[] split = event.getMessage().split(" ");
        OdinPlayer player = Odin.getPlayer(event.getPlayer());
        String command = split[0];

        if (OdinManager.getCommands().isCommand(event.getMessage())) {
            OdinBukkitCommand bukkitCommand = BukkitCommandManager.getCommand(event.getMessage());
            if (bukkitCommand == null) {
                OdinManager.getLogger().error("Something went wrong when initializing the '" + command + "' command.");
            } else if (!bukkitCommand.isPermitted(player)) {
                player.sendMessage("protection.denied");
            } else {
                String[] args;
                if (bukkitCommand.getCommand().matches(".*\\s+.*") && !event.getMessage().equals(bukkitCommand.getCommand())) {
                    args = event.getMessage().replace(bukkitCommand.getCommand() + " ", "").split(" ");
                } else {
                    args = event.getMessage().replace(bukkitCommand.getCommand(), "").split(" ");
                }
                if (args.length == 1 && args[0].isEmpty()) {
                    // First item in the array is empty, the array should have 0 items.
                    args = new String[0];
                }
                try {
                    bukkitCommand.execute(player, args);
                } catch (IllegalAccessException e) {
                    OdinManager.getLogger().error(e.getMessage());
                    OdinManager.getLogger().stackTrace(e);
                }
            }
            event.setMessage("******"); // TODO
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        OdinPlayer player = Odin.getPlayer(event.getPlayer());
        if (!player.isAuthenticated() && player.getJoinTime() != 0 && !player.hasPermissions(OdinPermission.guest_movement)) {
            if (OdinManager.getConfig().getBoolean("protection.freeze.enabled") &&
                    (player.getJoinTime() + OdinManager.getConfig().getInt("protection.freeze.delay")) <
                            System.currentTimeMillis() / 1000) {
                player.setJoinTime(0);
            }
            event.setTo(event.getFrom());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerEvent(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        OdinPlayer player = Odin.getPlayer(event.getPlayer());

        if (!player.isAuthenticated()) {
            if (OdinManager.getConfig().getString("login.method").equalsIgnoreCase("prompt")) {
                // TODO: Make sure this works
                if (player.isRegistered() && player.hasPermissions(OdinPermission.command_login)) {
                    String[] split = event.getMessage().split(" ");
                    player.sendMessage("login.processing");
                    if (split.length > 1) {
                        player.sendMessage("login.prompt");
                    } else if (player.login(split[0])) {
                        if(player.callEventResults(Event.LOGIN)) {
                            player.sendMessage("login.success");
                        }  else {
                            player.sendMessage("login.failure");
                        }
                    } else {
                        player.sendMessage("login.failure");
                    }
                    OdinManager.getLogger().debug(player.getName() + " login ********");
                    event.setMessage(" has logged in!");
                    event.setCancelled(true);
                }
            }

            if (player.isGuest() && !player.hasPermissions(OdinPermission.guest_chat)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        OdinPlayer player = Odin.getPlayer(event.getPlayer());
        if (!player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && !player.hasPermissions(OdinPermission.guest_interactions)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        OdinPlayer player =  Odin.getPlayer(event.getPlayer());
        if (!player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && !player.hasPermissions(OdinPermission.guest_pickup)) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        OdinPlayer player =  Odin.getPlayer(event.getPlayer());
        if (!player.isAuthenticated()) {
            if (player.isRegistered()) {
                event.setCancelled(true);
            } else if (player.isGuest() && !player.hasPermissions(OdinPermission.guest_drop)) {
                event.setCancelled(true);
            }
        }
    }
}
