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
package com.craftfire.odin.managers;

import java.util.Map;
import java.util.regex.Matcher;

import com.craftfire.commons.TimeUtil;
import com.craftfire.commons.yaml.YamlCombiner;
import com.craftfire.commons.yaml.YamlManager;

public class MessageManager {
    private YamlCombiner messages = new YamlCombiner();
    private YamlCombiner defaults = new YamlCombiner();

    public boolean isInitialized() {
        return (this.messages.getFinalNodeCount() != 0 && this.defaults.getFinalNodeCount() != 0);
    }

    public void setLoggingHandler(LoggingHandler loggingHandler) {
        getMessages().setLoggingManager(loggingHandler);
        getDefaults().setLoggingManager(loggingHandler);
    }

    public String getMessage(String node) {
        return getMessage(node, null);
    }

    public String getMessage(String node, OdinUser user) {
        OdinManager.getLogger().debug("Getting message from node: '" + node + "'.");
        if (exist(node)) {
            String message = replace(this.messages.getString(node), user);
            OdinManager.getLogger().debug("Found message for node '" + node + "'.");
            OdinManager.getLogger().debug("Raw message (" + node + "): '" + this.messages.getString(node) + "'.");
            OdinManager.getLogger().debug("Formatted message (" + node + "): '" + message + "'.");
            return message;
        } else if (existDefault(node)) {
            String message = replace(this.defaults.getString(node), user);
            OdinManager.getLogger().error("Could not find message for node '" + node + "', using default node instead.");
            OdinManager.getLogger().debug("Raw message (" + node + "): '" + this.defaults.getString(node) + "'.");
            OdinManager.getLogger().debug("Formatted message (" + node + "): '" + message + "'.");
            return message;
        }
        OdinManager.getLogger().error("Could not find a message for node '" + node + "', returning null.");
        OdinManager.getLogger().debug("Custom messages size: " + this.messages.getFinalNodeCount());
        OdinManager.getLogger().debug("Default messages size: " + this.defaults.getFinalNodeCount());
        return null;
    }

    public String getString(String node) {
        if (exist(node)) {
            return this.messages.getString(node);
        } else if (existDefault(node)) {
            return this.defaults.getString(node);
        }
        return null;
    }

    public Map<String, Object> getNodes() {
        return this.messages.getNodes();
    }

    public Map<String, Object> getDefaultNodes() {
        return this.defaults.getNodes();
    }

    public YamlCombiner getMessages() {
        return this.messages;
    }

    public YamlCombiner getDefaults() {
        return this.defaults;
    }

    public void load(YamlManager messages, YamlManager defaults) {
        OdinManager.getLogger().debug("Adding custom messages : " + messages.getNodes().toString());
        this.messages.addYamlManager(messages);
        OdinManager.getLogger().debug("Adding default messages : " + messages.getNodes().toString());
        this.defaults.addYamlManager(defaults);
        OdinManager.getLogger().debug("Custom messages size: " + this.messages.getFinalNodeCount());
        OdinManager.getLogger().debug("Default messages size: " + this.defaults.getFinalNodeCount());
        if (this.messages.getFinalNodeCount() == 0) {
            OdinManager.getLogger().error("Failed loading custom messages!");
        }
        if (this.defaults.getFinalNodeCount() == 0) {
            OdinManager.getLogger().error("Failed loading default messages!");
        }
    }

    private boolean exist(String node) {
        return node != null && this.messages.exist(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return node != null && this.defaults.exist(node.toLowerCase());
    }

    public String replace(String message) {
        return replace(message, null);
    }

    public String replace(String message, OdinUser user) {
        if (message != null) {
            String string = message;
            // TODO: string = string.replaceAll("\\{IP\\}", );
            if (user != null) {
                string = string.replaceAll("\\{PLAYER\\}", user.getUsername());
            }
            // TODO: string = string.replaceAll("\\{NEWPLAYER\\}", "");
            // TODO: string = string.replaceAll("\\{PLAYERNEW\\}", "");
            string = string.replaceAll("&", "Â§");
            // TODO: string = string.replaceAll("\\{DISPLAYNAME\\}", checkOtherName(player.getName()));
            String email = "";
            if (OdinManager.getConfig().getBoolean("customdb.emailrequired", true)) {
                email = "email";
            }

            string = string.replaceAll("\\{USERMIN\\}", "" + OdinManager.getConfig().getInt("username.minimum", true));
            string = string.replaceAll("\\{USERMAX\\}", "" + OdinManager.getConfig().getInt("username.maximum", true));
            string = string.replaceAll("\\{PASSMIN\\}", "" + OdinManager.getConfig().getInt("password.minimum", true));
            string = string.replaceAll("\\{PASSMAX\\}", "" + OdinManager.getConfig().getInt("password.maximum", true));
            string = string.replaceAll("\\{PLUGIN\\}", OdinManager.getPluginName());
            string = string.replaceAll("\\{VERSION\\}", OdinManager.getPluginVersion());
            string = string.replaceAll("\\{LOGINTIMEOUT\\}",
                    OdinManager.getConfig().getString("login.timeout", true).split(" ")[0] + " " +
                            stringToTimeLanguage(OdinManager.getConfig().getString("login.timeout", true)));
            string = string.replaceAll("\\{REGISTERTIMEOUT\\}",
                    OdinManager.getConfig().getString("register.timeout", true).split(" ")[0] + " " +
                            stringToTimeLanguage(OdinManager.getConfig().getString("register.timeout", true)));
            string = string.replaceAll("\\{USERBADCHARACTERS\\}", Matcher.quoteReplacement(
                    OdinManager.getConfig().getString("filter.username", true)));
            string = string.replaceAll("\\{PASSBADCHARACTERS\\}", Matcher.quoteReplacement(
                    OdinManager.getConfig().getString("filter.password", true)));
            string = string.replaceAll("\\{EMAILREQUIRED\\}", email);
            string = string.replaceAll("\\{NEWLINE\\}", System.getProperty("line.separator"));
            string = string.replaceAll("\\{newline\\}", System.getProperty("line.separator"));
            string = string.replaceAll("\\{N\\}", System.getProperty("line.separator"));
            string = string.replaceAll("\\{n\\}", System.getProperty("line.separator"));
            string = string.replaceAll("\\{NL\\}", System.getProperty("line.separator"));
            string = string.replaceAll("\\{nl\\}", System.getProperty("line.separator"));

            string = string.replaceAll("\\{REGISTERCMD\\}", OdinManager.getCommands().getCommand("user.register", true) +
                    " (" + OdinManager.getCommands().getAlias("user.register", true) + ")");
            string = string.replaceAll("\\{REGISTERCMD\\}", OdinManager.getCommands().getCommand("user.link", true) +
                    " (" + OdinManager.getCommands().getAlias("user.link", true) + ")");
            string = string.replaceAll("\\{REGISTERCMD\\}", OdinManager.getCommands().getCommand("user.unlink", true) +
                    " (" + OdinManager.getCommands().getAlias("user.unlink", true) + ")");
            string = string.replaceAll("\\{REGISTERCMD\\}", OdinManager.getCommands().getCommand("user.login", true) +
                    " (" + OdinManager.getCommands().getAlias("user.login", true) + ")");

            string = string.replaceAll("\\{BLACK\\}", "§0");
            string = string.replaceAll("\\{DARKBLUE\\}", "§1");
            string = string.replaceAll("\\{DARKGREEN\\}", "§2");
            string = string.replaceAll("\\{DARKTEAL\\}", "§3");
            string = string.replaceAll("\\{DARKRED\\}", "§4");
            string = string.replaceAll("\\{PURPLE\\}", "§5");
            string = string.replaceAll("\\{GOLD\\}", "§6");
            string = string.replaceAll("\\{GRAY\\}", "§7");
            string = string.replaceAll("\\{DARKGRAY\\}", "§8");
            string = string.replaceAll("\\{BLUE\\}", "§9");
            string = string.replaceAll("\\{BRIGHTGREEN\\}", "§a");
            string = string.replaceAll("\\{TEAL\\}", "§b");
            string = string.replaceAll("\\{RED\\}", "§c");
            string = string.replaceAll("\\{PINK\\}", "§d");
            string = string.replaceAll("\\{YELLOW\\}", "§e");
            string = string.replaceAll("\\{WHITE\\}", "§f");

            string = string.replaceAll("\\{BLACK\\}", "§0");
            string = string.replaceAll("\\{NAVY\\}", "§1");
            string = string.replaceAll("\\{GREEN\\}", "§2");
            string = string.replaceAll("\\{BLUE\\}", "§3");
            string = string.replaceAll("\\{RED\\}", "§4");
            string = string.replaceAll("\\{PURPLE\\}", "§5");
            string = string.replaceAll("\\{GOLD\\}", "§6");
            string = string.replaceAll("\\{LIGHTGRAY\\}", "§7");
            string = string.replaceAll("\\{GRAY\\}", "§8");
            string = string.replaceAll("\\{DARKPURPLE\\}", "§9");
            string = string.replaceAll("\\{LIGHTGREEN\\}", "§a");
            string = string.replaceAll("\\{LIGHTBLUE\\}", "§b");
            string = string.replaceAll("\\{ROSE\\}", "§c");
            string = string.replaceAll("\\{LIGHTPURPLE\\}", "§d");
            string = string.replaceAll("\\{YELLOW\\}", "§e");
            string = string.replaceAll("\\{WHITE\\}", "§f");

            string = string.replaceAll("\\{black\\}", "§0");
            string = string.replaceAll("\\{darkblue\\}", "§1");
            string = string.replaceAll("\\{darkgreen\\}", "§2");
            string = string.replaceAll("\\{darkteal\\}", "§3");
            string = string.replaceAll("\\{darkred\\}", "§4");
            string = string.replaceAll("\\{purple\\}", "§5");
            string = string.replaceAll("\\{gold\\}", "§6");
            string = string.replaceAll("\\{gray\\}", "§7");
            string = string.replaceAll("\\{darkgray\\}", "§8");
            string = string.replaceAll("\\{blue\\}", "§9");
            string = string.replaceAll("\\{brightgreen\\}", "§a");
            string = string.replaceAll("\\{teal\\}", "§b");
            string = string.replaceAll("\\{red\\}", "§c");
            string = string.replaceAll("\\{pink\\}", "§d");
            string = string.replaceAll("\\{yellow\\}", "§e");
            string = string.replaceAll("\\{white\\}", "§f");

            string = string.replaceAll("\\{black\\}", "§0");
            string = string.replaceAll("\\{navy\\}", "§1");
            string = string.replaceAll("\\{green\\}", "§2");
            string = string.replaceAll("\\{blue\\}", "§3");
            string = string.replaceAll("\\{red\\}", "§4");
            string = string.replaceAll("\\{purple\\}", "§5");
            string = string.replaceAll("\\{gold\\}", "§6");
            string = string.replaceAll("\\{lightgray\\}", "§7");
            string = string.replaceAll("\\{gray\\}", "§8");
            string = string.replaceAll("\\{darkpurple\\}", "§9");
            string = string.replaceAll("\\{lightgreen\\}", "§a");
            string = string.replaceAll("\\{lightblue\\}", "§b");
            string = string.replaceAll("\\{rose\\}", "§c");
            string = string.replaceAll("\\{lightpurple\\}", "§d");
            string = string.replaceAll("\\{yellow\\}", "§e");
            string = string.replaceAll("\\{white\\}", "§f");

            return string;
        }
        return null;
    }

    public String stringToTimeLanguage(String timeString) {
        String[] split = timeString.split(" ");
        return stringToTimeLanguage(split[0], TimeUtil.TimeUnit.getUnit(split[1]));
    }

    public String stringToTimeLanguage(String length, TimeUtil.TimeUnit unit) {
        int integer = Integer.parseInt(length);
        if (unit.equals(TimeUtil.TimeUnit.DAY)) {
            if (integer > 1) {
                return getString("time.days");
            } else {
                return getString("time.day");
            }
        } else if (unit.equals(TimeUtil.TimeUnit.HOUR)) {
            if (integer > 1) {
                return getString("time.hours");
            } else {
                return getString("time.hour");
            }
        } else if (unit.equals(TimeUtil.TimeUnit.MINUTE)) {
            if (integer > 1) {
                return getString("time.minutes");
            } else {
                return getString("time.minute");
            }
        } else if (unit.equals(TimeUtil.TimeUnit.SECOND)) {
            if (integer > 1) {
                return getString("time.seconds");
            } else {
                return getString("time.second");
            }
        } else if (unit.equals(TimeUtil.TimeUnit.MILLISECOND)) {
            if (integer > 1) {
                return getString("time.milliseconds");
            } else {
                return getString("time.millisecond");
            }
        }
        return unit.getName();
    }
}
