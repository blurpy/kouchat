
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

import static org.junit.Assert.*;

import java.io.File;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.settings.Settings;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link ArgumentSettingsLoader}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ArgumentSettingsLoaderTest {

    private ArgumentSettingsLoader loader;
    private Settings settings;

    @Before
    public void setUp() {
        loader = new ArgumentSettingsLoader();
        settings = new Settings();
    }

    @Test
    public void loadSettingsWithNoArgumentsShouldKeepDefaultSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments();

        loader.loadSettings(argumentParser, settings);

        assertFalse(settings.isAlwaysLog());
        assertFalse(settings.isNoPrivateChat());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void loadSettingsWithUnrelatedArgumentsShouldKeepDefaultSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.CONSOLE.getFullArgumentName(),
                Argument.DEBUG.getFullArgumentName(),
                Argument.HELP.getFullArgumentName(),
                Argument.VERSION.getFullArgumentName());

        loader.loadSettings(argumentParser, settings);

        assertFalse(settings.isAlwaysLog());
        assertFalse(settings.isNoPrivateChat());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void loadSettingsWithAlwaysLogArgumentShouldSetAlwaysLogInTheSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.ALWAYS_LOG.getFullArgumentName());

        loader.loadSettings(argumentParser, settings);

        assertTrue(settings.isAlwaysLog());
        assertFalse(settings.isNoPrivateChat());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void loadSettingsWithNoPrivateChatArgumentShouldSetNoPrivateChatInTheSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.NO_PRIVATE_CHAT.getFullArgumentName());

        loader.loadSettings(argumentParser, settings);

        assertFalse(settings.isAlwaysLog());
        assertTrue(settings.isNoPrivateChat());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void loadSettingsWithLogLocationArgumentShouldSetLogLocationInTheSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.LOG_LOCATION.getFullArgumentName() + "=/home/user/logs");

        loader.loadSettings(argumentParser, settings);

        assertFalse(settings.isAlwaysLog());
        assertFalse(settings.isNoPrivateChat());

        // It appends missing slash or backslash, depending on the OS
        assertEquals("/home/user/logs" + File.separator, settings.getLogLocation());
    }

    @Test
    public void loadSettingsWithAllArgumentsShouldSetAllArgumentsInTheSettings() {
        final ArgumentParser argumentParser = argumentParserWithArguments(
                Argument.ALWAYS_LOG.getFullArgumentName(),
                Argument.NO_PRIVATE_CHAT.getFullArgumentName(),
                Argument.LOG_LOCATION.getFullArgumentName() + "=/home/user/logs");

        loader.loadSettings(argumentParser, settings);

        assertTrue(settings.isAlwaysLog());
        assertTrue(settings.isNoPrivateChat());
        assertEquals("/home/user/logs" + File.separator, settings.getLogLocation());
    }

    private ArgumentParser argumentParserWithArguments(final String... arguments) {
        return new ArgumentParser(arguments);
    }
}
