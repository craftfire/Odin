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
import com.craftfire.odin.managers.permissions.OdinPermission;

public class CommandEmail extends OdinBukkitCommand {
    public CommandEmail() {
        super("user.email", OdinPermission.command_email, "TODO");
    }

    @Override
    public void execute(OdinPlayer player, String[] args) {
        if (preCheck(player, args)) {
        }
    }

    private boolean preCheck(OdinPlayer player, String[] args) {
        // TODO
        return false;
    }
}
