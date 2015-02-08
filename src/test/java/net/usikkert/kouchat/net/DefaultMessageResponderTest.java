
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

package net.usikkert.kouchat.net;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.UserInterface;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link DefaultMessageResponder}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class DefaultMessageResponderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DefaultMessageResponder responder;

    private Controller controller;
    private UserInterface userInterface;
    private Settings settings;
    private MessageController messageController;

    private User user;
    private User me;

    @Before
    public void setUp() {
        controller = mock(Controller.class);
        userInterface = mock(UserInterface.class);
        settings = new Settings();
        messageController = mock(MessageController.class);

        when(userInterface.getMessageController()).thenReturn(messageController);

        responder = new DefaultMessageResponder(controller, userInterface, settings);

        user = new User("Tester", 100);
        me = settings.getMe();

        //  Get rid of constructor operations from list of verifications
        verify(controller).getTransferList();
        verify(controller).getWaitingList();
        verify(controller).getChatState();
        verify(userInterface).getMessageController();
    }

    @Test
    public void constructorShouldThrowExceptionIfControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Controller can not be null");

        new DefaultMessageResponder(null, userInterface, settings);
    }

    @Test
    public void constructorShouldThrowExceptionIfUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("UserInterface can not be null");

        new DefaultMessageResponder(controller, null, settings);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new DefaultMessageResponder(controller, userInterface, null);
    }

    @Test
    public void messageArrivedShouldShowMessageAndNotifyUserInterfaceAndSetNewMessageFlagWhenVisibleButNotFocused() {
        setUpExistingUser();

        when(userInterface.isVisible()).thenReturn(true);
        when(userInterface.isFocused()).thenReturn(false);

        responder.messageArrived(100, "msg", 200);

        verify(messageController).showUserMessage("Tester", "msg", 200);
        verify(userInterface).notifyMessageArrived(user);
        assertTrue(me.isNewMsg());
    }

    @Test
    public void messageArrivedShouldShowMessageAndNotifyUserInterfaceButNotSetNewMessageFlagWhenVisibleAndFocused() {
        setUpExistingUser();

        when(userInterface.isVisible()).thenReturn(true);
        when(userInterface.isFocused()).thenReturn(true);

        responder.messageArrived(100, "msg", 200);

        verify(messageController).showUserMessage("Tester", "msg", 200);
        verify(userInterface).notifyMessageArrived(user);
        assertFalse(me.isNewMsg());
    }

    @Test
    public void messageArrivedShouldShowMessageAndNotifyUserInterfaceButNotSetNewMessageFlagWhenNotVisible() {
        setUpExistingUser();

        when(userInterface.isVisible()).thenReturn(false);
        when(userInterface.isFocused()).thenReturn(false); // Can't be focused if not visible

        responder.messageArrived(100, "msg", 200);

        verify(messageController).showUserMessage("Tester", "msg", 200);
        verify(userInterface).notifyMessageArrived(user);
        assertFalse(me.isNewMsg());
    }

    @Test
    public void messageArrivedShouldDoNothingIfUserIsAway() {
        setUpExistingUser();
        user.setAway(true);

        responder.messageArrived(100, "msg", 200);

        verifyZeroInteractions(messageController, userInterface);
    }

    @Test
    public void messageArrivedShouldDoNothingIfUserIsUnknown() {
        setUpUnknownUser();

        responder.messageArrived(100, "msg", 200);

        verifyZeroInteractions(messageController, userInterface);
    }

    @Test
    public void userLogOffShouldDoNothingIfUserIsUnknown() {
        setUpUnknownUser();

        responder.userLogOff(100);

        verifyZeroInteractions(messageController);
        verify(controller, never()).removeUser(any(User.class), anyString());
    }

    @Test
    public void userLogOffShouldRemoveUserAndShowSystemMessage() {
        setUpExistingUser();

        responder.userLogOff(100);

        verify(messageController).showSystemMessage("Tester logged off");
        verify(controller).removeUser(user, "Tester logged off");
    }

    private void setUpExistingUser() {
        when(controller.isNewUser(100)).thenReturn(false);
        when(controller.getUser(100)).thenReturn(user);
    }

    private void setUpUnknownUser() {
        when(controller.isNewUser(100)).thenReturn(true);
        when(controller.getUser(100)).thenReturn(null);
    }
}
