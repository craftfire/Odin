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

import com.craftfire.odin.managers.OdinManager;

final class BukkitCommandManager {
    private BukkitCommandManager() {  }

    public static OdinBukkitCommand getCommand(String command) {
        String name = OdinManager.getCommands().getCommandName(command);
        if (name != null) {
            OdinBukkitCommand bukkitCommand = null;
            if (name.equalsIgnoreCase("user.link")) {
                bukkitCommand = new CommandLink();
            } if (name.equalsIgnoreCase("user.login")) {
                bukkitCommand = new CommandLogin();
            } else if (name.equalsIgnoreCase("user.logout")) {
                bukkitCommand = new CommandLogout();
            } else if (name.equalsIgnoreCase("user.register")) {
                bukkitCommand = new CommandRegister();
            } else if (name.equalsIgnoreCase("user.unlink")) {
                bukkitCommand = new CommandUnlink();
            } else if (name.equalsIgnoreCase("admin.activate")) {
                bukkitCommand = new CommandOdinActivate();
            } else if (name.equalsIgnoreCase("admin.deactivate")) {
                bukkitCommand = new CommandOdinDeactivate();
            } else if (name.equalsIgnoreCase("admin.login")) {
                bukkitCommand = new CommandOdinLogin();
            } else if (name.equalsIgnoreCase("admin.logout")) {
                bukkitCommand = new CommandOdinLogout();
            } else if (name.equalsIgnoreCase("admin.register")) {
                bukkitCommand = new CommandOdinRegister();
            } else if (name.equalsIgnoreCase("admin.users")) {
                bukkitCommand = new CommandOdinUsers();
            }
            if (bukkitCommand != null) {
                OdinManager.getLogger().debug("Found supported Odin Bukkit command: '" + command.toLowerCase()
                                            + "' (" + name.toLowerCase() + ").");
                return bukkitCommand;
            } else {
                OdinManager.getLogger().error("Found supported Odin Bukkit command: '" + command.toLowerCase()
                                            + "' (" + name.toLowerCase() + "), but it was not implemented into the "
                                            + "BukkitCommandManager.");
            }
        }
        OdinManager.getLogger().debug("The command: '" + command.toLowerCase() + "' is not supported by Odin.");
        return null;
    }
}
