
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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.message.PropertyFileMessages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link PrivateChatFrame}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PrivateChatFrameTest {

    private PrivateChatFrame privateChatFrame;

    private JMenu fileMenu;
    private JMenu toolsMenu;
    private JMenuItem closeMenuItem;
    private JMenuItem clearMenuItem;

    private JTextField msgTF;

    private User user;
    private User me;

    private StatusIcons statusIcons;
    private UITools uiTools;
    private Mediator mediator;
    private ErrorHandler errorHandler;
    private Messages messages;
    private ImageLoader imageLoader;
    private Settings settings;

    @Before
    public void setUp() {
        user = new User("Test", 1234);
        me = new User("Me", 1235);

        errorHandler = mock(ErrorHandler.class);
        messages = new PropertyFileMessages("messages.swing");
        imageLoader = new ImageLoader(errorHandler, messages, new ResourceValidator(), new ResourceLoader());

        settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(me);

        mediator = mock(Mediator.class);
        statusIcons = new StatusIcons(imageLoader);

        privateChatFrame = spy(new PrivateChatFrame(mediator, user, imageLoader, settings, messages, errorHandler));

        final JMenuBar menuBar = privateChatFrame.getJMenuBar();
        fileMenu = menuBar.getMenu(0);
        toolsMenu = menuBar.getMenu(1);
        closeMenuItem = fileMenu.getItem(0);
        clearMenuItem = toolsMenu.getItem(0);

        msgTF = TestUtils.setFieldValueWithMock(privateChatFrame, "msgTF", JTextField.class);

        uiTools = TestUtils.setFieldValueWithMock(privateChatFrame, "uiTools", UITools.class);
        doAnswer(new RunArgumentAnswer()).when(uiTools).invokeLater(any(Runnable.class));
        doCallRealMethod().when(uiTools).createTitle(anyString());
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorShouldThrowExceptionIfMediatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Mediator can not be null");

        new PrivateChatFrame(null, user, imageLoader, settings, messages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        new PrivateChatFrame(mediator, null, imageLoader, settings, messages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new PrivateChatFrame(mediator, user, null, settings, messages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new PrivateChatFrame(mediator, user, imageLoader, null, messages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Messages can not be null");

        new PrivateChatFrame(mediator, user, imageLoader, settings, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new PrivateChatFrame(mediator, user, imageLoader, settings, messages, null);
    }

    @Test
    public void fileMenuShouldHaveCorrectText() {
        assertEquals("File", fileMenu.getText());
    }

    @Test
    public void fileMenuShouldHaveCorrectMnemonic() {
        assertEquals('F', fileMenu.getMnemonic());
    }

    @Test
    public void closeMenuItemShouldHaveCorrectText() {
        assertEquals("Close", closeMenuItem.getText());
    }

    @Test
    public void closeMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('C', closeMenuItem.getMnemonic());
    }

    @Test
    public void toolsMenuShouldHaveCorrectText() {
        assertEquals("Tools", toolsMenu.getText());
    }

    @Test
    public void toolsMenuShouldHaveCorrectMnemonic() {
        assertEquals('T', toolsMenu.getMnemonic());
    }

    @Test
    public void clearMenuItemShouldHaveCorrectText() {
        assertEquals("Clear chat", clearMenuItem.getText());
    }

    @Test
    public void clearMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('C', clearMenuItem.getMnemonic());
    }

    @Test
    public void updateUserInformationShouldSetNickNameInTitle() {
        assertEquals("Test - KouChat", privateChatFrame.getTitle());
        user.setNick("Dolly");

        privateChatFrame.updateUserInformation();

        assertEquals("Dolly - KouChat", privateChatFrame.getTitle());
    }

    @Test
    public void updateUserInformationShouldIncludeAwayInTitleWhenAway() {
        assertEquals("Test - KouChat", privateChatFrame.getTitle());
        user.setAway(true);

        privateChatFrame.updateUserInformation();

        assertEquals("Test (Away) - KouChat", privateChatFrame.getTitle());
    }

    @Test
    public void updateUserInformationShouldUpdateWindowIcon() {
        privateChatFrame.updateUserInformation();

        verify(privateChatFrame).updateWindowIcon();
    }

    @Test
    public void updateWindowIconShouldSetNormalIconWhenNotAwayAndNoNewPrivateMessages() {
        assertFalse(me.isAway());
        assertFalse(user.isAway());
        assertFalse(user.isNewPrivMsg());

        privateChatFrame.updateWindowIcon();

        verify(privateChatFrame).setWindowIcon(statusIcons.getNormalIcon());
    }

    @Test
    public void updateWindowIconShouldSetAwayIconWhenMeAwayAndNoNewPrivateMessages() {
        me.setAway(true);
        assertFalse(user.isAway());
        assertFalse(user.isNewPrivMsg());

        privateChatFrame.updateWindowIcon();

        verify(privateChatFrame).setWindowIcon(statusIcons.getAwayIcon());
    }

    @Test
    public void updateWindowIconShouldSetAwayIconWhenUserAwayAndNoNewPrivateMessages() {
        assertFalse(me.isAway());
        user.setAway(true);
        assertFalse(user.isNewPrivMsg());

        privateChatFrame.updateWindowIcon();

        verify(privateChatFrame).setWindowIcon(statusIcons.getAwayIcon());
    }

    @Test
    public void updateWindowIconShouldSetNormalActivityIconWhenNotAwayAndNewPrivateMessages() {
        assertFalse(me.isAway());
        assertFalse(user.isAway());
        user.setNewPrivMsg(true);

        privateChatFrame.updateWindowIcon();

        verify(privateChatFrame).setWindowIcon(statusIcons.getNormalActivityIcon());
    }

    @Test
    public void updateWindowIconShouldSetAwayActivityIconWhenMeAwayAndNoNewPrivateMessages() {
        me.setAway(true);
        assertFalse(user.isAway());
        user.setNewPrivMsg(true);

        privateChatFrame.updateWindowIcon();

        verify(privateChatFrame).setWindowIcon(statusIcons.getAwayActivityIcon());
    }

    @Test
    public void updateWindowIconShouldSetAwayActivityIconWhenUserAwayAndNoNewPrivateMessages() {
        assertFalse(me.isAway());
        user.setAway(true);
        user.setNewPrivMsg(true);

        privateChatFrame.updateWindowIcon();

        verify(privateChatFrame).setWindowIcon(statusIcons.getAwayActivityIcon());
    }

    @Test
    public void updateAwayStateShouldDisableInputAndUpdateUserInfoWhenMeAway() {
        me.setAway(true);
        user.setAway(false);

        privateChatFrame.updateAwayState();

        verify(msgTF).setEnabled(false);
        verify(privateChatFrame).updateUserInformation();
        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void updateAwayStateShouldDisableInputAndUpdateUserInfoWhenUserAway() {
        me.setAway(false);
        user.setAway(true);

        privateChatFrame.updateAwayState();

        verify(msgTF).setEnabled(false);
        verify(privateChatFrame).updateUserInformation();
        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void updateAwayStateShouldDisableInputAndUpdateUserInfoWhenBothAway() {
        me.setAway(true);
        user.setAway(true);

        privateChatFrame.updateAwayState();

        verify(msgTF).setEnabled(false);
        verify(privateChatFrame).updateUserInformation();
        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void updateAwayStateShouldEnableInputAndUpdateUserInfoWhenNoOneAway() {
        me.setAway(false);
        user.setAway(false);

        privateChatFrame.updateAwayState();

        verify(msgTF).setEnabled(true);
        verify(privateChatFrame).updateUserInformation();
        verify(uiTools).invokeLater(any(Runnable.class));
    }
}
