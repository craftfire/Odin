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

import com.craftfire.odin.layer.bukkit.Odin;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.managers.OdinPermission;

public class CommandOdinDeactivate extends OdinBukkitCommand {

    public CommandOdinDeactivate() {
        super("admin.deactivate", OdinPermission.command_admin_deactivate, "TODO");
    }

    @Override
    public void execute(OdinPlayer adminPlayer, String[] args) {
        adminPlayer.sendMessage("deactivate.processing");
        if (preCheck(adminPlayer, args)) {
            OdinPlayer player = Odin.getPlayer(Odin.getInstance().getServer().matchPlayer(args[0]).get(0));
            player.deactivate();
            adminPlayer.sendMessage("deactivate.adminsuccess");
            player.sendMessage("deactivate.admin");
        }
    }

    private boolean preCheck(OdinPlayer adminPlayer, String[] args) {
        if (args.length != 1) {
            adminPlayer.sendMessage("deactivate.adminusage");
            return false;
        } else if (Odin.getInstance().getServer().matchPlayer(args[0]).isEmpty()) {
            adminPlayer.sendMessage("deactivate.adminnotfound");
            return false;
        }
        OdinPlayer player = Odin.getPlayer(Odin.getInstance().getServer().matchPlayer(args[0]).get(0));
        if (!player.isActivated()) {
            adminPlayer.sendMessage("deactivate.adminfailure");
            return false;
        }
        return true;
    }
}