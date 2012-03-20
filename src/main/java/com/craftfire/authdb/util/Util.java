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
package com.craftfire.authdb.util;

import com.craftfire.authdb.managers.AuthDBManager;
import com.craftfire.authdb.managers.AuthDBUser;

public class Util {
    public static AuthDBUser getUser(String username) {
        return new AuthDBUser(username);
    }

    public static boolean hasBadCharacters(String string, String filter) {
        char char1, char2;
        for (int i= 0; i < string.length(); i++) {
            char1 = string.charAt(i);
            for (int a= 0; a < filter.length(); a++) {
                char2 = filter.charAt(a);
                if (char1 == char2 || char1 == '\'' || char1 == '\"') {
                    /* TODO Logging */
                    return true;
                }
                a++;
            }
        }
        return false;
    }

    public static String stringToTimeLanguage(String timestring) {
        String[] split = timestring.split(" ");
        return stringToTimeLanguage(split[0], split[1]);
    }

    public static String stringToTimeLanguage(String length, String time) {
        int integer = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.days");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.day");
            }
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") ||
                   time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.hours");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.hour");
            }
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") ||
                   time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.minutes");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.minute");
            }
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") ||
                   time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.seconds");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.second");
            }
        } else if (time.equalsIgnoreCase("milliseconds") || time.equalsIgnoreCase("millisecond") ||
                   time.equalsIgnoreCase("milli") || time.equalsIgnoreCase("ms")) {
            if (integer > 1) {
                return AuthDBManager.msgMngr.getString("Core.time.milliseconds");
            } else {
                return AuthDBManager.msgMngr.getString("Core.time.millisecond");
            }
        }
        return time;
    }

    public static int stringToTicks(String string) {
        String[] split = string.split(" ");
        String length = split[0];
        String time = split[1].toLowerCase();
        int lengthint = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            return lengthint * 1728000;
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") ||
                   time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            return lengthint * 72000;
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") ||
                   time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            return lengthint * 1200;
        } else if (time.equalsIgnoreCase("seconds") || time.equalsIgnoreCase("seconds") ||
                   time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            return lengthint * 20;
        }
        return 0;
    }

    public static int stringToSeconds(String string) {
        String[] split = string.split(" ");
        String length = split[0];
        String time = split[1].toLowerCase();
        int lengthint = Integer.parseInt(length);
        if (time.equalsIgnoreCase("days") || time.equalsIgnoreCase("day") || time.equalsIgnoreCase("d")) {
            return lengthint * 86400;
        } else if (time.equalsIgnoreCase("hours") || time.equalsIgnoreCase("hour") || time.equalsIgnoreCase("hr") ||
                   time.equalsIgnoreCase("hrs") || time.equalsIgnoreCase("h")) {
            return lengthint * 3600;
        } else if (time.equalsIgnoreCase("minute") || time.equalsIgnoreCase("minutes") ||
                   time.equalsIgnoreCase("min") || time.equalsIgnoreCase("mins") || time.equalsIgnoreCase("m")) {
            return lengthint * 60;
        } else if (time.equalsIgnoreCase("second") || time.equalsIgnoreCase("seconds") ||
                   time.equalsIgnoreCase("sec") || time.equalsIgnoreCase("s")) {
            return lengthint;
        }
        return 0;
    }
}
