
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

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.usikkert.kouchat.jmx.JMXAgent;
import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.message.PropertyFileMessages;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.SortedUserList;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.ui.swing.settings.SettingsDialog;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link SwingMediator}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SwingMediatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SwingMediator mediator;

    private User me;
    private JTextField messageTF;
    private UITools uiTools;
    private Controller controller;
    private JMXAgent jmxAgent;
    private ComponentHandler componentHandler;

    @Before
    public void setUp() {
        messageTF = mock(JTextField.class);

        final MainPanel mainPanel = mock(MainPanel.class);
        when(mainPanel.getMsgTF()).thenReturn(messageTF);

        componentHandler = spy(new ComponentHandler());
        componentHandler.setButtonPanel(mock(ButtonPanel.class));
        componentHandler.setGui(mock(KouChatFrame.class));
        componentHandler.setMainPanel(mainPanel);
        componentHandler.setMenuBar(mock(MenuBar.class));
        componentHandler.setSettingsDialog(mock(SettingsDialog.class));
        componentHandler.setSidePanel(mock(SidePanel.class));
        componentHandler.setSysTray(mock(SysTray.class));

        me = new User("Me", 1234);
        me.setMe(true);

        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(me);

        final PropertyFileMessages messages = new PropertyFileMessages("messages.swing");

        mediator = spy(new SwingMediator(componentHandler, mock(ImageLoader.class), settings, messages));

        uiTools = TestUtils.setFieldValueWithMock(mediator, "uiTools", UITools.class);
        controller = TestUtils.setFieldValueWithMock(mediator, "controller", Controller.class);
        TestUtils.setFieldValueWithMock(mediator, "msgController", MessageController.class);
        jmxAgent = TestUtils.setFieldValueWithMock(mediator, "jmxAgent", JMXAgent.class);

        when(controller.getUserList()).thenReturn(new SortedUserList());
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
}
