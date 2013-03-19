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
package com.craftfire.odin.managers.commands;

import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinPermission;

public abstract class OdinCommand {
    protected String commandName, alias;
    private OdinPermission permission;
    private String description, usage, usageNode;

    public OdinCommand(String commandName, OdinPermission permission, String description) {
        this.commandName = commandName.toLowerCase();
        this.alias = OdinManager.getCommands().getAlias(commandName.toLowerCase());
        this.permission = permission;
        this.description = description;
        this.usageNode = commandName.toLowerCase() + ".usage";
        this.usage = OdinManager.getMessages().getMessage(this.usageNode);
    }

    public String getName() {
        return this.commandName;
    }

    public String getAlias() {
        return this.alias;
    }

    public OdinPermission getPermission() {
        return this.permission;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getUsageNode() {
        return this.usageNode;
    }
}
