
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

package net.usikkert.kouchat;

import net.usikkert.kouchat.argument.Argument;
import net.usikkert.kouchat.argument.ArgumentParser;
import net.usikkert.kouchat.argument.ArgumentResponder;
import net.usikkert.kouchat.argument.ArgumentSettingsLoader;
import net.usikkert.kouchat.settings.PropertyFileSettingsLoader;
import net.usikkert.kouchat.settings.Settings;
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
        setSystemProperties();

        final ArgumentParser argumentParser = new ArgumentParser(arguments);
        final ArgumentResponder argumentResponder = new ArgumentResponder(argumentParser);

        if (!argumentResponder.respond()) {
            return;
        }

        new LogInitializer(argumentParser.hasArgument(Argument.DEBUG));
        // Initialize as early as possible to catch all exceptions
        final UncaughtExceptionLogger uncaughtExceptionLogger = new UncaughtExceptionLogger();

        final Settings settings = loadSettings(argumentParser);

        loadUserInterface(argumentParser, settings, uncaughtExceptionLogger);
    }

    private static void setSystemProperties() {
        // Move the menubar to the top of the screen on Mac OS X
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    private static Settings loadSettings(final ArgumentParser argumentParser) {
        final Settings settings = new Settings();

        final ArgumentSettingsLoader argumentSettingsLoader = new ArgumentSettingsLoader();
        argumentSettingsLoader.loadSettings(argumentParser, settings);

        final PropertyFileSettingsLoader propertyFileSettingsLoader = new PropertyFileSettingsLoader();
        propertyFileSettingsLoader.loadSettings(settings);

        return settings;
    }

    private static void loadUserInterface(final ArgumentParser argumentParser, final Settings settings,
                                          final UncaughtExceptionLogger uncaughtExceptionLogger) {
        try {
            final UIFactory uiFactory = new UIFactory(argumentParser, settings, uncaughtExceptionLogger);
            uiFactory.loadUI();
        }

        catch (final UIException e) {
            System.err.println(e.getMessage());
        }
    }
}
