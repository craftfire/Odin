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
}
