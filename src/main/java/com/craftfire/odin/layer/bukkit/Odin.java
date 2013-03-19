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
package com.craftfire.odin.layer.bukkit;

import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.TranslationManager;
import com.craftfire.odin.layer.bukkit.api.events.plugin.OdinDisableEvent;
import com.craftfire.odin.layer.bukkit.api.events.plugin.OdinEnableEvent;
import com.craftfire.odin.layer.bukkit.listeners.OdinPlayerListener;
import com.craftfire.odin.layer.bukkit.managers.InventoryManager;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.layer.bukkit.util.Util;
import com.craftfire.odin.managers.OdinManager;
import com.craftfire.odin.util.MainUtils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Odin extends JavaPlugin {
    private static Permission permission = null;
    private static Economy economy = null;
    private static Chat chat = null;
    private InventoryManager inventoryManager = new InventoryManager();
    private static Odin instance;

    public static Odin getInstance() {
        return instance;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        getDataFolder().mkdirs();
        getServer().getPluginManager().registerEvents(new OdinPlayerListener(), this);
        setupPermissions();
        setupChat();
        setupEconomy();

        if (!loadLibraries()) {
            OdinManager.getLogger().error("Failed loading Odin databases, check log for more information.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else if (OdinManager.init(getDataFolder(), getDescription().getVersion())) {
            Bukkit.getServer().getPluginManager().callEvent(new OdinEnableEvent());
        } else {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        for (Player p : getServer().getOnlinePlayers()) {
            OdinPlayer player = Util.getPlayer(p);
            OdinManager.getLogger().debug("Checking user '" + player.getName() + "' for persistence, reload time: '" + player.getReloadTime() + "'");
            if (player.getReloadTime() + 30 > (System.currentTimeMillis() / 1000)) {
                OdinManager.getLogger().debug("Found persistence for user '" + player.getName() + "', logging in the user.");
                player.login();
            } else {
                OdinManager.getLogger().debug("Could not find persistence for user '" + player.getName() + "', doing nothing.");
            }
        }
    }

    @Override
    public void onDisable() {
        OdinManager.disable();
        Bukkit.getServer().getPluginManager().callEvent(new OdinDisableEvent());
    }

    private boolean loadLibraries() {
        if (!OdinManager.loadLibraries(getDataFolder())) {
            return false;
        }
        File h2Driver = new File(getDataFolder() + File.separator + "lib" + File.separator + "h2.jar");
        OdinManager.getLogger().debug("Trying to load library driver: " + getDataFolder() + File.separator + "lib" + File.separator + "h2.jar");
        if (!CraftCommons.hasClass("org.h2.Driver") && h2Driver.exists()) {
            try {
                ((PluginClassLoader) getInstance().getClassLoader()).addURL(h2Driver.toURI().toURL());
                return true;
            } catch (MalformedURLException e) {
                OdinManager.getLogger().stackTrace(e);
                return false;
            }
        }
        return false;
    }

    public static Permission getPermissions() {
        return permission;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Economy getEconomy() {
        return economy;
    }

    private Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private Boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private Boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }
}
