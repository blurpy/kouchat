
/***************************************************************************
 *   Copyright 2006-2009 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouchat.argument;

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.util.Validate;

/**
 * Parses the supported startup arguments for the application.
 *
 * @see Argument
 *
 * @author Christian Ihle
 */
public class ArgumentParser {

    private final String[] originalArguments;
    private final List<ParsedArgument> parsedArguments;

    /**
     * Creates a parser for the arguments.
     *
     * @param arguments The arguments to parse.
     */
    public ArgumentParser(final String[] arguments) {
        this.originalArguments = arguments;
        this.parsedArguments = new ArrayList<ParsedArgument>();

        parseArguments();
    }

    /**
     * Checks if the parser found the argument.
     *
     * @param argument The argument to check for.
     * @return If the argument was found by the parser.
     */
    public boolean hasArgument(final Argument argument) {
        Validate.notNull(argument, "Argument can not be null");

        for (final ParsedArgument parsedArgument : parsedArguments) {
            if (parsedArgument.isEqualTo(argument)) {
                return true;
            }
        }

        return false;
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

    private void parseArguments() {
        if (originalArguments == null) {
            return;
        }

        for (final String originalArgument : originalArguments) {
            parsedArgument(originalArgument);
        }
    }

    private void parsedArgument(final String originalArgument) {
        parsedArguments.add(new ParsedArgument(originalArgument, getArgument(originalArgument)));
    }

    private Argument getArgument(final String originalArgument) {
        for (final Argument argument : Argument.values()) {
            if (argument.isEqualTo(originalArgument)) {
                return argument;
            }
        }

        return Argument.UNKNOWN;
    }
}
