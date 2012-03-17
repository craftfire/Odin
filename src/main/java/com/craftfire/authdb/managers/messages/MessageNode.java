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
package com.craftfire.authdb.managers.messages;

public enum MessageNode {
    core_time_millisecond ("core.time.millisecond", "millisecond"),
    core_time_milliseconds ("core.time.milliseconds", "milliseconds"),
    core_time_second ("core.time.second", "second"),
    core_time_seconds ("core.time.seconds", "seconds"),
    core_time_minute ("core.time.minute", "minute"),
    core_time_minutes ("core.time.minutes", "minutes"),
    core_time_hour ("core.time.hour", "hour"),
    core_time_hours ("core.time.hours", "hours"),
    core_time_day ("core.time.day", "day"),
    core_time_days ("core.time.days", "days"),

    core_database_failure ("core.database.failure",
                           "{RED}Database connection failed! Access is denied! Contact the server admin."),

    core_reload_success ("core.reload.success", "AuthDB has successfully reloaded!"),
    core_reload_failure ("core.reload.failure", "AuthDB could not be reloaded!");

    public String node;
    public String data;
    MessageNode(String node, String data) {
        this.data = data;
        this.node = node;
    }

}