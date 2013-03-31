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
package com.craftfire.odin.layer.bukkit.commands;

import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.layer.bukkit.util.event.Event;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.permissions.OdinPermission;

// TODO: check if account is activated, do not login if not activated.
public class CommandLogin extends OdinBukkitCommand {
    public CommandLogin() {
        super("user.login", OdinPermission.command_login, "TODO");
    }

    @Override
    public void execute(OdinPlayer player, String[] args) {
        player.sendMessage("login.processing");
        if (preCheck(player, args)) {
            if (player.login(args[0])) {
                // TODO: Make sure this works.
                if (player.callEventResults(Event.LOGIN)) {
                    player.sendMessage("login.success");
                }  else {
                    player.sendMessage("login.failure");
                }
            } else {
                if (!OdinManager.getDataManager().hasConnection()) {
                    player.sendMessage("login.offline");
                } else {
                    player.sendMessage("login.failure");
                }
            }
            OdinManager.getLogger().debug(player.getName() + " login ********");
        }
    }

    private boolean preCheck(OdinPlayer player, String[] args) {
        if (player.isAuthenticated()) {
            player.sendMessage("login.authorized");
            return false;
        } else if (!player.isRegistered()) {
            player.sendMessage("general.notregistered");
            return false;
        } else if (args.length < 1) {
            player.sendMessage("login.usage");
            return false;
        }
        return true;
    }
}
