
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

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

        popup = new CopyPopup(textPane);

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

        new CopyPopup(null);
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
    public void copyMenuItemShouldHaveCtrlCAsAccelerator() {
        final KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);

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
    public void selectAllMenuItemShouldHaveCtrlAAsAccelerator() {
        final KeyStroke ctrlA = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK);

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
    public void settingPopupVisibleWithNoTextShouldDisableBothMenuItems() {
        when(textPane.getText()).thenReturn("");
        when(textPane.getSelectedText()).thenReturn(null);

        popup.setVisible(true);

        assertFalse(copyMenuItem.isEnabled());
        assertFalse(selectAllMenuItem.isEnabled());
    }

    @Test
    public void settingPopupVisibleWithSelectionShouldEnableBothMenuItems() {
        when(textPane.getText()).thenReturn("Text");
        when(textPane.getSelectedText()).thenReturn("Text");

        popup.setVisible(true);

        assertTrue(copyMenuItem.isEnabled());
        assertTrue(selectAllMenuItem.isEnabled());
    }

    @Test
    public void settingPopupVisibleWithNoSelectionShouldDisableCopyMenuItem() {
        when(textPane.getText()).thenReturn("Text");
        when(textPane.getSelectedText()).thenReturn(null);

        popup.setVisible(true);

        assertFalse(copyMenuItem.isEnabled());
        assertTrue(selectAllMenuItem.isEnabled());
    }
}
