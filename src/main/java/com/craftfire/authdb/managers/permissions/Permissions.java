/*
 * This file is part of AuthDB <http://www.authdb.com/>.
 *
 * AuthDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.authdb.managers.permissions;

public enum Permissions {
    command_register (PermissionsManager.userPermissions + "register"),
    command_unregister (PermissionsManager.userPermissions + "unregister"),
    command_login (PermissionsManager.userPermissions + "login"),
    command_logout (PermissionsManager.userPermissions + "logout"),
    command_link (PermissionsManager.userPermissions + "link"),
    command_unlink (PermissionsManager.userPermissions + "unlink"),
    command_password (PermissionsManager.userPermissions + "password"),
    command_email (PermissionsManager.userPermissions + "email"),
    command_users (PermissionsManager.userPermissions + "users"),
    command_admin_unregister (PermissionsManager.adminPermissions + "unregister"),
    command_admin_login (PermissionsManager.adminPermissions + "login"),
    command_admin_logout (PermissionsManager.adminPermissions + "logout"),
    command_admin_password (PermissionsManager.adminPermissions + "password"),
    command_admin_reload (PermissionsManager.adminPermissions + "reload");

    public String permission;
    Permissions(String permission) {
        this.permission = permission;
    }
}