
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

package net.usikkert.kouchat.ui.console;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link KouChatConsole}.
 *
 * @author Christian Ihle
 */
public class KouChatConsoleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private KouChatConsole kouChatConsole;

    private Settings settings;
    private ConsoleMediator consoleMediator;

    @Before
    public void setUp() {
        settings = new Settings();
        kouChatConsole = new KouChatConsole(settings);
        consoleMediator = TestUtils.setFieldValueWithMock(kouChatConsole, "consoleMediator", ConsoleMediator.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new KouChatConsole(null);
    }

    @Test
    public void constructorShouldSetClientInSettings() {
        assertEquals("KouChat v" + Constants.APP_VERSION + " Console", settings.getMe().getClient());
    }

    @Test
    public void startShouldStartTheMediator() {
        kouChatConsole.start();

        verify(consoleMediator).start();
    }
}
