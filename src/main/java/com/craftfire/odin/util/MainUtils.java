/*
 * This file is part of Odin.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
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
package com.craftfire.odin.util;

import com.craftfire.commons.classes.FileDownloader;
import com.craftfire.commons.classes.TimeUtil;
import com.craftfire.commons.managers.YamlManager;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinUser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainUtils {
    public static OdinUser getUser(String username) {
        return new OdinUser(username);
    }

    public static boolean downloadLibrary(File outputFile, Set<String> urls) {
        try {
            FileDownloader downloader = new FileDownloader(urls, outputFile);
            if (downloader.hasMirror()) {
                downloader.download();
                if (downloader.wasSuccessful()) {
                    OdinManager.getLogging().info("Successfully downloaded " + outputFile.getName() + ". Downloaded "
                                                 + downloader.getFileSize() + " bytes in "
                                                 + downloader.getElapsedSeconds() + " seconds.");
                    return true;
                }
            } else {
                OdinManager.getLogging().error("Could not find a mirror for " + outputFile.getName() + ".");
            }
        } catch (IOException e1) {
            OdinManager.getLogging().error("Could not download " + outputFile.getName() +
                                           ", see log for more information.");
            OdinManager.getLogging().stackTrace(e1);
        }
        return false;
    }

    public static TimeUtil getTimeUtil(String timeString) {
        return new TimeUtil(timeString);
    }

    public static boolean hasBadCharacters(String string, String filter) {
        char char1, char2;
        for (int i = 0; i < string.length(); i++) {
            char1 = string.charAt(i);
            for (int a = 0; a < filter.length(); a++) {
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

    public void loadLanguage(String dir, String type) throws IOException {
        CodeSource src = getClass().getProtectionDomain().getCodeSource();
        String language = "English";
        File allLanguages = new File(dir);
        if(!allLanguages.exists()) {
            if (allLanguages.mkdir()) {
               OdinManager.getLogging().debug("Sucesfully created directory: " + allLanguages);
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
                            OdinManager.getLogging().debug("Directory: " + directory);
                            File f = new File(dir + "/" + directory + "/" + type + ".yml");
                            if (! f.exists()) {
                                OdinManager.getLogging().debug(type + ".yml" + " could not be found in " + dir + "/" +
                                                            directory + "/! Creating " + type + ".yml");
                                defaultFile(dir + directory + "\\", "translations/" + directory, type + ".yml");
                            }
                            if (type.equals("commands") &&
                                OdinManager.getConfig().getString("plugin.language.commands").equalsIgnoreCase(directory)) {
                                set = true;
                                language = directory;
                            } else if (type.equals("messages") &&
                                OdinManager.getConfig().getString("plugin.language.messages").equalsIgnoreCase(directory)) {
                                set = true;
                                language = directory;
                            }
                        }
                    }
                }
                zip.close();
            } catch (IOException e) {
                OdinManager.getLogging().stackTrace(e);
            }
        }

        File[] files = allLanguages.listFiles(fileFilter);
        if (files.length > 0) {
            OdinManager.getLogging().debug("Found " + files.length + " directories for " + type);
        } else {
            OdinManager.getLogging().error("Error! Could not find any directories for " + type);
        }
        if (!set) {
            for (File file : files) {
                if (type.equalsIgnoreCase("commands") &&
                    OdinManager.getConfig().getString("plugin.language.commands").equalsIgnoreCase(file.getName()))  {
                    set = true;
                    language = file.getName();
                } else if (type.equalsIgnoreCase("messages") &&
                    OdinManager.getConfig().getString("plugin.language.messages").equalsIgnoreCase(file.getName()))  {
                    set = true;
                    language = file.getName();
                }
            }
        }
        if (!set && type.equalsIgnoreCase("commands")) {
            OdinManager.getLogging().info(
                    "Could not find translation files for " +
                            OdinManager.getConfig().getString("plugin.language.commands") + ", defaulting to " + language);
        } else if (!set && type.equalsIgnoreCase("messages")) {
            OdinManager.getLogging().info(
                            "Could not find translation files for " +
                            OdinManager.getConfig().getString("plugin.language.messages") + ", defaulting to " + language);
        } else if (type.equalsIgnoreCase("commands")) {
            OdinManager.getLogging().info(type + " language set to " +
                                      OdinManager.getConfig().getString("plugin.language.commands"));
        } else if (type.equalsIgnoreCase("messages")) {
            OdinManager.getLogging().info(type + " language set to " +
                                      OdinManager.getConfig().getString("plugin.language.messages"));
        }
        if (type.equalsIgnoreCase("commands")) {
            OdinManager.getCommands().load(new YamlManager(new File(dir + language + "/", type + ".yml")),
                                    new YamlManager(("files/translations/advanced.yml")));
        } else if (type.equalsIgnoreCase("messages")) {
            OdinManager.getMessages().load(new YamlManager(new File(dir + language + "/", type + ".yml")),
                                    new YamlManager("files/translations/advanced.yml"));
        }
    }

    public void defaultFile(String directory, String subdirectory, String file) {
        File actual = new File(directory, file);
        File dir = new File(directory, "");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                OdinManager.getLogging().debug("Sucesfully created directory: " + dir);
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
                    OdinManager.getLogging().info("Written default setup for " + file);
                } catch (Exception e) {
                    OdinManager.getLogging().stackTrace(e);
                } finally {
                    try {
                        input.close();
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) {
                        OdinManager.getLogging().stackTrace(e);
                    }
                }
            }
        }
    }

}
