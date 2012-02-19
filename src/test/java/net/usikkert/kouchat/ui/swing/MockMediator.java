
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

import java.io.File;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.ui.PrivateChatWindow;

/**
 * This is a mock implementation of the mediator, for use in unit tests.
 *
 * @author Christian Ihle
 */
public class MockMediator implements Mediator
{
    /** If the file transfer dialog is closed. */
    private boolean close;

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void activatedPrivChat(final User user) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean changeNick(final String nick) {
        return false;
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void clearChat() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void minimize() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void quit() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void sendFile(final User user, final File selectedFile) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void setAway() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void setTopic() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void showCommands() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void showPrivChat(final User user) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void showSettings() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void showOrHideWindow() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void minimizeWindowIfHidden() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void start() {

    }

    /**
     * A simpler implementation of the cancel/close handling of the transfer dialog.
     *
     * @param transferDialog The file transfer dialog.
     */
    @Override
    public void transferCancelled(final TransferDialog transferDialog) {
        if (transferDialog.getCancelButtonText().equals("Close"))
            close = true;

        else
        {
            transferDialog.setCancelButtonText("Close");
            FileTransfer fileTransfer = transferDialog.getFileTransfer();
            fileTransfer.cancel();
        }
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void updateTitleAndTray() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void updateWriting() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void write() {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void writePrivate(final PrivateChatWindow privchat) {

    }

    /**
     * If the file transfer dialog is closed.
     *
     * @return if the file transfer dialog is closed.
     */
    public boolean isClose() {
        return close;
    }
}
