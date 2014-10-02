
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

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link ConsoleMessages}.
 *
 * @author Christian Ihle
 */
public class ConsoleMessagesTest {

    private ConsoleMessages consoleMessages;

    @Before
    public void setUp() {
        consoleMessages = new ConsoleMessages();
    }

    @Test
    public void getMessageShouldReturnMessageFromConsoleProperties() {
        final String message = consoleMessages.getMessage("console.privateChat.messageFormat", 20);

        assertEquals("(privmsg) 20", message);
    }

    @Test
    public void hasMessageShouldOnlyReturnTrueForMessagesInConsoleProperties() {
        assertTrue(consoleMessages.hasMessage("console.quit.message"));
        assertFalse(consoleMessages.hasMessage("swing.button.ok"));
    }
}
