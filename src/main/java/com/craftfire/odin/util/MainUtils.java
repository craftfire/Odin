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
package com.craftfire.odin.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.craftfire.commons.FileDownloader;
import com.craftfire.commons.TimeUtil;

import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.managers.OdinUser;

public class MainUtils {
    public static OdinUser getUser(String username) {
        if (OdinManager.getStorage().isCachedUser(username)) {
            return OdinManager.getStorage().getCachedUser(username);
        }
        return null;
    }

    public static boolean downloadLibrary(File outputFile, Set<String> urls) {
        try {
            FileDownloader downloader = new FileDownloader(urls, outputFile);
            if (downloader.hasMirror()) {
                downloader.download();
                if (downloader.wasSuccessful()) {
                    OdinManager.getLogger().info("Successfully downloaded " + outputFile.getName() + ". Downloaded "
                            + downloader.getFileSize() + " bytes in "
                            + downloader.getElapsedSeconds() + " seconds.");
                    return true;
                }
            } else {
                OdinManager.getLogger().error("Could not find a mirror for " + outputFile.getName() + ".");
            }
        } catch (IOException e1) {
            OdinManager.getLogger().error("Could not download " + outputFile.getName() +
                    ", see log for more information.");
            OdinManager.getLogger().stackTrace(e1);
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
                    // TODO: Logging
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
        if (!allLanguages.exists()) {
            if (allLanguages.mkdir()) {
                OdinManager.getLogger().debug("Sucesfully created directory: " + allLanguages);
            }
        }
        boolean set = false;
        FileFilter fileFilter = new FileFilter() {
            @Override
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
                    if (directory.startsWith("files" + File.separator + "translations" + File.separator) && !directory.endsWith(".yml")) {
                        directory = directory.replace("files" + File.separator + "translations" + File.separator, "");
                        directory = directory.replace(File.separator, "");
                        if (!directory.equals("")) {
                            OdinManager.getLogger().debug("Directory: " + directory);
                            File f = new File(dir + File.separator + directory + File.separator + type + ".yml");
                            if (!f.exists()) {
                                OdinManager.getLogger().debug(type + ".yml" + " could not be found in " + dir + File.separator +
                                        directory + File.separator + "! Creating " + type + ".yml");
                                defaultFile(dir + directory + File.separator, "translations" + File.separator + directory, type + ".yml");
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
                OdinManager.getLogger().stackTrace(e);
            }
        }

        File[] files = allLanguages.listFiles(fileFilter);
        if (files.length > 0) {
            OdinManager.getLogger().debug("Found '" + files.length + "' directories for '" + type + "'.");
        } else {
            OdinManager.getLogger().error("Error! Could not find any directories for '" + type + "'.");
        }
        if (!set) {
            for (File file : files) {
                if (type.equalsIgnoreCase("commands") &&
                        OdinManager.getConfig().getString("plugin.language.commands").equalsIgnoreCase(file.getName())) {
                    set = true;
                    language = file.getName();
                } else if (type.equalsIgnoreCase("messages") &&
                        OdinManager.getConfig().getString("plugin.language.messages").equalsIgnoreCase(file.getName())) {
                    set = true;
                    language = file.getName();
                }
            }
        }
        if (!set && type.equalsIgnoreCase("commands")) {
            OdinManager.getLogger().info(
                    "Could not find translation files for " +
                            OdinManager.getConfig().getString("plugin.language.commands") + ", defaulting to " + language);
        } else if (!set && type.equalsIgnoreCase("messages")) {
            OdinManager.getLogger().info(
                    "Could not find translation files for " +
                            OdinManager.getConfig().getString("plugin.language.messages") + ", defaulting to " + language);
        } else if (type.equalsIgnoreCase("commands")) {
            OdinManager.getLogger().info(type + " language set to " +
                    OdinManager.getConfig().getString("plugin.language.commands"));
        } else if (type.equalsIgnoreCase("messages")) {
            OdinManager.getLogger().info(type + " language set to " +
                    OdinManager.getConfig().getString("plugin.language.messages"));
        }
        OdinManager.getLogger().debug("Loading external language file for '" + type.toLowerCase() + "': "
                + new File(dir + language + File.separator, type + ".yml").getPath());
        OdinManager.getLogger().debug("Loading internal language file for '" + type.toLowerCase() + "': "
                + "files/translations/English/" + type + ".yml");
        if (type.equalsIgnoreCase("commands")) {
            OdinManager.getCommands().getCommands().load(new File(dir + language + File.separator, type + ".yml"));
            OdinManager.getCommands().getDefaults().load("files/translations/English/" + type + ".yml");
            OdinManager.getLogger().debug("Custom commands size: " + OdinManager.getCommands().getNodes().size());
            OdinManager.getLogger().debug("Default commands size: " + OdinManager.getCommands().getDefaultNodes().size());
            if (OdinManager.getCommands().getNodes().size() == 0) {
                OdinManager.getLogger().error("Failed loading custom commands!");
            }
            if (OdinManager.getCommands().getDefaultNodes().size() == 0) {
                OdinManager.getLogger().error("Failed loading default commands!");
            }
            if (OdinManager.getCommands().getDefaultNodes().size() > OdinManager.getCommands().getNodes().size()) {
                Set<String> missingNodes = new TreeSet<String>();
                OdinManager.getLogger().info("You are missing "
                        + (OdinManager.getCommands().getDefaultNodes().size()
                        - OdinManager.getCommands().getNodes().size())
                        + " command nodes "
                        + "(" + OdinManager.getCommands().getDefaultNodes().size()
                        + " > "
                        + OdinManager.getCommands().getNodes().size() + ")"
                        + ((OdinManager.getConfig().getBoolean("plugin.debugmode")) ? "."
                                : ", enable debug to see which."));
                OdinManager.getLogger().debug("You are missing the following command nodes ("
                        + (OdinManager.getCommands().getDefaultNodes().size()
                        - OdinManager.getCommands().getNodes().size()) + "):");
                for (Map.Entry<String, Object> stringObjectEntry : OdinManager.getCommands().getDefaultNodes().entrySet()) {
                    Map.Entry pairs = stringObjectEntry;
                    if (OdinManager.getCommands().getNodes().containsKey(pairs.getKey())) {
                        missingNodes.add((String) pairs.getKey());
                    }
                }
                for (String node : missingNodes) {
                    OdinManager.getLogger().debug("* " + node);
                }
                OdinManager.getLogger().debug("Finished listing all of the "
                        + (OdinManager.getCommands().getDefaultNodes().size()
                        - OdinManager.getCommands().getNodes().size()) + " missing command nodes.");
            }
            try {
                OdinManager.getCommands().initialize();
            } catch (IllegalAccessException e) {
                OdinManager.getLogger().error(e.getMessage());
                OdinManager.getLogger().stackTrace(e);
            }
        } else if (type.equalsIgnoreCase("messages")) {
            OdinManager.getMessages().getMessages().load(new File(dir + language + File.separator, type + ".yml"));
            OdinManager.getMessages().getDefaults().load("files/translations/English/" + type + ".yml");
            OdinManager.getLogger().debug("Custom messages size: " + OdinManager.getMessages().getMessages().getFinalNodeCount());
            OdinManager.getLogger().debug("Default messages size: " + OdinManager.getMessages().getDefaults().getFinalNodeCount());
            if (OdinManager.getMessages().getMessages().getFinalNodeCount() == 0) {
                OdinManager.getLogger().error("Failed loading custom messages!");
            }
            if (OdinManager.getMessages().getDefaults().getFinalNodeCount() == 0) {
                OdinManager.getLogger().error("Failed loading default messages!");
            }
            if (OdinManager.getMessages().getDefaultNodes().size() > OdinManager.getMessages().getNodes().size()) {
                Set<String> missingNodes = new TreeSet<String>();
                OdinManager.getLogger().info("You are missing "
                        + (OdinManager.getMessages().getDefaultNodes().size()
                        - OdinManager.getMessages().getNodes().size())
                        + " message nodes "
                        + "(" + OdinManager.getMessages().getDefaultNodes().size()
                        + " > "
                        + OdinManager.getMessages().getNodes().size() + ")"
                        + ((OdinManager.getConfig().getBoolean("plugin.debugmode")) ? "."
                                : ", enable debug to see which."));
                OdinManager.getLogger().debug("You are missing the following message nodes ("
                        + (OdinManager.getMessages().getDefaultNodes().size()
                        - OdinManager.getMessages().getNodes().size()) + "):");
                for (Map.Entry<String, Object> stringObjectEntry : OdinManager.getMessages().getDefaultNodes().entrySet()) {
                    Map.Entry pairs = stringObjectEntry;
                    if (OdinManager.getMessages().getNodes().containsKey(pairs.getKey())) {
                        missingNodes.add((String) pairs.getKey());
                    }
                }
                for (String node : missingNodes) {
                    OdinManager.getLogger().debug("* " + node);
                }
                OdinManager.getLogger().debug("Finished listing all of the "
                        + (OdinManager.getMessages().getDefaultNodes().size()
                        - OdinManager.getMessages().getNodes().size()) + " missing message nodes.");
            }
        }
    }

    public void defaultFile(String directory, String subdirectory, String file) {
        File actual = new File(directory, file);
        File dir = new File(directory, "");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                OdinManager.getLogger().debug("Successfully created directory: " + dir);
            }
        }
        if (!actual.exists()) {
            InputStream input = getClass().getClassLoader().getResourceAsStream("files" + File.separator + subdirectory + File.separator + file);
            if (input != null) {
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    OdinManager.getLogger().info("Written default setup for " + file);
                } catch (Exception e) {
                    OdinManager.getLogger().stackTrace(e);
                } finally {
                    try {
                        input.close();
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) {
                        OdinManager.getLogger().stackTrace(e);
                    }
                }
            }
        }
    }
}
