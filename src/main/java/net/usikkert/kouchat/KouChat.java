
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

package net.usikkert.kouchat;

import net.usikkert.kouchat.argument.Argument;
import net.usikkert.kouchat.argument.ArgumentParser;
import net.usikkert.kouchat.ui.UIChoice;
import net.usikkert.kouchat.ui.UIException;
import net.usikkert.kouchat.ui.UIFactory;
import net.usikkert.kouchat.util.LogInitializer;
import net.usikkert.kouchat.util.UncaughtExceptionLogger;

/**
 * This class contains KouChat's main method.
 *
 * It prints out some information at the console, and
 * parses the arguments, if any.
 *
 * Two different User Interfaces can be loaded from here.
 * Swing is the default, and a console version can be loaded
 * by using the --console argument.
 *
 * @author Christian Ihle
 */
public final class KouChat {

    /**
     * Private constructor. This class should be run like an application,
     * not instantiated.
     */
    private KouChat() {

    }

    /**
     * The main method, for starting the application.
     *
     * <p>See {@link Argument} for the supported arguments.</p>
     *
     * @param arguments The arguments given when starting KouChat.
     */
    public static void main(final String[] arguments) {
        System.out.println(Constants.APP_NAME + " v" + Constants.APP_VERSION);
        System.out.println("By " + Constants.AUTHOR_NAME + " - " + Constants.AUTHOR_MAIL + " - " + Constants.APP_WEB);

        final ArgumentParser argumentParser = new ArgumentParser(arguments);

        if (argumentParser.getNumberOfArguments() == 0) {
            System.out.println("Use " + Argument.HELP + " for more information");
        }

        if (argumentParser.getNumberOfUnknownArguments() > 0) {
            System.out.println("\nUnknown arguments: " + argumentParser.getUnknownArguments() +
                    ". Use " + Argument.HELP + " for more information");
            return;
        }

        if (argumentParser.hasArgument(Argument.VERSION)) {
            return;
        }

        if (argumentParser.hasArgument(Argument.HELP)) {
            System.out.println("\nArguments:");
            System.out.println(Argument.getArgumentsAsString());
            return;
        }

        new LogInitializer(argumentParser.hasArgument(Argument.DEBUG));
        // Initialize as early as possible to catch all exceptions
        new UncaughtExceptionLogger();

        loadUserInterface(argumentParser);
    }

    private static void loadUserInterface(final ArgumentParser argumentParser) {
        try {
            if (!argumentParser.hasArgument(Argument.CONSOLE)) {
                System.out.println("\nLoading Swing User Interface\n");
                new UIFactory().loadUI(UIChoice.SWING);
            }

            else {
                System.out.println("\nLoading Console User Interface\n");
                new UIFactory().loadUI(UIChoice.CONSOLE);
            }
        }

        catch (final UIException e) {
            System.err.println(e);
        }
    }
}
