
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

package net.usikkert.kouchat.ui.console;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.jmx.JMXAgent;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.message.CoreMessages;
import net.usikkert.kouchat.misc.ChatLogger;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.util.Sleeper;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link ConsoleMediator}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ConsoleMediatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ConsoleMediator mediator;

    private MessageController msgController;
    private Controller controller;
    private ConsoleInput consoleInput;
    private Sleeper sleeper;
    private JMXAgent jmxAgent;
    private Settings settings;
    private ConsoleMessages consoleMessages;
    private CoreMessages coreMessages;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        settings = new Settings();
        consoleMessages = new ConsoleMessages();
        coreMessages = new CoreMessages();
        errorHandler = mock(ErrorHandler.class);

        mediator = new ConsoleMediator(settings, consoleMessages, coreMessages, errorHandler);

        msgController = TestUtils.setFieldValueWithMock(mediator, "msgController", MessageController.class);
        controller = TestUtils.setFieldValueWithMock(mediator, "controller", Controller.class);
        consoleInput = TestUtils.setFieldValueWithMock(mediator, "consoleInput", ConsoleInput.class);
        sleeper = TestUtils.setFieldValueWithMock(mediator, "sleeper", Sleeper.class);
        jmxAgent = TestUtils.setFieldValueWithMock(mediator, "jmxAgent", JMXAgent.class);
    }

    @Test
    public void constructShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new ConsoleMediator(null, consoleMessages, coreMessages, errorHandler);
    }

    @Test
    public void constructShouldThrowExceptionIfConsoleMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Console messages can not be null");

        new ConsoleMediator(settings, null, coreMessages, errorHandler);
    }

    @Test
    public void constructShouldThrowExceptionIfCoreMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Core messages can not be null");

        new ConsoleMediator(settings, consoleMessages, null, errorHandler);
    }

    @Test
    public void constructShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new ConsoleMediator(settings, consoleMessages, coreMessages, null);
    }

    @Test
    public void startShouldLogOnControllerAndStartInputThreadAndActivateJMXAgent() {
        mediator.start();

        verify(controller).start();
        verify(controller).logOn();
        verify(consoleInput).start();
        verify(jmxAgent).activate();
    }

    @Test
    public void askFileSaveShouldReturnTrueAndPrintSystemMessage() {
        assertTrue(mediator.askFileSave(null, null, null));

        verify(msgController).showSystemMessage("/receive or /reject the file");
    }

    @Test
    public void changeAwayShouldDoNothing() {
        mediator.changeAway(false);
        mediator.changeAway(true);

        verifyZeroInteractions(msgController, consoleInput, controller);
    }

    @Test
    public void clearChatShouldShowSystemMessage() {
        mediator.clearChat();

        verify(msgController).showSystemMessage("Clear chat is not supported in console mode");
    }

    @Test
    public void showFileSaveShouldWaitForCancelFromUser() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.isCanceled()).thenReturn(false, false, true);

        mediator.showFileSave(fileReceiver);

        verify(sleeper, times(2)).sleep(500);
    }

    @Test
    public void showFileSaveShouldNotSleepIfAlreadyCanceled() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.isCanceled()).thenReturn(true);

        mediator.showFileSave(fileReceiver);

        verifyZeroInteractions(sleeper);
    }

    @Test
    public void showFileSaveShouldWaitForAcceptFromUser() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.isAccepted()).thenReturn(false, false, true);

        mediator.showFileSave(fileReceiver);

        verify(sleeper, times(2)).sleep(500);
    }

    @Test
    public void showFileSaveShouldNotSleepIfAlreadyAccepted() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.isAccepted()).thenReturn(true);

        mediator.showFileSave(fileReceiver);

        verifyZeroInteractions(sleeper);
    }

    @Test
    public void showFileSaveShouldWaitForRejectFromUser() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.isRejected()).thenReturn(false, false, true);

        mediator.showFileSave(fileReceiver);

        verify(sleeper, times(2)).sleep(500);
    }

    @Test
    public void showFileSaveShouldNotSleepIfAlreadyRejected() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.isRejected()).thenReturn(true);

        mediator.showFileSave(fileReceiver);

        verifyZeroInteractions(sleeper);
    }

    @Test
    public void showTopicShouldDoNothing() {
        mediator.showTopic();

        verifyZeroInteractions(msgController, consoleInput, controller);
    }

    @Test
    public void showTransferForFileReceiverShouldCreateTransferHandler() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);

        mediator.showTransfer(fileReceiver);

        verify(fileReceiver).registerListener(any(TransferHandler.class));
    }

    @Test
    public void showTransferForFileSenderShouldCreateTransferHandler() {
        final FileSender fileSender = mock(FileSender.class);

        mediator.showTransfer(fileSender);

        verify(fileSender).registerListener(any(TransferHandler.class));
    }

    @Test
    public void notifyMessageArrivedShouldDoNothing() {
        mediator.notifyMessageArrived(null);

        verifyZeroInteractions(msgController, consoleInput, controller);
    }

    @Test
    public void notifyPrivateMessageArrivedShouldDoNothing() {
        mediator.notifyPrivateMessageArrived(null);

        verifyZeroInteractions(msgController, consoleInput, controller);
    }

    @Test
    public void createPrivChatWithNoChatWindowShouldSetNewChatWindow() {
        final User user = new User("User", 123);
        assertNull(user.getPrivchat());

        mediator.createPrivChat(user);

        assertEquals(PrivateChatConsole.class, user.getPrivchat().getClass());
    }

    @Test
    public void createPrivChatWithExistingChatWindowShouldNotSetNewChatWindow() {
        final User user = new User("User", 123);
        final PrivateChatWindow chatWindow = mock(PrivateChatWindow.class);
        user.setPrivchat(chatWindow);

        mediator.createPrivChat(user);

        assertSame(chatWindow, user.getPrivchat());
    }

    @Test
    public void createPrivChatWithNoChatLoggerShouldSetNewChatLogger() {
        final User user = new User("User", 123);
        assertNull(user.getPrivateChatLogger());

        mediator.createPrivChat(user);

        assertEquals(ChatLogger.class, user.getPrivateChatLogger().getClass());
    }

    @Test
    public void createPrivChatWithExistingChatLoggerShouldNotSetNewChatLogger() {
        final User user = new User("User", 123);
        final ChatLogger chatLogger = mock(ChatLogger.class);
        user.setPrivateChatLogger(chatLogger);

        mediator.createPrivChat(user);

        assertSame(chatLogger, user.getPrivateChatLogger());
    }

    @Test
    public void getMessageControllerShouldReturnMessageLogger() {
        assertSame(msgController, mediator.getMessageController());
    }

    @Test
    public void isFocusedShouldReturnTrue() {
        assertTrue(mediator.isFocused());
    }

    @Test
    public void isVisibleShouldReturnTrue() {
        assertTrue(mediator.isVisible());
    }
}
