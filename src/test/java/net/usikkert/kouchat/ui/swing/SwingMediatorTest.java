
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
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.SortedUserList;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test of {@link SwingMediator}.
 *
 * @author Christian Ihle
 */
public class SwingMediatorTest {

    private SwingMediator mediator;

    private User me;
    private JTextField messageTF;
    private UITools uiTools;
    private Controller controller;
    private MessageController msgController;
    private JMXAgent jmxAgent;

    @Before
    public void setUp() {
        messageTF = mock(JTextField.class);

        final MainPanel mainPanel = mock(MainPanel.class);
        when(mainPanel.getMsgTF()).thenReturn(messageTF);

        final ComponentHandler compHandler = new ComponentHandler();
        compHandler.setButtonPanel(mock(ButtonPanel.class));
        compHandler.setGui(mock(KouChatFrame.class));
        compHandler.setMainPanel(mainPanel);
        compHandler.setMenuBar(mock(MenuBar.class));
        compHandler.setSettingsDialog(mock(SettingsDialog.class));
        compHandler.setSidePanel(mock(SidePanel.class));
        compHandler.setSysTray(mock(SysTray.class));

        me = new User("Me", 1234);
        me.setMe(true);

        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(me);

        mediator = spy(new SwingMediator(compHandler, mock(ImageLoader.class), settings));

        uiTools = TestUtils.setFieldValueWithMock(mediator, "uiTools", UITools.class);
        controller = TestUtils.setFieldValueWithMock(mediator, "controller", Controller.class);
        msgController = TestUtils.setFieldValueWithMock(mediator, "msgController", MessageController.class);
        jmxAgent = TestUtils.setFieldValueWithMock(mediator, "jmxAgent", JMXAgent.class);

        when(controller.getUserList()).thenReturn(new SortedUserList());
    }

    @Test
    public void setAwayWhenBackShouldAskAwayMessageAndGoAway() throws CommandException {
        when(uiTools.showInputDialog(anyString(), anyString(), anyString())).thenReturn("Going away");
        doAnswer(new Answer<Void>() {
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                me.setAwayMsg((String) invocation.getArguments()[2]); // Third argument is the away message
                return null;
            }
        }).when(controller).changeAwayStatus(anyInt(), anyBoolean(), anyString());

        mediator.setAway();

        verify(uiTools).showInputDialog("Reason for away?", "Away", null);
        verify(controller).changeAwayStatus(1234, true, "Going away");
        verify(mediator).changeAway(true);
        verify(msgController).showSystemMessage("You went away: Going away");
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
        doThrow(new CommandException("Don't go away"))
                .when(controller).changeAwayStatus(anyInt(), anyBoolean(), anyString());

        mediator.setAway();

        verify(controller).changeAwayStatus(1234, true, "Leaving");
        verify(mediator, never()).changeAway(anyBoolean());
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
        verify(controller).changeAwayStatus(1234, false, "");
        verify(mediator).changeAway(false);
        verify(msgController).showSystemMessage("You came back");
    }

    @Test
    public void setAwayWhenAwayShouldShowWarningMessageIfChangeFails() throws CommandException {
        me.setAway(true);
        me.setAwayMsg("Gone");

        when(uiTools.showOptionDialog(anyString(), anyString())).thenReturn(JOptionPane.YES_OPTION);
        doThrow(new CommandException("Don't come back"))
                .when(controller).changeAwayStatus(anyInt(), anyBoolean(), anyString());

        mediator.setAway();

        verify(uiTools).showOptionDialog("Back from 'Gone'?", "Away");
        verify(controller).changeAwayStatus(1234, false, "");
        verify(mediator, never()).changeAway(anyBoolean());
        verify(uiTools).showWarningMessage("Don't come back", "Change away");
    }

    @Test
    public void setAwayWhenAwayShouldRequestFocusOnInputField() {
        me.setAway(true);

        mediator.setAway();

        verify(messageTF).requestFocusInWindow();
    }

    @Test
    public void activateJMXShouldActiveJMXAgent() {
        mediator.activateJMX();

        verify(jmxAgent).activate();
    }
}
