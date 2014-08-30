
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.Validate;

/**
 * Opens a text file in a 80x24 character dialog window.
 *
 * @author Christian Ihle
 */
public class TextViewerDialog extends JDialog {

    private static final Logger LOG = Logger.getLogger(TextViewerDialog.class.getName());

    private final UITools uiTools = new UITools();

    private final ResourceLoader resourceLoader;
    private final Messages messages;
    private final ErrorHandler errorHandler;

    private final JTextPane viewerTP;
    private final JScrollPane viewerScroll;
    private final MutableAttributeSet viewerAttr;
    private final StyledDocument viewerDoc;
    private final String textFile;
    private boolean fileOpened;

    /**
     * Constructor.
     *
     * Creates the dialog window, and opens the file.
     *
     * @param textFile The text file to open and view.
     * @param title The title to use for the dialog window.
     * @param links True to enabled support for opening urls by clicking on them.
     * @param imageLoader The image loader.
     * @param resourceLoader The resource loader to use to load the text file.
     * @param messages The messages to use in this dialog.
     * @param settings The settings to use.
     * @param errorHandler The error handler to use to show messages if text file could not be opened.
     */
    public TextViewerDialog(final String textFile, final String title, final boolean links,
                            final ImageLoader imageLoader, final ResourceLoader resourceLoader,
                            final Messages messages, final Settings settings, final ErrorHandler errorHandler) {
        Validate.notEmpty(textFile, "Text file can not be empty");
        Validate.notEmpty(title, "Title can not be empty");
        Validate.notNull(imageLoader, "Image loader can not be null");
        Validate.notNull(resourceLoader, "Resource loader can not be null");
        Validate.notNull(messages, "Messages can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.textFile = textFile;
        this.resourceLoader = resourceLoader;
        this.messages = messages;
        this.errorHandler = errorHandler;

        viewerTP = new JTextPane();
        viewerTP.setFont(new Font("Monospaced", Font.PLAIN, viewerTP.getFont().getSize()));
        viewerTP.setEditable(false);
        viewerDoc = viewerTP.getStyledDocument();

        // Enables the url support
        if (links) {
            final URLMouseListener urlML = new URLMouseListener(viewerTP, settings, errorHandler, messages);
            viewerTP.addMouseListener(urlML);
            viewerTP.addMouseMotionListener(urlML);
            final AbstractDocument doc = (AbstractDocument) viewerDoc;
            doc.setDocumentFilter(new URLDocumentFilter(true));
        }

        new CopyPopup(viewerTP, messages);
        viewerAttr = new SimpleAttributeSet();
        viewerScroll = new JScrollPane(viewerTP);
        viewerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 4, 4, 4));
        panel.add(viewerScroll, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);

        // To get 80 columns and 24 rows
        final FontMetrics fm = viewerTP.getFontMetrics(viewerTP.getFont());
        final int width = fm.charWidth('_') * 80;
        final int height = fm.getHeight() * 24;
        viewerTP.setPreferredSize(new Dimension(width, height));

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle(uiTools.createTitle(title));
        setIconImage(new StatusIcons(imageLoader).getNormalIcon());
        setResizable(false);
        readFile();
        pack();
    }

    /**
     * Shows the window if the text file was opened, and scrolls to the
     * beginning of the text.
     *
     * {@inheritDoc}
     */
    @Override
    public void setVisible(final boolean visible) {
        if (fileOpened) {
            if (visible) {
                try {
                    final Rectangle r = viewerTP.modelToView(0);
                    viewerScroll.getViewport().setViewPosition(new Point(r.x, r.y));
                }

                catch (final BadLocationException e) {
                    LOG.log(Level.SEVERE, e.toString());
                }
            }

            super.setVisible(visible);
        }

        else {
            errorHandler.showError(messages.getMessage("swing.textViewerDialog.errorPopup.openFile", textFile));
        }
    }

    /**
     * Reads the text file, and adds the contents to the text area.
     */
    private void readFile() {
        final URL fileURL = resourceLoader.getResource("/" + textFile);

        if (fileURL != null) {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new InputStreamReader(fileURL.openStream()));

                while (reader.ready()) {
                    viewerDoc.insertString(viewerDoc.getLength(), reader.readLine() + "\n", viewerAttr);
                }

                viewerTP.setCaretPosition(0);
                fileOpened = true;
            }

            catch (final IOException e) {
                LOG.log(Level.SEVERE, e.toString());
            }

            catch (final BadLocationException e) {
                LOG.log(Level.SEVERE, e.toString());
            }

            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }

                    catch (final IOException e) {
                        LOG.log(Level.WARNING, "Problems closing: " + textFile);
                    }
                }
            }
        }

        else {
            LOG.log(Level.SEVERE, "Text file not found: " + textFile);
        }
    }
}
