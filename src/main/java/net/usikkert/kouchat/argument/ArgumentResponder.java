
/***************************************************************************
 *   Copyright 2006-2016 by Christian Ihle                                 *
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

import net.usikkert.kouchat.Constants;

/**
 * Responds to the startup arguments in the console.
 *
 * @author Christian Ihle
 */
public class ArgumentResponder {

    private final ArgumentParser argumentParser;

    public ArgumentResponder(final ArgumentParser argumentParser) {
        this.argumentParser = argumentParser;
    }

    /**
     * Prints the default information with version and contact details, and responds to the arguments.
     *
     * <p>Handles the following arguments:</p>
     * <ul>
     *   <li><code>--help</code> - prints details about supported arguments, and halts.</li>
     *   <li><code>--version</code> - prints nothing, and halts.</li>
     *   <li><code>Unknown arguments</code> - prints the unknown arguments, and halts.</li>
     * </ul>
     *
     * <p></p>
     *
     * @return <code>true</code> if startup should continue, <code>false</code> if startup should halt.
     */
    public boolean respond() {
        System.out.println(Constants.APP_NAME + " v" + Constants.APP_VERSION);
        System.out.println("By " + Constants.AUTHOR_NAME + " - " + Constants.AUTHOR_MAIL + " - " + Constants.APP_WEB);

        // No arguments - continue
        if (argumentParser.getNumberOfArguments() == 0) {
            System.out.println("Use " + Argument.HELP + " for more information");
            return true;
        }

        // Unknown arguments - halt
        if (argumentParser.getNumberOfUnknownArguments() > 0) {
            System.out.println("\nUnknown arguments: " + argumentParser.getUnknownArguments() +
                    ". Use " + Argument.HELP + " for more information");
            return false;
        }

        // --help - halt
        if (argumentParser.hasArgument(Argument.HELP)) {
            System.out.println("\nArguments:");
            System.out.println(Argument.getArgumentsAsString());
            return false;
        }

        // -- version - halt
        if (argumentParser.hasArgument(Argument.VERSION)) {
            return false;
        }

        // Unhandled arguments - continue
        return true;
    }
}
