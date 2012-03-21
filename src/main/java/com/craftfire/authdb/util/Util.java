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
                return AuthDBManager.msgMngr.getString("Core.time.days");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.day");
            }
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") ||
                   time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.hours");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.hour");
            }
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") ||
                   time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.minutes");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.minute");
            }
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") ||
                   time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.seconds");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.second");
            }
        } else if (time.equalsIgnoreCase("milliseconds") || time.equalsIgnoreCase("millisecond") ||
                   time.equalsIgnoreCase("milli") || time.equalsIgnoreCase("ms")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.milliseconds");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.millisecond");
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
                //TODO Util.logging.Debug("Sucesfully created directory: " + allLanguages);
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
                    if (directory.startsWith("files/translations/") && directory.endsWith(".yml") == false)  {
                        directory = directory.replace("files/translations/", "");
                        directory = directory.replace("/", "");
                        if (directory.equals("") == false) {
                            //TODO Util.logging.Debug("Directory: "+directory);
                            File f = new File(dir + "/" + directory + "/" + type + ".yml");
                            if (!f.exists()) {
                                //TODO Util.logging.Info(type + ".yml" + " could not be found in " + dir + "/" + directory + "/! Creating " + type + ".yml");
                                defaultFile(dir ,"translations/" + directory, type + ".yml");
                            }
                            if (type.equals("commands") &&
                                (AuthDBManager.cfgMngr.getString("language.commands").equalsIgnoreCase(directory))) {
                                set = true;
                                language = directory;
                            } else if (type.equals("messages") &&
                                (AuthDBManager.cfgMngr.getString("language.messages").equalsIgnoreCase(directory)))  {
                                set = true;
                                language = directory;
                            }
                        }
                    }
                }
                zip.close();
            } catch (IOException e) {
                //TODO: CATCH
            }
        }

        File[] files = allLanguages.listFiles(fileFilter);
        if (files.length > 0) {
            //TODO Util.logging.Debug("Found " + directories.length + " directories for " + type);
        } else {
            //TODO Util.logging.error("Error! Could not find any directories for " + type);
        }
        if (! set) {
            for (File file : files) {
                if (type.equalsIgnoreCase("commands") &&
                    AuthDBManager.cfgMngr.getString("language.commands").equalsIgnoreCase(file.getName()))  {
                    set = true;
                    language = file.getName();
                } else if (type.equalsIgnoreCase("messages") &&
                    AuthDBManager.cfgMngr.getString("language.messages").equalsIgnoreCase(file.getName()))  {
                    set = true;
                    language = file.getName();
                }
            }
        }
        if (!set && type.equalsIgnoreCase("commands")) {
            //TODO Util.logging.Info("Could not find translation files for " + Config.language_commands + ", defaulting to " + language);
        } else if (!set && type.equalsIgnoreCase("messages")) {
            //TODO Util.logging.Info("Could not find translation files for " + Config.language_messages + ", defaulting to " + language);
        } else if (type.equalsIgnoreCase("commands")) {
            //TODO Util.logging.Info(type + " language set to " + Config.language_commands);
        } else if (type.equalsIgnoreCase("messages")) {
            //TODO Util.logging.Info(type + " language set to " + Config.language_messages);
        }
        if (type.equalsIgnoreCase("commands")) {
            AuthDBManager.cfgMngr.load(
                                CraftCommons.loadYaml(new File(dir + "/translations/" + language + "/", type + ".yml")),
                                AuthDBManager.craftCommons.loadLocalYaml("files/config/advanced.yml"));
        } else if (type.equalsIgnoreCase("messages")) {
            AuthDBManager.msgMngr.load(
                                CraftCommons.loadYaml(new File(dir + "/translations/" + language + "/", type + ".yml")),
                                AuthDBManager.craftCommons.loadLocalYaml("files/config/advanced.yml"));
        }
    }

    public void defaultFile(String directory, String subdirectory, String file) {
        File actual = new File(directory + "/" + subdirectory + "/", file);
        File direct = new File(directory + "/" + subdirectory + "/", "");
        if (! direct.exists()) {
            if (direct.mkdir()) {
                //TODO Util.logging.Debug("Sucesfully created directory: "+direc);
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
                    //TODO System.out.println("[" + pluginName + "] Written default setup for " + name);
                } catch (Exception e) {
                    //TODO: CATCH
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) {
                        //TODO: CATCH
                    }
                }
            }
        }
    }

}
