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
package com.craftfire.odin.managers;

import com.craftfire.bifrost.classes.general.ScriptUser;
import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.commons.encryption.Encryption;
import com.craftfire.commons.ip.IPAddress;
import com.craftfire.odin.managers.inventory.InventoryItem;
import com.craftfire.odin.managers.inventory.InventoryManager;
import com.craftfire.odin.managers.inventory.ItemEnchantment;
import com.craftfire.odin.util.MainUtils;
import com.craftfire.commons.CraftCommons;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class OdinUser {
    private String username, linkedUsername;
    private IPAddress ipAddress;
    private ScriptUser user = null;
    private Status status;
    private boolean hasBadCharacters = false, authenticated = false;
    private int passwordAttempts = 0;
    private long timeout = 0, joinTime = 0;

    /**
     * Default constructor for the object.
     *
     * @param username name of the player.
     */
    public OdinUser(final String username) {
        OdinManager.getLogger().debug("Creating new OdinUser instance for username '" + username + "'.");
        this.username = username;
        this.hasBadCharacters = MainUtils.hasBadCharacters(username, OdinManager.getConfig().getString("filter.username"));
        if (OdinManager.getStorage().isCachedUser(username)) {
            /* TODO */
            OdinUser user = OdinManager.getStorage().getCachedUser(this.username);
            this.username = user.getUsername();
            this.user = user.getUser();
            this.status = user.getStatus();
            this.ipAddress = user.getIP();
        }
    }

    public void save() {
        OdinManager.getLogger().debug("Saving username '" + this.username + "'.");
        OdinManager.getStorage().putCachedUser(this);
    }

    public void sync() {
        OdinManager.getLogger().debug("Running sync for username '" + this.username + "'.");
        //TODO
    }
    
    public String getUsername() {
        return this.username;
    }

    public String getName() {
        return this.username;
    }
    
    public ScriptUser getUser() {
        return this.user;
    }
    
    public void setUser() {
        try {
            this.user = OdinManager.getScript().getUser(this.username);
        } catch (ScriptException e) {
            OdinManager.getLogger().stackTrace(e);
        } catch (SQLException e) {
            OdinManager.getLogger().stackTrace(e);
        }
    }

    public boolean isLinked() {
        return OdinManager.getStorage().hasLinkedUsername(this.username);
    }

    public String getLinkedName() {
        if (this.username == null && OdinManager.getStorage().hasLinkedUsername(this.username)) {
            String username = OdinManager.getStorage().getLinkedUsername(this.username);
            this.linkedUsername = username;
            return username;
        }
        return this.username;
    }

    public boolean isAuthenticated() {
        if (!this.authenticated && OdinManager.getStorage().isAuthenticated(this.username)) {
            this.authenticated = true;
            return true;
        }
        return this.authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            OdinManager.getStorage().putAuthenticated(this.username);
            if (OdinManager.getConfig().getBoolean("session.enabled")) {
                setSession();
            }
        } else {
            logout();
        }
    }

    public boolean logout() {
        if (OdinManager.getStorage().isAuthenticated(this.username)) {
            OdinManager.getStorage().removeAuthenticated(this.username);
            this.passwordAttempts = 0;
            this.timeout = 0;
            return true;
        }
        return false;
    }

    public boolean login(String password) {
        try {
            if (!OdinManager.getStorage().isAuthenticated(this.username)) {
                if (isLinked() && OdinManager.getScript().authenticate(getLinkedName(), password)) {
                    OdinManager.getStorage().putAuthenticated(this.username);
                    this.authenticated = true;
                    return true;
                } else if (OdinManager.getScript().authenticate(this.username, password)) {
                    OdinManager.getStorage().putAuthenticated(this.username);
                    this.authenticated = true;
                    return true;
                }
            }
        } catch (ScriptException e) {
            OdinManager.getLogger().stackTrace(e);
        }
        return false;
    }

    public void login() {
        OdinManager.getStorage().putAuthenticated(this.username);
        this.authenticated = true;
    }

    public boolean unlink() {
        if (this.linkedUsername != null || OdinManager.getStorage().hasLinkedUsername(this.username)) {
            OdinManager.getStorage().removeLinkedUsername(this.username);
            this.linkedUsername = null;
            return true;
        }
        return false;
    }

    public void link(String name) {
        //TODO: link
    }

    public IPAddress getIP() {
        return this.ipAddress;
    }
    
    public void setIP(String ip) {
        this.ipAddress = IPAddress.valueOf(ip);
    }

    public boolean hasSession() {
        //TODO
        return OdinManager.getStorage().hasSession(this);
    }
    
    public long getSessionTime() {
        if (OdinManager.getConfig().getBoolean("session.enabled") && hasSession()) {
            return OdinManager.getStorage().getSessionTime(this);
        }
        return 0;
    }
    
    public void setSession() {
        if (OdinManager.getConfig().getBoolean("session.enabled")) {
            OdinManager.getStorage().putSession(this);
        }
    }

    public boolean isRegistered() {
        if (this.status != null) {
            switch (this.status) {
                case Authenticated:
                    return true;
                case Registered:
                    return true;
                case Guest:
                    return false;
            }
        } else try {
            if (OdinManager.getScript().getScript().isRegistered(this.username)) {
                this.status = Status.Registered;
                return true;
            }
        } catch (ScriptException e) {
            OdinManager.getLogger().stackTrace(e);
            return false;
        }
        this.status = Status.Guest;
        return false;
    }

    public boolean isGuest() {
        if (this.status != null) {
            return this.status.equals(Status.Guest);
        } else {
            if (isRegistered()) {
                this.status = Status.Guest;
                return true;
            } else {
                this.status = Status.Registered;
            }
        }
        return false;
    }

    public Status getStatus() {
        return this.status;
    }

    public enum Status {
        Guest, Registered, Authenticated
    }

    public boolean hasMinLength() {
        OdinManager.getLogger().debug("Checking (username.minimum) for '" + this.username + "' length is '" + this.username.length() + "'. " +
                                      "'" + this.username.length() + "' < '" + OdinManager.getConfig().getInt("username.minimum") + "': " +
                                      "'" + (this.username.length() < OdinManager.getConfig().getInt("username.minimum")) + "'");
        return this.username.length() > OdinManager.getConfig().getInt("username.minimum");
    }

    public boolean hasMaxLength() {
        OdinManager.getLogger().debug("Checking (username.maximum) for '" + this.username + "' length is '" + this.username.length() + "'. " +
                                      "'" + this.username.length() + "' > '" + OdinManager.getConfig().getInt("username.maximum") + "': " +
                                      "'" + (this.username.length() > OdinManager.getConfig().getInt("username.maximum")) + "'");
        return this.username.length() < OdinManager.getConfig().getInt("username.maximum");
    }
    
    public long getJoinTime() {
        return this.joinTime;
    }
    
    public void setJoinTime() {
        this.joinTime = System.currentTimeMillis();
    }

    public void setJoinTime(long time) {
        this.joinTime = time;
    }
    
    public int getPasswordAttempts() {
        return this.passwordAttempts;
    }

    public void setPasswordAttempts(int attempts) {
        this.passwordAttempts = attempts;
    }

    public void clearPasswordAttempts() {
        this.passwordAttempts = 0;
    }

    public void increasePasswordAttempts() {
        this.passwordAttempts++;
    }
    
    public boolean hasBadCharacters() {
        return this.hasBadCharacters;
    }
    
    public boolean isFilterWhitelisted() {
        return OdinManager.getConfig().getString("filter.whitelist").contains(getUsername());
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void setTimeout() {
        this.timeout = System.currentTimeMillis();
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void storeInventory(InventoryItem[] inventory, InventoryItem[] armor) {
        OdinManager.getInventories().setInventory(getUsername(), inventory);
        OdinManager.getInventories().setArmor(getUsername(), armor);
    }
}
