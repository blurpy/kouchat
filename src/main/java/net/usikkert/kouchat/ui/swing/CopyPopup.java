
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

/**
 * Shows a popup menu with copy and select all menu items.
 *
 * @author Christian Ihle
 */
public class CopyPopup extends JPopupMenu implements MouseListener, ActionListener
{
    /** Standard serial version UID. */
    private static final long serialVersionUID = 1L;

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
     */
    public CopyPopup(final JTextPane textpane)
    {
        this.textpane = textpane;

        copyMI = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMI.setText("Copy");
        copyMI.setMnemonic('C');
        copyMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));

        selectAllMI = new JMenuItem("Select All");
        selectAllMI.setMnemonic('A');
        selectAllMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));

        add(copyMI);
        add(selectAllMI);

        textpane.addMouseListener(this);
        selectAllMI.addActionListener(this);
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(final MouseEvent e)
    {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(final MouseEvent e)
    {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(final MouseEvent e)
    {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(final MouseEvent e)
    {

    }

    /**
     * Shows the popup menu if right mouse button was used.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
        if (isPopupTrigger(e))
        {
            if (textpane.getSelectedText() == null)
                copyMI.setEnabled(false);
            else
                copyMI.setEnabled(true);

            if (textpane.getText().length() == 0)
                selectAllMI.setEnabled(false);
            else
                selectAllMI.setEnabled(true);

            show(textpane, e.getX(), e.getY());
        }
    }

    /**
     * Selects all the text.
     *
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        textpane.requestFocusInWindow();
        textpane.selectAll();
    }
}
