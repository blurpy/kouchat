
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

package net.usikkert.kouchat.net;

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link AsyncMessageResponderWrapper}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class AsyncMessageResponderWrapperTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AsyncMessageResponderWrapper wrapper;

    private MessageResponder messageResponder;

    @Before
    public void setUp() {
        messageResponder = mock(MessageResponder.class);

        wrapper = new AsyncMessageResponderWrapper(messageResponder);
    }

    @Test
    public void constructorShouldThrowExceptionIfMessageResponderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("MessageResponder can not be null");

        new AsyncMessageResponderWrapper(null);
    }

    @Test
    public void messageArrivedShouldPassThrough() {
        wrapper.messageArrived(100, "msg", 200);

        verify(messageResponder).messageArrived(100, "msg", 200);
    }

    @Test
    public void topicChangedShouldPassThrough() {
        wrapper.topicChanged(100, "newTopic", "nick", 300);

        verify(messageResponder).topicChanged(100, "newTopic", "nick", 300);
    }

    @Test
    public void topicRequestedShouldPassThrough() {
        wrapper.topicRequested();

        verify(messageResponder).topicRequested();
    }

    @Test
    public void awayChangedShouldPassThrough() {
        wrapper.awayChanged(100, true, "awayMsg");

        verify(messageResponder).awayChanged(100, true, "awayMsg");
    }

    @Test
    public void nickChangedShouldPassThrough() {
        wrapper.nickChanged(100, "newNick");

        verify(messageResponder).nickChanged(100, "newNick");
    }

    @Test
    public void nickCrashShouldPassThrough() {
        wrapper.nickCrash();

        verify(messageResponder).nickCrash();
    }

    @Test
    public void meLogOnShouldPassThrough() {
        wrapper.meLogOn("ipAddress");

        verify(messageResponder).meLogOn("ipAddress");
    }

    @Test
    public void userLogOnShouldPassThrough() {
        final User user = new User("User", 123);

        wrapper.userLogOn(user);

        verify(messageResponder).userLogOn(user);
    }

    @Test
    public void userLogOffShouldPassThrough() {
        wrapper.userLogOff(100);

        verify(messageResponder).userLogOff(100);
    }

    @Test
    public void userExposingShouldPassThrough() {
        final User user = new User("User", 123);

        wrapper.userExposing(user);

        verify(messageResponder).userExposing(user);
    }

    @Test
    public void exposeRequestedShouldPassThrough() {
        wrapper.exposeRequested();

        verify(messageResponder).exposeRequested();
    }

    @Test
    public void writingChangedShouldPassThrough() {
        wrapper.writingChanged(100, true);

        verify(messageResponder).writingChanged(100, true);
    }

    @Test
    public void meIdleShouldPassThrough() {
        wrapper.meIdle("ipAddress");

        verify(messageResponder).meIdle("ipAddress");
    }

    @Test
    public void userIdleShouldPassThrough() {
        wrapper.userIdle(100, "ipAddress");

        verify(messageResponder).userIdle(100, "ipAddress");
    }

    @Test
    public void fileSendShouldPassThrough() {
        wrapper.fileSend(100, 3000, "fileName", "user", 98765);

        verify(messageResponder).fileSend(100, 3000, "fileName", "user", 98765);
    }

    @Test
    public void fileSendAbortedShouldPassThrough() {
        wrapper.fileSendAborted(100, "fileName", 98765);

        verify(messageResponder).fileSendAborted(100, "fileName", 98765);
    }

    @Test
    public void fileSendAcceptedShouldPassThrough() {
        wrapper.fileSendAccepted(100, "fileName", 98765, 1050);

        verify(messageResponder).fileSendAccepted(100, "fileName", 98765, 1050);
    }

    @Test
    public void clientInfoShouldPassThrough() {
        wrapper.clientInfo(100, "client", 70000, "os", 4500);

        verify(messageResponder).clientInfo(100, "client", 70000, "os", 4500);
    }
}
