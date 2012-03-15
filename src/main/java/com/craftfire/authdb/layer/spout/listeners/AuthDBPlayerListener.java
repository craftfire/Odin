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
package com.craftfire.authdb.layer.spout.listeners;

import com.craftfire.authdb.layer.spout.AuthDB;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.player.PlayerChatEvent;
import org.spout.api.event.player.PlayerInteractEvent;
import org.spout.api.event.player.PlayerJoinEvent;
import org.spout.api.event.player.PlayerLeaveEvent;
import org.spout.api.event.server.PreCommandEvent;

public class AuthDBPlayerListener implements Listener {
    private AuthDB plugin;

    public AuthDBPlayerListener(AuthDB plugin) {
        this.plugin = plugin;
    }

    @EventHandler(order = Order.EARLIEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
    }

    @EventHandler(order = Order.EARLIEST)
    public void onPlayerQuit(PlayerLeaveEvent event) {
    }

    @EventHandler(order = Order.EARLIEST)
    public void onPlayerCommandPreprocess(PreCommandEvent event) {
    }

    @EventHandler(order = Order.EARLIEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
    }

    @EventHandler(order = Order.EARLIEST)
    public void onPlayerChat(PlayerChatEvent event) {
    }
}
