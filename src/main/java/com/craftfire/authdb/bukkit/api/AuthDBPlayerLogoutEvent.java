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
package com.craftfire.authdb.bukkit.api;

import com.craftfire.authdb.bukkit.managers.AuthDBBukkitPlayer;
import org.bukkit.event.HandlerList;

public class AuthDBPlayerLogoutEvent extends AuthDBPlayerEvent {
    protected AuthDBBukkitPlayer player;
    
    protected AuthDBPlayerLogoutEvent(AuthDBBukkitPlayer player) {
        this.player = player;
    }

    /**
     * Get the player who tried to logout.
     *
     * @return player who tried to login
     */
    public AuthDBBukkitPlayer getPlayer() {
        return this.player;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
