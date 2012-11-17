
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

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
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
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.UncaughtExceptionLogger;

/**
 * This is the main chat window.
 *
 * @author Christian Ihle
 */
public class KouChatFrame extends JFrame implements WindowListener, FocusListener {

    private static final Logger LOG = Logger.getLogger(KouChatFrame.class.getName());

    /** Standard serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The panel that contains all the other panels and components. */
    private final MainPanel mainP;

    /** The panel with the buttons and user list. */
    private final SidePanel sideP;

    /** The mediator that connects all the panels. */
    private final Mediator mediator;

    /** The menu bar. */
    private final MenuBar menuBar;

    /** The settings. */
    private final Settings settings;

    /** The application user. */
    private final User me;

    /** The icons to use for the window frame. */
    private final StatusIcons statusIcons;

    /**
     * Constructor.
     *
     * Initializes all components, shows the window, and starts the network.
     */
    public KouChatFrame() {
        System.setProperty(Constants.PROPERTY_CLIENT_UI, "Swing");
        settings = Settings.getSettings();
        me = settings.getMe();
        setLookAndFeel();
        new SwingPopupErrorHandler();
        final ImageLoader imageLoader = new ImageLoader();
        registerUncaughtExceptionListener(imageLoader);
        statusIcons = new StatusIcons(imageLoader);

        final ButtonPanel buttonP = new ButtonPanel();
        sideP = new SidePanel(buttonP, imageLoader);
        mainP = new MainPanel(sideP, imageLoader);
        final SysTray sysTray = new SysTray(imageLoader);
        final SettingsDialog settingsDialog = new SettingsDialog(imageLoader);
        menuBar = new MenuBar(imageLoader);

        final ComponentHandler compHandler = new ComponentHandler();
        compHandler.setGui(this);
        compHandler.setButtonPanel(buttonP);
        compHandler.setSidePanel(sideP);
        compHandler.setMainPanel(mainP);
        compHandler.setSysTray(sysTray);
        compHandler.setSettingsDialog(settingsDialog);
        compHandler.setMenuBar(menuBar);

        mediator = new SwingMediator(compHandler, imageLoader);
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
        setVisible(true);

        getRootPane().addFocusListener(this);
        addWindowListener(this);
        fixTextFieldFocus();
        hideWithEscape();
        mediator.updateTitleAndTray();

        // Try to stop the gui from lagging during startup
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mediator.start();
                mainP.getMsgTF().requestFocusInWindow();
            }
        });
    }

    /**
     * Registers the {@link ExceptionDialog} as an uncaught exception listener.
     *
     * @param imageLoader The image loader.
     */
    private void registerUncaughtExceptionListener(final ImageLoader imageLoader) {
        final UncaughtExceptionLogger uncaughtExceptionLogger =
            (UncaughtExceptionLogger) Thread.getDefaultUncaughtExceptionHandler();
        uncaughtExceptionLogger.registerUncaughtExceptionListener(
                new ExceptionDialog(null, true, imageLoader));
    }

    /**
     * Sets the correct look and feel.
     *
     * <p>The correct look and feel is either the saved look and feel,
     * or the system look and feel if one exists. If none of those are
     * available, then no look and feel is set.</p>
     */
    private void setLookAndFeel() {
        final LookAndFeelInfo lookAndFeel = UITools.getLookAndFeel(settings.getLookAndFeel());

        if (lookAndFeel == null) {
            if (UITools.isSystemLookAndFeelSupported()) {
                UITools.setSystemLookAndFeel();
            }
        }

        else {
            UITools.setLookAndFeel(settings.getLookAndFeel());
        }
    }

    /**
     * Adds a shortcut to hide the window when escape is pressed.
     */
    private void hideWithEscape() {
        final KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        final Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

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

        if (UITools.isRunningOnKDE()) {
            new FocusWindowInKdeThread().start();
        }
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

    /**
     * This is a hack to work around the focus stealing prevention in KDE that does its best to keep
     * Java applications from acquiring focus.
     *
     * <p>Without setAlwaysOnTop then the window will always be opened behind other applications.
     * With it, it will be opened in front, but it will not get focus. Trying to request focus will
     * lead to focus for a few milliseconds, before it's taken away by KDE again.</p>
     *
     * <p>To hack around this, a robot is created that clicks on the window frame to give it focus in
     * a way that KDE finds acceptable.</p>
     *
     * <p>Solution adapted from a post by <code>user942821</code> on
     * <code>http://stackoverflow.com/questions/309023/howto-bring-a-java-window-to-the-front</code>.</p>
     */
    private final class FocusWindowInKdeThread extends Thread {

        private FocusWindowInKdeThread() {
            setName("FocusWindowInKdeThread");
        }

        @Override
        public void run() {
            try {
                // Required to make the window appear in front
                setAlwaysOnTopOnEDT(true);

                // Need to sleep a short period to give the window time to show itself
                Tools.sleep(50);

                // Keep the original mouse location
                final Point oldMouseLocation = MouseInfo.getPointerInfo().getLocation();

                // Move the mouse and simulate a mouse click on the title bar of the window
                final Robot robot = new Robot();
                robot.mouseMove(getX() + 100, getY() + 5);
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);

                // Move the mouse back to the original location
                robot.mouseMove((int) oldMouseLocation.getX(), (int) oldMouseLocation.getY());

            } catch (final AWTException e) {
                // Not very important if this fails. Just log the event.
                LOG.log(Level.WARNING, "Failed to use a robot to focus the window in KDE", e);

            } finally {
                // Need to reset this, or else it's impossible to get other windows in the foreground
                setAlwaysOnTopOnEDT(false);
            }
        }

        private void setAlwaysOnTopOnEDT(final boolean alwaysOnTop) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setAlwaysOnTop(alwaysOnTop);
                }
            });
        }
    }
}
