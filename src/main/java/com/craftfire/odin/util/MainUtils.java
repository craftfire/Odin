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
package com.craftfire.odin.util;

import com.craftfire.commons.managers.YamlManager;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinUser;
import com.craftfire.odin.managers.LoggingHandler;
import com.craftfire.commons.CraftCommons;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainUtils {
    public static OdinUser getUser(String username) {
        return new OdinUser(username);
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
               OdinManager.logMgr.debug("Sucesfully created directory: " + allLanguages);
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
                            OdinManager.logMgr.debug("Directory: " + directory);
                            File f = new File(dir + "/" + directory + "/" + type + ".yml");
                            if (! f.exists()) {
                                OdinManager.logMgr.debug(type + ".yml" + " could not be found in " + dir + "/" +
                                                            directory + "/! Creating " + type + ".yml");
                                defaultFile(dir + directory + "\\", "translations/" + directory, type + ".yml");
                            }
                            if (type.equals("commands") &&
                                OdinManager.cfgMgr.getString("plugin.language.commands").equalsIgnoreCase(directory)) {
                                set = true;
                                language = directory;
                            } else if (type.equals("messages") &&
                                OdinManager.cfgMgr.getString("plugin.language.messages").equalsIgnoreCase(directory)) {
                                set = true;
                                language = directory;
                            }
                        }
                    }
                }
                zip.close();
            } catch (IOException e) {
                LoggingHandler.stackTrace(e);
            }
        }

        File[] files = allLanguages.listFiles(fileFilter);
        if (files.length > 0) {
            OdinManager.logMgr.debug("Found " + files.length + " directories for " + type);
        } else {
            OdinManager.logMgr.error("Error! Could not find any directories for " + type);
        }
        if (!set) {
            for (File file : files) {
                if (type.equalsIgnoreCase("commands") &&
                    OdinManager.cfgMgr.getString("plugin.language.commands").equalsIgnoreCase(file.getName()))  {
                    set = true;
                    language = file.getName();
                } else if (type.equalsIgnoreCase("messages") &&
                    OdinManager.cfgMgr.getString("plugin.language.messages").equalsIgnoreCase(file.getName()))  {
                    set = true;
                    language = file.getName();
                }
            }
        }
        if (!set && type.equalsIgnoreCase("commands")) {
            OdinManager.logMgr.info(
                    "Could not find translation files for " +
                            OdinManager.cfgMgr.getString("plugin.language.commands") + ", defaulting to " + language);
        } else if (!set && type.equalsIgnoreCase("messages")) {
            OdinManager.logMgr.info(
                            "Could not find translation files for " +
                            OdinManager.cfgMgr.getString("plugin.language.messages") + ", defaulting to " + language);
        } else if (type.equalsIgnoreCase("commands")) {
            OdinManager.logMgr.info(type + " language set to " +
                                      OdinManager.cfgMgr.getString("plugin.language.commands"));
        } else if (type.equalsIgnoreCase("messages")) {
            OdinManager.logMgr.info(type + " language set to " +
                                      OdinManager.cfgMgr.getString("plugin.language.messages"));
        }
        if (type.equalsIgnoreCase("commands")) {
            OdinManager.cmdMgr.load(new YamlManager(new File(dir + language + "/", type + ".yml")),
                                    new YamlManager(("files/translations/advanced.yml")));
        } else if (type.equalsIgnoreCase("messages")) {
            OdinManager.msgMgr.load(new YamlManager(new File(dir + language + "/", type + ".yml")),
                                    new YamlManager("files/translations/advanced.yml"));
        }
    }

    public void defaultFile(String directory, String subdirectory, String file) {
        File actual = new File(directory, file);
        File dir = new File(directory, "");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                OdinManager.logMgr.debug("Sucesfully created directory: " + dir);
            }
        }
        if (!actual.exists()) {
            InputStream input = getClass().getClassLoader().getResourceAsStream("files/" + subdirectory + "/" + file);
            if (input != null) {
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    OdinManager.logMgr.info("Written default setup for " + file);
                } catch (Exception e) {
                    LoggingHandler.stackTrace(e);
                } finally {
                    try {
                        input.close();
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) {
                        LoggingHandler.stackTrace(e);
                    }
                }
            }
        }
    }

}
