
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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link UITools}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class UIToolsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UITools uiTools;

    private Logger log;
    private ErrorHandler errorHandler;
    private Settings settings;
    private SwingMessages messages;

    @Before
    public void setUp() {
        uiTools = spy(new UITools());

        log = TestUtils.setFieldValueWithMock(uiTools, "LOG", Logger.class);
        errorHandler = mock(ErrorHandler.class);
        settings = mock(Settings.class);
        messages = new SwingMessages();
    }

    @Test
    public void browseShouldThrowExceptionIfUrlIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Url can not be empty");

        uiTools.browse(null, settings, errorHandler, messages);
    }

    @Test
    public void browseShouldThrowExceptionIfUrlIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Url can not be empty");

        uiTools.browse(" ", settings, errorHandler, messages);
    }

    @Test
    public void browseShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        uiTools.browse("url", null, errorHandler, messages);
    }

    @Test
    public void browseShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        uiTools.browse("url", settings, null, messages);
    }

    @Test
    public void browseShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        uiTools.browse("url", settings, errorHandler, null);
    }

    @Test
    public void browseShouldOpenSpecifiedUrlWithBrowserFromSettingsWhenDefined() throws IOException {
        when(settings.getBrowser()).thenReturn("opera");
        doReturn(null).when(uiTools).runCommand(anyString());

        uiTools.browse("www.kouchat.net", settings, errorHandler, messages);

        verify(uiTools).runCommand("opera www.kouchat.net");
        verifyZeroInteractions(errorHandler, log);
    }

    @Test
    public void browseShouldShowErrorIfRunningBrowserFromSettingsFail() throws IOException {
        when(settings.getBrowser()).thenReturn("firefox");

        final IOException exception = new IOException("Don't run command");
        doThrow(exception).when(uiTools).runCommand(anyString());

        uiTools.browse("www.kouchat.net", settings, errorHandler, messages);

        verify(uiTools).runCommand("firefox www.kouchat.net");
        verify(errorHandler).showError("Could not open the browser 'firefox'. Please check the settings.");
        verify(log).log(Level.WARNING,
                        "Failed to run command to open browser from settings: 'firefox www.kouchat.net'",
                        exception);
    }

    @Test
    public void browseShouldUseDefaultBrowserWhenSupportedIfBrowserFromSettingsIsMissing() throws IOException,
                                                                                                  URISyntaxException {
        when(uiTools.isDesktopActionSupported(any(Desktop.Action.class))).thenReturn(true);
        doNothing().when(uiTools).browse(anyString());

        uiTools.browse("www.kouchat.net", settings, errorHandler, messages);

        verify(uiTools).browse("www.kouchat.net");
        verify(uiTools).isDesktopActionSupported(Desktop.Action.BROWSE);
        verify(uiTools, never()).runCommand(anyString());
        verifyZeroInteractions(errorHandler, log);
    }

    @Test
    public void browseShouldUseDefaultBrowserWhenSupportedIfBrowserFromSettingsIsEmpty() throws IOException,
                                                                                                URISyntaxException {
        when(settings.getBrowser()).thenReturn(" ");
        when(uiTools.isDesktopActionSupported(any(Desktop.Action.class))).thenReturn(true);
        doNothing().when(uiTools).browse(anyString());

        uiTools.browse("www.kouchat.net", settings, errorHandler, messages);

        verify(uiTools).browse("www.kouchat.net");
        verify(uiTools).isDesktopActionSupported(Desktop.Action.BROWSE);
        verify(uiTools, never()).runCommand(anyString());
        verifyZeroInteractions(errorHandler, log);
    }

    @Test
    public void browseShouldShowErrorIfRunningDefaultBrowserFails() throws IOException, URISyntaxException {
        when(uiTools.isDesktopActionSupported(any(Desktop.Action.class))).thenReturn(true);

        final IOException exception = new IOException("Don't browse");
        doThrow(exception).when(uiTools).browse(anyString());

        uiTools.browse("www.kouchat.net", settings, errorHandler, messages);

        verify(uiTools).browse("www.kouchat.net");
        verify(errorHandler).showError("Could not open 'www.kouchat.net' with the default browser. " +
                                               "Try setting a browser in the settings.");
        verify(log).log(Level.WARNING, "Failed to open 'www.kouchat.net' in default browser", exception);
    }

    @Test
    public void browseShouldShowErrorIfRunningDefaultBrowserFailsWithInvalidUrl() throws IOException,
                                                                                         URISyntaxException {
        when(uiTools.isDesktopActionSupported(any(Desktop.Action.class))).thenReturn(true);

        final URISyntaxException exception = new URISyntaxException("url", "Invalid url");
        doThrow(exception).when(uiTools).browse(anyString());

        uiTools.browse("www.kouchat.net", settings, errorHandler, messages);

        verify(uiTools).browse("www.kouchat.net");
        verify(errorHandler).showError("Could not open 'www.kouchat.net' with the default browser. Invalid url?");
        verify(log).log(Level.WARNING, "Failed to open 'www.kouchat.net' in default browser", exception);
    }

    @Test
    public void browseShouldShowErrorIfNoBrowserInTheSettingsAndNoDefaultBrowserIsSupported() throws IOException,
                                                                                                     URISyntaxException {
        when(uiTools.isDesktopActionSupported(any(Desktop.Action.class))).thenReturn(false);

        uiTools.browse("www.kouchat.net", settings, errorHandler, messages);

        verify(uiTools, never()).runCommand(anyString());
        verify(uiTools, never()).browse(anyString());
        verify(errorHandler).showError("No browser detected. A browser can be chosen in the settings.");
    }
}
