
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

import javax.swing.JButton;

import net.usikkert.kouchat.message.PropertyFileMessages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link MessageDialog}.
 *
 * @author Christian Ihle
 */
public class MessageDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MessageDialog messageDialog;

    private ImageLoader imageLoader;

    @Before
    public void setUp() {
        final PropertyFileMessages messages = new PropertyFileMessages("messages.swing");
        imageLoader = new ImageLoader(mock(ErrorHandler.class), messages, new ResourceValidator(), new ResourceLoader());

        messageDialog = new MessageDialog(imageLoader);
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new MessageDialog(null);
    }

    @Test
    public void okButtonShouldHaveCorrectText() {
        final JButton okButton = messageDialog.getRootPane().getDefaultButton();

        assertEquals("OK", okButton.getText());
    }

    @Test
    public void okButtonShouldDisposeOnClick() {
        final boolean[] disposed = {false};

        final MessageDialog messageDialog1 = new MessageDialog(imageLoader) {
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
