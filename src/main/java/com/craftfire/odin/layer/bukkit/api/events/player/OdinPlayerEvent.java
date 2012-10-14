/*
 * This file is part of Odin <http://www.odin.com/>.
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
package com.craftfire.odin.layer.bukkit.api.events.player;

import com.craftfire.odin.layer.bukkit.api.events.OdinEvent;
import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.layer.bukkit.util.Util;
import org.bukkit.entity.Player;

public abstract class OdinPlayerEvent extends OdinEvent {
    protected Player player;
    protected OdinPlayer odinPlayer;
    
    public OdinPlayerEvent(final Player player) {
        this.player = player;
        this.odinPlayer = Util.getPlayer(player);
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
     * Returns the Odin involved in this event
     *
     * @return Odin player involved in this event
     */
    public final OdinPlayer getOdinPlayer() {
        return this.odinPlayer;
    }
}
