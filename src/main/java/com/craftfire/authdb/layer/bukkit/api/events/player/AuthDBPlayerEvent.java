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

import com.craftfire.authdb.layer.bukkit.api.events.AuthDBEvent;
import com.craftfire.authdb.layer.bukkit.managers.AuthDBPlayer;
import com.craftfire.authdb.layer.bukkit.util.Util;
import org.bukkit.entity.Player;

public abstract class AuthDBPlayerEvent extends AuthDBEvent {
    protected Player player;
    protected AuthDBPlayer authDBPlayer;
    
    public AuthDBPlayerEvent(final Player player) {
        this.player = player;
        this.authDBPlayer = Util.getPlayer(player);
    }

    /**
     * Returns the player involved in this event
     *
     * @return Player who is involved in this event
     */
    public final Player getPlayer() {
        return player;
    }

    /**
     * Returns the AuthDB involved in this event
     *
     * @return AuthDB player involved in this event
     */
    public final AuthDBPlayer getAuthDBPlayer() {
        return this.authDBPlayer;
    }
}
