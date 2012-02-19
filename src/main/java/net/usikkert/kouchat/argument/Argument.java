
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
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * The valid startup arguments for the application.
 *
 * @author Christian Ihle
 */
public enum Argument {

    CONSOLE("-c", "--console", "Starts " + Constants.APP_NAME + " in console mode", false),
    DEBUG("-d", "--debug", "Starts " + Constants.APP_NAME + " with verbose debug output enabled", false),
    HELP("-h", "--help", "Shows this help message", false),
    VERSION("-v", "--version", "Shows version information", false),
    NO_PRIVATE_CHAT(null, "--no-private-chat", "Disables private chat", false),
    ALWAYS_LOG(null, "--always-log", "Enables logging, without option to disable", false),
    LOG_LOCATION(null, "--log-location", "Location to store log files", true),
    UNKNOWN(null, null, null, false);

    private final String shortArgumentName;
    private final String fullArgumentName;
    private final String description;
    private final boolean requiresArgument;

    Argument(final String shortArgumentName, final String fullArgumentName, final String description,
             final boolean requiresArgument) {
        this.shortArgumentName = shortArgumentName;
        this.fullArgumentName = fullArgumentName;
        this.description = description;
        this.requiresArgument = requiresArgument;
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

    @Override
    public String toString() {
        return fullArgumentName;
    }

    String getArgumentName() {
        if (shortArgumentName != null) {
            return fullArgumentName + " (" + shortArgumentName + ")";
        }

        if (requiresArgument) {
            return fullArgumentName + "=<arg>";
        }

        return fullArgumentName;
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
            builder.append("\n ");
            builder.append(Tools.postPadString(argument.getArgumentName(), 22));
            builder.append(argument.description);
        }

        return builder.toString().replaceFirst("\n", "");
    }

    static Argument[] getValidArguments() {
        final Argument[] arguments = Argument.values();
        return Arrays.copyOf(arguments, arguments.length - 1); // Skips UNKNOWN
    }
}
