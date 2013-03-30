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

import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.permissions.OdinPermission;

public class CommandLink extends OdinBukkitCommand {
    public CommandLink() {
        super("user.link", OdinPermission.command_link, "TODO");
    }

    @Override
    public void execute(OdinPlayer player, String[] args) {
        player.sendMessage("link.processing");
        if (preCheck(player, args)) {
            player.link(args[0]);
            player.sendMessage("link.success");
            OdinManager.getLogger().debug(player.getName() + " link ******** ********");
        }
    }

    private boolean preCheck(OdinPlayer player, String[] args) {
        try {
            if (OdinManager.getConfig().getBoolean("join.restrict") || !OdinManager.getConfig().getBoolean("link.enabled")) {
                player.sendMessage("link.disabled");
                return false;
            } else if (args.length != 2) {
                player.sendMessage("link.usage");
                return false;
            } else if (player.getName().equals(args[0])) {
                player.sendMessage("link.invaliduser");
                return false;
            } else if (player.isRegistered()) {
                player.sendMessage("link.registered");
                return false;
            } else if (OdinManager.getStorage().hasLinkedUsername(player.getUsername())) {
                player.sendMessage("link.exists");
                return false;
            } else if (OdinManager.getStorage().isLinkedUsername(args[0])) {
                player.sendMessage("link.duplicate");
                return false;
            } else if (!OdinManager.getScript().authenticate(args[0], args[1])) {
                player.sendMessage("link.invalidpass");
                return false;
            }
        } catch (ScriptException e) {
            player.sendMessage("link.failure");
            OdinManager.getLogger().error("Failed to authenticate unlink '" + player.getName()
                                        + "' with linked name '" + player.getLinkedName() + "'.");
            OdinManager.getLogger().stackTrace(e);
            return false;
        }
        return true;
    }
}
