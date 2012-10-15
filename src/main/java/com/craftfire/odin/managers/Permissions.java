/*
 * This file is part of Odin <http://www.odin.com/>.
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
package com.craftfire.odin.managers;

import com.craftfire.odin.managers.OdinManager;

public enum Permissions {

    command_register (getUserNode() + "register"),
    command_unregister (getUserNode() + "unregister"),
    command_login (getUserNode() + "login"),
    command_logout (getUserNode() + "logout"),
    command_link (getUserNode() + "link"),
    command_unlink (getUserNode() + "unlink"),
    command_password (getUserNode() + "password"),
    command_email (getUserNode() + "email"),
    command_users (getUserNode() + "users"),
    command_admin_unregister (getAdminNode() + "unregister"),
    command_admin_login (getAdminNode() + "login"),
    command_admin_logout (getAdminNode() + "logout"),
    command_admin_password (getAdminNode() + "password"),
    command_admin_reload (getAdminNode() + "reload");

    public String permission;
    Permissions(String permission) {
        this.permission = permission;
    }

    public static String getUserNode() {
        return OdinManager.getPluginName() + ".command.user.";
    }

    public static String getAdminNode() {
        return OdinManager.getPluginName() + ".command.admin.";
    }
}