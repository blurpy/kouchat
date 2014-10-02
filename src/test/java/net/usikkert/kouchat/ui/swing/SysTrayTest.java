
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link SysTray}.
 *
 * @author Christian Ihle
 */
public class SysTrayTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SysTray sysTray;

    private UITools uiTools;
    private Logger log;
    private SwingMessages messages;

    @Before
    public void setUp() {
        messages = new SwingMessages();
        final ImageLoader imageLoader =
                new ImageLoader(mock(ErrorHandler.class), messages, new ResourceValidator(), new ResourceLoader());

        sysTray = spy(new SysTray(imageLoader, mock(Settings.class), messages));

        uiTools = TestUtils.setFieldValueWithMock(sysTray, "uiTools", UITools.class);
        log = TestUtils.setFieldValueWithMock(sysTray, "LOG", Logger.class);

        final SystemTray systemTray = mock(SystemTray.class);
        when(systemTray.getTrayIconSize()).thenReturn(new Dimension(16, 16));
        when(uiTools.getSystemTray()).thenReturn(systemTray);
        doReturn(mock(TrayIcon.class)).when(sysTray).createTrayIcon(any(Image.class), any(PopupMenu.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new SysTray(null, mock(Settings.class), messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new SysTray(mock(ImageLoader.class), null, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Messages can not be null");

        new SysTray(mock(ImageLoader.class), mock(Settings.class), null);
    }

    @Test
    public void activateShouldNotDoAnythingIfSystemTrayIsUnsupported() {
        assertFalse(sysTray.isSystemTraySupport());
        when(uiTools.isSystemTraySupported()).thenReturn(false);

        sysTray.activate();

        assertFalse(sysTray.isSystemTraySupport());
        verify(log).log(Level.SEVERE, "System Tray is not supported. Deactivating System Tray support.");
        assertTrue(TestUtils.fieldValueIsNull(sysTray, "trayIcon"));
    }

    @Test
    public void activateShouldSetSystemTrayAsSupportedIfSupported() {
        assertFalse(sysTray.isSystemTraySupport());
        when(uiTools.isSystemTraySupported()).thenReturn(true);

        sysTray.activate();

        assertTrue(sysTray.isSystemTraySupport());
        assertFalse(TestUtils.fieldValueIsNull(sysTray, "trayIcon"));
    }

    @Test
    public void activateShouldSetCorrectLabelOnQuitMenuItem() {
        when(uiTools.isSystemTraySupported()).thenReturn(true);

        sysTray.activate();

        final MenuItem quitMI = TestUtils.getFieldValue(sysTray, MenuItem.class, "quitMI");
        assertEquals("Quit", quitMI.getLabel());
    }
}
