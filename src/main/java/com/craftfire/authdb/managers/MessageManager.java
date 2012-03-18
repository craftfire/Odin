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
package com.craftfire.authdb.managers;

import java.util.HashMap;
import java.util.regex.Matcher;

public class MessageManager {
    private HashMap<String, String> messages = new HashMap<String, String>();
    private HashMap<String, String> defaults = new HashMap<String, String>();

    public String getString(String node) {
        if (exist(node.toLowerCase())) {
            return this.messages.get(node.toLowerCase());
        } else if (existDefault(node.toLowerCase())) {
            return this.defaults.get(node.toLowerCase());
        }
        return null;
    }

    public void load(HashMap<String, String> defaults, HashMap<String, String> messages) {
        this.defaults.putAll(defaults);
        this.messages.putAll(messages);
    }

    private boolean exist(String node) {
        return this.messages.containsKey(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return this.defaults.containsKey(node.toLowerCase());
    }
    
    private String replace(String string, String additional, AuthDBUser user) {
        String extra = "";
        if (additional != null) {
            extra = additional;
        }
        if (!Config.has_badcharacters && Config.database_ison && player != null && player.getName().length() >= Integer.parseInt(Config.username_minimum) && player.getName().length() <= Integer.parseInt(Config.username_maximum) && extra.equalsIgnoreCase("login") == false) {
            string = string.replaceAll("\\{IP\\}", craftFirePlayer.getIP(player));
            string = string.replaceAll("\\{PLAYER\\}", player.getName());
            string = string.replaceAll("\\{NEWPLAYER\\}", "");
            string = string.replaceAll("\\{PLAYERNEW\\}", "");
            string = string.replaceAll("&", "Â§");
            if (!Util.checkOtherName(player.getName()).equals(player.getName())) {
                string = string.replaceAll("\\{DISPLAYNAME\\}", checkOtherName(player.getName()));
            }
        } else {
            string = string.replaceAll("&", Matcher.quoteReplacement("Â§"));
        }
        String email = "";
        if (Config.custom_emailrequired) {
            email = "email";
        }

        // Replacement variables
        string = string.replaceAll("\\{USERMIN\\}", Config.username_minimum);
        string = string.replaceAll("\\{USERMAX\\}", Config.username_maximum);
        string = string.replaceAll("\\{PASSMIN\\}", Config.password_minimum);
        string = string.replaceAll("\\{PASSMAX\\}", Config.password_maximum);
        string = string.replaceAll("\\{PLUGIN\\}", AuthDB.pluginName);
        string = string.replaceAll("\\{VERSION\\}", AuthDB.pluginVersion);
        string = string.replaceAll("\\{LOGINTIMEOUT\\}", Config.login_timeout_length + " " + replaceTime(Config.login_timeout_length, Config.login_timeout_time));
        string = string.replaceAll("\\{REGISTERTIMEOUT\\}", "" + Config.register_timeout_length + " " + replaceTime(Config.register_timeout_length, Config.register_timeout_time));
        string = string.replaceAll("\\{USERBADCHARACTERS\\}", Matcher.quoteReplacement(Config.filter_username));
        string = string.replaceAll("\\{PASSBADCHARACTERS\\}", Matcher.quoteReplacement(Config.filter_password));
        string = string.replaceAll("\\{EMAILREQUIRED\\}", email);
        string = string.replaceAll("\\{NEWLINE\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{newline\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{N\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{n\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{NL\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{nl\\}", System.getProperty("line.separator"));
        // Commands
        string = string.replaceAll("\\{REGISTERCMD\\}", Config.commands_user_register + " (" + Config.aliases_user_register + ")");
        string = string.replaceAll("\\{LINKCMD\\}", Config.commands_user_link + " (" + Config.aliases_user_link + ")");
        string = string.replaceAll("\\{UNLINKCMD\\}", Config.commands_user_unlink + " (" + Config.aliases_user_unlink + ")");
        string = string.replaceAll("\\{LOGINCMD\\}", Config.commands_user_login + " (" + Config.aliases_user_login + ")");

        // Uppercase colors
        string = string.replaceAll("\\{BLACK\\}", "Â§0");
        string = string.replaceAll("\\{DARKBLUE\\}", "Â§1");
        string = string.replaceAll("\\{DARKGREEN\\}", "Â§2");
        string = string.replaceAll("\\{DARKTEAL\\}", "Â§3");
        string = string.replaceAll("\\{DARKRED\\}", "Â§4");
        string = string.replaceAll("\\{PURPLE\\}", "Â§5");
        string = string.replaceAll("\\{GOLD\\}", "Â§6");
        string = string.replaceAll("\\{GRAY\\}", "Â§7");
        string = string.replaceAll("\\{DARKGRAY\\}", "Â§8");
        string = string.replaceAll("\\{BLUE\\}", "Â§9");
        string = string.replaceAll("\\{BRIGHTGREEN\\}", "Â§a");
        string = string.replaceAll("\\{TEAL\\}", "Â§b");
        string = string.replaceAll("\\{RED\\}", "Â§c");
        string = string.replaceAll("\\{PINK\\}", "Â§d");
        string = string.replaceAll("\\{YELLOW\\}", "Â§e");
        string = string.replaceAll("\\{WHITE\\}", "Â§f");

        string = string.replaceAll("\\{BLACK\\}", "Â§0");
        string = string.replaceAll("\\{NAVY\\}", "Â§1");
        string = string.replaceAll("\\{GREEN\\}", "Â§2");
        string = string.replaceAll("\\{BLUE\\}", "Â§3");
        string = string.replaceAll("\\{RED\\}", "Â§4");
        string = string.replaceAll("\\{PURPLE\\}", "Â§5");
        string = string.replaceAll("\\{GOLD\\}", "Â§6");
        string = string.replaceAll("\\{LIGHTGRAY\\}", "Â§7");
        string = string.replaceAll("\\{GRAY\\}", "Â§8");
        string = string.replaceAll("\\{DARKPURPLE\\}", "Â§9");
        string = string.replaceAll("\\{LIGHTGREEN\\}", "Â§a");
        string = string.replaceAll("\\{LIGHTBLUE\\}", "Â§b");
        string = string.replaceAll("\\{ROSE\\}", "Â§c");
        string = string.replaceAll("\\{LIGHTPURPLE\\}", "Â§d");
        string = string.replaceAll("\\{YELLOW\\}", "Â§e");
        string = string.replaceAll("\\{WHITE\\}", "Â§f");

        // Lowercase colors
        string = string.replaceAll("\\{black\\}", "Â§0");
        string = string.replaceAll("\\{darkblue\\}", "Â§1");
        string = string.replaceAll("\\{darkgreen\\}", "Â§2");
        string = string.replaceAll("\\{darkteal\\}", "Â§3");
        string = string.replaceAll("\\{darkred\\}", "Â§4");
        string = string.replaceAll("\\{purple\\}", "Â§5");
        string = string.replaceAll("\\{gold\\}", "Â§6");
        string = string.replaceAll("\\{gray\\}", "Â§7");
        string = string.replaceAll("\\{darkgray\\}", "Â§8");
        string = string.replaceAll("\\{blue\\}", "Â§9");
        string = string.replaceAll("\\{brightgreen\\}", "Â§a");
        string = string.replaceAll("\\{teal\\}", "Â§b");
        string = string.replaceAll("\\{red\\}", "Â§c");
        string = string.replaceAll("\\{pink\\}", "Â§d");
        string = string.replaceAll("\\{yellow\\}", "Â§e");
        string = string.replaceAll("\\{white\\}", "Â§f");

        string = string.replaceAll("\\{black\\}", "Â§0");
        string = string.replaceAll("\\{navy\\}", "Â§1");
        string = string.replaceAll("\\{green\\}", "Â§2");
        string = string.replaceAll("\\{blue\\}", "Â§3");
        string = string.replaceAll("\\{red\\}", "Â§4");
        string = string.replaceAll("\\{purple\\}", "Â§5");
        string = string.replaceAll("\\{gold\\}", "Â§6");
        string = string.replaceAll("\\{lightgray\\}", "Â§7");
        string = string.replaceAll("\\{gray\\}", "Â§8");
        string = string.replaceAll("\\{darkpurple\\}", "Â§9");
        string = string.replaceAll("\\{lightgreen\\}", "Â§a");
        string = string.replaceAll("\\{lightblue\\}", "Â§b");
        string = string.replaceAll("\\{rose\\}", "Â§c");
        string = string.replaceAll("\\{lightpurple\\}", "Â§d");
        string = string.replaceAll("\\{yellow\\}", "Â§e");
        string = string.replaceAll("\\{white\\}", "Â§f");

        return string;
    }
}
