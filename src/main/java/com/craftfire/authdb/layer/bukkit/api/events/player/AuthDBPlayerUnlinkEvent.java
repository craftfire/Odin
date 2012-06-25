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

public class AuthDBPlayerUnlinkEvent extends AuthDBPlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String linkedName;
    private boolean cancel;

    public AuthDBPlayerUnlinkEvent(Player player, String linkedName) {
        super(player);
        this.linkedName = linkedName;
        this.cancel = false;
    }

    /**
     * Returns the name of the linked user
     *
     * @return username of the linked name
     */
    public String getLinkedName() {
        return this.linkedName;
    }

    /**
     * Sets the name of the linked user
     *
     * @param linkedName username of the linked name
     */
    public void setLinkedName(String linkedName) {
        this.linkedName = linkedName;
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
