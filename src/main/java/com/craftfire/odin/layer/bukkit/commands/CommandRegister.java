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

import java.sql.SQLException;

import com.craftfire.bifrost.classes.general.ScriptUser;
import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.commons.util.Util;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.permissions.OdinPermission;

public class CommandRegister extends OdinBukkitCommand {
    public CommandRegister() {
        super("user.register", OdinPermission.command_register, "TODO");
    }

    @Override
    public void execute(OdinPlayer player, String[] args) {
        player.sendMessage("register.processing");
        // TODO: Do something with this
        /*Boolean email = true;
        if (Config.custom_enabled) {
            email = Config.custom_emailrequired;
            if (Config.custom_emailfield == null || Config.custom_emailfield.equals("")) {
                email = false;
            }
        }*/
        if (preCheck(player, args, true)) { // TODO: Email check
            ScriptUser user = OdinManager.getScript().newScriptUser(player.getUsername(), args[0]);
            // TODO: Make it so people have to activate their account
            user.setActivated(OdinManager.getConfig().getBoolean("register.activate"));
            user.setEmail(args[1]);
            user.setRegIP(player.getIP().toIPv4().toString());
            try {
                OdinManager.getScript().createUser(user);
                player.login();
                player.sendMessage("register.success");
            } catch (SQLException e) {
                player.sendMessage("register.failure");
                OdinManager.getLogger().error("Could not register user '" + player.getUsername()
                                            + "' due to error: " + e.getMessage());
                OdinManager.getLogger().stackTrace(e);
            } catch (ScriptException e) {
                player.sendMessage("register.failure");
                OdinManager.getLogger().error("Could not register user '" + player.getUsername()
                                            + "' due to error: " + e.getMessage());
                OdinManager.getLogger().stackTrace(e);
            }
            OdinManager.getLogger().debug(player.getName() + " register ********");
        }
    }

    private boolean preCheck(OdinPlayer player, String[] args, boolean emailRequired) {
        if (OdinManager.getConfig().getBoolean("join.restrict") || !OdinManager.getConfig().getBoolean("register.enabled")) {
            player.sendMessage("register.disabled");
            return false;
        } else if (player.isRegistered()) {
            player.sendMessage("register.exists");
            return false;
        } else if (args.length < 1) {
            player.sendMessage("register.usage");
            return false;
        } else if (args.length < 2 && emailRequired) {
            player.sendMessage("email.required");
            return false;
        } else if ((args.length >= 2 && emailRequired) && (!Util.isEmail(args[1]))) {
            player.sendMessage("email.invalid");
            return false;
        }

        // TODO: Register IP limit
        /*if (Config.register_limit > 0
            && EBean.getAmount("ip", player.getAddress().getAddress().toString().substring(1)) > Config.register_limit) {
            Messages.sendMessage(Message.register_limit, player, null);
        }*/
        return args.length >= 2 || (!emailRequired && args.length >= 1);
    }
}
