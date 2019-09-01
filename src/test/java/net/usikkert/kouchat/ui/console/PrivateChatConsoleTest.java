
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
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

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.junit.ExpectedSystemOut;
import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link PrivateChatConsole}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PrivateChatConsoleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public ExpectedSystemOut expectedSystemOut = new ExpectedSystemOut();

    private PrivateChatConsole privateChat;

    private User user;
    private ConsoleMessages messages;

    @Before
    public void setUp() {
        user = new User("Test", 1234);
        messages = new ConsoleMessages();

        privateChat = new PrivateChatConsole(user, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        new PrivateChatConsole(null, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfConsoleMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Console messages can not be null");

        new PrivateChatConsole(user, null);
    }

    @Test
    public void appendToPrivateChatShouldPrintPrefixedMessage() {
        privateChat.appendToPrivateChat("the message", 10);

        verify(System.out).println("(privmsg) the message");
    }
}
