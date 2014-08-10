
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

import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileTransfer.Direction;
import net.usikkert.kouchat.net.MockFileTransfer;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration test for the {@link TransferDialog}.
 *
 * @author Christian Ihle
 */
@Ignore("Run manually")
@SuppressWarnings("HardCodedStringLiteral")
public class TransferDialogIntegrationTest {

    private ImageLoader imageLoader;
    private Settings settings;

    @Before
    public void setUp() {
        final User me = new User("Me", 123);
        me.setIpAddress("192.168.1.2");

        settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(me);

        imageLoader = new ImageLoader(mock(ErrorHandler.class), mock(Messages.class), new ResourceValidator(), new ResourceLoader());
    }

    /**
     * Creates a {@link TransferDialog} for receiving a file,
     * and simulates the file transfer.
     *
     * @throws InterruptedException In case of sleep issues.
     */
    @Test
    public void showReceiveDialog() throws InterruptedException {
        final MockMediator mediator = new MockMediator();
        final MockFileTransfer fileTransfer = new MockFileTransfer(Direction.RECEIVE);

        new TransferDialog(mediator, fileTransfer, imageLoader, settings);

        // Returns true when the close button is clicked
        while (!mediator.isClose()) {
            Thread.sleep(100);
        }
    }

    /**
     * Creates a {@link TransferDialog} for sending a file,
     * and simulates the file transfer.
     *
     * @throws InterruptedException In case of sleep issues.
     */
    @Test
    public void showSendDialog() throws InterruptedException {
        final MockMediator mediator = new MockMediator();
        final MockFileTransfer fileTransfer = new MockFileTransfer(Direction.SEND);

        new TransferDialog(mediator, fileTransfer, imageLoader, settings);

        // Returns true when the close button is clicked
        while (!mediator.isClose()) {
            Thread.sleep(100);
        }
    }
}
