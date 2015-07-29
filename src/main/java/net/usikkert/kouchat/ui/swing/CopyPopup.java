
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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;

import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.Validate;

/**
 * Shows a popup menu with copy and select all menu items.
 *
 * <p>Can be activated using the right mouse button or the menu button on the keyboard.</p>
 *
 * @author Christian Ihle
 */
public class CopyPopup extends JPopupMenu implements PopupMenuListener, ActionListener {

    /** Menu item to copy selected text in the text pane. */
    private final JMenuItem copyMI;

    /** Menu item to select all the text in the text pane. */
    private final JMenuItem selectAllMI;

    /** The text pane. */
    private final JTextPane textpane;

    /**
     * Constructor. Creates the menu.
     *
     * @param textpane The text pane to use the popup on.
     * @param swingMessages The swing messages to use for the menu items.
     */
    public CopyPopup(final JTextPane textpane, final SwingMessages swingMessages) {
        Validate.notNull(textpane, "Text pane can not be null");
        Validate.notNull(swingMessages, "Swing messages can not be null");

        this.textpane = textpane;

        final int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        copyMI = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMI.setText(swingMessages.getMessage("swing.rightClickPopup.menu.copy"));
        copyMI.setMnemonic(keyCode(swingMessages.getMessage("swing.rightClickPopup.menu.copy.mnemonic")));
        copyMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, menuShortcutKeyMask));

        selectAllMI = new JMenuItem(swingMessages.getMessage("swing.rightClickPopup.menu.selectAll"));
        selectAllMI.setMnemonic(keyCode(swingMessages.getMessage("swing.rightClickPopup.menu.selectAll.mnemonic")));
        selectAllMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, menuShortcutKeyMask));

        add(copyMI);
        add(selectAllMI);

        textpane.setComponentPopupMenu(this);
        selectAllMI.addActionListener(this);

        addPopupMenuListener(this);
    }

    @Override
    public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
        if (textpane.getSelectedText() == null) {
            copyMI.setEnabled(false);
        } else {
            copyMI.setEnabled(true);
        }

        if (textpane.getText().length() == 0) {
            selectAllMI.setEnabled(false);
        } else {
            selectAllMI.setEnabled(true);
        }
    }

    @Override
    public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) { }

    @Override
    public void popupMenuCanceled(final PopupMenuEvent e) { }

    /**
     * Selects all the text.
     *
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        textpane.requestFocusInWindow();
        textpane.selectAll();
    }

    private int keyCode(final String key) {
        return KeyStroke.getKeyStroke(key).getKeyCode();
    }
}
