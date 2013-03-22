/*
 * This file is part of Odin.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
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
import com.craftfire.odin.managers.OdinPermission;
import com.craftfire.odin.managers.OdinUser;

public class CommandLogout extends OdinBukkitCommand {

    public CommandLogout() {
        super("user.logout", OdinPermission.command_logout, "TODO");
    }

    @Override
    public void execute(OdinPlayer player, String[] args) {
        player.sendMessage("logout.processing");
        if (player.logout()) {
            player.sendMessage("logout.success");
            if (OdinManager.getConfig().getString("login.method").equalsIgnoreCase("prompt")) {
                player.sendMessage("login.prompt");
            } else {
                player.sendMessage("login.normal");
            }
        } else {
            player.sendMessage("logout.failure");
        }
    }
}
