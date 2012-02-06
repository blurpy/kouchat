
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.argument;

import net.usikkert.kouchat.util.Validate;

/**
 * The valid startup arguments for the application.
 *
 * @author Christian Ihle
 */
public enum Argument {

    HELP("-h", "--help"),
    DEBUG("-d", "--debug"),
    CONSOLE("-c", "--console"),
    VERSION("-v", "--version"),
    UNKNOWN(null, null);

    private final String shortArgumentName;
    private final String fullArgumentName;

    Argument(final String shortArgumentName, final String fullArgumentName) {
        this.shortArgumentName = shortArgumentName;
        this.fullArgumentName = fullArgumentName;
    }

    /**
     * Checks if the argument in the parameter is equal to "this" enum argument.
     *
     * @param argument The argument to compare with.
     * @return If they are equal.
     */
    public boolean isEqualTo(final String argument) {
        Validate.notNull(argument, "Argument can not be null");

        return argument.equalsIgnoreCase(shortArgumentName) || argument.equalsIgnoreCase(fullArgumentName);
    }
}
