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
package com.craftfire.odin.managers;

import com.craftfire.odin.util.PermissionType;

public enum OdinPermission {
    command_register (PermissionType.USER, "register"),
    command_unregister (PermissionType.USER, "unregister"),
    command_login (PermissionType.USER, "login"),
    command_logout (PermissionType.USER, "logout"),
    command_link (PermissionType.USER, "link"),
    command_unlink (PermissionType.USER, "unlink"),
    command_password (PermissionType.USER, "password"),
    command_email (PermissionType.USER, "email"),
    command_users (PermissionType.USER, "users"),
    command_admin_unregister (PermissionType.ADMIN, "unregister"),
    command_admin_login (PermissionType.ADMIN, "login"),
    command_admin_logout (PermissionType.ADMIN, "logout"),
    command_admin_password (PermissionType.ADMIN, "password"),
    command_admin_reload (PermissionType.ADMIN, "reload");

    private PermissionType type;
    private String permission;
    OdinPermission(PermissionType type, String permission) {
        this.type = type;
        this.permission = permission;
    }

    public String getNode() {
        return (isUserNode() ? getUserNode() + this.permission : getAdminNode() + this.permission);
    }

    public boolean isUserNode() {
        return getType().equals(PermissionType.USER);
    }

    public boolean isAdminNode() {
        return getType().equals(PermissionType.ADMIN);
    }

    public PermissionType getType() {
        return this.type;
    }

    public String getPermission() {
        return this.permission;
    }

    public static String getUserNode() {
        return OdinManager.getPluginName() + ".command.user.";
    }

    public static String getAdminNode() {
        return OdinManager.getPluginName() + ".command.admin.";
    }

    public static String getNode(PermissionType type) {
        return OdinManager.getPluginName() + ".command." + type.name();
    }
}