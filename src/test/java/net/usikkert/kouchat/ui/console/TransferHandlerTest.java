
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

package net.usikkert.kouchat.ui.console;

import static org.mockito.Mockito.*;

import java.io.File;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileToSend;
import net.usikkert.kouchat.net.FileTransfer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link TransferHandler}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class TransferHandlerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TransferHandler transferHandler;

    private FileTransfer fileTransfer;
    private MessageController messageController;
    private ConsoleMessages messages;

    @Before
    public void setUp() {
        fileTransfer = mock(FileSender.class);
        messageController = mock(MessageController.class);
        messages = new ConsoleMessages();

        transferHandler = new TransferHandler(fileTransfer, messageController, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfFileReceiverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File transfer can not be null");

        new TransferHandler(null, messageController, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfMessageControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Message controller can not be null");

        new TransferHandler(fileTransfer, null, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfConsoleMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Console messages can not be null");

        new TransferHandler(fileTransfer, messageController, null);
    }

    @Test
    public void constructorShouldRegisterListener() {
        verify(fileTransfer).registerListener(transferHandler);
    }

    @Test
    public void statusCompletedShouldDoNothing() {
        transferHandler.statusCompleted();
    }

    @Test
    public void statusConnectingShouldDoNothing() {
        transferHandler.statusConnecting();
    }

    @Test
    public void statusFailedShouldDoNothing() {
        transferHandler.statusFailed();
    }

    @Test
    public void statusTransferringWhenReceivingShouldShowSystemMessage() {
        final FileReceiver fileReceiver = new FileReceiver(new User("Dude", 1234), new File("sunset.jpg"), 100, 1);
        final TransferHandler fileReceiverTransferHandler =
                new TransferHandler(fileReceiver, messageController, messages);

        fileReceiverTransferHandler.statusTransferring();

        verify(messageController).showSystemMessage("Receiving sunset.jpg from Dude");
    }

    @Test
    public void statusTransferringWhenSendingShouldDoNothing() {
        final FileSender fileSender = new FileSender(new User("Dude", 1234), new FileToSend(new File("sunset.jpg")), 2);
        final TransferHandler fileSenderTransferHandler = new TransferHandler(fileSender, messageController, messages);

        fileSenderTransferHandler.statusTransferring();

        verifyZeroInteractions(messageController);
    }

    @Test
    public void statusWaitingShouldDoNothing() {
        transferHandler.statusWaiting();
    }

    @Test
    public void transferUpdateShouldDoNothing() {
        transferHandler.transferUpdate();
    }
}
