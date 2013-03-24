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

import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinPermission;

public class CommandUnlink extends OdinBukkitCommand {
    //TODO

    public CommandUnlink() {
        super("user.unlink", OdinPermission.command_unlink, "TODO");
    }

    @Override
    public void execute(OdinPlayer player, String[] args) {
        player.sendMessage("unlink.processing");
        if (preCheck(player, args)) {
            player.unlink();
            player.sendMessage("unlink.success");
        }
    }

    private boolean preCheck(OdinPlayer player, String[] args) {
        try {
            if (args.length != 2) {
                player.sendMessage("unlink.usage");
                return false;
            } else if (!player.isLinked()) {
                player.sendMessage("unlink.nonexist");
                return false;
            } else if (!args[0].equals(player.getLinkedName())) {
                player.sendMessage("unlink.invaliduser");
                return false;
            } else if (!OdinManager.getScript().authenticate(args[0], args[1])) {
                player.sendMessage("unlink.invalidpass");
                return false;
            }
        } catch (ScriptException e) {
            player.sendMessage("unlink.failure");
            OdinManager.getLogger().error("Failed to authenticate unlink '" + player.getName()
                                        + "' with linked name '" + player.getLinkedName() + "'.");
            OdinManager.getLogger().stackTrace(e);
            return false;
        }
        return true;
    }
}