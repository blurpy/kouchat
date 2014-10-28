
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

import java.util.Date;

import javax.swing.JMenuItem;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.SortedUserList;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.TestUtils;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link SidePanel}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SidePanelTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SidePanel sidePanel;

    private Mediator mediator;
    private UITools uiTools;
    private SwingMessages messages;

    private JMenuItem infoMenuItem;
    private JMenuItem sendfileMenuItem;
    private JMenuItem privchatMenuItem;

    private User abby;
    private User dorothy;
    private User sandra;

    @Before
    public void setUp() {
        messages = new SwingMessages();

        sidePanel = new SidePanel(new ButtonPanel(messages), mock(ImageLoader.class), mock(Settings.class), messages);

        mediator = mock(Mediator.class);
        sidePanel.setMediator(mediator);

        abby = new User("Abby", 123);
        dorothy = new User("Dorothy", 124);
        sandra = new User("Sandra", 125);

        final SortedUserList userList = new SortedUserList();
        userList.add(abby);
        userList.add(dorothy);
        userList.add(sandra);

        sidePanel.setUserList(userList);

        uiTools = TestUtils.setFieldValueWithMock(sidePanel, "uiTools", UITools.class);
        doAnswer(new RunArgumentAnswer()).when(uiTools).invokeLater(any(Runnable.class));

        infoMenuItem = TestUtils.getFieldValue(sidePanel, JMenuItem.class, "infoMI");
        sendfileMenuItem = TestUtils.getFieldValue(sidePanel, JMenuItem.class, "sendfileMI");
        privchatMenuItem = TestUtils.getFieldValue(sidePanel, JMenuItem.class, "privchatMI");
    }

    @Test
    public void constructorShouldThrowExceptionIfButtonPanelIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Button panel can not be null");

        new SidePanel(null, mock(ImageLoader.class), mock(Settings.class), messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new SidePanel(mock(ButtonPanel.class), null, mock(Settings.class), messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new SidePanel(mock(ButtonPanel.class), mock(ImageLoader.class), null, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        new SidePanel(mock(ButtonPanel.class), mock(ImageLoader.class), mock(Settings.class), null);
    }

    @Test
    public void infoMenuItemShouldHaveCorrectText() {
        assertEquals("Information", infoMenuItem.getText());
    }

    @Test
    public void infoMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('I', infoMenuItem.getMnemonic());
    }

    @Test
    public void sendFileMenuItemShouldHaveCorrectText() {
        assertEquals("Send file", sendfileMenuItem.getText());
    }

    @Test
    public void sendFileMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('S', sendfileMenuItem.getMnemonic());
    }

    @Test
    public void privateChatMenuItemShouldHaveCorrectText() {
        assertEquals("Private chat", privchatMenuItem.getText());
    }

    @Test
    public void privateChatMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('P', privchatMenuItem.getMnemonic());
    }

    @Test
    public void setMediatorShouldThrowExceptionIfMediatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Mediator can not be null");

        sidePanel.setMediator(null);
    }

    @Test
    public void setUserListShouldThrowExceptionIfUserListIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User list can not be null");

        sidePanel.setUserList(null);
    }

    @Test
    public void clickOnSendFileShouldSendFileToSelectedUser() {
        sidePanel.getUserList().setSelectedIndex(1);

        sendfileMenuItem.doClick();

        verify(mediator).sendFile(dorothy, null);
    }

    @Test
    public void clickOnPrivateChatShouldOpenPrivateChatWithSelectedUser() {
        sidePanel.getUserList().setSelectedIndex(2);

        privchatMenuItem.doClick();

        verify(mediator).showPrivChat(sandra);
    }

    @Test
    public void clickOnInformationShouldShowInfoAboutSelectedUser() {
        sidePanel.getUserList().setSelectedIndex(0);

        final Date logonTime = new LocalDateTime()
                .minusDays(1)
                .minusHours(4)
                .minusMinutes(12)
                .minusSeconds(45)
                .toDate();

        abby.setClient("JUnit");
        abby.setOperatingSystem("Solaris");
        abby.setIpAddress("192.168.1.1");
        abby.setLogonTime(logonTime.getTime());

        infoMenuItem.doClick();

        // Note: this might fail when logonTime is summer time and current time is not
        verify(uiTools).showInfoMessage("Information about Abby.\n" +
                                                "\n" +
                                                "IP address: 192.168.1.1\n" +
                                                "Client: JUnit\n" +
                                                "Operating System: Solaris\n" +
                                                "\n" +
                                                "Online: 1 days, 04:12:45",
                                        "Info");
    }

    @Test
    public void clickOnInformationShouldIncludeAwayInfoWhenAway() {
        sidePanel.getUserList().setSelectedIndex(0);

        final Date logonTime = new LocalDateTime()
                .minusDays(1)
                .minusHours(4)
                .minusMinutes(12)
                .minusSeconds(45)
                .toDate();

        abby.setClient("JUnit");
        abby.setOperatingSystem("Solaris");
        abby.setIpAddress("192.168.1.1");
        abby.setLogonTime(logonTime.getTime());
        abby.setAway(true);
        abby.setAwayMsg("Gone home");

        infoMenuItem.doClick();

        // Note: this might fail when logonTime is summer time and current time is not
        verify(uiTools).showInfoMessage("Information about Abby (Away).\n" +
                                                "\n" +
                                                "IP address: 192.168.1.1\n" +
                                                "Client: JUnit\n" +
                                                "Operating System: Solaris\n" +
                                                "\n" +
                                                "Online: 1 days, 04:12:45\n" +
                                                "Away message: Gone home",
                                        "Info");
    }

    @Test
    public void clickOnInformationShouldIncludeHostNameWhenAvailable() {
        sidePanel.getUserList().setSelectedIndex(1);

        final Date logonTime = new LocalDateTime()
                .minusDays(2)
                .minusHours(4)
                .minusMinutes(12)
                .minusSeconds(45)
                .toDate();

        dorothy.setClient("PC");
        dorothy.setOperatingSystem("XP");
        dorothy.setIpAddress("192.168.1.2");
        dorothy.setLogonTime(logonTime.getTime());
        dorothy.setHostName("dorothy.kouchat.net");

        infoMenuItem.doClick();

        // Note: this might fail when logonTime is summer time and current time is not
        verify(uiTools).showInfoMessage("Information about Dorothy.\n" +
                                                "\n" +
                                                "IP address: 192.168.1.2\n" +
                                                "Host name: dorothy.kouchat.net\n" +
                                                "Client: PC\n" +
                                                "Operating System: XP\n" +
                                                "\n" +
                                                "Online: 2 days, 04:12:45",
                                        "Info");
    }

    @Test
    public void clickOnInformationShouldIncludeHostNameAndAwayInfoWhenAvailable() {
        sidePanel.getUserList().setSelectedIndex(1);

        final Date logonTime = new LocalDateTime()
                .minusDays(2)
                .minusHours(4)
                .minusMinutes(12)
                .minusSeconds(45)
                .toDate();

        dorothy.setClient("PC");
        dorothy.setOperatingSystem("XP");
        dorothy.setIpAddress("192.168.1.2");
        dorothy.setLogonTime(logonTime.getTime());
        dorothy.setHostName("dorothy.kouchat.net");
        dorothy.setAway(true);
        dorothy.setAwayMsg("Shopping");

        infoMenuItem.doClick();

        // Note: this might fail when logonTime is summer time and current time is not
        verify(uiTools).showInfoMessage("Information about Dorothy (Away).\n" +
                                                "\n" +
                                                "IP address: 192.168.1.2\n" +
                                                "Host name: dorothy.kouchat.net\n" +
                                                "Client: PC\n" +
                                                "Operating System: XP\n" +
                                                "\n" +
                                                "Online: 2 days, 04:12:45\n" +
                                                "Away message: Shopping",
                                        "Info");
    }
}
