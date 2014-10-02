
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
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration test of {@link MessageDialog}.
 *
 * @author Christian Ihle
 */
public class MessageDialogIntegrationTest {

    @Test
    @Ignore("Run manually")
    public void showMessageDialog() {
        final SwingMessages messages = new SwingMessages();
        final ImageLoader imageLoader = new ImageLoader(mock(ErrorHandler.class), messages,
                                                        new ResourceValidator(), new ResourceLoader());

        final MessageDialog messageDialog = new MessageDialog(imageLoader, messages);

        messageDialog.setVisible(true);
    }
}
