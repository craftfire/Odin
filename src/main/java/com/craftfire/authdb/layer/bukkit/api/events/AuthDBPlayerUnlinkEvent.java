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
package com.craftfire.authdb.layer.bukkit.api.events;

import com.craftfire.authdb.layer.bukkit.managers.AuthDBPlayer;
import com.craftfire.authdb.layer.bukkit.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AuthDBPlayerUnlinkEvent extends AuthDBPlayerEvent {
    protected Player player;
    protected AuthDBPlayer authDBPlayer;
    protected String linkedname;
    protected boolean successful;

    protected AuthDBPlayerUnlinkEvent(Player player, boolean successful, String linkedname) {
        this.player = player;
        this.authDBPlayer = Util.getPlayer(player);
        this.linkedname = linkedname;
        this.successful = successful;
    }

    /**
     * Get the player who unlinked
     *
     * @return player who unlinked
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the AuthDB player who unlinked
     *
     * @return AuthDB player who unlinked
     */
    public AuthDBPlayer getAuthDBPlayer() {
        return this.authDBPlayer;
    }

    /**
     * Get the name of the linked user
     *
     * @return username of the linked name
     */
    public String getLinkedname() {
        return this.linkedname;
    }

    /**
     * Returns a true if the unlink was successful.
     *
     * @return true if unlink was successful, false if not.
     */
    public boolean isSuccessful() {
        return this.successful;
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
