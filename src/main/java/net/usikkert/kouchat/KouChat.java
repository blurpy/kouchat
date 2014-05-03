
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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

package net.usikkert.kouchat;

import net.usikkert.kouchat.argument.Argument;
import net.usikkert.kouchat.argument.ArgumentParser;
import net.usikkert.kouchat.argument.ArgumentResponder;
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
        final ArgumentParser argumentParser = new ArgumentParser(arguments);
        final ArgumentResponder argumentResponder = new ArgumentResponder(argumentParser, System.out);

        if (!argumentResponder.respond()) {
            return;
        }

        new LogInitializer(argumentParser.hasArgument(Argument.DEBUG));
        // Initialize as early as possible to catch all exceptions
        new UncaughtExceptionLogger();

        setSettingsFromArguments(argumentParser);
        loadUserInterface(argumentParser);
    }

    private static void setSettingsFromArguments(final ArgumentParser argumentParser) {
        // Using system properties instead of using Settings directly to avoid loading the settings to early,
        // so client property doesn't end up being null.
        System.setProperty(Constants.SETTINGS_ALWAYS_LOG, Boolean.toString(argumentParser.hasArgument(Argument.ALWAYS_LOG)));
        System.setProperty(Constants.SETTINGS_NO_PRIVATE_CHAT, Boolean.toString(argumentParser.hasArgument(Argument.NO_PRIVATE_CHAT)));

        if (argumentParser.hasArgument(Argument.LOG_LOCATION)) {
            System.setProperty(Constants.SETTINGS_LOG_LOCATION, argumentParser.getArgument(Argument.LOG_LOCATION).getValue());
        }
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
