
/***************************************************************************
 *   Copyright 2006-2018 by Christian Ihle                                 *
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

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.Validate;

/**
 * This listener adds support for opening a url in a browser
 * by clicking on a link. The mouse cursor will also change when
 * hovering over a link.
 *
 * @author Christian Ihle
 */
public class URLMouseListener implements MouseListener, MouseMotionListener {

    private final UITools uiTools = new UITools();

    private final Cursor handCursor;
    private final JTextPane textPane;
    private final StyledDocument doc;
    private final Settings settings;
    private final ErrorHandler errorHandler;
    private final SwingMessages swingMessages;

    /**
     * Constructor.
     *
     * @param textPane The text pane this listener is registered to.
     * @param settings The settings to use.
     * @param errorHandler The error handler to use for opening the browser.
     * @param swingMessages The swing messages to use.
     */
    public URLMouseListener(final JTextPane textPane, final Settings settings, final ErrorHandler errorHandler,
                            final SwingMessages swingMessages) {
        Validate.notNull(textPane, "TextPane can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");
        Validate.notNull(swingMessages, "Swing messages can not be null");

        this.textPane = textPane;
        this.settings = settings;
        this.errorHandler = errorHandler;
        this.swingMessages = swingMessages;

        doc = textPane.getStyledDocument();
        handCursor = new Cursor(Cursor.HAND_CURSOR);
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(final MouseEvent e) {

    }

    /**
     * Updates the mouse cursor when hovering over a link.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(final MouseEvent e) {
        final int mousePos = textPane.viewToModel(e.getPoint());

        final AttributeSet attr = doc.getCharacterElement(mousePos).getAttributes();

        if (StyleConstants.isUnderline(attr)) {
            if (textPane.getCursor() != handCursor) {
                textPane.setCursor(handCursor);
            }
        }

        else {
            if (textPane.getCursor() == handCursor) {
                textPane.setCursor(null);
            }
        }
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(final MouseEvent e) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(final MouseEvent e) {

    }

    /**
     * Opens the clicked link in a browser.
     *
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            final int clickPos = textPane.viewToModel(e.getPoint());

            final AttributeSet attr = doc.getCharacterElement(clickPos).getAttributes();

            if (StyleConstants.isUnderline(attr)) {
                final Object obj = attr.getAttribute(URLDocumentFilter.URL_ATTRIBUTE);

                if (obj != null) {
                    final String url = obj.toString();

                    uiTools.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            uiTools.browse(url, settings, errorHandler, swingMessages);
                        }
                    });
                }
            }
        }
    }
}
