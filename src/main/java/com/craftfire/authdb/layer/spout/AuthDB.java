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
package com.craftfire.authdb.layer.spout;

import com.craftfire.authdb.layer.spout.listeners.AuthDBPlayerListener;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.util.Named;

import java.util.logging.Logger;

public class AuthDB extends CommonPlugin implements Named {
    private static final Logger logger = Logger.getLogger("Minecraft.AuthDB");

    private static AuthDB instance;
    {
        instance = this;
    }

    public void onEnable() {
        logger.info("AuthDB " + getDescription().getVersion() + " enabled.");
        getDataFolder().mkdirs();
        registerEvents();
    }

    public void onDisable() {
        getGame().getScheduler().cancelTasks(this);
    }

    protected void registerEvents() {
        getGame().getEventManager().registerEvents(new AuthDBPlayerListener(this), this);
    }
}
