
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
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

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link CopyPopup}.
 *
 * @author Christian Ihle
 */
public class CopyPopupTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CopyPopup popup;

    private JTextPane textPane;
    private JMenuItem copyMenuItem;
    private JMenuItem selectAllMenuItem;

    @Before
    public void setUp() {
        textPane = mock(JTextPane.class);

        popup = new CopyPopup(textPane, new SwingMessages());

        copyMenuItem = (JMenuItem) popup.getComponent(0);
        selectAllMenuItem = (JMenuItem) popup.getComponent(1);
    }

    @After
    public void tearDown() {
        popup.setVisible(false); // To keep the popup from showing during test runs
    }

    @Test
    public void constructorShouldThrowExceptionIfTextPaneIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Text pane can not be null");

        new CopyPopup(null, mock(SwingMessages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        new CopyPopup(mock(JTextPane.class), null);
    }

    @Test
    public void constructorShouldSetItselfAsComponentPopupMenu() {
        verify(textPane).setComponentPopupMenu(popup);
    }

    @Test
    public void copyMenuItemShouldHaveCorrectText() {
        assertEquals("Copy", copyMenuItem.getText());
    }

    @Test
    public void copyMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('C', copyMenuItem.getMnemonic());
    }

    @Test
    public void copyMenuItemShouldHaveCorrectKeyAsAccelerator() {
        final KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, getMenuKey());

        final KeyStroke accelerator = copyMenuItem.getAccelerator();

        assertSame(ctrlC, accelerator);
    }

    @Test
    public void copyMenuItemShouldHaveCopyAction() {
        final Action action = copyMenuItem.getAction();

        assertEquals(DefaultEditorKit.CopyAction.class, action.getClass());
    }

    @Test
    public void selectAllMenuItemShouldHaveCorrectText() {
        assertEquals("Select All", selectAllMenuItem.getText());
    }

    @Test
    public void selectAllMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('A', selectAllMenuItem.getMnemonic());
    }

    @Test
    public void selectAllMenuItemShouldHaveCorrectKeyAsAccelerator() {
        final KeyStroke ctrlA = KeyStroke.getKeyStroke(KeyEvent.VK_A, getMenuKey());

        final KeyStroke accelerator = selectAllMenuItem.getAccelerator();

        assertSame(ctrlA, accelerator);
    }

    @Test
    public void selectAllMenuItemShouldRequestFocusAndSelectAll() {
        selectAllMenuItem.doClick();

        verify(textPane).requestFocusInWindow();
        verify(textPane).selectAll();
    }

    @Test
    public void settingPopupVisibleWithNoTextShouldDisableBothMenuItemsWhenEnabled() {
        setMenuItemsEnabled(true);

        verifyPopupVisibleWithNoText();
    }

    @Test
    public void settingPopupVisibleWithNoTextShouldDisableBothMenuItemsWhenDisabled() {
        setMenuItemsEnabled(false);

        verifyPopupVisibleWithNoText();
    }

    @Test
    public void settingPopupVisibleWithSelectionShouldEnableBothMenuItemsWhenEnabled() {
        setMenuItemsEnabled(true);

        verifyPopupVisibleWithSelection();
    }

    @Test
    public void settingPopupVisibleWithSelectionShouldEnableBothMenuItemsWhenDisabled() {
        setMenuItemsEnabled(false);

        verifyPopupVisibleWithSelection();
    }

    @Test
    public void settingPopupVisibleWithNoSelectionShouldDisableCopyMenuItemWhenEnabled() {
        setMenuItemsEnabled(true);

        verifyPopupVisibleWithNoSelection();
    }

    @Test
    public void settingPopupVisibleWithNoSelectionShouldDisableCopyMenuItemWhenDisabled() {
        setMenuItemsEnabled(false);

        verifyPopupVisibleWithNoSelection();
    }

    private void verifyPopupVisibleWithNoText() {
        when(textPane.getText()).thenReturn("");
        when(textPane.getSelectedText()).thenReturn(null);

        popup.setVisible(true);

        assertFalse(copyMenuItem.isEnabled());
        assertFalse(selectAllMenuItem.isEnabled());
    }

    private void verifyPopupVisibleWithSelection() {
        when(textPane.getText()).thenReturn("Text");
        when(textPane.getSelectedText()).thenReturn("Text");

        popup.setVisible(true);

        assertTrue(copyMenuItem.isEnabled());
        assertTrue(selectAllMenuItem.isEnabled());
    }

    private void verifyPopupVisibleWithNoSelection() {
        when(textPane.getText()).thenReturn("Text");
        when(textPane.getSelectedText()).thenReturn(null);

        popup.setVisible(true);

        assertFalse(copyMenuItem.isEnabled());
        assertTrue(selectAllMenuItem.isEnabled());
    }

    private void setMenuItemsEnabled(final boolean enabled) {
        copyMenuItem.setEnabled(enabled);
        selectAllMenuItem.setEnabled(enabled);
    }

    private int getMenuKey() {
        if (TestUtils.isMac()) {
            return KeyEvent.META_MASK;
        }

        return KeyEvent.CTRL_MASK;
    }
}
