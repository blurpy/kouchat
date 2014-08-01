
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

import javax.swing.JMenuItem;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link SidePanel}.
 *
 * @author Christian Ihle
 */
public class SidePanelTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SidePanel sidePanel;

    private JMenuItem infoMenuItem;
    private JMenuItem sendfileMenuItem;
    private JMenuItem privchatMenuItem;

    @Before
    public void setUp() {
        sidePanel = new SidePanel(mock(ButtonPanel.class), mock(ImageLoader.class), mock(Settings.class));

        infoMenuItem = TestUtils.getFieldValue(sidePanel, JMenuItem.class, "infoMI");
        sendfileMenuItem = TestUtils.getFieldValue(sidePanel, JMenuItem.class, "sendfileMI");
        privchatMenuItem = TestUtils.getFieldValue(sidePanel, JMenuItem.class, "privchatMI");
    }

    @Test
    public void constructorShouldThrowExceptionIfButtonPanelIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Button panel can not be null");

        new SidePanel(null, mock(ImageLoader.class), mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new SidePanel(mock(ButtonPanel.class), null, mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new SidePanel(mock(ButtonPanel.class), mock(ImageLoader.class), null);
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
}
