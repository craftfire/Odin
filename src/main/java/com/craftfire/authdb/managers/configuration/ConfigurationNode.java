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
package com.craftfire.authdb.managers.configuration;

public enum ConfigurationNode {
    plugin_language_commands (ConfigurationFile.basic, "plugin.language.commands"),
    plugin_language_messages (ConfigurationFile.basic, "plugin.language.messages"),
    plugin_debugmode (ConfigurationFile.basic, "plugin.debugmode"),
    plugin_usagestats (ConfigurationFile.basic, "plugin.usagestats"),
    plugin_logging (ConfigurationFile.basic, "plugin.logging"),
    plugin_logformat (ConfigurationFile.basic, "plugin.logformat"),

    database_type (ConfigurationFile.basic, "database.type"),
    database_host (ConfigurationFile.basic, "database.host"),
    database_port (ConfigurationFile.basic, "database.port"),
    database_username (ConfigurationFile.basic, "database.username"),
    database_password (ConfigurationFile.basic, "database.password"),
    database_name (ConfigurationFile.basic, "database.name"),
    database_keepalive (ConfigurationFile.basic, "database.keepalive"),

    script_name (ConfigurationFile.basic, "script.name"),
    script_version (ConfigurationFile.basic, "script.version"),
    script_tableprefix (ConfigurationFile.basic, "script.tableprefix"),

    customdb_enabled (ConfigurationFile.advanced, "customdb.enabled"),
    customdb_autocreate (ConfigurationFile.advanced, "customdb.autocreate"),
    customdb_table (ConfigurationFile.advanced, "customdb.table"),
    customdb_userfield (ConfigurationFile.advanced, "customdb.userfield"),
    customdb_passfield (ConfigurationFile.advanced, "customdb.passfield"),
    customdb_emailfield (ConfigurationFile.advanced, "customdb.emailfield"),
    customdb_emailrequired (ConfigurationFile.advanced, "customdb.emailrequired"),
    customdb_encryption (ConfigurationFile.advanced, "customdb.encryption"),

    join_restrict (ConfigurationFile.advanced, "session.restrict"),

    register_enabled (ConfigurationFile.advanced, "register.enabled"),
    register_force (ConfigurationFile.advanced, "register.force"),
    register_delay (ConfigurationFile.advanced, "register.delay"),
    register_timeout (ConfigurationFile.advanced, "register.timeout"),

    login_enabled (ConfigurationFile.advanced, "login.enabled"),
    login_method (ConfigurationFile.advanced, "login.method"),
    login_delay (ConfigurationFile.advanced, "login.delay"),
    login_timeout (ConfigurationFile.advanced, "login.timeout"),
    login_tries (ConfigurationFile.advanced, "login.tries"),
    login_action (ConfigurationFile.advanced, "login.action"),

    link_enabled (ConfigurationFile.advanced, "link.enabled"),
    link_rename (ConfigurationFile.advanced, "link.rename"),

    unlink_enabled (ConfigurationFile.advanced, "unlink.enabled"),
    unlink_rename (ConfigurationFile.advanced, "unlink.rename"),

    password_minimum (ConfigurationFile.advanced, "password.minimum"),
    password_maximum (ConfigurationFile.advanced, "password.maximum"),

    username_minimum (ConfigurationFile.advanced, "username.minimum"),
    username_maximum (ConfigurationFile.advanced, "username.maximum"),

    session_enabled (ConfigurationFile.advanced, "session.enabled"),
    session_start (ConfigurationFile.advanced, "session.start"),
    session_length (ConfigurationFile.advanced, "session.length"),
    session_protect (ConfigurationFile.advanced, "session.protect"),

    guest_commands (ConfigurationFile.advanced, "guest.commands"),
    guest_chat (ConfigurationFile.advanced, "guest.chat"),
    guest_building (ConfigurationFile.advanced, "guest.building"),
    guest_destruction (ConfigurationFile.advanced, "guest.destruction"),
    guest_movement (ConfigurationFile.advanced, "guest.movement"),
    guest_interactions (ConfigurationFile.advanced, "guest.interactions"),
    guest_inventory (ConfigurationFile.advanced, "guest.inventory"),
    guest_drop (ConfigurationFile.advanced, "guest.drop"),
    guest_pickup (ConfigurationFile.advanced, "guest.pickup"),
    guest_health (ConfigurationFile.advanced, "guest.health"),
    guest_pvp (ConfigurationFile.advanced, "guest.pvp"),
    guest_mobtargeting (ConfigurationFile.advanced, "guest.mobtargeting"),
    guest_mobdamage (ConfigurationFile.advanced, "guest.mobdamage"),

    protection_freeze_enabled (ConfigurationFile.advanced, "protection.freeze.enabled"),
    protection_freeze_delay (ConfigurationFile.advanced, "protection.freeze.delay"),
    protection_notify_enabled (ConfigurationFile.advanced, "protection.notify.enabled"),
    protection_notify_delay (ConfigurationFile.advanced, "protection.notify.delay"),

    filter_action (ConfigurationFile.advanced, "filter.action"),
    filter_username (ConfigurationFile.advanced, "filter.username"),
    filter_password (ConfigurationFile.advanced, "filter.password"),
    filter_whitelist (ConfigurationFile.advanced, "filter.whitelist");

    public String node;
    public ConfigurationFile file;
    ConfigurationNode(ConfigurationFile file, String node) {
        this.file = file;
        this.node = node;
    }

}
