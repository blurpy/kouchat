
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
import javax.swing.JMenuItem;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link MenuBar}.
 *
 * @author Christian Ihle
 */
public class MenuBarTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MenuBar menuBar;

    private JMenu fileMenu;
    private JMenuItem minimizeMenuItem;
    private JMenuItem quitMenuItem;

    private JMenu toolsMenu;
    private JMenuItem clearMenuItem;
    private JMenuItem awayMenuItem;
    private JMenuItem topicMenuItem;
    private JMenuItem settingsMenuItem;

    private JMenu helpMenu;
    private JMenuItem faqMenuItem;
    private JMenuItem licenseMenuItem;
    private JMenuItem tipsMenuItem;
    private JMenuItem commandsMenuItem;
    private JMenuItem aboutMenuItem;

    @Before
    public void setUp() {
        menuBar = new MenuBar(mock(ImageLoader.class), mock(Settings.class));

        fileMenu = TestUtils.getFieldValue(menuBar, JMenu.class, "fileMenu");
        minimizeMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "minimizeMI");
        quitMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "quitMI");

        toolsMenu = TestUtils.getFieldValue(menuBar, JMenu.class, "toolsMenu");
        clearMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "clearMI");
        awayMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "awayMI");
        topicMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "topicMI");
        settingsMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "settingsMI");

        helpMenu = TestUtils.getFieldValue(menuBar, JMenu.class, "helpMenu");
        faqMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "faqMI");
        licenseMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "licenseMI");
        tipsMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "tipsMI");
        commandsMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "commandsMI");
        aboutMenuItem = TestUtils.getFieldValue(menuBar, JMenuItem.class, "aboutMI");
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new MenuBar(null, mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new MenuBar(mock(ImageLoader.class), null);
    }

    @Test
    public void fileMenuShouldHaveCorrectText() {
        assertEquals("File", fileMenu.getText());
        assertEquals('F', fileMenu.getMnemonic());
    }

    @Test
    public void minimizeMenuItemShouldHaveCorrectText() {
        assertEquals("Minimize", minimizeMenuItem.getText());
        assertEquals('M', minimizeMenuItem.getMnemonic());
    }

    @Test
    public void quitMenuItemShouldHaveCorrectText() {
        assertEquals("Quit", quitMenuItem.getText());
        assertEquals('Q', quitMenuItem.getMnemonic());
    }

    @Test
    public void toolsMenuShouldHaveCorrectText() {
        assertEquals("Tools", toolsMenu.getText());
        assertEquals('T', toolsMenu.getMnemonic());
    }

    @Test
    public void clearMenuItemShouldHaveCorrectText() {
        assertEquals("Clear chat", clearMenuItem.getText());
        assertEquals('C', clearMenuItem.getMnemonic());
    }

    @Test
    public void awayMenuItemShouldHaveCorrectText() {
        assertEquals("Set away", awayMenuItem.getText());
        assertEquals('A', awayMenuItem.getMnemonic());
    }

    @Test
    public void topicMenuItemShouldHaveCorrectText() {
        assertEquals("Change topic", topicMenuItem.getText());
        assertEquals('O', topicMenuItem.getMnemonic());
    }

    @Test
    public void settingsMenuItemShouldHaveCorrectText() {
        assertEquals("Settings", settingsMenuItem.getText());
        assertEquals('S', settingsMenuItem.getMnemonic());
    }

    @Test
    public void helpMenuShouldHaveCorrectText() {
        assertEquals("Help", helpMenu.getText());
        assertEquals('H', helpMenu.getMnemonic());
    }

    @Test
    public void faqMenuItemShouldHaveCorrectText() {
        assertEquals("FAQ", faqMenuItem.getText());
        assertEquals('F', faqMenuItem.getMnemonic());
    }

    @Test
    public void licenseMenuItemShouldHaveCorrectText() {
        assertEquals("License", licenseMenuItem.getText());
        assertEquals('L', licenseMenuItem.getMnemonic());
    }

    @Test
    public void tipsMenuItemShouldHaveCorrectText() {
        assertEquals("Tips & tricks", tipsMenuItem.getText());
        assertEquals('T', tipsMenuItem.getMnemonic());
    }

    @Test
    public void commandsMenuItemShouldHaveCorrectText() {
        assertEquals("Commands", commandsMenuItem.getText());
        assertEquals('C', commandsMenuItem.getMnemonic());
    }

    @Test
    public void aboutMenuItemShouldHaveCorrectText() {
        assertEquals("About", aboutMenuItem.getText());
        assertEquals('A', aboutMenuItem.getMnemonic());
    }
}
