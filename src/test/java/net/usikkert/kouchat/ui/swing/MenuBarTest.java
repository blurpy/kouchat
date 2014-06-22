
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

import java.awt.Component;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

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
    public void minimizeMenuItemShouldHaveActionListener() {
        assertSame(menuBar, minimizeMenuItem.getActionListeners()[0]);
    }

    @Test
    public void quitMenuItemShouldHaveCorrectText() {
        assertEquals("Quit", quitMenuItem.getText());
        assertEquals('Q', quitMenuItem.getMnemonic());
    }

    @Test
    public void quitMenuItemShouldHaveActionListener() {
        assertSame(menuBar, quitMenuItem.getActionListeners()[0]);
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
    public void clearMenuItemShouldHaveActionListener() {
        assertSame(menuBar, clearMenuItem.getActionListeners()[0]);
    }

    @Test
    public void awayMenuItemShouldHaveCorrectText() {
        assertEquals("Set away", awayMenuItem.getText());
        assertEquals('A', awayMenuItem.getMnemonic());
    }

    @Test
    public void awayMenuItemShouldHaveActionListener() {
        assertSame(menuBar, awayMenuItem.getActionListeners()[0]);
    }

    @Test
    public void awayMenuItemShouldHaveShortcutKeyF2() {
        assertSame(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), awayMenuItem.getAccelerator());
    }

    @Test
    public void topicMenuItemShouldHaveCorrectText() {
        assertEquals("Change topic", topicMenuItem.getText());
        assertEquals('O', topicMenuItem.getMnemonic());
    }

    @Test
    public void topicMenuItemShouldHaveActionListener() {
        assertSame(menuBar, topicMenuItem.getActionListeners()[0]);
    }

    @Test
    public void topicMenuItemShouldHaveShortcutKeyF3() {
        assertSame(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), topicMenuItem.getAccelerator());
    }

    @Test
    public void settingsMenuItemShouldHaveCorrectText() {
        assertEquals("Settings", settingsMenuItem.getText());
        assertEquals('S', settingsMenuItem.getMnemonic());
    }

    @Test
    public void settingsMenuItemShouldHaveActionListener() {
        assertSame(menuBar, settingsMenuItem.getActionListeners()[0]);
    }

    @Test
    public void settingsMenuItemShouldHaveShortcutKeyF4() {
        assertSame(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), settingsMenuItem.getAccelerator());
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
    public void faqMenuItemShouldHaveActionListener() {
        assertSame(menuBar, faqMenuItem.getActionListeners()[0]);
    }

    @Test
    public void faqMenuItemShouldHaveShortcutKeyF1() {
        assertSame(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), faqMenuItem.getAccelerator());
    }

    @Test
    public void licenseMenuItemShouldHaveCorrectText() {
        assertEquals("License", licenseMenuItem.getText());
        assertEquals('L', licenseMenuItem.getMnemonic());
    }

    @Test
    public void licenseMenuItemShouldHaveActionListener() {
        assertSame(menuBar, licenseMenuItem.getActionListeners()[0]);
    }

    @Test
    public void tipsMenuItemShouldHaveCorrectText() {
        assertEquals("Tips & tricks", tipsMenuItem.getText());
        assertEquals('T', tipsMenuItem.getMnemonic());
    }

    @Test
    public void tipsMenuItemShouldHaveActionListener() {
        assertSame(menuBar, tipsMenuItem.getActionListeners()[0]);
    }

    @Test
    public void commandsMenuItemShouldHaveCorrectText() {
        assertEquals("Commands", commandsMenuItem.getText());
        assertEquals('C', commandsMenuItem.getMnemonic());
    }

    @Test
    public void commandMenuItemShouldHaveActionListener() {
        assertSame(menuBar, commandsMenuItem.getActionListeners()[0]);
    }

    @Test
    public void aboutMenuItemShouldHaveCorrectText() {
        assertEquals("About", aboutMenuItem.getText());
        assertEquals('A', aboutMenuItem.getMnemonic());
    }

    @Test
    public void aboutMenuItemShouldHaveActionListener() {
        assertSame(menuBar, aboutMenuItem.getActionListeners()[0]);
    }

    @Test
    public void layoutShouldIncludeAllMenus() {
        final Component[] components = menuBar.getComponents();
        assertEquals(3, components.length);

        assertSame(fileMenu, components[0]);
        assertSame(toolsMenu, components[1]);
        assertSame(helpMenu, components[2]);
    }

    @Test
    public void fileMenuShouldIncludeAllMenuItems() {
        final Component[] menuComponents = fileMenu.getMenuComponents();

        assertEquals(3, menuComponents.length);

        assertSame(minimizeMenuItem, menuComponents[0]);
        assertEquals(JPopupMenu.Separator.class, menuComponents[1].getClass());
        assertSame(quitMenuItem, menuComponents[2]);
    }

    @Test
    public void toolsMenuShouldIncludeAllMenuItems() {
        final Component[] menuComponents = toolsMenu.getMenuComponents();

        assertEquals(5, menuComponents.length);

        assertSame(clearMenuItem, menuComponents[0]);
        assertSame(awayMenuItem, menuComponents[1]);
        assertSame(topicMenuItem, menuComponents[2]);
        assertEquals(JPopupMenu.Separator.class, menuComponents[3].getClass());
        assertSame(settingsMenuItem, menuComponents[4]);
    }

    @Test
    public void helpMenuShouldIncludeAllMenuItems() {
        final Component[] menuComponents = helpMenu.getMenuComponents();

        assertEquals(7, menuComponents.length);

        assertSame(faqMenuItem, menuComponents[0]);
        assertSame(tipsMenuItem, menuComponents[1]);
        assertSame(licenseMenuItem, menuComponents[2]);
        assertEquals(JPopupMenu.Separator.class, menuComponents[3].getClass());
        assertSame(commandsMenuItem, menuComponents[4]);
        assertEquals(JPopupMenu.Separator.class, menuComponents[5].getClass());
        assertSame(aboutMenuItem, menuComponents[6]);
    }

    @Test
    public void setMediatorShouldThrowExceptionIfMediatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Mediator can not be null");

        menuBar.setMediator(null);
    }

    @Test
    public void setAwayStateShouldDisableSettingsAndTopicMenuItemsWhenAway() {
        assertTrue(settingsMenuItem.isEnabled());
        assertTrue(topicMenuItem.isEnabled());

        menuBar.setAwayState(true);
        assertFalse(settingsMenuItem.isEnabled());
        assertFalse(topicMenuItem.isEnabled());

        menuBar.setAwayState(false);
        assertTrue(settingsMenuItem.isEnabled());
        assertTrue(topicMenuItem.isEnabled());
    }

    @Test
    public void isPopupMenuVisibleShouldBeTrueWhenFileMenuIsVisible() {
        // Unable to make the real implementation visible in a test, so mocking instead
        final JMenu mockFileMenu = TestUtils.setFieldValueWithMock(menuBar, "fileMenu", JMenu.class);

        assertFalse(menuBar.isPopupMenuVisible());

        when(mockFileMenu.isPopupMenuVisible()).thenReturn(true);

        assertTrue(menuBar.isPopupMenuVisible());
    }

    @Test
    public void isPopupMenuVisibleShouldBeTrueWhenToolsMenuIsVisible() {
        // Unable to make the real implementation visible in a test, so mocking instead
        final JMenu mockToolsMenu = TestUtils.setFieldValueWithMock(menuBar, "toolsMenu", JMenu.class);

        assertFalse(menuBar.isPopupMenuVisible());

        when(mockToolsMenu.isPopupMenuVisible()).thenReturn(true);

        assertTrue(menuBar.isPopupMenuVisible());
    }

    @Test
    public void isPopupMenuVisibleShouldBeTrueWhenHelpMenuIsVisible() {
        // Unable to make the real implementation visible in a test, so mocking instead
        final JMenu mockHelpMenu = TestUtils.setFieldValueWithMock(menuBar, "helpMenu", JMenu.class);

        assertFalse(menuBar.isPopupMenuVisible());

        when(mockHelpMenu.isPopupMenuVisible()).thenReturn(true);

        assertTrue(menuBar.isPopupMenuVisible());
    }
}
