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

import com.craftfire.commons.managers.YamlManager;
import com.craftfire.odin.util.MainUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class MessageManager {
    private YamlManager messages = null;
    private YamlManager defaults = null;

    public String getMessage(String node, OdinUser user) {
        if (exist(node.toLowerCase())) {
            return replace(this.messages.getString(node), user);
        } else if (existDefault(node.toLowerCase())) {
            return replace(this.defaults.getString(node), user);
        }
        return null;
    }

    public String getString(String node) {
        if (exist(node)) {
            return this.messages.getString(node);
        } else if (existDefault(node.toLowerCase())) {
            return this.defaults.getString(node);
        }
        return null;
    }

    public Map<String, Object> getNodes() {
        return this.messages.getNodes();
    }

    public Map<String, Object> getDefaults() {
        return this.defaults.getNodes();
    }

    public void load(YamlManager config, YamlManager defaults) {
        if (this.messages == null) {
            this.messages = config;
        } else {
            this.messages.addNodes(config);
        }
        if (this.defaults == null) {
            this.defaults = defaults;
        } else {
            this.defaults.addNodes(defaults);
        }
    }

    private boolean exist(String node) {
        return this.messages.exist(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return this.defaults.exist(node.toLowerCase());
    }
    
    public String replace(String message, OdinUser user) {
        String string = message;
        //TODO string = string.replaceAll("\\{IP\\}", );
        string = string.replaceAll("\\{PLAYER\\}", user.getUsername());
        //TODO string = string.replaceAll("\\{NEWPLAYER\\}", "");
        //TODO string = string.replaceAll("\\{PLAYERNEW\\}", "");
        string = string.replaceAll("&", "Â§");
            //TODO string = string.replaceAll("\\{DISPLAYNAME\\}", checkOtherName(player.getName()));
        String email = "";
        if (OdinManager.getConfig().getBoolean("customdb.emailrequired")) {
            email = "email";
        }

        string = string.replaceAll("\\{USERMIN\\}", OdinManager.getConfig().getString("username.minimum"));
        string = string.replaceAll("\\{USERMAX\\}", OdinManager.getConfig().getString("username.maximum"));
        string = string.replaceAll("\\{PASSMIN\\}", OdinManager.getConfig().getString("password.minimum"));
        string = string.replaceAll("\\{PASSMAX\\}", OdinManager.getConfig().getString("password.maximum"));
        string = string.replaceAll("\\{PLUGIN\\}", OdinManager.getPluginName());
        string = string.replaceAll("\\{VERSION\\}", OdinManager.getPluginVersion());
        string = string.replaceAll("\\{LOGINTIMEOUT\\}",
                            OdinManager.getConfig().getString("login.timeout").split(" ")[0] + " " +
                                    stringToTimeLanguage(OdinManager.getConfig().getString("login.timeout")));
        string = string.replaceAll("\\{REGISTERTIMEOUT\\}",
                            OdinManager.getConfig().getString("register.timeout").split(" ")[0] + " " +
                                    stringToTimeLanguage(OdinManager.getConfig().getString("register.timeout")));
        string = string.replaceAll("\\{USERBADCHARACTERS\\}", Matcher.quoteReplacement(
                                                              OdinManager.getConfig().getString("filter.username")));
        string = string.replaceAll("\\{PASSBADCHARACTERS\\}", Matcher.quoteReplacement(
                                                              OdinManager.getConfig().getString("filter.password")));
        string = string.replaceAll("\\{EMAILREQUIRED\\}", email);
        string = string.replaceAll("\\{NEWLINE\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{newline\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{N\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{n\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{NL\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{nl\\}", System.getProperty("line.separator"));

        string = string.replaceAll("\\{REGISTERCMD\\}", OdinManager.getCommands().getCommand("user.register") +
                                                        " (" + OdinManager.getCommands().getAlias("user.register") + ")");
        string = string.replaceAll("\\{REGISTERCMD\\}", OdinManager.getCommands().getCommand("user.link") +
                                                        " (" + OdinManager.getCommands().getAlias("user.link") + ")");
        string = string.replaceAll("\\{REGISTERCMD\\}", OdinManager.getCommands().getCommand("user.unlink") +
                                                        " (" + OdinManager.getCommands().getAlias("user.unlink") + ")");
        string = string.replaceAll("\\{REGISTERCMD\\}", OdinManager.getCommands().getCommand("user.login") +
                                                        " (" + OdinManager.getCommands().getAlias("user.login") + ")");

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

    public String stringToTimeLanguage(String timeString) {
        String[] split = timeString.split(" ");
        return stringToTimeLanguage(split[0], split[1]);
    }

    public String stringToTimeLanguage(String length, String time) {
        int integer = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            if (integer > 1) {
                return getString("Core.time.days");
            } else {
                return getString("Core.time.day");
            }
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") ||
                time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            if (integer > 1) {
                return getString("Core.time.hours");
            } else {
                return getString("Core.time.hour");
            }
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") ||
                time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            if (integer > 1) {
                return getString("Core.time.minutes");
            } else {
                return getString("Core.time.minute");
            }
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") ||
                time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            if (integer > 1) {
                return getString("Core.time.seconds");
            } else {
                return getString("Core.time.second");
            }
        } else if (time.equalsIgnoreCase("milliseconds") || time.equalsIgnoreCase("millisecond") ||
                time.equalsIgnoreCase("milli") || time.equalsIgnoreCase("ms")) {
            if (integer > 1) {
                return getString("Core.time.milliseconds");
            } else {
                return getString("Core.time.millisecond");
            }
        }
        return time;
    }
}
