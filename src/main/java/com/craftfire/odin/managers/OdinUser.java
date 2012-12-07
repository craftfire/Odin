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
    private String username;
    private ScriptUser user = null;
    private Status status;
    private String ip;
    private boolean badcharacters;

    /**
     * Default constructor for the object.
     *
     * @param username name of the player.
     */
    public OdinUser(final String username) {
        this.username = username;
        this.badcharacters = MainUtils.hasBadCharacters(username, OdinManager.getConfig().getString("filter.username"));
        if (OdinManager.getUserStorage().containsKey(this.username)) {
            /* TODO */
            OdinUser user = OdinManager.getUserStorage().get(this.username);
            this.username = user.getUsername();
            this.user = user.getUser();
            this.status = user.getStatus();
            this.ip = user.getIP();
        }
    }

    public void save() {
        OdinManager.getUserStorage().put(this.username, this);
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
        return OdinManager.getLinkedUsernames().containsKey(this.username);
    }

    public String getLinkedName() {
        if (OdinManager.getLinkedUsernames().containsKey(this.username)) {
            return OdinManager.getLinkedUsernames().get(this.username);
        }
        return null;
    }

    public boolean isAuthenticated() {
        return OdinManager.getAuthenticatedUsers().contains(this.username);
    }

    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            OdinManager.getAuthenticatedUsers().add(this.username);
            if (OdinManager.getConfig().getBoolean("session.enabled")) {
                setSession();
            }
        } else {
            logout();
        }
    }

    public boolean logout() {
        if (OdinManager.getAuthenticatedUsers().contains(this.username)) {
            OdinManager.getAuthenticatedUsers().remove(this.username);
            if (OdinManager.getUserPasswordAttempts().containsKey(this.username)) {
                OdinManager.getUserPasswordAttempts().remove(this.username);
            }
            if (OdinManager.getUserTimeouts().contains(this.username)) {
                OdinManager.getUserTimeouts().remove(this.username);
            }
            return true;
        }
        return false;
    }

    public boolean login(String password) {
        try {
            if (!OdinManager.getAuthenticatedUsers().contains(this.username)) {
                if (isLinked()) {
                    if (OdinManager.getScript().authenticate(getLinkedName(), password)) {
                        OdinManager.getAuthenticatedUsers().add(this.username);
                        return true;
                    }
                } else {
                    if (OdinManager.getScript().authenticate(this.username, password)) {
                        OdinManager.getAuthenticatedUsers().add(this.username);
                        return true;
                    }
                }
            }
        } catch (ScriptException e) {
            OdinManager.getLogger().stackTrace(e);
        }
        return false;
    }

    public void login() {
        if (!OdinManager.getAuthenticatedUsers().contains(this.username)) {
            OdinManager.getAuthenticatedUsers().add(this.username);
        }
    }

    public boolean unlink() {
        if (OdinManager.getLinkedUsernames().containsKey(this.username)) {
            OdinManager.getLinkedUsernames().remove(this.username);
            return true;
        }
        return false;
    }

    public String getIP() {
        return ip;
    }
    
    public void setIP(String ip) {
        this.ip = ip;
    }

    public boolean hasSession() {
        //TODO: Check storage for session.
        return OdinManager.getUserSessions().containsKey(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip));
    }
    
    public long getSessionTime() {
        if (OdinManager.getConfig().getBoolean("session.enabled") && hasSession()) {
            return OdinManager.getUserSessions().get(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip));
        }
        return 0;
    }
    
    public void setSession() {
        if (OdinManager.getConfig().getBoolean("session.enabled")) {
            OdinManager.getUserSessions().put(CraftCommons.encrypt(Encryption.MD5, this.username + this.ip), new Date().getTime() / 1000);
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
        Guest,
        Registered,
        Authenticated
    }

    public boolean hasMinLength() {
        return this.username.length() < OdinManager.getConfig().getInt("username.minimum");
    }

    public boolean hasMaxLength() {
        return this.username.length() > OdinManager.getConfig().getInt("username.maximum");
    }
    
    public long getJoinTime() {
        if (OdinManager.getPlayerJoins().containsKey(getUsername())) {
            return OdinManager.getPlayerJoins().get(getUsername());
        }
        return 0;
    }
    
    public void setJoinTime() {
        OdinManager.getPlayerJoins().put(getUsername(), System.currentTimeMillis());
    }

    public void setJoinTime(long time) {
        OdinManager.getPlayerJoins().put(getUsername(), time);
    }
    
    public int getPasswordAttempts() {
        if (OdinManager.getUserPasswordAttempts().containsKey(getUsername())) {
            return OdinManager.getUserPasswordAttempts().get(getUsername());
        }
        return 0;
    }

    public void setPasswordAttempts(int attempts) {
        OdinManager.getUserPasswordAttempts().put(getUsername(), attempts);
    }

    public void clearPasswordAttempts() {
        OdinManager.getUserPasswordAttempts().put(getUsername(), 0);
    }

    public void increasePasswordAttempts() {
        if (OdinManager.getUserPasswordAttempts().containsKey(getUsername())) {
            OdinManager.getUserPasswordAttempts().put(getUsername(),
                                                   OdinManager.getUserPasswordAttempts().get(getUsername()) + 1);
        } else {
            OdinManager.getUserPasswordAttempts().put(getUsername(), 1);
        }
    }
    
    public boolean hasBadCharacters() {
        return this.badcharacters;
    }
    
    public boolean isFilterWhitelisted() {
        return OdinManager.getConfig().getString("filter.whitelist").contains(getUsername());
    }

    public void setTimeout() {
        OdinManager.getUserTimeouts().add(getUsername());
    }

    public void storeInventory(InventoryItem[] inventory, InventoryItem[] armor) {
        OdinManager.getInventories().setInventory(getUsername(), inventory);
        OdinManager.getInventories().setArmor(getUsername(), armor);
    }
}
