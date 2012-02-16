
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

import java.util.Arrays;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.util.Validate;

/**
 * The valid startup arguments for the application.
 *
 * @author Christian Ihle
 */
public enum Argument {

    CONSOLE("-c", "--console", "starts " + Constants.APP_NAME + " in console mode"),
    DEBUG("-d", "--debug", "starts " + Constants.APP_NAME + " with verbose debug output enabled"),
    HELP("-h", "--help", "shows this help message"),
    VERSION("-v", "--version", "shows version information"),
    UNKNOWN(null, null, null);

    private final String shortArgumentName;
    private final String fullArgumentName;
    private final String description;

    Argument(final String shortArgumentName, final String fullArgumentName, final String description) {
        this.shortArgumentName = shortArgumentName;
        this.fullArgumentName = fullArgumentName;
        this.description = description;
    }

    /**
     * Checks if the argument in the parameter is equal to "this" enum argument.
     *
     * @param argument The argument to compare with.
     * @return If they are equal.
     */
    public boolean isEqualTo(final String argument) {
        Validate.notNull(argument, "Argument can not be null");

        return argument.equals(shortArgumentName) || argument.equals(fullArgumentName);
    }

    /**
     * Returns a formatted list of all the arguments with short name, full name and description.
     * One argument on each line.
     *
     * @return String with all the arguments.
     */
    public static String getArgumentsAsString() {
        final Argument[] arguments = getValidArguments();
        final StringBuilder builder = new StringBuilder();

        for (final Argument argument : arguments) {
            builder.append("\n ")
                   .append(argument.shortArgumentName)
                   .append(", ")
                   .append(argument.fullArgumentName)
                   .append(" \t ")
                   .append(argument.description);
        }

        return builder.toString().replaceFirst("\n", "");
    }

    static Argument[] getValidArguments() {
        final Argument[] arguments = Argument.values();
        return Arrays.copyOf(arguments, arguments.length - 1); // Skips UNKNOWN
    }
}
