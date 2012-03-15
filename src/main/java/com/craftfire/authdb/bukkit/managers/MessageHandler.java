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
package com.craftfire.authdb.bukkit.managers;

import com.craftfire.authdb.bukkit.AuthDBPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MessageHandler {
    private final AuthDBPlugin plugin;
    private final File configfile;
    private final String file = "messages.yml";
    private FileConfiguration config = null;

    public MessageHandler(final AuthDBPlugin plugin) {
        this.plugin = plugin;
        this.configfile = new File(plugin.getDataFolder(), this.file);
    }

    public FileConfiguration getConfig() {
        if (this.config == null) {
            reloadConfig();
        }
        return this.config;
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configfile);
        InputStream configStream = plugin.getResource(this.file);
        if (configStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(configStream);
            this.config.setDefaults(defConfig);
        }
    }

    public void saveConfig() {
        try {
            getConfig().save(configfile);
        } catch (IOException e) {
            /* TODO */
        }
    }
}
