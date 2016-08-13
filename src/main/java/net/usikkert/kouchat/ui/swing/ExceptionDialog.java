
/***************************************************************************
 *   Copyright 2006-2016 by Christian Ihle                                 *
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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.UncaughtExceptionListener;
import net.usikkert.kouchat.util.Validate;

/**
 * This is a dialog window for showing stack traces from unhandled exceptions.
 *
 * @author Christian Ihle
 */
public class ExceptionDialog extends JDialog implements UncaughtExceptionListener {

    private static final String TIMESTAMP_FORMAT = "dd.MMM.yyyy HH:mm:ss";

    private final UITools uiTools = new UITools();

    /** The textpane to put stack traces. */
    private final JTextPane exceptionTP;

    private final SwingMessages swingMessages;

    /**
     * Creates the exception dialog, but does not show it.
     *
     * @param imageLoader The image loader.
     * @param swingMessages The swing messages to use in the copy popup.
     */
    public ExceptionDialog(final ImageLoader imageLoader, final SwingMessages swingMessages) {
        super((Frame) null, true);

        Validate.notNull(imageLoader, "Image loader can not be null");
        Validate.notNull(swingMessages, "Swing messages can not be null");

        this.swingMessages = swingMessages;

        final JLabel titleL = new JLabel();
        titleL.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        titleL.setText(swingMessages.getMessage("swing.exceptionDialog.topText"));
        titleL.setFont(new Font("Dialog", Font.PLAIN, 20));

        final JLabel detailL = new JLabel();
        // Using html to keep the text from appearing in a single line
        detailL.setText(swingMessages.getMessage("swing.exceptionDialog.message", Constants.APP_NAME, Constants.APP_WEB));

        exceptionTP = new JTextPaneWithoutWrap();
        exceptionTP.setEditable(false);
        final JScrollPane exceptionScroll = new JScrollPane(exceptionTP);
        new CopyPopup(exceptionTP, swingMessages);

        final JButton closeB = new JButton();
        closeB.setText(swingMessages.getMessage("swing.button.close"));
        closeB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        });

        final JPanel titleP = new JPanel();
        titleP.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));
        titleP.add(titleL);
        titleP.setBackground(Color.WHITE);
        titleP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        final JPanel buttonP = new JPanel();
        buttonP.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonP.add(closeB);

        final JPanel infoP = new JPanel();
        infoP.setLayout(new BorderLayout(5, 10));
        infoP.add(detailL, BorderLayout.PAGE_START);
        infoP.add(exceptionScroll, BorderLayout.CENTER);
        infoP.setBorder(BorderFactory.createEmptyBorder(8, 4, 2, 4));

        getContentPane().add(titleP, BorderLayout.PAGE_START);
        getContentPane().add(buttonP, BorderLayout.PAGE_END);
        getContentPane().add(infoP, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(uiTools.createTitle(swingMessages.getMessage("swing.exceptionDialog.title")));
        setIconImage(new StatusIcons(imageLoader).getNormalIcon());
        setSize(630, 450);
    }

    /**
     * Adds the stack trace in the exception to the textpane,
     * and shows the dialog.
     *
     * {@inheritDoc}
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        uiTools.invokeLater(new Runnable() {
            @Override
            public void run() {
                final StringWriter stringWriter = new StringWriter();

                stringWriter.append(swingMessages.getMessage("swing.exceptionDialog.details",
                                                             timestamp(new Date()), thread.getName(),
                                                             thread.getId(), thread.getPriority()));
                stringWriter.append("\n");

                final PrintWriter printWriter = new PrintWriter(stringWriter);
                throwable.printStackTrace(printWriter);
                printWriter.close();

                if (exceptionTP.getText().length() > 0) {
                    stringWriter.append("\n");
                    stringWriter.append(exceptionTP.getText());
                }

                exceptionTP.setText(stringWriter.toString());
                exceptionTP.setCaretPosition(0);
                showDialog();
            }
        });
    }

    String timestamp(final Date date) {
        return Tools.dateToString(date, TIMESTAMP_FORMAT);
    }

    void showDialog() {
        setLocationRelativeTo(getParent());
        setVisible(true);
    }
}
