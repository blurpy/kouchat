
/***************************************************************************
 *   Copyright 2006-2018 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Parses the supported startup arguments for the application.
 *
 * @see Argument
 *
 * @author Christian Ihle
 */
public class ArgumentParser {

    /** Looks for a pattern of type <code>argument=value</code>. */
    private static final Pattern VALUE_REGEX = Pattern.compile(".+=(.+)");

    private final String[] originalArguments;
    private final List<ParsedArgument> parsedArguments;

    /**
     * Creates a parser for the arguments.
     *
     * @param arguments The arguments to parse.
     */
    public ArgumentParser(final String[] arguments) {
        this.originalArguments = arguments;
        this.parsedArguments = new ArrayList<>();

        parseArguments();
    }

    /**
     * Checks if the parser found the argument.
     *
     * @param argument The argument to check for.
     * @return If the argument was found by the parser.
     */
    public boolean hasArgument(final Argument argument) {
        final ParsedArgument parsedArgument = getArgument(argument);

        return parsedArgument != null;
    }

    /**
     * Gets a parsed argument for the requested argument.
     *
     * @param argument The argument to get a parsed argument for.
     * @return The parsed argument, or <code>null</code> if none was found by the parser.
     */
    @Nullable
    public ParsedArgument getArgument(final Argument argument) {
        Validate.notNull(argument, "Argument can not be null");

        for (final ParsedArgument parsedArgument : parsedArguments) {
            if (parsedArgument.isEqualTo(argument)) {
                return parsedArgument;
            }
        }

        return null;
    }

    /**
     * Gets the number of arguments that was found by the parser.
     *
     * @return The number of arguments.
     */
    public int getNumberOfArguments() {
        return parsedArguments.size();
    }

    /**
     * Gets all the parsed arguments.
     *
     * @return All the arguments.
     */
    public List<ParsedArgument> getArguments() {
        return parsedArguments;
    }

    /**
     * Gets a list of all the parsed arguments that did not match any of the valid arguments.
     *
     * @return All unknown arguments.
     */
    public List<ParsedArgument> getUnknownArguments() {
        final ArrayList<ParsedArgument> unknownArguments = new ArrayList<>();

        for (final ParsedArgument parsedArgument : parsedArguments) {
            if (parsedArgument.isEqualTo(Argument.UNKNOWN)) {
                unknownArguments.add(parsedArgument);
            }
        }

        return unknownArguments;
    }

    /**
     * Gets the number of all the parsed arguments that did not match any of the valid arguments.
     *
     * @return Number of unknown arguments.
     */
    public int getNumberOfUnknownArguments() {
        return getUnknownArguments().size();
    }

    private void parseArguments() {
        if (originalArguments == null) {
            return;
        }

        for (final String originalArgument : originalArguments) {
            parsedArgument(originalArgument);
        }
    }

    private void parsedArgument(final String originalArgument) {
        parsedArguments.add(
                new ParsedArgument(originalArgument, getArgument(originalArgument), getValue(originalArgument)));
    }

    private Argument getArgument(final String originalArgument) {
        for (final Argument argument : Argument.values()) {
            if (argument.isEqualTo(originalArgument)) {
                return argument;
            }
        }

        return Argument.UNKNOWN;
    }

    @Nullable
    private String getValue(final String originalArgument) {
        final Matcher valueMatcher = VALUE_REGEX.matcher(originalArgument);

        if (valueMatcher.matches()) {
            return valueMatcher.group(1);
        }

        else {
            return null;
        }
    }
}
