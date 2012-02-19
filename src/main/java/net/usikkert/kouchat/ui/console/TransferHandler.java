
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

package net.usikkert.kouchat.ui.console;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.net.FileTransfer.Direction;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the console implementation of a file transfer listener.
 * Does not do anything, but is needed to get file transfer support
 * in console mode.
 *
 * @author Christian Ihle
 */
public class TransferHandler implements FileTransferListener {

    /** The file transfer to handle. */
    private final FileTransfer fileTransfer;

    /** The message controller. */
    private final MessageController msgController;

    /**
     * Constructor. Registers this class as a listener of
     * file transfer events.
     *
     * @param fileTransfer The file transfer to handle.
     * @param msgController The message controller.
     */
    public TransferHandler(final FileTransfer fileTransfer, final MessageController msgController) {
        Validate.notNull(fileTransfer, "File transfer can not be null");
        Validate.notNull(msgController, "Message controller can not be null");

        this.fileTransfer = fileTransfer;
        this.msgController = msgController;

        fileTransfer.registerListener(this);
    }

    /**
     * Not implemented.
     */
    @Override
    public void statusCompleted() {

    }

    /**
     * Not implemented.
     */
    @Override
    public void statusConnecting() {

    }

    /**
     * Not implemented.
     */
    @Override
    public void statusFailed() {

    }

    /**
     * Shows a message if starting to receive a file.
     * There is no need to show a message when sending a message,
     * as that is taken care of elsewhere.
     */
    @Override
    public void statusTransferring() {
        if (fileTransfer.getDirection() == Direction.RECEIVE)
        {
            msgController.showSystemMessage("Receiving " + fileTransfer.getFile().getName()
                    + " from " + fileTransfer.getUser().getNick());
        }
    }

    /**
     * Not implemented.
     */
    @Override
    public void statusWaiting() {

    }

    /**
     * Not implemented.
     */
    @Override
    public void transferUpdate() {

    }
}
