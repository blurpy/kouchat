
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

package net.usikkert.kouchat.ui.swing;

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.TestUtils;
import net.usikkert.kouchat.util.UncaughtExceptionLogger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link KouChatFrame}.
 *
 * @author Christian Ihle
 */
public class KouChatFrameTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private KouChatFrame kouChatFrame;

    private Mediator mediator;
    private SysTray sysTray;
    private UITools uiTools;

    @Before
    public void setUp() {
        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(new User("Me", 123));

        kouChatFrame = spy(new KouChatFrame(settings, mock(UncaughtExceptionLogger.class), mock(ErrorHandler.class)));

        mediator = TestUtils.setFieldValueWithMock(kouChatFrame, "mediator", Mediator.class);
        sysTray = TestUtils.setFieldValueWithMock(kouChatFrame, "sysTray", SysTray.class);
        uiTools = TestUtils.setFieldValueWithMock(kouChatFrame, "uiTools", UITools.class);

        doNothing().when(kouChatFrame).setVisible(anyBoolean());
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new KouChatFrame(null, mock(UncaughtExceptionLogger.class), mock(ErrorHandler.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfUncaughtExceptionLoggerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Uncaught exception logger can not be null");

        new KouChatFrame(mock(Settings.class), null, mock(ErrorHandler.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new KouChatFrame(mock(Settings.class), mock(UncaughtExceptionLogger.class), null);
    }

    @Test
    public void startShouldActivateTheSystemTray() {
        kouChatFrame.start(false);

        verify(sysTray).activate();
    }

    @Test
    public void startWithMinimizedFalseShouldSetTheWindowVisible() {
        kouChatFrame.start(false);

        verify(kouChatFrame).setVisible(true);
        verifyZeroInteractions(uiTools);
    }

    @Test
    public void startWithMinimizedTrueAndSupportForSystemTrayShouldNotTouchWindow() {
        when(sysTray.isSystemTraySupport()).thenReturn(true);

        kouChatFrame.start(true);

        verify(kouChatFrame, never()).setVisible(anyBoolean());
        verifyZeroInteractions(uiTools);
    }

    @Test
    public void startWithMinimizedTrueAndNoSupportForSystemTrayShouldMinimizeAndSetWindowVisible() {
        when(sysTray.isSystemTraySupport()).thenReturn(false);

        kouChatFrame.start(true);

        verify(uiTools).minimize(kouChatFrame);
        verify(kouChatFrame).setVisible(true);
    }

    @Test
    public void startShouldStartTheMediator() {
        kouChatFrame.start(false);

        verify(mediator).start();
    }
}
