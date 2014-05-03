
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

package net.usikkert.kouchat.argument;

import static org.junit.Assert.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.Settings;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link ArgumentSettingsLoader}.
 *
 * @author Christian Ihle
 */
public class ArgumentSettingsLoaderTest {

    private ArgumentSettingsLoader loader;
    private Settings settings;

    @Before
    public void setUp() {
        loader = new ArgumentSettingsLoader();
        settings = new Settings();
    }

    @Test
    public void loadSettingsFromArgumentsWithNoArgumentsShouldKeepDefaultSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments();

        loader.loadSettingsFromArguments(argumentParser, settings);

        assertFalse(settings.isAlwaysLog());
        assertFalse(settings.isNoPrivateChat());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void loadSettingsFromArgumentsWithUnrelatedArgumentsShouldKeepDefaultSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.CONSOLE.getFullArgumentName(),
                Argument.DEBUG.getFullArgumentName(),
                Argument.HELP.getFullArgumentName(),
                Argument.VERSION.getFullArgumentName());

        loader.loadSettingsFromArguments(argumentParser, settings);

        assertFalse(settings.isAlwaysLog());
        assertFalse(settings.isNoPrivateChat());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void loadSettingsFromArgumentsWithAlwaysLogArgumentShouldSetAlwaysLogInTheSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.ALWAYS_LOG.getFullArgumentName());

        loader.loadSettingsFromArguments(argumentParser, settings);

        assertTrue(settings.isAlwaysLog());
        assertFalse(settings.isNoPrivateChat());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void loadSettingsFromArgumentsWithNoPrivateChatArgumentShouldSetNoPrivateChatInTheSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.NO_PRIVATE_CHAT.getFullArgumentName());

        loader.loadSettingsFromArguments(argumentParser, settings);

        assertFalse(settings.isAlwaysLog());
        assertTrue(settings.isNoPrivateChat());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void loadSettingsFromArgumentsWithLogLocationArgumentShouldSetLogLocationInTheSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.LOG_LOCATION.getFullArgumentName() + "=/home/user/logs");

        loader.loadSettingsFromArguments(argumentParser, settings);

        assertFalse(settings.isAlwaysLog());
        assertFalse(settings.isNoPrivateChat());
        assertEquals("/home/user/logs/", settings.getLogLocation());
    }

    @Test
    public void loadSettingsFromArgumentsWithAllArgumentsShouldSetAllArgumentsInTheSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.ALWAYS_LOG.getFullArgumentName(),
                Argument.NO_PRIVATE_CHAT.getFullArgumentName(),
                Argument.LOG_LOCATION.getFullArgumentName() + "=/home/user/logs");

        loader.loadSettingsFromArguments(argumentParser, settings);

        assertTrue(settings.isAlwaysLog());
        assertTrue(settings.isNoPrivateChat());
        assertEquals("/home/user/logs/", settings.getLogLocation());
    }

    private ArgumentParser argumentParserWithArguments(final String... arguments) {
        return new ArgumentParser(arguments);
    }
}
