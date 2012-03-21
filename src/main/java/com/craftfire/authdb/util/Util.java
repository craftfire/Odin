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
package com.craftfire.authdb.util;

import com.craftfire.authdb.managers.AuthDBManager;
import com.craftfire.authdb.managers.AuthDBUser;
import com.craftfire.authdb.managers.LoggingHandler;
import com.craftfire.commons.CraftCommons;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Util {
    public static AuthDBUser getUser(String username) {
        return new AuthDBUser(username);
    }

    public static boolean hasBadCharacters(String string, String filter) {
        char char1, char2;
        for (int i= 0; i < string.length(); i++) {
            char1 = string.charAt(i);
            for (int a= 0; a < filter.length(); a++) {
                char2 = filter.charAt(a);
                if (char1 == char2 || char1 == '\'' || char1 == '\"') {
                    /* TODO Logging */
                    return true;
                }
                a++;
            }
        }
        return false;
    }

    public static String stringToTimeLanguage(String timestring) {
        String[] split = timestring.split(" ");
        return stringToTimeLanguage(split[0], split[1]);
    }

    public static String stringToTimeLanguage(String length, String time) {
        int integer = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            if (integer > 1) {
                return AuthDBManager.msgMgr.getString("Core.time.days");
            } else {
                return AuthDBManager.msgMgr.getString("Core.time.day");
            }
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") ||
                   time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            if (integer > 1) {
                return AuthDBManager.msgMgr.getString("Core.time.hours");
            } else {
                return AuthDBManager.msgMgr.getString("Core.time.hour");
            }
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") ||
                   time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            if (integer > 1) {
                return AuthDBManager.msgMgr.getString("Core.time.minutes");
            } else {
                return AuthDBManager.msgMgr.getString("Core.time.minute");
            }
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") ||
                   time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            if (integer > 1) {
                return AuthDBManager.msgMgr.getString("Core.time.seconds");
            } else {
                return AuthDBManager.msgMgr.getString("Core.time.second");
            }
        } else if (time.equalsIgnoreCase("milliseconds") || time.equalsIgnoreCase("millisecond") ||
                   time.equalsIgnoreCase("milli") || time.equalsIgnoreCase("ms")) {
            if (integer > 1) {
                return AuthDBManager.msgMgr.getString("Core.time.milliseconds");
            } else {
                return AuthDBManager.msgMgr.getString("Core.time.millisecond");
            }
        }
        return time;
    }

    public static int stringToTicks(String string) {
        String[] split = string.split(" ");
        String length = split[0];
        String time = split[1].toLowerCase();
        int lengthint = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            return lengthint * 1728000;
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") ||
                   time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            return lengthint * 72000;
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") ||
                   time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            return lengthint * 1200;
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") ||
                   time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            return lengthint * 20;
        }
        return 0;
    }

    public static int stringToSeconds(String string) {
        String[] split = string.split(" ");
        String length = split[0];
        String time = split[1].toLowerCase();
        int lengthint = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            return lengthint * 86400;
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") ||
                   time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            return lengthint * 3600;
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") ||
                   time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            return lengthint * 60;
        } else if (time.equalsIgnoreCase("second") || time.equalsIgnoreCase("seconds") ||
                   time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            return lengthint;
        }
        return 0;
    }

    public void loadLanguage(String dir, String type) throws IOException {
        CodeSource src = getClass().getProtectionDomain().getCodeSource();
        String language = "English";
        File allLanguages = new File(dir);
        if(! allLanguages.exists()) {
            if (allLanguages.mkdir()) {
               AuthDBManager.logMgr.debug("Sucesfully created directory: " + allLanguages);
            }
        }
        boolean set = false;
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        if (src != null) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry ze = null;
                while ((ze = zip.getNextEntry()) != null) {
                    String directory = ze.getName();
                    if (directory.startsWith("files/translations/") && ! directory.endsWith(".yml"))  {
                        directory = directory.replace("files/translations/", "");
                        directory = directory.replace("/", "");
                        if (! directory.equals("")) {
                            AuthDBManager.logMgr.debug("Directory: "+directory);
                            File f = new File(dir + "/" + directory + "/" + type + ".yml");
                            if (!f.exists()) {
                                AuthDBManager.logMgr.debug(type + ".yml" + " could not be found in " + dir + "/" +
                                                            directory + "/! Creating " + type + ".yml");
                                defaultFile(dir ,"translations/" + directory, type + ".yml");
                            }
                            if (type.equals("commands") &&
                                AuthDBManager.cfgMgr.getString("plugin.language.commands").equalsIgnoreCase(directory)) {
                                set = true;
                                language = directory;
                            } else if (type.equals("messages") &&
                                AuthDBManager.cfgMgr.getString("plugin.language.messages").equalsIgnoreCase(directory)) {
                                set = true;
                                language = directory;
                            }
                        }
                    }
                }
                zip.close();
            } catch (IOException e) {
                LoggingHandler.stackTrace(e, Thread.currentThread());
            }
        }

        File[] files = allLanguages.listFiles(fileFilter);
        if (files.length > 0) {
            AuthDBManager.logMgr.debug("Found " + files.length + " directories for " + type);
        } else {
            AuthDBManager.logMgr.error("Error! Could not find any directories for " + type);
        }
        if (! set) {
            for (File file : files) {
                if (type.equalsIgnoreCase("commands") &&
                    AuthDBManager.cfgMgr.getString("plugin.language.commands").equalsIgnoreCase(file.getName()))  {
                    set = true;
                    language = file.getName();
                } else if (type.equalsIgnoreCase("messages") &&
                    AuthDBManager.cfgMgr.getString("plugin.language.messages").equalsIgnoreCase(file.getName()))  {
                    set = true;
                    language = file.getName();
                }
            }
        }
        if (!set && type.equalsIgnoreCase("commands")) {
            AuthDBManager.logMgr.info(
                            "Could not find translation files for " +
                            AuthDBManager.cfgMgr.getString("plugin.language.commands") + ", defaulting to " + language);
        } else if (!set && type.equalsIgnoreCase("messages")) {
            AuthDBManager.logMgr.info(
                            "Could not find translation files for " +
                            AuthDBManager.cfgMgr.getString("plugin.language.messages") + ", defaulting to " + language);
        } else if (type.equalsIgnoreCase("commands")) {
            AuthDBManager.logMgr.info(type + " language set to " +
                                      AuthDBManager.cfgMgr.getString("plugin.language.commands"));
        } else if (type.equalsIgnoreCase("messages")) {
            AuthDBManager.logMgr.info(type + " language set to " +
                                      AuthDBManager.cfgMgr.getString("plugin.language.messages"));
        }
        if (type.equalsIgnoreCase("commands")) {
            AuthDBManager.cfgMgr.load(
                                CraftCommons.loadYaml(
                                        new File("plugins/" + dir + "/translations/" + language + "/", type + ".yml")),
                                AuthDBManager.craftCommons.loadLocalYaml("files/config/advanced.yml"));
        } else if (type.equalsIgnoreCase("messages")) {
            AuthDBManager.msgMgr.load(
                                CraftCommons.loadYaml(
                                        new File("plugins/" + dir + "/translations/" + language + "/", type + ".yml")),
                                AuthDBManager.craftCommons.loadLocalYaml("files/config/advanced.yml"));
        }
    }

    public void defaultFile(String directory, String subdirectory, String file) {
        File actual = new File(directory + "/" + subdirectory + "/", file);
        File dir = new File(directory + "/" + subdirectory + "/", "");
        if (! dir.exists()) {
            if (dir.mkdir()) {
                AuthDBManager.logMgr.debug("Sucesfully created directory: " + dir);
            }
        }
        if (!actual.exists()) {
            java.io.InputStream input = getClass().getResourceAsStream("/files/" + subdirectory + "/" + file);
            if (input != null) {
                java.io.FileOutputStream output = null;
                try {
                    output = new java.io.FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    AuthDBManager.logMgr.info("Written default setup for " + file);
                } catch (Exception e) {
                    LoggingHandler.stackTrace(e, Thread.currentThread());
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) {
                        LoggingHandler.stackTrace(e, Thread.currentThread());
                    }
                }
            }
        }
    }

}
