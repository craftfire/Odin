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
import java.util.List;

import org.bukkit.entity.Player;

import com.craftfire.bifrost.classes.general.ScriptUser;
import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.commons.util.Util;
import com.craftfire.odin.layer.bukkit.Odin;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinPermission;

public class CommandOdinRegister extends OdinBukkitCommand {
    public CommandOdinRegister() {
        super("admin.register", OdinPermission.command_admin_register, "TODO");
    }

    @Override
    public void execute(OdinPlayer adminPlayer, String[] args) {
        // TODO: email check
        if (preCheck(adminPlayer, args, true)) {
            ScriptUser user = OdinManager.getScript().newScriptUser(args[0], args[1]);
            // TODO: Make it so people have to activate their account
            user.setActivated(OdinManager.getConfig().getBoolean("register.activate"));
            user.setEmail(args[2]);
            //user.setRegIP(player.getIP().toIPv4().toString());
            List<Player> craftPlayer = Odin.getInstance().getServer().matchPlayer(args[0]);
            try {
                OdinManager.getScript().createUser(user);
                if (!Odin.getInstance().getServer().matchPlayer(args[0]).isEmpty()) {
                    OdinPlayer player = Odin.getPlayer(Odin.getInstance().getServer().matchPlayer(args[0]).get(0));
                    player.login();
                    player.sendMessage("register.admin");
                }
                adminPlayer.sendMessage("register.adminsuccess");
            } catch (SQLException e) {
                adminPlayer.sendMessage("register.adminfailure");
                OdinManager.getLogger().error("Could not register user '" + args[0]
                                            + "' due to error: " + e.getMessage());
                OdinManager.getLogger().stackTrace(e);
            } catch (ScriptException e) {
                adminPlayer.sendMessage("register.adminfailure");
                OdinManager.getLogger().error("Could not register user '" + args[0]
                                             + "' due to error: " + e.getMessage());
                OdinManager.getLogger().stackTrace(e);
            }
            //OdinManager.getLogger().debug(player.getName() + " register ********");
        }
    }

    private boolean preCheck(OdinPlayer adminPlayer, String[] args, boolean emailRequired) {
        try {
            if (OdinManager.getConfig().getBoolean("join.restrict") || !OdinManager.getConfig().getBoolean("register.enabled")) {
                adminPlayer.sendMessage("register.disabled");
                return false;
            } else if (args.length < 2) {
                adminPlayer.sendMessage("register.adminusage");
                return false;
            } else if (args.length < 3 && emailRequired) {
                adminPlayer.sendMessage("email.required");
                return false;
            } else if ((args.length >= 3 && emailRequired) && (!Util.isEmail(args[2]))) {
                adminPlayer.sendMessage("email.invalid");
                return false;
            } else if (OdinManager.getScript().isRegistered(args[0])) {
                adminPlayer.sendMessage("register.adminfailure");
                return false;
            }
        } catch (ScriptException e) {
            adminPlayer.sendMessage("register.failure");
            OdinManager.getLogger().error("Failed to register username '" + args[0] + "' with email '" + args[2] + "'.");
            OdinManager.getLogger().stackTrace(e);
        }

        // TODO: Register IP limit
        /*if (Config.register_limit > 0
            && EBean.getAmount("ip", player.getAddress().getAddress().toString().substring(1)) > Config.register_limit) {
            Messages.sendMessage(Message.register_limit, player, null);
        }*/
        return args.length >= 3 || (!emailRequired && args.length >= 2);
    }
}
