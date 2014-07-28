
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
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.message.PropertyFileMessages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.ui.swing.settings.SettingsDialog;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.UncaughtExceptionLogger;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the main chat window.
 *
 * @author Christian Ihle
 */
public class KouChatFrame extends JFrame implements WindowListener, FocusListener {

    private static final Logger LOG = Logger.getLogger(KouChatFrame.class.getName());

    private final UITools uiTools = new UITools();

    /** The panel that contains all the other panels and components. */
    private final MainPanel mainP;

    /** The panel with the buttons and user list. */
    private final SidePanel sideP;

    /** The mediator that connects all the panels. */
    private final Mediator mediator;

    /** The menu bar. */
    private final MenuBar menuBar;

    private final SysTray sysTray;

    /** The settings. */
    private final Settings settings;

    /** The application user. */
    private final User me;

    /** The icons to use for the window frame. */
    private final StatusIcons statusIcons;

    /**
     * Constructor.
     *
     * <p>Initializes all components.</p>
     *
     * @param settings The settings to use for this application.
     * @param uncaughtExceptionLogger The uncaught exception logger to use for registering uncaught exception listener.
     * @param errorHandler The error handler to use for registering the swing error listener.
     */
    public KouChatFrame(final Settings settings,
                        final UncaughtExceptionLogger uncaughtExceptionLogger,
                        final ErrorHandler errorHandler) {
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(uncaughtExceptionLogger, "Uncaught exception logger can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.settings = settings;

        settings.setClient("Swing");
        me = settings.getMe();

        final Messages messages = new PropertyFileMessages("messages.swing");

        setLookAndFeel();
        errorHandler.addErrorListener(new SwingPopupErrorHandler());
        final ImageLoader imageLoader = new ImageLoader(errorHandler, messages, new ResourceValidator(), new ResourceLoader());
        uncaughtExceptionLogger.registerUncaughtExceptionListener(new ExceptionDialog(null, true, imageLoader));
        statusIcons = new StatusIcons(imageLoader);

        final ButtonPanel buttonP = new ButtonPanel(messages);
        sideP = new SidePanel(buttonP, imageLoader, settings);
        mainP = new MainPanel(sideP, imageLoader, settings);
        sysTray = new SysTray(imageLoader, settings);
        final SettingsDialog settingsDialog = new SettingsDialog(imageLoader, settings, errorHandler, messages);
        menuBar = new MenuBar(imageLoader, settings, messages, errorHandler);

        final ComponentHandler compHandler = new ComponentHandler();
        compHandler.setGui(this);
        compHandler.setButtonPanel(buttonP);
        compHandler.setSidePanel(sideP);
        compHandler.setMainPanel(mainP);
        compHandler.setSysTray(sysTray);
        compHandler.setSettingsDialog(settingsDialog);
        compHandler.setMenuBar(menuBar);

        mediator = new SwingMediator(compHandler, imageLoader, settings, messages);

        buttonP.setMediator(mediator);
        sideP.setMediator(mediator);
        mainP.setMediator(mediator);
        sysTray.setMediator(mediator);
        settingsDialog.setMediator(mediator);
        menuBar.setMediator(mediator);

        // Show tooltips for 10 seconds. Default is very short.
        ToolTipManager.sharedInstance().setDismissDelay(10000);

        setJMenuBar(menuBar);
        getContentPane().add(mainP, BorderLayout.CENTER);
        setTitle(Constants.APP_NAME);
        setIconImage(statusIcons.getNormalIcon());
        setSize(650, 480);
        setMinimumSize(new Dimension(450, 300));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        getRootPane().addFocusListener(this);
        addWindowListener(this);
        fixTextFieldFocus();
        hideWithEscape();
        mediator.updateTitleAndTray();
    }

    /**
     * Activates the system tray, shows the window, and starts the network.
     *
     * <p>Supports starting with the window minimized. If the system tray is activated,
     * then the window will be hidden instead.</p>
     *
     * @param startMinimized If the window should start minimized/hidden.
     */
    public void start(final boolean startMinimized) {
        sysTray.activate();

        if (startMinimized) {
            startMinimized();
        } else {
            setVisible(true);
        }

        mediator.start();
    }

    /**
     * If the system tray is activated, then there is nothing to do. The window will be hidden by default,
     * and a click on the system tray icon will show the window.
     *
     * <p>If the system tray is unavailable, then the window must be set visible after the
     * minimized state has been set.</p>
     */
    private void startMinimized() {
        if (sysTray.isSystemTraySupport()) {
            LOG.fine("Starting minimized to the system tray");
        }

        else {
            LOG.fine("Starting minimized to the task bar");

            uiTools.minimize(this);
            setVisible(true);
        }
    }

    /**
     * Sets the correct look and feel.
     *
     * <p>The correct look and feel is either the saved look and feel,
     * or the system look and feel if one exists. If none of those are
     * available, then no look and feel is set.</p>
     */
    private void setLookAndFeel() {
        final LookAndFeelInfo lookAndFeel = uiTools.getLookAndFeel(settings.getLookAndFeel());

        if (lookAndFeel == null) {
            if (uiTools.isSystemLookAndFeelSupported()) {
                uiTools.setSystemLookAndFeel();
            }
        }

        else {
            uiTools.setLookAndFeel(settings.getLookAndFeel());
        }
    }

    /**
     * Adds a shortcut to hide the window when escape is pressed.
     */
    private void hideWithEscape() {
        final KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        final Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                mediator.minimize();
            }
        };

        mainP.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeKeyStroke, "ESCAPE");
        mainP.getActionMap().put("ESCAPE", escapeAction);
    }

    /**
     * If this window is focused, the text field will get the keyboard events
     * if the chat area or the user list was focused when typing was started.
     */
    private void fixTextFieldFocus() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(final KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_TYPED && isFocused() && (e.getSource() == mainP.getChatTP() || e.getSource() == sideP.getUserList())) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(mainP.getMsgTF(), e);
                    mainP.getMsgTF().requestFocusInWindow();

                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void focusGained(final FocusEvent e) {

    }

    /**
     * Make sure the menubar gets focus when navigating with the keyboard.
     *
     * {@inheritDoc}
     */
    @Override
    public void focusLost(final FocusEvent e) {
        if (menuBar.isPopupMenuVisible()) {
            getRootPane().requestFocusInWindow();
        }
    }

    /**
     * Shut down the right way.
     *
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(final WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mediator.quit();
            }
        });
    }

    /**
     * Fix focus and repaint issues when the window gets focused.
     *
     * {@inheritDoc}
     */
    @Override
    public void windowActivated(final WindowEvent e) {
        mainP.getChatSP().repaint();
        sideP.repaintPanel();
        mainP.getMsgTF().requestFocusInWindow();

        if (me.isNewMsg()) {
            me.setNewMsg(false);
            mediator.updateTitleAndTray();
        }
    }

    /**
     * Shows the window, and brings it to front.
     */
    public void showWindow() {
        setVisible(true);
        toFront();
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void windowClosed(final WindowEvent e) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void windowDeactivated(final WindowEvent e) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void windowDeiconified(final WindowEvent e) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void windowIconified(final WindowEvent e) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void windowOpened(final WindowEvent e) {

    }

    /**
     * Changes the window icon depending on away status and if a new message has arrived.
     */
    public void updateWindowIcon() {
        if (me.isNewMsg()) {
            if (me.isAway()) {
                setWindowIcon(statusIcons.getAwayActivityIcon());
            } else {
                setWindowIcon(statusIcons.getNormalActivityIcon());
            }
        }

        else {
            if (me.isAway()) {
                setWindowIcon(statusIcons.getAwayIcon());
            } else {
                setWindowIcon(statusIcons.getNormalIcon());
            }
        }
    }

    /**
     * Sets the window icon if it's different from the icon already in use.
     *
     * @param icon The window icon to use.
     */
    public void setWindowIcon(final Image icon) {
        if (getIconImage() != icon) {
            setIconImage(icon);
        }
    }
}
