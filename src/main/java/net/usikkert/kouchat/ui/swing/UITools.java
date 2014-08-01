
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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.SystemTray;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * This is a collection of practical and reusable methods
 * for ui use.
 *
 * @author Christian Ihle
 */
public class UITools {

    private static final Logger LOG = Logger.getLogger(UITools.class.getName());
    private static final ErrorHandler ERRORHANDLER = ErrorHandler.getErrorHandler();

    /** Name of the environment variable used to detect if running on KDE. */
    private static final String KDE_FULL_SESSION = "KDE_FULL_SESSION";

    /**
     * Runs a command using {@link Runtime#exec(String)}.
     *
     * @param command The command to run.
     * @return A {@link Process} to manage the result of the command.
     * @throws IOException If the command fails.
     */
    public Process runCommand(final String command) throws IOException {
        return Runtime.getRuntime().exec(command);
    }

    /**
     * Opens a url in a browser. The first choice is taken from the settings,
     * but if no browser i configured there, the systems default browser
     * is tried.
     *
     * @param url The url to open in the browser.
     * @param settings The settings to use.
     */
    public void browse(final String url, final Settings settings) {
        Validate.notEmpty(url, "Url can not be empty");
        Validate.notNull(settings, "Settings can not be null");

        final String browser = settings.getBrowser();

        // The default is to use the browser in the settings.
        if (browser != null && browser.trim().length() > 0) {
            try {
                runCommand(browser + " " + url);
            }

            catch (final IOException e) {
                LOG.log(Level.WARNING, e.toString());
                ERRORHANDLER.showError("Could not open the browser '" +
                        browser + "'. Please check the settings.");
            }
        }

        // But if no browser is set there, try opening the system default browser
        else if (isDesktopActionSupported(Action.BROWSE)) {
            try {
                browse(url);
            }

            catch (final IOException e) {
                LOG.log(Level.WARNING, e.toString());
                ERRORHANDLER.showError("Could not open '" + url + "' with the default browser." +
                        " Try setting a browser in the settings.");
            }

            catch (final URISyntaxException e) {
                LOG.log(Level.WARNING, e.toString());
            }
        }

        else {
            ERRORHANDLER.showError("No browser detected." +
                    " A browser can be chosen in the settings.");
        }
    }

    /**
     * Opens a url in a browser.
     *
     * <p>Only for special cases. Don't use this directly, as it doesn't respect the browser set in the settings.
     * Use {@link #browse(String, Settings)} instead.</p>
     *
     * @param url The url to open in the browser.
     * @throws URISyntaxException If the url is invalid.
     * @throws IOException If unable to open the browser.
     */
    public void browse(final String url) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(url));
    }

    /**
     * Opens a file in the registered application for the file type.
     *
     * <p>If this fails, {@link #browse(String, Settings)} is used as a fallback.</p>
     *
     * @param file A file or directory to open.
     * @param settings The settings to use.
     */
    public void open(final File file, final Settings settings) {
        Validate.notNull(file, "File can not be null");
        Validate.notNull(settings, "Settings can not be null");

        boolean desktopOpenSuccess = false;

        if (isDesktopActionSupported(Action.OPEN)) {
            try {
                Desktop.getDesktop().open(file);
                desktopOpenSuccess = true;
            }

            catch (final IOException e) {
                LOG.log(Level.WARNING, e.toString());
            }
        }

        if (!desktopOpenSuccess) {
            browse(file.getAbsolutePath(), settings);
        }
    }

    /**
     * Checks if the desktop api is supported in this system,
     * and if that is the case, then a check to see whether the
     * chosen desktop action is supported on this system is performed.
     *
     * <p>The reason to do the checks so thorough is because an
     * unchecked exception is thrown when {@link Desktop#getDesktop()}
     * is called on an unsupported system.</p>
     *
     * @param action The action to check.
     * @return If the system supports this action or not.
     */
    public boolean isDesktopActionSupported(final Action action) {
        if (Desktop.isDesktopSupported()) {
            if (Desktop.getDesktop().isSupported(action)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Changes to the system Look And Feel.
     * Ignores any exceptions, as this is not critical.
     */
    public void setSystemLookAndFeel() {
        if (isSystemLookAndFeelSupported()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

            catch (final ClassNotFoundException e) {
                LOG.log(Level.WARNING, e.toString());
            }

            catch (final InstantiationException e) {
                LOG.log(Level.WARNING, e.toString());
            }

            catch (final IllegalAccessException e) {
                LOG.log(Level.WARNING, e.toString());
            }

            catch (final UnsupportedLookAndFeelException e) {
                LOG.log(Level.WARNING, e.toString());
            }
        }
    }

    /**
     * Changes to the chosen look and feel. Ignores any exceptions.
     *
     * @param lnfName Name of the look and feel to change to.
     */
    public void setLookAndFeel(final String lnfName) {
        try {
            final LookAndFeelInfo lookAndFeel = getLookAndFeel(lnfName);

            if (lookAndFeel != null) {
                UIManager.setLookAndFeel(lookAndFeel.getClassName());
            }
        }

        catch (final ClassNotFoundException e) {
            LOG.log(Level.WARNING, e.toString());
        }

        catch (final InstantiationException e) {
            LOG.log(Level.WARNING, e.toString());
        }

        catch (final IllegalAccessException e) {
            LOG.log(Level.WARNING, e.toString());
        }

        catch (final UnsupportedLookAndFeelException e) {
            LOG.log(Level.WARNING, e.toString());
        }
    }

    /**
     * Checks if the system look and feel differs
     * from the cross platform look and feel.
     *
     * @return True if the system look and feel is different
     * from the cross platform look and feel.
     */
    public boolean isSystemLookAndFeelSupported() {
        return !UIManager.getSystemLookAndFeelClassName().equals(UIManager.getCrossPlatformLookAndFeelClassName());
    }

    /**
     * Gets an array of the available look and feels, in a wrapper.
     *
     * @return All the available look and feels.
     */
    public LookAndFeelWrapper[] getLookAndFeels() {
        final LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
        final LookAndFeelWrapper[] lookAndFeelWrappers = new LookAndFeelWrapper[lookAndFeels.length];

        for (int i = 0; i < lookAndFeels.length; i++) {
            lookAndFeelWrappers[i] = new LookAndFeelWrapper(lookAndFeels[i]);
        }

        return lookAndFeelWrappers;
    }

    /**
     * Gets the {@link LookAndFeelInfo} found with the specified name,
     * or null if none was found.
     *
     * @param lnfName The name of the look and feel to look for.
     * @return The LookAndFeelInfo for that name.
     */
    public LookAndFeelInfo getLookAndFeel(final String lnfName) {
        final LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();

        for (final LookAndFeelInfo lookAndFeelInfo : lookAndFeels) {
            if (lookAndFeelInfo.getName().equals(lnfName)) {
                return lookAndFeelInfo;
            }
        }

        return null;
    }

    /**
     * Gets {@link LookAndFeelInfo} for the current look and feel.
     *
     * <p>Swing throws NullPointerExceptions everywhere if look and feel is not set,
     * so this is validated before returning.</p>
     *
     * @return The current look and feel.
     * @throws IllegalStateException If no look and feel is set, or look and feel info can't
     *                               be obtained for the current look and feel.
     */
    public LookAndFeelInfo getCurrentLookAndFeel() {
        final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();

        if (lookAndFeel == null) {
            throw new IllegalStateException("No look and feel set. That's unexpected.");
        }

        final LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();

        for (final LookAndFeelInfo lookAndFeelInfo : lookAndFeels) {
            if (lookAndFeelInfo.getClassName().equals(lookAndFeel.getClass().getName())) {
                return lookAndFeelInfo;
            }
        }

        throw new IllegalStateException(
                String.format("No look and feel info found for '%s'. That's unexpected.", lookAndFeel.getName()));
    }

    /**
     * Gets the width of the text in pixels with the specified font.
     *
     * @param text The text to check the width of.
     * @param graphics Needed to be able to check the width.
     * @param font The font the text uses.
     * @return The text width, in pixels.
     */
    public double getTextWidth(final String text, final Graphics graphics, final Font font) {
        final FontMetrics fm = graphics.getFontMetrics(font);
        return fm.getStringBounds(text, graphics).getWidth();
    }

    /**
     * Shows an information message dialog with the specified message and title.
     *
     * @param message The message to show.
     * @param title The title of the dialog box.
     */
    public void showInfoMessage(final String message, final String title) {
        showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a warning message dialog with the specified message and title.
     *
     * @param message The message to show.
     * @param title The title of the dialog box.
     */
    public void showWarningMessage(final String message, final String title) {
        showMessageDialog(message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows an error message dialog with the specified message and title.
     *
     * @param message The message to show.
     * @param title The title of the dialog box.
     */
    public void showErrorMessage(final String message, final String title) {
        showMessageDialog(message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a message dialog with the specified message, title and type.
     *
     * @param message The message to show.
     * @param title The title of the dialog box.
     * @param messageType The type of message. See {@link JOptionPane} for types.
     */
    public void showMessageDialog(final String message, final String title, final int messageType) {
        JOptionPane.showMessageDialog(null, message, createTitle(title), messageType);
    }

    /**
     * Shows an input dialog with the specified message, title and initial value
     * in the input field.
     *
     * @param message The message to show.
     * @param title The title of the dialog box.
     * @param initialValue The initial value, or <code>null</code> if the field should be empty.
     * @return The input from the user, or <code>null</code> if cancel was selected.
     */
    public String showInputDialog(final String message, final String title, final String initialValue) {
        return (String) JOptionPane.showInputDialog(null, message, createTitle(title),
                JOptionPane.QUESTION_MESSAGE, null, null, initialValue);
    }

    /**
     * Shows an option dialog with the specified message and title,
     * with the buttons set to "Yes" and "Cancel".
     *
     * @param message The message to show.
     * @param title The title of the dialog box.
     * @return Which button the user pressed. See {@link JOptionPane} for options.
     */
    public int showOptionDialog(final String message, final String title) {
        final Object[] options = {"Yes", "Cancel"};
        final int[] choice = new int[1];

        invokeAndWait(new Runnable() {
            @Override
            public void run() {
                choice[0] = JOptionPane.showOptionDialog(null, message, createTitle(title),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
            }
        });

        return choice[0];
    }

    /**
     * Creates a new title by appending a dash and the application name
     * after the original title.
     *
     * @param title The original title.
     * @return The new title.
     */
    public String createTitle(final String title) {
        return title + " - " + Constants.APP_NAME;
    }

    /**
     * Creates a new file chooser with the specified title.
     *
     * @param title The title of the file chooser.
     * @return A new file chooser.
     */
    public JFileChooser createFileChooser(final String title) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(createTitle(title));
        return fileChooser;
    }

    /**
     * Shows the color chooser with the chosen title, with the initial color.
     *
     * @param title The title of the color chooser.
     * @param initialColor The initial color to use in the color chooser.
     * @return The selected color.
     */
    public Color showColorChooser(final String title, final Color initialColor) {
        return JColorChooser.showDialog(null, createTitle(title), initialColor);
    }

    /**
     * Checks if a window is minimized to the taskbar.
     *
     * @param frame The window to check.
     * @return If the window is minimized.
     */
    public boolean isMinimized(final JFrame frame) {
        return (frame.getExtendedState() & JFrame.ICONIFIED) != 0;
    }

    /**
     * Restores a minimized window so it's visible again.
     *
     * @param frame The window to restore.
     */
    public void restore(final JFrame frame) {
        if (isMinimized(frame)) {
            frame.setExtendedState(frame.getExtendedState() & ~JFrame.ICONIFIED);
        }
    }

    /**
     * Minimizes a window to the taskbar.
     *
     * @param frame The window to minimize.
     */
    public void minimize(final JFrame frame) {
        if (!isMinimized(frame)) {
            frame.setExtendedState(frame.getExtendedState() | JFrame.ICONIFIED);
        }
    }

    /**
     * A wrapper for {@link SwingUtilities#invokeAndWait(Runnable)}, that catches checked exceptions,
     * and rethrows them as unchecked, but only if necessary. Runs the runnable directly if already on
     * the EDT.
     *
     * @param runnable The runnable to invoke and wait for.
     */
    public void invokeAndWait(final Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }

        else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            } catch (final InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * A wrapper for {@link SwingUtilities#invokeLater(Runnable)}.
     *
     * @param runnable The runnable to invoke later.
     */
    public void invokeLater(final Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Checks if the application is running on a KDE desktop. This is detected by the presence of
     * the environment variable <code>KDE_FULL_SESSION</code>.
     *
     * @return If running on KDE.
     */
    public boolean isRunningOnKDE() {
        return System.getenv(KDE_FULL_SESSION) != null;
    }

    /**
     * Checks if the {@link SystemTray} is supported on the current system.
     *
     * <p>See {@link SystemTray#isSupported()} for more details.</p>
     *
     * @return If the system tray is supported.
     */
    public boolean isSystemTraySupported() {
        return SystemTray.isSupported();
    }

    /**
     * Gets an instance of the {@link SystemTray}. Use {@link #isSystemTraySupported()} first to avoid exceptions.
     *
     * <p>See {@link SystemTray#getSystemTray()} for more details.</p>
     *
     * @return An instance of the system tray.
     */
    public SystemTray getSystemTray() {
        return SystemTray.getSystemTray();
    }
}
