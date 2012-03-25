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
package com.craftfire.authdb.layer.bukkit.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class AuthDBPlayerKickEvent extends AuthDBPlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String reason;
    private boolean cancel;

    public AuthDBPlayerKickEvent(final Player player, String reason) {
        super(player);
        this.reason = reason;
        this.cancel = false;
    }

    /**
     * Returns the reason for the kick
     *
     * @return reason for the kick
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Sets the reason for the kick
     *
     * @param reason reason for the kick
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
