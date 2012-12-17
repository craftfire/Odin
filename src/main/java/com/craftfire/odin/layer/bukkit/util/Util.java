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
package com.craftfire.odin.layer.bukkit.util;


import com.craftfire.odin.layer.bukkit.managers.OdinPlayer;
import com.craftfire.odin.managers.OdinManager;
import org.bukkit.entity.Player;

public class Util {
    public static OdinPlayer getPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Parameter for getPlayer() cannot be null!");
        } else if (OdinManager.getStorage().isCachedUser(player.getName())) {
            return (OdinPlayer) OdinManager.getStorage().getCachedUser(player.getName());
        } else {
            OdinPlayer odinPlayer = new OdinPlayer(player);
            OdinManager.getStorage().putCachedUser(odinPlayer);
            return odinPlayer;
        }
    }
}
