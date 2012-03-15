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
package com.craftfire.authdb.bukkit;

import com.craftfire.authapi.AuthAPI;
import com.craftfire.authapi.ScriptAPI;
import com.craftfire.authdb.authdb.managers.AuthDBManager;
import com.craftfire.authdb.bukkit.api.AuthDBDisableEvent;
import com.craftfire.authdb.bukkit.api.AuthDBEnableEvent;
import com.craftfire.authdb.bukkit.listeners.AuthDBPlayerListener;
import com.craftfire.authdb.bukkit.managers.InventoryManager;
import com.craftfire.commons.DataManager;
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

public class AuthDBPlugin extends JavaPlugin {
    private static final Logger logger = Logger.getLogger("Minecraft.AuthDB");

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    public static InventoryManager inventoryManager = new InventoryManager();

    private static AuthDBPlugin instance;
    {
        instance = this;
    }

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        loadConfiguration();

        AuthDBManager.dataManager = new DataManager(true,
                                                    0,
                                                    "localhost",
                                                    3306,
                                                    "craftfire",
                                                    "craftfire",
                                                    "craftfire",
                                                    "xf__112__");
        AuthDBManager.authAPI = new AuthAPI(ScriptAPI.Scripts.XF, "1.1.2",  AuthDBManager.dataManager);

        getServer().getPluginManager().registerEvents(new AuthDBPlayerListener(this), this);
        setupPermissions();
        setupChat();
        setupEconomy();
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

    private void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        msgCtrl = new MessageHandler(this);
        msgCtrl.getConfig().options().copyDefaults(true);
        msgCtrl.saveConfig();
        msgCtrl.reloadConfig();
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
