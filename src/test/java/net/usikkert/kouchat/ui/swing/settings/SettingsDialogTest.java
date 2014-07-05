
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

package net.usikkert.kouchat.ui.swing.settings;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.ui.swing.CopyPastePopup;
import net.usikkert.kouchat.ui.swing.ImageLoader;
import net.usikkert.kouchat.ui.swing.Mediator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link SettingsDialog}.
 *
 * @author Christian Ihle
 */
public class SettingsDialogTest  {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SettingsDialog settingsDialog;

    private JPanel chooseNickPanel;
    private JLabel nickLabel;
    private JTextField nickTextField;

    private JPanel chooseBrowserPanel;
    private JLabel browserLabel;
    private JTextField browserTextField;
    private JButton chooseBrowserButton;
    private JButton testBrowserButton;

    private JPanel chooseLookPanel;
    private JLabel ownColorLabel;
    private JButton changeOwnColorButton;
    private JLabel systemColorLabel;
    private JButton changeSystemColorButton;
    private JLabel lookAndFeelLabel;

    private JPanel miscPanel;
    private JCheckBox soundCheckBox;
    private JCheckBox loggingCheckBox;
    private JCheckBox smileysCheckBox;
    private JCheckBox balloonCheckBox;
    private JLabel networkInterfaceLabel;
    private JComboBox networkInterfaceComboBox;

    private JButton okButton;
    private JButton cancelButton;

    private Settings settings;
    private ImageLoader imageLoader;

    @Before
    public void setUp() {
        settings = mock(Settings.class);
        imageLoader = new ImageLoader();

        when(settings.getLogLocation()).thenReturn("/home/user/kouchat/logs");

        settingsDialog = new SettingsDialog(imageLoader, settings);

        settingsDialog.setMediator(mock(Mediator.class));

        final JPanel mainPanel = (JPanel) settingsDialog.getContentPane().getComponent(0);
        final JPanel centerPanel = (JPanel) mainPanel.getComponent(1);
        final JPanel buttonPanel = (JPanel) mainPanel.getComponent(2);

        chooseNickPanel = (JPanel) mainPanel.getComponent(0);
        nickLabel = (JLabel) chooseNickPanel.getComponent(0);
        nickTextField = (JTextField) chooseNickPanel.getComponent(1);

        chooseBrowserPanel = (JPanel) centerPanel.getComponent(2);
        final JPanel browserTopPanel = (JPanel) chooseBrowserPanel.getComponent(0);
        browserLabel = (JLabel) browserTopPanel.getComponent(0);
        browserTextField = (JTextField) browserTopPanel.getComponent(1);
        final JPanel browserBottomPanel = (JPanel) chooseBrowserPanel.getComponent(1);
        chooseBrowserButton = (JButton) browserBottomPanel.getComponent(0);
        testBrowserButton = (JButton) browserBottomPanel.getComponent(1);

        chooseLookPanel = (JPanel) centerPanel.getComponent(0);
        final JPanel ownColorPanel = (JPanel) chooseLookPanel.getComponent(0);
        ownColorLabel = (JLabel) ownColorPanel.getComponent(0);
        changeOwnColorButton = (JButton) ownColorPanel.getComponent(2);
        final JPanel systemColorPanel = (JPanel) chooseLookPanel.getComponent(1);
        systemColorLabel = (JLabel) systemColorPanel.getComponent(0);
        changeSystemColorButton = (JButton) systemColorPanel.getComponent(2);
        final JPanel lookAndFeelPanel = (JPanel) chooseLookPanel.getComponent(2);
        lookAndFeelLabel = (JLabel) lookAndFeelPanel.getComponent(0);

        miscPanel = (JPanel) centerPanel.getComponent(1);
        final JPanel miscCheckBoxPanel = (JPanel) miscPanel.getComponent(0);
        soundCheckBox = (JCheckBox) miscCheckBoxPanel.getComponent(0);
        loggingCheckBox = (JCheckBox) miscCheckBoxPanel.getComponent(1);
        smileysCheckBox = (JCheckBox) miscCheckBoxPanel.getComponent(2);
        balloonCheckBox = (JCheckBox) miscCheckBoxPanel.getComponent(3);
        final JPanel networkInterfacePanel = (JPanel) miscPanel.getComponent(1);
        networkInterfaceLabel = (JLabel) networkInterfacePanel.getComponent(0);
        networkInterfaceComboBox = (JComboBox) networkInterfacePanel.getComponent(2);

        okButton = (JButton) buttonPanel.getComponent(0);
        cancelButton = (JButton) buttonPanel.getComponent(1);
    }

    @Test
    @Ignore("Run manually to see the settings dialog")
    public void showSettings() {
        final SettingsDialog dialog = new SettingsDialog(imageLoader, new Settings());

        final Mediator mediator = mock(Mediator.class);
        when(mediator.changeNick(anyString())).thenReturn(true);
        dialog.setMediator(mediator);

        dialog.showSettings();
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new SettingsDialog(null, settings);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new SettingsDialog(imageLoader, null);
    }

    @Test
    public void nickNamePanelShouldHaveCorrectBorderText() {
        final TitledBorder border = (TitledBorder) chooseNickPanel.getBorder();

        assertEquals("Choose nick", border.getTitle());
    }

    @Test
    public void nickNameLabelShouldHaveCorrectText() {
        assertEquals("Nick:", nickLabel.getText());
    }

    @Test
    public void browserPanelShouldHaveCorrectBorderText() {
        final TitledBorder border = (TitledBorder) chooseBrowserPanel.getBorder();

        assertEquals("Choose browser", border.getTitle());
    }

    @Test
    public void browserLabelShouldHaveCorrectText() {
        assertEquals("Browser: ", browserLabel.getText());
    }

    @Test
    public void browserTextFieldShouldHaveCorrectToolTip() {
        assertEquals(
                "<html>When you click on a link in the chat it will open" +
                "<br>in the browser defined here. If this field" +
                "<br>is empty the default browser on your system" +
                "<br>will be used, if possible.</html>",
                browserTextField.getToolTipText());
    }

    @Test
    public void chooseBrowserButtonShouldHaveCorrectText() {
        assertEquals("Choose", chooseBrowserButton.getText());
    }

    @Test
    public void testBrowserButtonShouldHaveCorrectText() {
        assertEquals("Test", testBrowserButton.getText());
    }

    @Test
    public void lookAndFeelPanelShouldHaveCorrectBorderText() {
        final CompoundBorder compoundBorder = (CompoundBorder) chooseLookPanel.getBorder();
        final TitledBorder border = (TitledBorder) compoundBorder.getOutsideBorder();

        assertEquals("Choose look", border.getTitle());
    }

    @Test
    public void ownColorLabelShouldHaveCorrectText() {
        assertEquals("Own text color looks like this", ownColorLabel.getText());
    }

    @Test
    public void ownColorLabelToolTipShouldHaveCorrectText() {
        assertEquals(
                "<html>You and other users will see" +
                "<br>the messages you write in this color.</html>",
                ownColorLabel.getToolTipText());
    }

    @Test
    public void changeOwnColorButtonShouldHaveCorrectText() {
        assertEquals("Change", changeOwnColorButton.getText());
    }

    @Test
    public void systemColorLabelShouldHaveCorrectText() {
        assertEquals("System text color looks like this", systemColorLabel.getText());
    }

    @Test
    public void systemColorLabelToolTipShouldHaveCorrectText() {
        assertEquals(
                "<html>Information messages from the application" +
                "<br>will be shown in this color.</html>",
                systemColorLabel.getToolTipText());
    }

    @Test
    public void changeSystemColorButtonShouldHaveCorrectText() {
        assertEquals("Change", changeSystemColorButton.getText());
    }


    @Test
    public void lookAndFeelLabelShouldHaveCorrectText() {
        assertEquals("Look and feel", lookAndFeelLabel.getText());
    }

    @Test
    public void lookAndFeelLabelToolTipShouldHaveCorrectText() {
        assertEquals(
                "<html>Gives a choice of all the different looks that are available." +
                "<br />Note that KouChat needs to be restarted for the" +
                "<br />changes to take effect.</html>",
                lookAndFeelLabel.getToolTipText());
    }

    @Test
    public void miscPanelShouldHaveCorrectBorderText() {
        final CompoundBorder compoundBorder = (CompoundBorder) miscPanel.getBorder();
        final TitledBorder border = (TitledBorder) compoundBorder.getOutsideBorder();

        assertEquals("Misc", border.getTitle());
    }

    @Test
    public void soundCheckBoxShouldHaveCorrectText() {
        assertEquals("Enable sound", soundCheckBox.getText());
    }

    @Test
    public void soundCheckBoxToolTipShouldHaveCorrectText() {
        assertEquals(
                "<html>Will give a short sound notification when" +
                "<br>a new message is received if KouChat" +
                "<br>is minimized to the system tray, and" +
                "<br>when asked to receive a file.</html>",
                soundCheckBox.getToolTipText());
    }

    @Test
    public void loggingCheckBoxShouldHaveCorrectText() {
        assertEquals("Enable logging", loggingCheckBox.getText());
    }

    @Test
    public void loggingCheckBoxToolTipShouldHaveCorrectText() {
        assertEquals(
                "<html>Stores the conversations in the main chat and private chats to log files in" +
                "<br><em>/home/user/kouchat/logs</em>." +
                "<br>Only text written after this option was enabled will be stored.</html>",
                loggingCheckBox.getToolTipText());

        verify(settings).getLogLocation();
    }

    @Test
    public void smileysCheckBoxShouldHaveCorrectText() {
        assertEquals("Enable smileys", smileysCheckBox.getText());
    }

    @Test
    public void smileysCheckBoxToolTipShouldHaveCorrectText() {
        assertEquals(
                "<html>Replaces text smileys in the chat with smiley images." +
                "<br>See the FAQ for a list of available smileys.</html>",
                smileysCheckBox.getToolTipText());
    }

    @Test
    public void ballonCheckBoxShouldHaveCorrectText() {
        assertEquals("Enable balloons", balloonCheckBox.getText());
    }

    @Test
    public void ballonCheckBoxToolTipShouldHaveCorrectText() {
        assertEquals(
                "<html>Shows balloon notifications in the system tray when new" +
                "<br>messages are received while the application is hidden.</html>",
                balloonCheckBox.getToolTipText());
    }

    @Test
    public void networkInterfaceLabelShouldHaveCorrectText() {
        assertEquals("Network interface", networkInterfaceLabel.getText());
    }

    @Test
    public void networkInterfaceLabelToolTipShouldHaveCorrectText() {
        assertEquals(
                "<html>Allows you to specify which network interface to use for " +
                "<br>communication with other clients. Or use <em>Auto</em> to " +
                "<br>let KouChat decide.</html>",
                networkInterfaceLabel.getToolTipText());
    }

    @Test
    public void okButtonShouldHaveCorrectText() {
        assertEquals("OK", okButton.getText());
    }

    @Test
    public void cancelButtonShouldHaveCorrectText() {
        assertEquals("Cancel", cancelButton.getText());
    }

    @Test
    public void dialogTitleShouldHaveCorrectText() {
        assertEquals("Settings - KouChat", settingsDialog.getTitle());
    }

    @Test
    public void nickNameTextFieldShouldRegisterCopyPastePopup() {
        final MouseListener[] mouseListeners = nickTextField.getMouseListeners();

        // Seems to be 4 listeners - three of them are not added by me
        assertEquals(CopyPastePopup.class, mouseListeners[mouseListeners.length - 1].getClass());
    }

    @Test
    public void browserTextFieldShouldRegisterCopyPastePopup() {
        final MouseListener[] mouseListeners = browserTextField.getMouseListeners();

        // Seems to be 5 listeners - four of them are not added by me
        assertEquals(CopyPastePopup.class, mouseListeners[mouseListeners.length - 1].getClass());
    }

    @Test
    public void dialogShouldHideOnClose() {
        assertEquals(WindowConstants.HIDE_ON_CLOSE, settingsDialog.getDefaultCloseOperation());
    }

    @Test
    public void dialogShouldBeModal() {
        assertTrue(settingsDialog.isModal());
    }

    @Test
    public void dialogShouldNotBeResizeable() {
        assertFalse(settingsDialog.isResizable());
    }

    @Test
    public void dialogShouldHaveCorrectIcon() {
        final List<Image> iconImages = settingsDialog.getIconImages();
        assertEquals(1, iconImages.size());

        final Image icon = iconImages.get(0);

        assertSame(imageLoader.getKouNormal32Icon().getImage(), icon);
    }

    @Test
    public void dialogShouldHideOnEscape() {
        final SettingsDialog fakeVisibleDialog = createFakeVisibleDialog();

        assertTrue(fakeVisibleDialog.isVisible());

        final KeyEvent escapeEvent =
                new KeyEvent(fakeVisibleDialog, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, '\u001b', 1);

        fakeVisibleDialog.dispatchEvent(escapeEvent);

        assertFalse(fakeVisibleDialog.isVisible());
    }

    @Test
    public void okShouldBeTheDefaultButton() {
        assertSame(okButton, settingsDialog.getRootPane().getDefaultButton());
    }

    @Test
    public void loggingCheckBoxShouldBeEnabledIfAlwaysLogIsNotEnabled() {
        assertFalse(settings.isAlwaysLog());
        assertTrue(loggingCheckBox.isEnabled());
    }

    @Test
    public void loggingCheckBoxShouldBeDisabledIfAlwaysLogIsEnabled() {
        when(settings.isAlwaysLog()).thenReturn(true);
        assertTrue(settings.isAlwaysLog());

        final SettingsDialog dialog = new SettingsDialog(imageLoader, settings);

        final JPanel dialogMainPanel = (JPanel) dialog.getContentPane().getComponent(0);
        final JPanel dialogCenterPanel = (JPanel) dialogMainPanel.getComponent(1);
        final JPanel dialogMiscPanel = (JPanel) dialogCenterPanel.getComponent(1);
        final JPanel dialogMiscCheckBoxPanel = (JPanel) dialogMiscPanel.getComponent(0);
        final JCheckBox dialogLoggingCheckBox = (JCheckBox) dialogMiscCheckBoxPanel.getComponent(1);

        assertFalse(dialogLoggingCheckBox.isEnabled());
    }

    @Test
    public void networkInterfaceComboBoxShouldUseNetworkChoiceCellRenderer() {
        final ListCellRenderer renderer = networkInterfaceComboBox.getRenderer();

        assertEquals(NetworkChoiceCellRenderer.class, renderer.getClass());
    }

    SettingsDialog createFakeVisibleDialog() {
        final SettingsDialog dialog = new SettingsDialog(imageLoader, settings) {

            private boolean visible;

            @Override
            public void setVisible(final boolean visible) {
                this.visible = visible;
            }

            @Override
            public boolean isVisible() {
                return visible;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public boolean isShowing() {
                return true;
            }
        };

        dialog.setVisible(true);

        return dialog;
    }

    // TODO action listeners
    // TODO drop down content
    // TODO ++
}
