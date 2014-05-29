
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

package net.usikkert.kouchat.ui.swing;

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;
import net.usikkert.kouchat.util.UncaughtExceptionLogger;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link KouChatFrame}.
 *
 * @author Christian Ihle
 */
public class KouChatFrameTest {

    private KouChatFrame kouChatFrame;

    private Mediator mediator;
    private SysTray sysTray;

    @Before
    public void setUp() {
        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(new User("Me", 123));

        kouChatFrame = spy(new KouChatFrame(settings, mock(UncaughtExceptionLogger.class), mock(ErrorHandler.class)));

        mediator = TestUtils.setFieldValueWithMock(kouChatFrame, "mediator", Mediator.class);
        sysTray = TestUtils.setFieldValueWithMock(kouChatFrame, "sysTray", SysTray.class);

        doNothing().when(kouChatFrame).setVisible(anyBoolean());
    }

    @Test
    public void startShouldActivateTheSystemTray() {
        kouChatFrame.start();

        verify(sysTray).activate();
    }

    @Test
    public void startShouldSetTheWindowVisible() {
        kouChatFrame.start();

        verify(kouChatFrame).setVisible(true);
    }

    @Test
    public void startShouldStartTheMediator() {
        kouChatFrame.start();

        verify(mediator).start();
    }
}
