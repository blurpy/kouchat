
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;

import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.Validate;

/**
 * Shows a popup menu with copy, cut, paste and clear menu items.
 *
 * <p>Can be activated using the right mouse button or the menu button on the keyboard.</p>
 *
 * @author Christian Ihle
 */
public class CopyPastePopup extends JPopupMenu implements PopupMenuListener, ActionListener {

    /** Menu item to copy selected text. */
    private final JMenuItem copyMI;

    /** Menu item to paste text into the text field. */
    private final JMenuItem pasteMI;

    /** Menu item to cut selected text. */
    private final JMenuItem cutMI;

    /** Menu item to clear all the text from the text field. */
    private final JMenuItem clearMI;

    /** The text field this popup is connected to. */
    private final JTextField textfield;

    /**
     * Constructor. Creates the menu.
     *
     * @param textfield The text field to use the popup on.
     * @param swingMessages The swing messages to use for the menu items.
     */
    public CopyPastePopup(final JTextField textfield, final SwingMessages swingMessages) {
        Validate.notNull(textfield, "Text field can not be null");
        Validate.notNull(swingMessages, "Swing messages can not be null");

        this.textfield = textfield;

        copyMI = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMI.setText(swingMessages.getMessage("swing.rightClickPopup.menu.copy"));
        copyMI.setMnemonic(keyCode(swingMessages.getMessage("swing.rightClickPopup.menu.copy.mnemonic")));
        copyMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));

        cutMI = new JMenuItem(new DefaultEditorKit.CutAction());
        cutMI.setText(swingMessages.getMessage("swing.rightClickPopup.menu.cut"));
        cutMI.setMnemonic(keyCode(swingMessages.getMessage("swing.rightClickPopup.menu.cut.mnemonic")));
        cutMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));

        pasteMI = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteMI.setText(swingMessages.getMessage("swing.rightClickPopup.menu.paste"));
        pasteMI.setMnemonic(keyCode(swingMessages.getMessage("swing.rightClickPopup.menu.paste.mnemonic")));
        pasteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));

        clearMI = new JMenuItem(swingMessages.getMessage("swing.rightClickPopup.menu.clear"));
        clearMI.setMnemonic(keyCode(swingMessages.getMessage("swing.rightClickPopup.menu.clear.mnemonic")));

        add(cutMI);
        add(copyMI);
        add(pasteMI);
        addSeparator();
        add(clearMI);

        textfield.setComponentPopupMenu(this);
        clearMI.addActionListener(this);

        addPopupMenuListener(this);
    }

    @Override
    public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
        if (textfield.getSelectedText() == null) {
            copyMI.setEnabled(false);
            cutMI.setEnabled(false);
        }

        else {
            copyMI.setEnabled(true);
            cutMI.setEnabled(true);
        }

        if (textfield.getText().length() > 0) {
            clearMI.setEnabled(true);
        } else {
            clearMI.setEnabled(false);
        }
    }

    @Override
    public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) { }

    @Override
    public void popupMenuCanceled(final PopupMenuEvent e) { }

    /**
     * Clears the text in the text field.
     *
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        textfield.setText("");
    }

    private int keyCode(final String key) {
        return KeyStroke.getKeyStroke(key).getKeyCode();
    }
}
