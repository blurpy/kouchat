
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

package net.usikkert.kouchat.ui.swing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.swing.JButton;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link MessageDialog}.
 *
 * @author Christian Ihle
 */
public class MessageDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MessageDialog messageDialog;

    private SwingMessages messages;
    private ImageLoader imageLoader;
    private ErrorHandler errorHandler;
    private Settings settings;

    @Before
    public void setUp() {
        messages = new SwingMessages();
        errorHandler = mock(ErrorHandler.class);
        settings = mock(Settings.class);
        imageLoader = new ImageLoader(errorHandler, messages, new ResourceValidator(), new ResourceLoader());

        messageDialog = new MessageDialog(imageLoader, messages, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new MessageDialog(null, messages, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        new MessageDialog(imageLoader, null, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new MessageDialog(imageLoader, messages, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new MessageDialog(imageLoader, messages, settings, null);
    }

    @Test
    public void okButtonShouldHaveCorrectText() {
        final JButton okButton = messageDialog.getRootPane().getDefaultButton();

        assertEquals("OK", okButton.getText());
    }

    @Test
    public void okButtonShouldDisposeOnClick() {
        final boolean[] disposed = {false};

        final MessageDialog messageDialog1 = new MessageDialog(imageLoader, messages, settings, errorHandler) {
            @Override
            public void dispose() {
                disposed[0] = true;
            }
        };

        final JButton okButton = messageDialog1.getRootPane().getDefaultButton();

        okButton.doClick();

        assertTrue(disposed[0]);
    }
}
