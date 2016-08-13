
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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the panel located to the right in the application.
 *
 * These buttons are shown:
 *
 * <ul>
 *   <li>Clear</li>
 *   <li>Away</li>
 *   <li>Topic</li>
 *   <li>Minimize</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class ButtonPanel extends JPanel implements ActionListener {

    private final UITools uiTools = new UITools();

    /** The minimize button. Minimizes the application to the system tray. */
    private final JButton minimizeB;

    /** The clear button. Clears the text in the main chat. */
    private final JButton clearB;

    /** The away button. Changes the away state of the user. */
    private final JButton awayB;

    /** The topic button. Changes the topic in the main chat. */
    private final JButton topicB;

    /** The mediator. */
    private Mediator mediator;

    /**
     * Constructor. Initializes the buttons.
     *
     * @param swingMessages The swing messages to use for the buttons.
     */
    public ButtonPanel(final SwingMessages swingMessages) {
        Validate.notNull(swingMessages, "Swing messages can not be null");

        setLayout(new GridLayout(4, 1));

        clearB = new JButton(swingMessages.getMessage("swing.buttonBar.clear"));
        clearB.addActionListener(this);
        clearB.setToolTipText(swingMessages.getMessage("swing.buttonBar.clear.tooltip"));
        add(clearB);

        awayB = new JButton(swingMessages.getMessage("swing.buttonBar.away"));
        awayB.addActionListener(this);
        awayB.setToolTipText(swingMessages.getMessage("swing.buttonBar.away.tooltip"));
        add(awayB);

        topicB = new JButton(swingMessages.getMessage("swing.buttonBar.topic"));
        topicB.addActionListener(this);
        topicB.setToolTipText(swingMessages.getMessage("swing.buttonBar.topic.tooltip"));
        add(topicB);

        minimizeB = new JButton(swingMessages.getMessage("swing.buttonBar.minimize"));
        minimizeB.addActionListener(this);
        minimizeB.setToolTipText(swingMessages.getMessage("swing.buttonBar.minimize.tooltip"));
        add(minimizeB);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 1));
    }

    /**
     * Sets the mediator to use.
     *
     * @param mediator The mediator to set.
     */
    public void setMediator(final Mediator mediator) {
        Validate.notNull(mediator, "Mediator can not be null");
        this.mediator = mediator;
    }

    /**
     * Enables or disabled the away button.
     *
     * @param away If away, the button is disabled. Else enabled.
     */
    public void setAwayState(final boolean away) {
        topicB.setEnabled(!away);
    }

    /**
     * The listener for button clicks.
     *
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == minimizeB) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.minimize();
                }
            });
        }

        else if (e.getSource() == clearB) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.clearChat();
                }
            });
        }

        else if (e.getSource() == awayB) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.setAway();
                }
            });
        }

        else if (e.getSource() == topicB) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.setTopic();
                }
            });
        }
    }
}
