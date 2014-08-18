
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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.usikkert.kouchat.jmx.JMXAgent;
import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.message.PropertyFileMessages;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.SortedUserList;
import net.usikkert.kouchat.misc.SoundBeeper;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.ui.swing.settings.SettingsDialog;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test of {@link SwingMediator}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SwingMediatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public ExpectedSystemExit expectedSystemExit = ExpectedSystemExit.none();

    private SwingMediator mediator;

    private User me;
    private User user;
    private JTextField messageTF;
    private UITools uiTools;
    private Controller controller;
    private JMXAgent jmxAgent;
    private CommandParser cmdParser;
    private ComponentHandler componentHandler;
    private KouChatFrame kouChatFrame;
    private SysTray sysTray;
    private MessageController msgController;
    private SoundBeeper beeper;
    private PrivateChatWindow privchat;
    private MenuBar menuBar;
    private ButtonPanel buttonP;

    @Before
    public void setUp() {
        messageTF = mock(JTextField.class);

        final MainPanel mainPanel = mock(MainPanel.class);
        when(mainPanel.getMsgTF()).thenReturn(messageTF);

        kouChatFrame = mock(KouChatFrame.class);
        sysTray = mock(SysTray.class);

        componentHandler = spy(new ComponentHandler());
        componentHandler.setButtonPanel(mock(ButtonPanel.class));
        componentHandler.setGui(kouChatFrame);
        componentHandler.setMainPanel(mainPanel);
        componentHandler.setMenuBar(mock(MenuBar.class));
        componentHandler.setSettingsDialog(mock(SettingsDialog.class));
        componentHandler.setSidePanel(mock(SidePanel.class));
        componentHandler.setSysTray(sysTray);

        me = new User("Me", 1234);
        me.setMe(true);

        privchat = mock(PrivateChatWindow.class);

        user = new User("Sally", 1235);
        user.setPrivchat(privchat);

        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(me);

        final PropertyFileMessages messages = new PropertyFileMessages("messages.swing");

        mediator = spy(new SwingMediator(componentHandler, mock(ImageLoader.class), settings, messages));

        uiTools = TestUtils.setFieldValueWithMock(mediator, "uiTools", UITools.class);
        controller = TestUtils.setFieldValueWithMock(mediator, "controller", Controller.class);
        msgController = TestUtils.setFieldValueWithMock(mediator, "msgController", MessageController.class);
        jmxAgent = TestUtils.setFieldValueWithMock(mediator, "jmxAgent", JMXAgent.class);
        cmdParser = TestUtils.setFieldValueWithMock(mediator, "cmdParser", CommandParser.class);
        beeper = TestUtils.setFieldValueWithMock(mediator, "beeper", SoundBeeper.class);
        menuBar = TestUtils.setFieldValueWithMock(mediator, "menuBar", MenuBar.class);
        buttonP = TestUtils.setFieldValueWithMock(mediator, "buttonP", ButtonPanel.class);

        when(controller.getUserList()).thenReturn(new SortedUserList());
        when(uiTools.createTitle(anyString())).thenCallRealMethod();
        when(controller.getTopic()).thenReturn(new Topic());
        doAnswer(new RunArgumentAnswer()).when(uiTools).invokeAndWait(any(Runnable.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfComponentHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Component handler can not be null");

        new SwingMediator(null, mock(ImageLoader.class), mock(Settings.class), mock(Messages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new SwingMediator(mock(ComponentHandler.class), null, mock(Settings.class), mock(Messages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new SwingMediator(mock(ComponentHandler.class), mock(ImageLoader.class), null, mock(Messages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Messages can not be null");

        new SwingMediator(mock(ComponentHandler.class), mock(ImageLoader.class), mock(Settings.class), null);
    }

    @Test
    public void constructorShouldValidateComponentHandler() {
        verify(componentHandler).validate();
    }

    @Test
    public void setAwayWhenBackShouldAskAwayMessageAndGoAway() throws CommandException {
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn("Going away");

        mediator.setAway();

        verify(uiTools).showInputDialog("Reason for away?", "Away", null);
        verify(controller).goAway("Going away");
        verify(uiTools, never()).showWarningMessage(anyString(), anyString());
    }

    @Test
    public void setAwayWhenBackShouldUpdateWritingStatusAndClearInputFieldIfCurrentlyWriting() {
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn("Going away");
        when(controller.isWrote()).thenReturn(true);

        mediator.setAway();

        verify(controller).changeWriting(1234, false);
        verify(messageTF).setText("");
    }

    @Test
    public void setAwayWhenBackShouldNotUpdateWritingStatusAndClearInputFieldIfNotCurrentlyWriting() {
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn("Going away");
        when(controller.isWrote()).thenReturn(false);

        mediator.setAway();

        verify(controller, never()).changeWriting(anyInt(), anyBoolean());
        verify(messageTF, never()).setText(anyString());
    }

    @Test
    public void setAwayWhenBackShouldShowWarningMessageIfChangeFails() throws CommandException {
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn("Leaving");
        doThrow(new CommandException("Don't go away")).when(controller).goAway(anyString());

        mediator.setAway();

        verify(controller).goAway("Leaving");
        verify(uiTools).showWarningMessage("Don't go away", "Change away");
    }

    @Test
    public void setAwayWhenBackShouldAskAwayMessageAndDoNothingIfMessageIsNull() {
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn(null);

        mediator.setAway();

        verifyZeroInteractions(controller);
    }

    @Test
    public void setAwayWhenBackShouldAskAwayMessageAndDoNothingIfMessageIsBlank() {
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn(" ");

        mediator.setAway();

        verifyZeroInteractions(controller);
    }

    @Test
    public void setAwayWhenBackShouldRequestFocusOnInputField() {
        mediator.setAway();

        verify(messageTF).requestFocusInWindow();
    }

    @Test
    public void setAwayWhenAwayShouldAskIfBackAndNotChangeIfNo() {
        me.setAway(true);
        me.setAwayMsg("Gone");

        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.NO_OPTION);

        mediator.setAway();

        verify(uiTools).showOptionDialog("Back from 'Gone'?", "Away");
        verifyZeroInteractions(controller);
    }

    @Test
    public void setAwayWhenAwayShouldAskIfBackAndChangeIfYes() throws CommandException {
        me.setAway(true);
        me.setAwayMsg("Gone");

        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.YES_OPTION);

        mediator.setAway();

        verify(uiTools).showOptionDialog("Back from 'Gone'?", "Away");
        verify(controller).comeBack();
        verify(uiTools, never()).showWarningMessage(anyString(), anyString());
    }

    @Test
    public void setAwayWhenAwayShouldShowWarningMessageIfChangeFails() throws CommandException {
        me.setAway(true);
        me.setAwayMsg("Gone");

        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.YES_OPTION);
        doThrow(new CommandException("Don't come back")).when(controller).comeBack();

        mediator.setAway();

        verify(uiTools).showOptionDialog("Back from 'Gone'?", "Away");
        verify(controller).comeBack();
        verify(uiTools).showWarningMessage("Don't come back", "Change away");
    }

    @Test
    public void setAwayWhenAwayShouldRequestFocusOnInputField() {
        me.setAway(true);

        mediator.setAway();

        verify(messageTF).requestFocusInWindow();
    }

    @Test
    public void startShouldLogOnControllerAndActivateJMXAgent() {
        mediator.start();

        verify(controller).start();
        verify(controller).logOn();
        verify(jmxAgent).activate();
    }

    @Test
    public void setTopicShouldUseExistingTopicAsInitialValue() {
        when(controller.getTopic()).thenReturn(new Topic("Initial topic", "Niles", System.currentTimeMillis()));

        mediator.setTopic();

        verify(uiTools).showInputDialog("Change topic?", "Topic", "Initial topic");
    }

    @Test
    public void setTopicShouldNotChangeTopicIfDialogWasCancelled() {
        when(controller.getTopic()).thenReturn(new Topic("Initial topic", "Niles", System.currentTimeMillis()));
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn(null);

        mediator.setTopic();

        verifyZeroInteractions(cmdParser);
        verify(messageTF).requestFocusInWindow();
    }

    @Test
    public void setTopicShouldChangeTopicIfDialogWasAccepted() throws CommandException {
        when(controller.getTopic()).thenReturn(new Topic("Initial topic", "Niles", System.currentTimeMillis()));
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn("new topic");

        mediator.setTopic();

        verify(cmdParser).fixTopic("new topic");
        verify(messageTF).requestFocusInWindow();
        verify(uiTools, never()).showWarningMessage(anyString(), anyString());
    }

    @Test
    public void setTopicShouldShowWarningMessageIfChangingTopicFails() throws CommandException {
        when(controller.getTopic()).thenReturn(new Topic("Initial topic", "Niles", System.currentTimeMillis()));
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn("new topic");
        doThrow(new CommandException("Topic error")).when(cmdParser).fixTopic(anyString());

        mediator.setTopic();

        verify(cmdParser).fixTopic("new topic");
        verify(messageTF).requestFocusInWindow();
        verify(uiTools).showWarningMessage("Topic error", "Change topic");
    }

    @Test
    public void quitShouldExitIfYes() {
        expectedSystemExit.expectSystemExitWithStatus(0);
        expectedSystemExit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                verify(uiTools).showOptionDialog("Are you sure you want to quit?", "Quit");
            }
        });

        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.YES_OPTION);

        mediator.quit();
    }

    @Test
    public void quitShouldNotExitIfCancel() {
        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.CANCEL_OPTION);

        mediator.quit();

        verify(uiTools).showOptionDialog("Are you sure you want to quit?", "Quit");
    }

    @Test
    public void updateTitleAndTrayShouldShowNotConnectedWhenNotConnectedAndNotLoggedOn() {
        when(controller.isConnected()).thenReturn(false);
        when(controller.isLoggedOn()).thenReturn(false);

        mediator.updateTitleAndTray();

        verify(kouChatFrame).setTitle("Me - Not connected - KouChat");
        verify(sysTray).setToolTip("Me - Not connected - KouChat");
    }

    @Test
    public void updateTitleAndTrayShouldShowNotConnectedWhenNotConnectedAndNotLoggedOnEvenWhenAwayAndWithTopicSet() {
        when(controller.isConnected()).thenReturn(false);
        when(controller.isLoggedOn()).thenReturn(false);

        when(controller.getTopic()).thenReturn(new Topic("Topic", "Niles", System.currentTimeMillis()));
        me.setAway(true);

        mediator.updateTitleAndTray();

        verify(kouChatFrame).setTitle("Me - Not connected - KouChat");
        verify(sysTray).setToolTip("Me - Not connected - KouChat");
    }

    @Test
    public void updateTitleAndTrayShouldShowConnectionLostWhenNotConnectedAndLoggedOn() {
        when(controller.isConnected()).thenReturn(false);
        when(controller.isLoggedOn()).thenReturn(true);

        mediator.updateTitleAndTray();

        verify(kouChatFrame).setTitle("Me - Connection lost - KouChat");
        verify(sysTray).setToolTip("Me - Connection lost - KouChat");
    }

    @Test
    public void updateTitleAndTrayShouldShowConnectionLostWhenNotConnectedAndLoggedOnEvenWhenAwayAndWithTopicSet() {
        when(controller.isConnected()).thenReturn(false);
        when(controller.isLoggedOn()).thenReturn(true);

        when(controller.getTopic()).thenReturn(new Topic("Topic", "Niles", System.currentTimeMillis()));
        me.setAway(true);

        mediator.updateTitleAndTray();

        verify(kouChatFrame).setTitle("Me - Connection lost - KouChat");
        verify(sysTray).setToolTip("Me - Connection lost - KouChat");
    }

    @Test
    public void updateTitleAndTrayShouldShowJustNickNameWhenOnline() {
        when(controller.isConnected()).thenReturn(true);
        when(controller.isLoggedOn()).thenReturn(true);

        mediator.updateTitleAndTray();

        verify(kouChatFrame).setTitle("Me - KouChat");
        verify(sysTray).setToolTip("Me - KouChat");
    }

    @Test
    public void updateTitleAndTrayShouldIncludeAwayWhenAway() {
        when(controller.isConnected()).thenReturn(true);
        when(controller.isLoggedOn()).thenReturn(true);
        me.setAway(true);

        mediator.updateTitleAndTray();

        verify(kouChatFrame).setTitle("Me (Away) - KouChat");
        verify(sysTray).setToolTip("Me (Away) - KouChat");
    }

    @Test
    public void updateTitleAndTrayShouldIncludeTopicWhenTopicIsSet() {
        when(controller.isConnected()).thenReturn(true);
        when(controller.isLoggedOn()).thenReturn(true);
        when(controller.getTopic()).thenReturn(new Topic("Christmas time", "Niles", System.currentTimeMillis()));

        mediator.updateTitleAndTray();

        verify(kouChatFrame).setTitle("Me - Topic: Christmas time (Niles) - KouChat");
        verify(sysTray).setToolTip("Me - Topic: Christmas time (Niles) - KouChat");
    }

    @Test
    public void updateTitleAndTrayShouldIncludeBothTopicAndAwayWhenAwayAndTopicIsSet() {
        when(controller.isConnected()).thenReturn(true);
        when(controller.isLoggedOn()).thenReturn(true);
        when(controller.getTopic()).thenReturn(new Topic("Smell you later", "Kenny", System.currentTimeMillis()));
        me.setAway(true);

        mediator.updateTitleAndTray();

        verify(kouChatFrame).setTitle("Me (Away) - Topic: Smell you later (Kenny) - KouChat");
        verify(sysTray).setToolTip("Me (Away) - Topic: Smell you later (Kenny) - KouChat");
    }

    @Test
    public void updateTitleAndTrayShouldUpdateWindowIcon() {
        mediator.updateTitleAndTray();

        verify(kouChatFrame).updateWindowIcon();
    }

    @Test
    public void sendFileShouldDoNothingIfUserIsNull() {
        mediator.sendFile(null, null);

        verifyZeroInteractions(cmdParser, uiTools);
    }

    @Test
    public void sendFileShouldShowWarningMessageIfUserIsMe() {
        mediator.sendFile(me, null);

        verify(uiTools).showWarningMessage("You cannot send files to yourself.", "Warning");
        verify(uiTools, never()).createFileChooser(anyString());
        verifyZeroInteractions(cmdParser);
    }

    @Test
    public void sendFileShouldShowWarningMessageIfMeIsAway() {
        me.setAway(true);

        mediator.sendFile(user, null);

        verify(uiTools).showWarningMessage("You cannot send files while you are away.", "Warning");
        verify(uiTools, never()).createFileChooser(anyString());
        verifyZeroInteractions(cmdParser);
    }

    @Test
    public void sendFileShouldShowWarningMessageIfUserIsAway() {
        user.setAway(true);

        mediator.sendFile(user, null);

        verify(uiTools).showWarningMessage("You cannot send files to Sally, which is away.", "Warning");
        verify(uiTools, never()).createFileChooser(anyString());
        verifyZeroInteractions(cmdParser);
    }

    @Test
    public void sendFileShouldShowWarningMessageIfUserHasLoggedOff() {
        user.setOnline(false);

        mediator.sendFile(user, null);

        verify(uiTools).showWarningMessage("You cannot send files to Sally, which is not online anymore.", "Warning");
        verify(uiTools, never()).createFileChooser(anyString());
        verifyZeroInteractions(cmdParser);
    }

    @Test
    public void sendFileShouldNotSetSelectedFileIfSelectedFileIsNull() {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.CANCEL_OPTION);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        mediator.sendFile(user, null);

        verify(uiTools).createFileChooser("Open");
        verify(fileChooser, never()).setSelectedFile(any(File.class));
    }

    @Test
    public void sendFileShouldNotSetSelectedFileIfSelectedFileDoesNotExist() {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.CANCEL_OPTION);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        final File selectedFile = new File("");
        assertFalse(selectedFile.exists());
        mediator.sendFile(user, selectedFile);

        verify(uiTools).createFileChooser("Open");
        verify(fileChooser, never()).setSelectedFile(any(File.class));
    }

    @Test
    public void sendFileShouldSetSelectedFileIfSelectedFileExist() {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.CANCEL_OPTION);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        final File selectedFile = new File("README");
        assertTrue(selectedFile.exists());

        mediator.sendFile(user, selectedFile);

        verify(uiTools).createFileChooser("Open");
        verify(fileChooser).setSelectedFile(selectedFile);
    }

    @Test
    public void sendFileShouldNotSendIfUserClicksCancel() {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.CANCEL_OPTION);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        mediator.sendFile(user, new File("README"));

        verify(uiTools).createFileChooser("Open");
        verify(uiTools, never()).showWarningMessage(anyString(), anyString());
        verifyZeroInteractions(cmdParser);
    }

    @Test
    public void sendFileShouldNotSendIfFileDoesNotExist() {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION);

        final File selectedFile = new File("nothing.txt");
        assertFalse(selectedFile.exists());
        when(fileChooser.getSelectedFile()).thenReturn(selectedFile);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        mediator.sendFile(user, null);

        verify(uiTools).createFileChooser("Open");
        verify(uiTools, never()).showWarningMessage(anyString(), anyString());
        verifyZeroInteractions(cmdParser);
    }

    @Test
    public void sendFileShouldNotSendIfFileIsDirectory() {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION);

        final File selectedFile = new File("icons");
        assertTrue(selectedFile.exists());
        assertFalse(selectedFile.isFile());
        when(fileChooser.getSelectedFile()).thenReturn(selectedFile);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        mediator.sendFile(user, null);

        verify(uiTools).createFileChooser("Open");
        verify(uiTools, never()).showWarningMessage(anyString(), anyString());
        verifyZeroInteractions(cmdParser);
    }

    @Test
    public void sendFileShouldSendIfFileIsValid() throws CommandException {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION);

        final File selectedFile = new File("README");
        assertTrue(selectedFile.exists());
        assertTrue(selectedFile.isFile());
        when(fileChooser.getSelectedFile()).thenReturn(selectedFile);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        mediator.sendFile(user, null);

        verify(uiTools).createFileChooser("Open");
        verify(uiTools, never()).showWarningMessage(anyString(), anyString());
        verify(cmdParser).sendFile(user, selectedFile.getAbsoluteFile());
    }

    @Test
    public void sendFileShouldShowWarningMessageIfSendingFileFailed() throws CommandException {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION);

        final File selectedFile = new File("README");
        assertTrue(selectedFile.exists());
        assertTrue(selectedFile.isFile());
        when(fileChooser.getSelectedFile()).thenReturn(selectedFile);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        doThrow(new CommandException("Don't send file")).when(cmdParser).sendFile(any(User.class), any(File.class));

        mediator.sendFile(user, null);

        verify(uiTools).createFileChooser("Open");
        verify(uiTools).showWarningMessage("Don't send file", "Send file");
        verify(cmdParser).sendFile(user, selectedFile.getAbsoluteFile());
    }

    @Test
    public void changeNickShouldReturnTrueAndDoNothingIfNickNameIsTheSame() {
        me.setNick("Lilly");

        assertTrue(mediator.changeNick("Lilly"));

        verifyZeroInteractions(controller, uiTools);
    }

    @Test
    public void changeNickShouldReturnTrueAndDoNothingIfTrimmedNickNameIsTheSame() {
        me.setNick("Lilly");

        assertTrue(mediator.changeNick("   Lilly   "));

        verifyZeroInteractions(controller, uiTools);
    }

    @Test
    public void changeNickShouldReturnFalseAndShowWarningIfNickNameIsInUse() {
        when(controller.isNickInUse(anyString())).thenReturn(true);

        assertFalse(mediator.changeNick(" Lilly "));

        verify(uiTools).showWarningMessage("The nick is in use by someone else.", "Change nick");
        verify(controller).isNickInUse("Lilly");
        verifyNoMoreInteractions(controller, uiTools);
    }

    @Test
    public void changeNickShouldReturnFalseAndShowWarningIfNickNameIsInvalid() {
        assertFalse(mediator.changeNick(" Lilly@ "));

        verify(uiTools).showWarningMessage("'Lilly@' is not a valid nick name.\n\n" +
                                                   "A nick name can have between 1 and 10 characters.\n" +
                                                   "Legal characters are 'a-z', '0-9', '-' and '_'.",
                                           "Change nick");
        verify(controller).isNickInUse("Lilly@");
        verifyNoMoreInteractions(controller, uiTools);
    }

    @Test
    public void changeNickShouldReturnTrueAndChangeNickNameIfNickNameIsValid() throws CommandException {
        doAnswer(withSetNickNameOnMe()).when(controller).changeMyNick(anyString());
        doNothing().when(mediator).updateTitleAndTray();

        assertTrue(mediator.changeNick(" Amy "));

        verify(controller).isNickInUse("Amy");
        verify(controller).changeMyNick("Amy");
        verify(msgController).showSystemMessage("You changed nick to Amy");
        verify(mediator).updateTitleAndTray();

        verifyNoMoreInteractions(controller);
        verifyZeroInteractions(uiTools);
    }

    @Test
    public void changeNickShouldReturnFalseAndShowWarningMessageIfChangeNickNameFails() throws CommandException {
        doThrow(new CommandException("Don't change nick")).when(controller).changeMyNick(anyString());

        assertFalse(mediator.changeNick(" Amy "));

        verify(controller).isNickInUse("Amy");
        verify(controller).changeMyNick("Amy");
        verify(uiTools).showWarningMessage("Don't change nick", "Change nick");

        verifyNoMoreInteractions(controller);
        verifyZeroInteractions(uiTools, msgController);
    }

    @Test
    public void notifyMessageArrivedWhenGuiNotVisibleAndMeIsAwayShouldSetAwayActivityStateInSystemTray() {
        when(kouChatFrame.isVisible()).thenReturn(false);
        when(kouChatFrame.isFocused()).thenReturn(false);
        me.setAway(true);

        mediator.notifyMessageArrived(user);

        verify(sysTray).setAwayActivityState();

        verifyNoMoreInteractions(sysTray);
        verifyZeroInteractions(beeper);
    }

    @Test
    public void notifyMessageArrivedWhenGuiNotVisibleAndMeIsNotAwayShouldSetNormalActivityStateAndBalloonInSystemTrayAndBeep() {
        when(kouChatFrame.isVisible()).thenReturn(false);
        when(kouChatFrame.isFocused()).thenReturn(false);
        me.setAway(false);

        mediator.notifyMessageArrived(user);

        verify(sysTray).setNormalActivityState();
        verify(sysTray).showBalloonMessage("Me - KouChat", "New message from Sally");
        verify(beeper).beep();

        verifyNoMoreInteractions(sysTray);
    }

    @Test
    public void notifyMessageArrivedWhenGuiNotFocusedAndMeIsAwayShouldUpdateTitleAndTray() {
        doNothing().when(mediator).updateTitleAndTray();
        when(kouChatFrame.isVisible()).thenReturn(true);
        when(kouChatFrame.isFocused()).thenReturn(false);
        me.setAway(true);

        mediator.notifyMessageArrived(user);

        verify(mediator).updateTitleAndTray();

        verifyZeroInteractions(beeper, sysTray);
    }

    @Test
    public void notifyMessageArrivedWhenGuiNotFocusedAndMeIsNotAwayShouldUpdateTitleAndTrayAndBeep() {
        doNothing().when(mediator).updateTitleAndTray();
        when(kouChatFrame.isVisible()).thenReturn(true);
        when(kouChatFrame.isFocused()).thenReturn(false);
        me.setAway(false);

        mediator.notifyMessageArrived(user);

        verify(mediator).updateTitleAndTray();
        verify(beeper).beep();

        verifyZeroInteractions(sysTray);
    }

    @Test
    public void notifyMessageArrivedWhenGuiFocusedShouldDoNothing() {
        when(kouChatFrame.isVisible()).thenReturn(true);
        when(kouChatFrame.isFocused()).thenReturn(true);

        mediator.notifyMessageArrived(user);

        verify(mediator, never()).updateTitleAndTray();
        verifyZeroInteractions(sysTray, beeper);
    }

    @Test
    public void notifyPrivateMessageArrivedWhenGuiHiddenAndPrivateChatFocusedShouldDoNothing() {
        when(kouChatFrame.isVisible()).thenReturn(false);
        when(kouChatFrame.isFocused()).thenReturn(false);

        when(privchat.isVisible()).thenReturn(true);
        when(privchat.isFocused()).thenReturn(true);

        mediator.notifyPrivateMessageArrived(user);

        verifyZeroInteractions(sysTray, beeper);
        assertFalse(me.isNewMsg());
    }

    @Test
    public void notifyPrivateMessageArrivedWhenGuiAndPrivateChatHiddenShouldSetNormalActivityStateAndBalloonInSystemTrayAndBeep() {
        when(kouChatFrame.isVisible()).thenReturn(false);
        when(kouChatFrame.isFocused()).thenReturn(false);

        when(privchat.isVisible()).thenReturn(false);
        when(privchat.isFocused()).thenReturn(false);

        mediator.notifyPrivateMessageArrived(user);

        verify(sysTray).setNormalActivityState();
        verify(sysTray).showBalloonMessage("Me - KouChat", "New private message from Sally");
        verify(beeper).beep();

        verifyNoMoreInteractions(sysTray);
        assertFalse(me.isNewMsg());
    }

    @Test
    public void notifyPrivateMessageArrivedWhenGuiHiddenAndPrivateChatOutOfFocusShouldUpdateAndBeep() {
        when(kouChatFrame.isVisible()).thenReturn(false);
        when(kouChatFrame.isFocused()).thenReturn(false);

        when(privchat.isVisible()).thenReturn(true);
        when(privchat.isFocused()).thenReturn(false);

        mediator.notifyPrivateMessageArrived(user);

        verify(privchat).updateUserInformation();
        verify(beeper).beep();

        verifyZeroInteractions(sysTray);
        assertFalse(me.isNewMsg());
    }

    @Test
    public void notifyPrivateMessageArrivedWhenGuiOutOfFocusAndPrivateChatFocusedShouldDoNothing() {
        when(kouChatFrame.isVisible()).thenReturn(true);
        when(kouChatFrame.isFocused()).thenReturn(false);

        when(privchat.isVisible()).thenReturn(true);
        when(privchat.isFocused()).thenReturn(true);

        mediator.notifyPrivateMessageArrived(user);

        verifyZeroInteractions(sysTray, beeper);
        assertFalse(me.isNewMsg());
    }

    @Test
    public void notifyPrivateMessageArrivedWhenGuiOutOfFocusAndPrivateChatHiddenShouldBeepAndUpdateAndSetNewMessage() {
        doNothing().when(mediator).updateTitleAndTray();
        when(kouChatFrame.isVisible()).thenReturn(true);
        when(kouChatFrame.isFocused()).thenReturn(false);

        when(privchat.isVisible()).thenReturn(false);
        when(privchat.isFocused()).thenReturn(false);

        assertFalse(me.isNewMsg());

        mediator.notifyPrivateMessageArrived(user);

        assertTrue(me.isNewMsg());
        verify(mediator).updateTitleAndTray();
        verify(beeper).beep();

        verifyZeroInteractions(sysTray);
    }

    @Test
    public void notifyPrivateMessageArrivedWhenGuiAndPrivateChatOutOfFocusShouldUpdateAndBeep() {
        when(kouChatFrame.isVisible()).thenReturn(true);
        when(kouChatFrame.isFocused()).thenReturn(false);

        when(privchat.isVisible()).thenReturn(true);
        when(privchat.isFocused()).thenReturn(false);

        mediator.notifyPrivateMessageArrived(user);

        verify(privchat).updateUserInformation();
        verify(beeper).beep();

        verifyZeroInteractions(sysTray);
        assertFalse(me.isNewMsg());
    }

    // Skipping test with both gui and privchat focused - not possible

    @Test
    public void notifyPrivateMessageArrivedWhenGuiInFocusAndPrivateChatHiddenShouldDoNothing() {
        doNothing().when(mediator).updateTitleAndTray();
        when(kouChatFrame.isVisible()).thenReturn(true);
        when(kouChatFrame.isFocused()).thenReturn(true);

        when(privchat.isVisible()).thenReturn(false);
        when(privchat.isFocused()).thenReturn(false);

        mediator.notifyPrivateMessageArrived(user);

        verifyZeroInteractions(sysTray, beeper);
        assertFalse(me.isNewMsg());
    }

    @Test
    public void notifyPrivateMessageArrivedWhenGuiInFocusAndPrivateChatOutOfFocusShouldUpdate() {
        when(kouChatFrame.isVisible()).thenReturn(true);
        when(kouChatFrame.isFocused()).thenReturn(true);

        when(privchat.isVisible()).thenReturn(true);
        when(privchat.isFocused()).thenReturn(false);

        mediator.notifyPrivateMessageArrived(user);

        verify(privchat).updateUserInformation();

        verifyZeroInteractions(sysTray, beeper);
        assertFalse(me.isNewMsg());
    }

    @Test
    public void askFileSaveShouldBeepAndAskToSaveFileAndReturnFalseOnNo() {
        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.NO_OPTION);

        assertFalse(mediator.askFileSave("Niles", "donald.png", "2048kb"));

        verify(beeper).beep();
        verify(uiTools).showOptionDialog("Niles wants to send you the file donald.png (2048kb)\nAccept?", "Receive file");
    }

    @Test
    public void askFileSaveShouldBeepAndAskToSaveFileAndReturnTrueOnYes() {
        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.YES_OPTION);

        assertTrue(mediator.askFileSave("Penny", "dolly.png", "1024kb"));

        verify(beeper).beep();
        verify(uiTools).showOptionDialog("Penny wants to send you the file dolly.png (1024kb)\nAccept?", "Receive file");
    }

    @Test
    public void showFileSaveShouldSetSelectedFileAndShowSaveDialogUsingInvokeAndWait() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        final JFileChooser fileChooser = mock(JFileChooser.class);
        final File file = mock(File.class);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);
        when(fileChooser.showSaveDialog(null)).thenReturn(JFileChooser.CANCEL_OPTION);
        when(fileReceiver.getFile()).thenReturn(file);

        mediator.showFileSave(fileReceiver);

        verify(uiTools).createFileChooser("Save");
        verify(fileChooser).setSelectedFile(file);
        verify(fileChooser).showSaveDialog(null);
        verify(uiTools).invokeAndWait(any(Runnable.class));
    }

    @Test
    public void showFileSaveShouldRejectOnCancel() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        final JFileChooser fileChooser = mock(JFileChooser.class);
        final File file = mock(File.class);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);
        when(fileChooser.showSaveDialog(null)).thenReturn(JFileChooser.CANCEL_OPTION);
        when(fileReceiver.getFile()).thenReturn(file);

        mediator.showFileSave(fileReceiver);

        verify(fileReceiver).reject();
        verify(fileReceiver, never()).accept();
    }

    @Test
    public void showFileSaveShouldAcceptAndSetSelectedFileOnSave() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        final JFileChooser fileChooser = mock(JFileChooser.class);

        final File selectedFile = new File("nothing.txt");
        assertFalse(selectedFile.exists());

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);
        when(fileChooser.showSaveDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION);
        when(fileReceiver.getFile()).thenReturn(mock(File.class));
        when(fileChooser.getSelectedFile()).thenReturn(selectedFile);

        mediator.showFileSave(fileReceiver);

        verify(fileReceiver).accept();
        verify(fileReceiver).setFile(selectedFile.getAbsoluteFile());

        verify(fileReceiver, never()).reject();
        verify(uiTools, never()).showOptionDialog(anyString(), anyString());
    }

    @Test
    public void showFileSaveShouldAcceptIfFileExistsOnSaveAndYesOnOverwrite() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        final JFileChooser fileChooser = mock(JFileChooser.class);

        final File selectedFile = new File("README");
        assertTrue(selectedFile.exists());

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);
        when(fileChooser.showSaveDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION);
        when(fileReceiver.getFile()).thenReturn(mock(File.class));
        when(fileChooser.getSelectedFile()).thenReturn(selectedFile);
        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.YES_OPTION);

        mediator.showFileSave(fileReceiver);

        verify(fileReceiver).accept();
        verify(fileReceiver).setFile(selectedFile.getAbsoluteFile());

        verify(fileReceiver, never()).reject();
        verify(uiTools).showOptionDialog("README already exists.\nOverwrite?", "File exists");
    }

    @Test
    public void showFileSaveShouldRetryUntilCancelIfFileExistsAndThenReject() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        final JFileChooser fileChooser = mock(JFileChooser.class);

        final File selectedFile = new File("README");
        assertTrue(selectedFile.exists());

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        // Cancel save dialog on the third try to break out of loop
        when(fileChooser.showSaveDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION,
                                                          JFileChooser.APPROVE_OPTION,
                                                          JFileChooser.CANCEL_OPTION);
        when(fileReceiver.getFile()).thenReturn(mock(File.class));
        when(fileChooser.getSelectedFile()).thenReturn(selectedFile);
        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.CANCEL_OPTION);

        mediator.showFileSave(fileReceiver);

        verify(fileReceiver, never()).accept();

        verify(fileReceiver).reject();
        verify(uiTools, times(2)).showOptionDialog("README already exists.\nOverwrite?", "File exists");
    }

    @Test
    public void showFileSaveShouldRetryUntilOverwriteIfFileExistsAndThenAccept() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        final JFileChooser fileChooser = mock(JFileChooser.class);

        final File selectedFile = new File("README");
        assertTrue(selectedFile.exists());

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);
        when(fileChooser.showSaveDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION);
        when(fileReceiver.getFile()).thenReturn(mock(File.class));
        when(fileChooser.getSelectedFile()).thenReturn(selectedFile);

        // Accept overwrite on third try to break out of loop
        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.CANCEL_OPTION,
                                                                            JOptionPane.CANCEL_OPTION,
                                                                            JOptionPane.YES_OPTION);

        mediator.showFileSave(fileReceiver);

        verify(fileReceiver).accept();
        verify(fileReceiver).setFile(selectedFile.getAbsoluteFile());

        verify(fileReceiver, never()).reject();
        verify(uiTools, times(3)).showOptionDialog("README already exists.\nOverwrite?", "File exists");
    }

    @Test
    public void changeAwayWhenAwayShouldSetAwayState() {
        mediator.changeAway(true);

        verify(sysTray).setAwayState();
        verify(messageTF).setEnabled(false);
        verify(menuBar).setAwayState(true);
        verify(buttonP).setAwayState(true);
    }

    @Test
    public void changeAwayWhenNotAwayShouldSetNormalState() {
        mediator.changeAway(false);

        verify(sysTray).setNormalState();
        verify(messageTF).setEnabled(true);
        verify(menuBar).setAwayState(false);
        verify(buttonP).setAwayState(false);
    }

    @Test
    public void changeAwayWhenAwayShouldUpdateTitleAndTray() {
        mediator.changeAway(true);

        verify(mediator).updateTitleAndTray();
    }

    @Test
    public void changeAwayWhenNotAwayShouldUpdateTitleAndTray() {
        mediator.changeAway(false);

        verify(mediator).updateTitleAndTray();
    }

    // TODO more tests of changeAway

    private Answer<Void> withSetNickNameOnMe() {
        return new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                final String newNick = (String) invocation.getArguments()[0];
                me.setNick(newNick);

                return null;
            }
        };
    }
}
