
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

package net.usikkert.kouchat.ui;

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;

import net.usikkert.kouchat.argument.Argument;
import net.usikkert.kouchat.argument.ArgumentParser;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.console.KouChatConsole;
import net.usikkert.kouchat.ui.swing.KouChatFrame;
import net.usikkert.kouchat.util.UncaughtExceptionLogger;
import net.usikkert.kouchat.util.Validate;

/**
 * This factory decides which User Interface to load, based on startup arguments.
 *
 * @author Christian Ihle
 */
public class UIFactory {

    private final ArgumentParser argumentParser;
    private final Settings settings;
    private final UncaughtExceptionLogger uncaughtExceptionLogger;
    private final ErrorHandler errorHandler;

    private boolean done;

    /**
     * Initializes the ui factory.
     *
     * @param argumentParser The arguments to use to select the ui to load.
     * @param settings The settings to use in the ui.
     * @param uncaughtExceptionLogger The exception logger to use in the ui.
     */
    public UIFactory(final ArgumentParser argumentParser, final Settings settings,
                     final UncaughtExceptionLogger uncaughtExceptionLogger) {
        Validate.notNull(argumentParser, "Argument parser can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(uncaughtExceptionLogger, "Uncaught exception logger can not be null");

        this.argumentParser = argumentParser;
        this.settings = settings;
        this.uncaughtExceptionLogger = uncaughtExceptionLogger;
        this.errorHandler = ErrorHandler.getErrorHandler();
    }

    /**
     * Loads the User Interface based on the startup arguments.
     *
     * <p>Loads the console user interface if the argument <code>--console</code> is present.
     * If not, the swing user interface is loaded.</p>
     *
     * @throws UIException If a ui has already been loaded, or if no graphical environment
     *                     was detected and swing was selected.
     */
    public void loadUI() throws UIException {
        if (done) {
            throw new UIException("Error: A User Interface has already been loaded.");
        }

        else {
            done = true;

            if (argumentParser.hasArgument(Argument.CONSOLE)) {
                loadConsoleUserInterface();
            }

            else {
                if (isHeadless()) {
                    throw new UIException("Error: The Swing User Interface could not be loaded" +
                            " because a graphical environment could not be detected.");
                }

                else {
                    final boolean startMinimized = argumentParser.hasArgument(Argument.MINIMIZED);
                    loadSwingUserInterface(startMinimized);
                }
            }
        }
    }

    boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

    void loadSwingUserInterface(final boolean startMinimized) {
        System.out.println("\nLoading Swing User Interface\n");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final KouChatFrame kouChatFrame = new KouChatFrame(settings, uncaughtExceptionLogger, errorHandler);
                kouChatFrame.start(startMinimized);
            }
        });
    }

    void loadConsoleUserInterface() {
        System.out.println("\nLoading Console User Interface\n");

        final KouChatConsole kouChatConsole = new KouChatConsole(settings, errorHandler);
        kouChatConsole.start();
    }
}
