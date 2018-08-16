
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

package net.usikkert.kouchat.ui.swing.messages;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link SwingMessages}.
 *
 * @author Christian Ihle
 */
public class SwingMessagesTest {

    private SwingMessages swingMessages;

    @Before
    public void setUp() {
        swingMessages = new SwingMessages();
    }

    @Test
    public void getMessageShouldReturnMessageFromSwingProperties() {
        final String message = swingMessages.getMessage("swing.systemTray.balloon.newMessage", 20);

        assertEquals("New message from 20", message);
    }

    @Test
    public void hasMessageShouldOnlyReturnTrueForMessagesInSwingProperties() {
        assertTrue(swingMessages.hasMessage("swing.button.ok"));
        assertFalse(swingMessages.hasMessage("console.quit.message"));
    }
}
