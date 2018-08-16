
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

package net.usikkert.kouchat.ui.console;

import static org.mockito.Mockito.*;

import java.io.BufferedReader;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.junit.ExpectedSystemOut;
import net.usikkert.kouchat.message.CoreMessages;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

/**
 * Test of {@link ConsoleInput}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ConsoleInputTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final ExpectedSystemExit expectedSystemExit = ExpectedSystemExit.none();

    @Rule
    public ExpectedSystemOut expectedSystemOut = new ExpectedSystemOut();

    private ConsoleInput consoleInput;

    private Controller controller;
    private UserInterface ui;
    private Settings settings;
    private ConsoleMessages consoleMessages;
    private CoreMessages coreMessages;

    @Before
    public void setUp() {
        controller = mock(Controller.class);
        ui = mock(UserInterface.class);
        settings = mock(Settings.class);
        consoleMessages = new ConsoleMessages();
        coreMessages = new CoreMessages();

        consoleInput = new ConsoleInput(controller, ui, settings, consoleMessages, coreMessages);

        TestUtils.setFieldValueWithMock(consoleInput, "stdin", BufferedReader.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Controller can not be null");

        new ConsoleInput(null, ui, settings, consoleMessages, coreMessages);
    }

    @Test
    public void constructorShouldThrowExceptionIfUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("UserInterface can not be null");

        new ConsoleInput(controller, null, settings, consoleMessages, coreMessages);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new ConsoleInput(controller, ui, null, consoleMessages, coreMessages);
    }

    @Test
    public void constructorShouldThrowExceptionIfConsoleMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Console messages can not be null");

        new ConsoleInput(controller, ui, settings, null, coreMessages);
    }

    @Test
    public void constructorShouldThrowExceptionIfCoreMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Core messages can not be null");

        new ConsoleInput(controller, ui, settings, consoleMessages, null);
    }

    @Test
    public void constructorShouldCreateShutdownHookThatPrintsQuitMessages() {
        final Thread shutdownHook = TestUtils.getFieldValue(consoleInput, Thread.class, "shutdownHook");

        verifyZeroInteractions(System.out);

        shutdownHook.run();

        verify(System.out).println("Quitting - good bye!");
    }
}
