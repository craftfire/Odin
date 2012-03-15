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
    plugin_language_commands (ConfigurationFile.basic, "plugin.language.commands", "English"),
    plugin_language_messages (ConfigurationFile.basic, "plugin.language.messages", "English"),
    plugin_debugmode (ConfigurationFile.basic, "plugin.debugmode", false),
    plugin_usagestats (ConfigurationFile.basic, "plugin.usagestats", true),
    plugin_logging (ConfigurationFile.basic, "plugin.logging", "true"),
    plugin_logformat (ConfigurationFile.basic, "plugin.logformat", "yyyy-MM-dd"),

    database_type (ConfigurationFile.basic, "database.type", "mysql"),
    database_host (ConfigurationFile.basic, "database.host", "localhost"),
    database_port (ConfigurationFile.basic, "database.port", 3306),
    database_username (ConfigurationFile.basic, "database.username", "root"),
    database_password (ConfigurationFile.basic, "database.password", "root"),
    database_name (ConfigurationFile.basic, "database.name", "forum"),
    database_keepalive (ConfigurationFile.basic, "database.keepalive", false),

    script_name (ConfigurationFile.basic, "script.name", "phpBB"),
    script_version (ConfigurationFile.basic, "script.version", "3.0.8"),
    script_tableprefix (ConfigurationFile.basic, "script.tableprefix", "phpbb__"),

    customdb_enabled (ConfigurationFile.advanced, "customdb.enabled", false),
    customdb_autocreate (ConfigurationFile.advanced, "customdb.autocreate", true),
    customdb_table (ConfigurationFile.advanced, "customdb.table", "authdb_users"),
    customdb_userfield (ConfigurationFile.advanced, "customdb.userfield", "username"),
    customdb_passfield (ConfigurationFile.advanced, "customdb.passfield", "password"),
    customdb_emailfield (ConfigurationFile.advanced, "customdb.emailfield", "email"),
    customdb_emailrequired (ConfigurationFile.advanced, "customdb.emailrequired", false),
    customdb_encryption (ConfigurationFile.advanced, "customdb.encryption", "md5"),

    join_restrict (ConfigurationFile.advanced, "session.restrict", true),

    register_enabled (ConfigurationFile.advanced, "register.enabled", true),
    register_force (ConfigurationFile.advanced, "register.force", true),
    register_delay (ConfigurationFile.advanced, "register.delay", "4 seconds"),
    register_timeout (ConfigurationFile.advanced, "register.timeout", "3 minutes"),

    link_enabled (ConfigurationFile.advanced, "link.enabled", true),
    link_rename (ConfigurationFile.advanced, "link.rename", true),

    username_minimum (ConfigurationFile.advanced, "username.minimum", 3),
    username_maximum (ConfigurationFile.advanced, "username.maximum", 16),

    session_enabled (ConfigurationFile.advanced, "session.enabled", true),
    session_start (ConfigurationFile.advanced, "session.start", "login"),
    session_length (ConfigurationFile.advanced, "session.length", "1 hour"),
    session_protect (ConfigurationFile.advanced, "session.protect", true),

    filter_action (ConfigurationFile.advanced, "filter.action", "kick"),
    filter_username (ConfigurationFile.advanced, "filter.username", "`~!@#$%^&*()-=+{[]}|\\\\:;\\\"<,>.?/"),
    filter_password (ConfigurationFile.advanced, "filter.password", "$&\\\\\\\""),
    filter_whitelist (ConfigurationFile.advanced, "filter.whitelist", "Contex,Wulfspider");

    public String node;
    public Object data;
    public ConfigurationFile file;
    ConfigurationNode(ConfigurationFile file, String node, Object data) {
        this.file = file;
        this.data = data;
        this.node = node;
    }

}
