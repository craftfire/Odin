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
package com.craftfire.authdb.layer.bukkit;

import com.craftfire.authdb.layer.bukkit.api.events.plugin.AuthDBDisableEvent;
import com.craftfire.authdb.layer.bukkit.api.events.plugin.AuthDBEnableEvent;
import com.craftfire.authdb.layer.bukkit.listeners.AuthDBPlayerListener;
import com.craftfire.authdb.layer.bukkit.managers.InventoryManager;
import com.craftfire.authdb.managers.AuthDBManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class AuthDB extends JavaPlugin {
    private static final Logger logger = Logger.getLogger("Minecraft.AuthDB");

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    public static InventoryManager inventoryManager = new InventoryManager();

    public static AuthDB instance;
    {
        instance = this;
    }

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        getServer().getPluginManager().registerEvents(new AuthDBPlayerListener(this), this);
        setupPermissions();
        setupChat();
        setupEconomy();
        loadAuthDB();
        logger.info("AuthDB " + getDescription().getVersion() + " enabled.");
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBEnableEvent());
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getPluginManager().callEvent(new AuthDBDisableEvent());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        return true;
    }
    
    protected void loadAuthDB() {
        AuthDBManager authDB = new AuthDBManager();
        authDB.load(getDataFolder());
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
