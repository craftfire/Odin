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

import com.craftfire.odin.managers.OdinManager;

public class BukkitCommandManager {
    public static OdinBukkitCommand getCommand(String command) {
        String name = OdinManager.getCommands().getCommandName(command);
        if (name != null) {
            if (name.equalsIgnoreCase("link")) {
                OdinManager.getLogger().debug("Found supported Odin Bukkit command: '" + command.toLowerCase() + "'.");
                return new CommandLink();
            } if (name.equalsIgnoreCase("login")) {
                OdinManager.getLogger().debug("Found supported Odin Bukkit command: '" + command.toLowerCase() + "'.");
                return new CommandLogin();
            } else if (name.equalsIgnoreCase("logout")) {
                OdinManager.getLogger().debug("Found supported Odin Bukkit command: '" + command.toLowerCase() + "'.");
                return new CommandLogout();
            } else if (name.equalsIgnoreCase("register")) {
                OdinManager.getLogger().debug("Found supported Odin Bukkit command: '" + command.toLowerCase() + "'.");
                return new CommandRegister();
            }
        }
        OdinManager.getLogger().debug("The command: '" + command.toLowerCase() + "' is not supported by Odin.");
        return null;
    }
}
