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

public class AuthDBPlayerMessageEvent extends AuthDBPlayerEvent {
    protected Player player;
    protected AuthDBPlayer authDBPlayer;
    protected String sender, message;

    public AuthDBPlayerMessageEvent(Player receiver, String sender, String message) {
        this.player = receiver;
        this.authDBPlayer = Util.getPlayer(receiver);
        this.message = message;
        this.sender = sender;
    }

    /**
     * Returns the player who sent/received the message
     *
     * @return player who sent/received the message
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Returns the AuthDB player who received the message
     *
     * @return AuthDB player who received the message
     */
    public AuthDBPlayer getAuthDBPlayer() {
        return this.authDBPlayer;
    }

    /**
     * Returns the name of the player who sent the message.
     * This can be the server as well.
     *
     * @return name of the player who sent the message
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * Returns the message
     *
     * @return message message of the event
     */
    public String getMessage() {
        return this.message;
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
