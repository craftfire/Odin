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
package com.craftfire.odin.managers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import com.craftfire.bifrost.classes.general.ScriptUser;
import com.craftfire.bifrost.exceptions.ScriptException;
import com.craftfire.commons.ip.IPAddress;
import com.craftfire.odin.managers.inventory.InventoryItem;
import com.craftfire.odin.managers.storage.StoredOdinUser;
import com.craftfire.odin.util.MainUtils;

public class OdinUser {
    private final String username;
    private StoredOdinUser storedUser;
    private String linkedUsername;
    private IPAddress ipAddress;
    private ScriptUser user = null;
    private Status status;
    private boolean hasBadCharacters = false, authenticated = false;
    private int passwordAttempts = 0;
    private long timeout = 0, joinTime = 0, reloadTime = 0;

    /**
     * Default constructor for the object.
     *
     * @param username name of the player.
     */
    public OdinUser(String username, InetAddress ipAddress) {
        this(username, IPAddress.valueOf(ipAddress));
    }

    /**
     * Default constructor for the object.
     *
     * @param username name of the player.
     */
    public OdinUser(String username, InetSocketAddress ipAddress) {
        if (username == null) {
            throw new IllegalArgumentException("Parameter for OdinUser username cannot be null!");
        }
        this.username = username;
        try {
            this.storedUser = OdinManager.getStorage().getUser(username);
        } catch (SQLException e) {
            OdinManager.getLogger().error("Could not grab user '" + username + "' from storage.");
            OdinManager.getLogger().stackTrace(e);
        }
        if (ipAddress != null) {
            new OdinUser(username, IPAddress.valueOf(ipAddress.getAddress()));
        } else {
            new OdinUser(username, (IPAddress) null);
        }
    }

    /**
     * Default constructor for the object.
     *
     * @param username name of the player.
     */
    public OdinUser(String username, String ipAddress) {
        this(username, IPAddress.valueOf(ipAddress));
    }

    /**
     * Default constructor for the object.
     *
     * @param username name of the player.
     */
    public OdinUser(final String username, IPAddress ipAddress) {
        if (username == null) {
            throw new IllegalArgumentException("Parameter for OdinUser username cannot be null!");
        }
        OdinManager.getLogger().debug("Creating new OdinUser instance for username '" + username + "'.");
        this.username = username;
        this.ipAddress = ipAddress;
        this.hasBadCharacters = MainUtils.hasBadCharacters(username, OdinManager.getConfig().getString("filter.username"));
        try {
            this.storedUser = OdinManager.getStorage().getUser(username);
            this.storedUser.setIPAddress(ipAddress);
            this.reloadTime = this.storedUser.getReloadTime();
        } catch (SQLException e) {
            OdinManager.getLogger().error("Could not grab user '" + username + "' from storage.");
            OdinManager.getLogger().stackTrace(e);
        }
        if (OdinManager.getStorage().isCachedUser(username)) {
            // TODO
            OdinUser cachedUser = OdinManager.getStorage().getCachedUser(username);
            this.user = cachedUser.getUser();
            this.status = cachedUser.getStatus();
            if (cachedUser.getIP() != null) {
                this.ipAddress = cachedUser.getIP();
            }
            this.storedUser = cachedUser.getStoredUser();
        }
    }

    public void save() {
        OdinManager.getLogger().debug("Saving username '" + this.username + "'.");
        OdinManager.getStorage().putCachedUser(this);
        getStoredUser().save();
        // TODO
    }

    public void sync() {
        OdinManager.getLogger().debug("Running sync for username '" + this.username + "'.");
        try {
            ScriptUser user = OdinManager.getScript().getUser(this.username);
            if (this.storedUser == null) {
                OdinManager.getLogger().debug("Could not find a stored user for '" + this.username + "'.");
                return;
            }
            if (!this.storedUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                OdinManager.getLogger().debug("Syncing the email from the database to local storage for user '"
                                             + this.username + "'.");
                this.storedUser.setEmail(user.getEmail());
            }
            if (!this.storedUser.getPassword().equalsIgnoreCase(user.getPassword())) {
                OdinManager.getLogger().debug("Syncing the password from the database to local storage for user '"
                                            + this.username + "'.");
                this.storedUser.setEmail(user.getEmail());
            }
            if (!this.storedUser.getPasswordSalt().equalsIgnoreCase(user.getPasswordSalt())) {
                OdinManager.getLogger().debug("Syncing the password salt from the database to local storage for user '"
                                            + this.username + "'.");
                this.storedUser.setEmail(user.getEmail());
            }
        } catch (ScriptException e) {
            OdinManager.getLogger().error("Failed to sync user '" + this.username + "': " + e.getMessage());
            OdinManager.getLogger().stackTrace(e);
        } catch (SQLException e) {
            OdinManager.getLogger().error("Failed to sync user '" + this.username + "': " + e.getMessage());
            OdinManager.getLogger().stackTrace(e);
        }
        // TODO
    }

    public StoredOdinUser getStoredUser() {
        return this.storedUser;
    }

    public String getUsername() {
        return this.username;
    }

    public String getName() {
        return this.username;
    }

    public String getEmail() {
        return this.storedUser.getEmail();
    }

    public void setEmail(String email) {
        this.storedUser.setEmail(email);
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
        if (this.linkedUsername == null && OdinManager.getStorage().hasLinkedUsername(this.username)) {
            this.linkedUsername = OdinManager.getStorage().getLinkedUsername(this.username);
        }
        return this.linkedUsername;
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
            OdinManager.getLogger().debug("User '" + getUsername() + "' is being logged out.");
            OdinManager.getStorage().removeAuthenticated(this.username);
            this.passwordAttempts = 0;
            this.timeout = 0;
            this.authenticated = false;
            return true;
        }
        OdinManager.getLogger().debug("User '" + getUsername() + "' was not authenticated, and was therefore not logged out.");
        return false;
    }

    public boolean login(String password) {
        // TODO: Add debug
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
        // TODO: Add debug
        OdinManager.getStorage().putAuthenticated(this.username);
        this.authenticated = true;
    }

    public boolean isActivated() {
        return getUser().isActivated();
    }

    public void activate() {
        OdinManager.getLogger().debug("Activating user: '" + getUsername() + "'.");
        getUser().setActivated(true);
    }

    public void deactivate() {
        OdinManager.getLogger().debug("Deactivating user: '" + getUsername() + "'.");
        getUser().setActivated(false);
    }

    public boolean unlink() {
        // TODO: Add debug
        if (this.linkedUsername != null || OdinManager.getStorage().hasLinkedUsername(this.username)) {
            getStoredUser().setLinkedName("");
            OdinManager.getStorage().removeLinkedUsername(this.username);
            this.linkedUsername = null;
            return true;
        }
        return false;
    }

    public void link(String name) {
        // TODO: Link
    }

    public IPAddress getIP() {
        if (this.ipAddress == null) {
            return IPAddress.valueOf("127.0.0.1");
        } else {
            return this.ipAddress;
        }
    }

    public void setIP(String ip) {
        this.ipAddress = IPAddress.valueOf(ip);
    }

    public boolean hasSession() {
        // TODO
        return OdinManager.getStorage().hasSession(this);
    }

    public long getSessionTime() {
        if (OdinManager.getConfig().getBoolean("session.enabled") && hasSession()) {
            return OdinManager.getStorage().getSessionTime(this);
        }
        return 0;
    }

    public void setSession() {
        getStoredUser().setSessionTime();
        if (OdinManager.getConfig().getBoolean("session.enabled")) {
            OdinManager.getStorage().putSession(this);
        }
    }

    public long getReloadTime() {
        return this.reloadTime;
    }

    public void setReloadTime() {
        this.reloadTime = System.currentTimeMillis();
        getStoredUser().setReloadTime();
    }

    public void setReloadTime(long reloadTime) {
        this.reloadTime = reloadTime;
        getStoredUser().setReloadTime(reloadTime);
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
                getStoredUser().setRegistered(true);
                this.status = Status.Registered;
                return true;
            }
        } catch (ScriptException e) {
            OdinManager.getLogger().stackTrace(e);
            return false;
        }
        getStoredUser().setRegistered(false);
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
