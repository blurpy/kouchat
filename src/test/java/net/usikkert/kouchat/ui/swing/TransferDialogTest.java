
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

import net.usikkert.kouchat.net.MockFileTransfer;
import net.usikkert.kouchat.net.FileTransfer.Direction;

import org.junit.Test;

/**
 * Test for the {@link TransferDialog}.
 *
 * @author Christian Ihle
 */
public class TransferDialogTest {

    /** The image loader. */
    private final ImageLoader imageLoader = new ImageLoader();

    /**
     * Creates a {@link TransferDialog} for receiving a file,
     * and simulates the file transfer.
     *
     * @throws InterruptedException In case of sleep issues.
     */
    @Test
    public void testReceiveDialog() throws InterruptedException {
        final MockMediator mediator = new MockMediator();
        final MockFileTransfer fileTransfer = new MockFileTransfer(Direction.RECEIVE);

        new TransferDialog(mediator, fileTransfer, imageLoader);

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
    public void testSendDialog() throws InterruptedException {
        final MockMediator mediator = new MockMediator();
        final MockFileTransfer fileTransfer = new MockFileTransfer(Direction.SEND);

        new TransferDialog(mediator, fileTransfer, imageLoader);

        // Returns true when the close button is clicked
        while (!mediator.isClose()) {
            Thread.sleep(100);
        }
    }
}
