
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the main menubar for the application.
 *
 * @author Christian Ihle
 */
public class MenuBar extends JMenuBar implements ActionListener {

    private final UITools uiTools = new UITools();

    private final JMenu fileMenu, toolsMenu, helpMenu;
    private final JMenuItem minimizeMI, quitMI;
    private final JMenuItem clearMI, awayMI, topicMI, settingsMI;
    private final JMenuItem aboutMI, commandsMI, faqMI, licenseMI, tipsMI;

    private final ImageLoader imageLoader;
    private final Settings settings;
    private final SwingMessages swingMessages;
    private final ErrorHandler errorHandler;

    private Mediator mediator;
    private TextViewerDialog faqViewer, licenseViewer, tipsViewer;

    /**
     * Constructor. Creates the menubar.
     *
     * @param imageLoader The image loader.
     * @param settings The settings to use.
     * @param swingMessages The swing messages to use for the menu bar.
     * @param errorHandler The error handler to use in the text viewer dialog.
     */
    public MenuBar(final ImageLoader imageLoader, final Settings settings, final SwingMessages swingMessages,
                   final ErrorHandler errorHandler) {
        Validate.notNull(imageLoader, "Image loader can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(swingMessages, "Swing messages can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.imageLoader = imageLoader;
        this.settings = settings;
        this.swingMessages = swingMessages;
        this.errorHandler = errorHandler;

        fileMenu = new JMenu(swingMessages.getMessage("swing.menu.file"));
        fileMenu.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.file.mnemonic")));
        minimizeMI = new JMenuItem(swingMessages.getMessage("swing.menu.file.minimize"));
        minimizeMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.file.minimize.mnemonic")));
        minimizeMI.addActionListener(this);
        quitMI = new JMenuItem(swingMessages.getMessage("swing.menu.file.quit"));
        quitMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.file.quit.mnemonic")));
        quitMI.addActionListener(this);

        fileMenu.add(minimizeMI);
        fileMenu.addSeparator();
        fileMenu.add(quitMI);

        toolsMenu = new JMenu(swingMessages.getMessage("swing.menu.tools"));
        toolsMenu.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.tools.mnemonic")));
        clearMI = new JMenuItem(swingMessages.getMessage("swing.menu.tools.clearChat"));
        clearMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.tools.clearChat.mnemonic")));
        clearMI.addActionListener(this);
        awayMI = new JMenuItem(swingMessages.getMessage("swing.menu.tools.setAway"));
        awayMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.tools.setAway.mnemonic")));
        awayMI.addActionListener(this);
        awayMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        topicMI = new JMenuItem(swingMessages.getMessage("swing.menu.tools.changeTopic"));
        topicMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.tools.changeTopic.mnemonic")));
        topicMI.addActionListener(this);
        topicMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        settingsMI = new JMenuItem(swingMessages.getMessage("swing.menu.tools.settings"));
        settingsMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.tools.settings.mnemonic")));
        settingsMI.addActionListener(this);
        settingsMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));

        toolsMenu.add(clearMI);
        toolsMenu.add(awayMI);
        toolsMenu.add(topicMI);
        toolsMenu.addSeparator();
        toolsMenu.add(settingsMI);

        helpMenu = new JMenu(swingMessages.getMessage("swing.menu.help"));
        helpMenu.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.help.mnemonic")));
        faqMI = new JMenuItem(swingMessages.getMessage("swing.menu.help.faq"));
        faqMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.help.faq.mnemonic")));
        faqMI.addActionListener(this);
        faqMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        licenseMI = new JMenuItem(swingMessages.getMessage("swing.menu.help.license"));
        licenseMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.help.license.mnemonic")));
        licenseMI.addActionListener(this);
        tipsMI = new JMenuItem(swingMessages.getMessage("swing.menu.help.tipsAndTricks"));
        tipsMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.help.tipsAndTricks.mnemonic")));
        tipsMI.addActionListener(this);
        commandsMI = new JMenuItem(swingMessages.getMessage("swing.menu.help.commands"));
        commandsMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.help.commands.mnemonic")));
        commandsMI.addActionListener(this);
        aboutMI = new JMenuItem(swingMessages.getMessage("swing.menu.help.about"));
        aboutMI.setMnemonic(keyCode(swingMessages.getMessage("swing.menu.help.about.mnemonic")));
        aboutMI.addActionListener(this);

        helpMenu.add(faqMI);
        helpMenu.add(tipsMI);
        helpMenu.add(licenseMI);
        helpMenu.addSeparator();
        helpMenu.add(commandsMI);
        helpMenu.addSeparator();
        helpMenu.add(aboutMI);

        add(fileMenu);
        add(toolsMenu);
        add(helpMenu);
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
     * If away, the settings and topic menu items are disabled.
     *
     * @param away If away or not.
     */
    public void setAwayState(final boolean away) {
        settingsMI.setEnabled(!away);
        topicMI.setEnabled(!away);
    }

    /**
     * Checks if any of the menus are visible.
     *
     * @return True if at least one menu is visible.
     */
    public boolean isPopupMenuVisible() {
        return fileMenu.isPopupMenuVisible() || toolsMenu.isPopupMenuVisible() || helpMenu.isPopupMenuVisible();
    }

    /**
     * ActionListener for the menu items.
     *
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        // File/Quit
        if (e.getSource() == quitMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.quit();
                }
            });
        }

        // Tools/Settings
        else if (e.getSource() == settingsMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.showSettings();
                }
            });
        }

        // File/Minimize
        else if (e.getSource() == minimizeMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.minimize();
                }
            });
        }

        // Tools/Set away
        else if (e.getSource() == awayMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.setAway();
                }
            });
        }

        // Tools/Change topic
        else if (e.getSource() == topicMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.setTopic();
                }
            });
        }

        // Tools/Clear chat
        else if (e.getSource() == clearMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.clearChat();
                }
            });
        }

        // Help/FAQ
        else if (e.getSource() == faqMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (faqViewer == null) {
                        faqViewer = createTextViewerDialog(Constants.FILE_FAQ,
                                swingMessages.getMessage("swing.textViewerDialog.faq.title"), true);
                    }

                    faqViewer.setVisible(true);
                }
            });
        }

        // Help/Tips & tricks
        else if (e.getSource() == tipsMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (tipsViewer == null) {
                        tipsViewer = createTextViewerDialog(Constants.FILE_TIPS,
                                swingMessages.getMessage("swing.textViewerDialog.tipsAndTricks.title"), false);
                    }

                    tipsViewer.setVisible(true);
                }
            });
        }

        // Help/License
        else if (e.getSource() == licenseMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (licenseViewer == null) {
                        licenseViewer = createTextViewerDialog(Constants.FILE_LICENSE, Constants.APP_LICENSE_NAME, false);
                    }

                    licenseViewer.setVisible(true);
                }
            });
        }

        // Help/Commands
        else if (e.getSource() == commandsMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mediator.showCommands();
                }
            });
        }

        // Help/About
        else if (e.getSource() == aboutMI) {
            uiTools.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final MessageDialog aboutD = createMessageDialog();

                    aboutD.setTitle(uiTools.createTitle(swingMessages.getMessage("swing.aboutDialog.title")));
                    aboutD.setTopText(swingMessages.getMessage("swing.aboutDialog.version", Constants.APP_NAME, Constants.APP_VERSION));
                    aboutD.setContent(swingMessages.getMessage("swing.aboutDialog.content",
                                                               Constants.APP_COPYRIGHT_YEARS, Constants.AUTHOR_NAME,
                                                               Constants.AUTHOR_MAIL, Constants.APP_WEB,
                                                               Constants.APP_LICENSE_NAME));

                    aboutD.setVisible(true);
                }
            });
        }
    }

    TextViewerDialog createTextViewerDialog(final String textFile, final String title, final boolean links) {
        return new TextViewerDialog(textFile, title, links, imageLoader, new ResourceLoader(), swingMessages,
                                    settings, errorHandler);
    }

    MessageDialog createMessageDialog() {
        return new MessageDialog(imageLoader, swingMessages, settings, errorHandler);
    }

    private int keyCode(final String key) {
        return KeyStroke.getKeyStroke(key).getKeyCode();
    }
}
