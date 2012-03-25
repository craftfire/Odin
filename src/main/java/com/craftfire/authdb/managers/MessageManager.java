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

import com.craftfire.authdb.util.MainUtils;

import java.util.HashMap;
import java.util.regex.Matcher;

public class MessageManager {
    private HashMap<String, Object> messages = new HashMap<String, Object>();
    private HashMap<String, Object> defaults = new HashMap<String, Object>();

    public String getMessage(String node, AuthDBUser user) {
        if (exist(node.toLowerCase())) {
            return replace((String) this.messages.get(node.toLowerCase()), user);
        } else if (existDefault(node.toLowerCase())) {
            return replace((String) this.defaults.get(node.toLowerCase()), user);
        }
        return null;
    }

    public String getString(String node) {
        if (exist(node.toLowerCase())) {
            return (String) this.messages.get(node.toLowerCase());
        } else if (existDefault(node.toLowerCase())) {
            return (String) this.defaults.get(node.toLowerCase());
        }
        return null;
    }

    public HashMap<String, Object> getNodes() {
        return this.messages;
    }

    public HashMap<String, Object> getDefaults() {
        return this.defaults;
    }

    public void load(HashMap<String, Object> defaults, HashMap<String, Object> messages) {
        this.defaults.putAll(defaults);
        this.messages.putAll(messages);
    }

    private boolean exist(String node) {
        return this.messages.containsKey(node.toLowerCase());
    }

    private boolean existDefault(String node) {
        return this.defaults.containsKey(node.toLowerCase());
    }
    
    public String replace(String string, AuthDBUser user) {
        //TODO string = string.replaceAll("\\{IP\\}", );
        string = string.replaceAll("\\{PLAYER\\}", user.getUsername());
        //TODO string = string.replaceAll("\\{NEWPLAYER\\}", "");
        //TODO string = string.replaceAll("\\{PLAYERNEW\\}", "");
        string = string.replaceAll("&", "Â§");
            //TODO string = string.replaceAll("\\{DISPLAYNAME\\}", checkOtherName(player.getName()));
        String email = "";
        if (AuthDBManager.cfgMgr.getBoolean("customdb.emailrequired")) {
            email = "email";
        }

        string = string.replaceAll("\\{USERMIN\\}", AuthDBManager.cfgMgr.getString("username.minimum"));
        string = string.replaceAll("\\{USERMAX\\}", AuthDBManager.cfgMgr.getString("username.maximum"));
        string = string.replaceAll("\\{PASSMIN\\}", AuthDBManager.cfgMgr.getString("password.minimum"));
        string = string.replaceAll("\\{PASSMAX\\}", AuthDBManager.cfgMgr.getString("password.maximum"));
        string = string.replaceAll("\\{PLUGIN\\}", AuthDBManager.pluginName);
        string = string.replaceAll("\\{VERSION\\}", AuthDBManager.pluginVersion);
        string = string.replaceAll("\\{LOGINTIMEOUT\\}",
                            AuthDBManager.cfgMgr.getString("login.timeout").split(" ")[0] + " " +
                                    MainUtils.stringToTimeLanguage(AuthDBManager.cfgMgr.getString("login.timeout")));
        string = string.replaceAll("\\{REGISTERTIMEOUT\\}",
                            AuthDBManager.cfgMgr.getString("register.timeout").split(" ")[0] + " " +
                                    MainUtils.stringToTimeLanguage(AuthDBManager.cfgMgr.getString("register.timeout")));
        string = string.replaceAll("\\{USERBADCHARACTERS\\}", Matcher.quoteReplacement(
                                                              AuthDBManager.cfgMgr.getString("filter.username")));
        string = string.replaceAll("\\{PASSBADCHARACTERS\\}", Matcher.quoteReplacement(
                                                              AuthDBManager.cfgMgr.getString("filter.password")));
        string = string.replaceAll("\\{EMAILREQUIRED\\}", email);
        string = string.replaceAll("\\{NEWLINE\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{newline\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{N\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{n\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{NL\\}", System.getProperty("line.separator"));
        string = string.replaceAll("\\{nl\\}", System.getProperty("line.separator"));

        string = string.replaceAll("\\{REGISTERCMD\\}", AuthDBManager.cmdMgr.getCommand("user.register") +
                                                        " (" + AuthDBManager.cmdMgr.getAlias("user.register") + ")");
        string = string.replaceAll("\\{REGISTERCMD\\}", AuthDBManager.cmdMgr.getCommand("user.link") +
                                                        " (" + AuthDBManager.cmdMgr.getAlias("user.link") + ")");
        string = string.replaceAll("\\{REGISTERCMD\\}", AuthDBManager.cmdMgr.getCommand("user.unlink") +
                                                        " (" + AuthDBManager.cmdMgr.getAlias("user.unlink") + ")");
        string = string.replaceAll("\\{REGISTERCMD\\}", AuthDBManager.cmdMgr.getCommand("user.login") +
                                                        " (" + AuthDBManager.cmdMgr.getAlias("user.login") + ")");

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
}
