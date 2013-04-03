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
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.encryption.Encryption;
import com.craftfire.commons.util.Util;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.permissions.OdinPermission;

import java.util.Arrays;

public class CommandPassword extends OdinBukkitCommand {
    public CommandPassword() {
        super("user.password", OdinPermission.command_password, "TODO");
    }

    @Override
    public void execute(OdinPlayer player, String[] args) {
        player.sendMessage("password.processing");
        if (preCheck(player, args)) {
            if (player.getTempPassword() == null) {
                player.setTempPassword(args[0]);
                player.sendMessage("password.retype");
            } else {
                player.setPassword(args[0]);
                player.sendMessage("password.success");
                player.setTempPassword(null);
            }
        }
    }

    private boolean preCheck(OdinPlayer player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("password.usage");
            return false;
        } else if (!player.isRegistered()) {
            player.sendMessage("general.notregistered");
            return false;
        } else if (player.getTempPassword() != null
                && !player.getTempPassword().equalsIgnoreCase(CraftCommons.encrypt(Encryption.MD5, args[0]))) {
            player.sendMessage("password.nomatch");
            return false;
        } else try {
            if (player.getPassword().equalsIgnoreCase(OdinManager.getScript().hashPassword(player.getName(), args[0]))) {
                player.sendMessage("password.duplicate");
                return false;
            }
        } catch (ScriptException e) {
            player.sendMessage("password.failure");
            OdinManager.getLogger().error("Failed hashing password for user '" + player.getName() + "'.");
            OdinManager.getLogger().stackTrace(e);
            return false;
        }
        return true;
    }
}
