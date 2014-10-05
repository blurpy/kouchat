
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link CopyPastePopup}.
 *
 * @author Christian Ihle
 */
public class CopyPastePopupTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CopyPastePopup popup;

    private JTextField textField;
    private JMenuItem cutMenuItem;
    private JMenuItem copyMenuItem;
    private JMenuItem pasteMenuItem;
    private JMenuItem clearMenuItem;

    @Before
    public void setUp() {
        textField = mock(JTextField.class);
        popup = new CopyPastePopup(textField, new SwingMessages());

        cutMenuItem = (JMenuItem) popup.getComponent(0);
        copyMenuItem = (JMenuItem) popup.getComponent(1);
        pasteMenuItem = (JMenuItem) popup.getComponent(2);
        clearMenuItem = (JMenuItem) popup.getComponent(4);
    }

    @After
    public void tearDown() {
        popup.setVisible(false); // To keep the popup from showing during test runs
    }

    @Test
    public void constructorShouldThrowExceptionIfTextPaneIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Text field can not be null");

        new CopyPastePopup(null, mock(SwingMessages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        new CopyPastePopup(mock(JTextField.class), null);
    }

    @Test
    public void constructorShouldSetItselfAsComponentPopupMenu() {
        verify(textField).setComponentPopupMenu(popup);
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
    public void cutMenuItemShouldHaveCorrectText() {
        assertEquals("Cut", cutMenuItem.getText());
    }

    @Test
    public void cutMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('U', cutMenuItem.getMnemonic());
    }

    @Test
    public void cutMenuItemShouldHaveCtrlXAsAccelerator() {
        final KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK);

        final KeyStroke accelerator = cutMenuItem.getAccelerator();

        assertSame(ctrlX, accelerator);
    }

    @Test
    public void cutMenuItemShouldHaveCutAction() {
        final Action action = cutMenuItem.getAction();

        assertEquals(DefaultEditorKit.CutAction.class, action.getClass());
    }

    @Test
    public void pasteMenuItemShouldHaveCorrectText() {
        assertEquals("Paste", pasteMenuItem.getText());
    }

    @Test
    public void pasteMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('P', pasteMenuItem.getMnemonic());
    }

    @Test
    public void pasteMenuItemShouldHaveCtrlVAsAccelerator() {
        final KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK);

        final KeyStroke accelerator = pasteMenuItem.getAccelerator();

        assertSame(ctrlV, accelerator);
    }

    @Test
    public void pasteMenuItemShouldHavePasteAction() {
        final Action action = pasteMenuItem.getAction();

        assertEquals(DefaultEditorKit.PasteAction.class, action.getClass());
    }

    @Test
    public void clearMenuItemShouldHaveCorrectText() {
        assertEquals("Clear", clearMenuItem.getText());
    }

    @Test
    public void clearMenuItemShouldHaveCorrectMnemonic() {
        assertEquals('L', clearMenuItem.getMnemonic());
    }

    @Test
    public void clearMenuItemShouldClearText() {
        clearMenuItem.doClick();

        verify(textField).setText("");
    }

    @Test
    public void settingPopupVisibleWithNoTextShouldDisableAllMenuItemsExceptPasteWhenEnabled() {
        setMenuItemsEnabled(true);

        verifyPopupVisibleWithNoText();
    }

    @Test
    public void settingPopupVisibleWithNoTextShouldDisableAllMenuItemsExceptPasteWhenDisabled() {
        setMenuItemsEnabled(false);

        verifyPopupVisibleWithNoText();
    }

    @Test
    public void settingPopupVisibleWithSelectionShouldEnableAllMenuItemsWhenEnabled() {
        setMenuItemsEnabled(true);

        verifyPopupVisibleWithSelection();
    }

    @Test
    public void settingPopupVisibleWithSelectionShouldEnableAllMenuItemsWhenDisabled() {
        setMenuItemsEnabled(false);

        verifyPopupVisibleWithSelection();
    }

    @Test
    public void settingPopupVisibleWithNoSelectionShouldDisableCutAndCopyMenuItemWhenEnabled() {
        setMenuItemsEnabled(true);

        verifyPopupVisibleWithNoSelection();
    }

    @Test
    public void settingPopupVisibleWithNoSelectionShouldDisableCutAndCopyMenuItemWhenDisabled() {
        setMenuItemsEnabled(false);

        verifyPopupVisibleWithNoSelection();
    }

    private void verifyPopupVisibleWithNoSelection() {
        when(textField.getText()).thenReturn("Text");
        when(textField.getSelectedText()).thenReturn(null);

        popup.setVisible(true);

        assertFalse(cutMenuItem.isEnabled());
        assertFalse(copyMenuItem.isEnabled());
        assertTrue(pasteMenuItem.isEnabled());
        assertTrue(clearMenuItem.isEnabled());
    }

    private void verifyPopupVisibleWithNoText() {
        when(textField.getText()).thenReturn("");
        when(textField.getSelectedText()).thenReturn(null);

        popup.setVisible(true);

        assertFalse(cutMenuItem.isEnabled());
        assertFalse(copyMenuItem.isEnabled());
        assertTrue(pasteMenuItem.isEnabled());
        assertFalse(clearMenuItem.isEnabled());
    }

    private void verifyPopupVisibleWithSelection() {
        when(textField.getText()).thenReturn("Text");
        when(textField.getSelectedText()).thenReturn("Text");

        popup.setVisible(true);

        assertTrue(cutMenuItem.isEnabled());
        assertTrue(copyMenuItem.isEnabled());
        assertTrue(pasteMenuItem.isEnabled());
        assertTrue(clearMenuItem.isEnabled());
    }

    private void setMenuItemsEnabled(final boolean enabled) {
        cutMenuItem.setEnabled(enabled);
        copyMenuItem.setEnabled(enabled);
        // pasteMenuItem.setEnabled(enabled); // Always enabled
        clearMenuItem.setEnabled(enabled);
    }
}
