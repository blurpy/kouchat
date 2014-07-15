
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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.message.PropertyFileMessages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.NetworkInterfaceInfo;
import net.usikkert.kouchat.net.NetworkUtils;
import net.usikkert.kouchat.ui.swing.CopyPastePopup;
import net.usikkert.kouchat.ui.swing.ImageLoader;
import net.usikkert.kouchat.ui.swing.LookAndFeelWrapper;
import net.usikkert.kouchat.ui.swing.Mediator;
import net.usikkert.kouchat.ui.swing.RunArgumentAnswer;
import net.usikkert.kouchat.ui.swing.UITools;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.TestUtils;

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
@SuppressWarnings("HardCodedStringLiteral")
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
    private JComboBox lookAndFeelComboBox;

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
    private Mediator mediator;
    private UITools uiTools;
    private NetworkUtils networkUtils;
    private ErrorHandler errorHandler;
    private Messages messages;

    @Before
    public void setUp() {
        settings = mock(Settings.class);
        errorHandler = mock(ErrorHandler.class);
        imageLoader = new ImageLoader(errorHandler, new ResourceValidator(), new ResourceLoader());
        messages = new PropertyFileMessages("messages.swing");

        when(settings.getLogLocation()).thenReturn("/home/user/kouchat/logs");

        settingsDialog = new SettingsDialog(imageLoader, settings, errorHandler, messages);

        mediator = mock(Mediator.class);
        settingsDialog.setMediator(mediator);

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
        lookAndFeelComboBox = (JComboBox) lookAndFeelPanel.getComponent(2);

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

        uiTools = TestUtils.setFieldValueWithMock(settingsDialog, "uiTools", UITools.class);
        doAnswer(new RunArgumentAnswer()).when(uiTools).invokeLater(any(Runnable.class));

        networkUtils = TestUtils.setFieldValueWithMock(settingsDialog, "networkUtils", NetworkUtils.class);

        lookAndFeelComboBox.addItem(createLookAndFeel("Metal"));
        networkInterfaceComboBox.addItem(new NetworkChoice("eth0", "3Com"));
    }

    @Test
    @Ignore("Run manually to see the settings dialog")
    public void showSettings() {
        final SettingsDialog dialog = new SettingsDialog(imageLoader, new Settings(), errorHandler, messages);

        when(mediator.changeNick(anyString())).thenReturn(true);
        dialog.setMediator(mediator);

        dialog.showSettings();
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new SettingsDialog(null, settings, errorHandler, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new SettingsDialog(imageLoader, null, errorHandler, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new SettingsDialog(imageLoader, settings, null, messages);
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Messages can not be null");

        new SettingsDialog(imageLoader, settings, errorHandler, null);
    }

    @Test
    public void nickNamePanelShouldHaveCorrectBorderText() {
        final TitledBorder border = (TitledBorder) chooseNickPanel.getBorder();

        assertEquals("Choose nick name", border.getTitle());
    }

    @Test
    public void nickNameLabelShouldHaveCorrectText() {
        assertEquals("Nick name:", nickLabel.getText());
    }

    @Test
    public void browserPanelShouldHaveCorrectBorderText() {
        final TitledBorder border = (TitledBorder) chooseBrowserPanel.getBorder();

        assertEquals("Choose browser", border.getTitle());
    }

    @Test
    public void browserLabelShouldHaveCorrectText() {
        assertEquals("Browser:", browserLabel.getText());
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
        assertEquals("Info text color looks like this", systemColorLabel.getText());
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
                "<br>Note that KouChat needs to be restarted for the" +
                "<br>changes to take effect.</html>",
                lookAndFeelLabel.getToolTipText());
    }

    @Test
    public void miscPanelShouldHaveCorrectBorderText() {
        final CompoundBorder compoundBorder = (CompoundBorder) miscPanel.getBorder();
        final TitledBorder border = (TitledBorder) compoundBorder.getOutsideBorder();

        assertEquals("Miscellaneous", border.getTitle());
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
        final JPopupMenu componentPopupMenu = nickTextField.getComponentPopupMenu();

        assertEquals(CopyPastePopup.class, componentPopupMenu.getClass());
    }

    @Test
    public void browserTextFieldShouldRegisterCopyPastePopup() {
        final JPopupMenu componentPopupMenu = browserTextField.getComponentPopupMenu();

        assertEquals(CopyPastePopup.class, componentPopupMenu.getClass());
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
        final JComboBox networkInterfaceCB = TestUtils.getFieldValue(fakeVisibleDialog, JComboBox.class, "networkInterfaceCB");

        // Adding an item to avoid NullPointerException in NetworkChoiceCellRenderer.getListCellRendererComponent()
        networkInterfaceCB.addItem(new NetworkChoice("eth0", "eth0"));

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

        final SettingsDialog dialog = new SettingsDialog(imageLoader, settings, errorHandler, messages);

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

    @Test
    public void setMediatorShouldThrowExceptionIfMediatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Mediator can not be null");

        settingsDialog.setMediator(null);
    }

    @Test
    public void okButtonShouldTryToChangeNickNameAndNotSaveIfInvalid() {
        reset(settings);
        nickTextField.setText("NotValid!");

        okButton.doClick();

        verify(mediator).changeNick("NotValid!");
        verifyZeroInteractions(settings);
    }

    @Test
    public void okButtonShouldSaveBrowser() {
        prepareClickOnOkButton();

        browserTextField.setText("Opera");

        okButton.doClick();

        verify(settings).setBrowser("Opera");
    }

    @Test
    public void okButtonShouldSaveColors() {
        prepareClickOnOkButton();

        ownColorLabel.setForeground(Color.CYAN);
        systemColorLabel.setForeground(Color.DARK_GRAY);

        okButton.doClick();

        verify(settings).setOwnColor(Color.CYAN.getRGB());
        verify(settings).setSysColor(Color.DARK_GRAY.getRGB());
    }

    @Test
    public void okButtonShouldSaveSelectedLookAndFeel() {
        prepareClickOnOkButton();

        lookAndFeelComboBox.removeAllItems();

        lookAndFeelComboBox.addItem(createLookAndFeel("Sunny"));
        lookAndFeelComboBox.addItem(createLookAndFeel("Cloudy"));
        lookAndFeelComboBox.addItem(createLookAndFeel("Rainy"));
        lookAndFeelComboBox.addItem(createLookAndFeel("Stormy"));

        lookAndFeelComboBox.setSelectedIndex(2);

        okButton.doClick();

        verify(settings).setLookAndFeel("Rainy");
    }

    @Test
    public void okButtonShouldSaveMiscCheckBoxes() {
        prepareClickOnOkButton();

        soundCheckBox.setSelected(true);
        smileysCheckBox.setSelected(false);
        loggingCheckBox.setSelected(true);
        balloonCheckBox.setSelected(false);

        okButton.doClick();

        verify(settings).setSound(true);
        verify(settings).setSmileys(false);
        verify(settings).setLogging(true);
        verify(settings).setBalloons(false);
    }

    @Test
    public void okButtonShouldSaveSelectedNetworkInterface() {
        prepareClickOnOkButton();

        networkInterfaceComboBox.removeAllItems();

        networkInterfaceComboBox.addItem(createNetworkInterface("eth0"));
        networkInterfaceComboBox.addItem(createNetworkInterface("eth1"));
        networkInterfaceComboBox.addItem(createNetworkInterface("lo"));
        networkInterfaceComboBox.addItem(createNetworkInterface("wlan0"));

        networkInterfaceComboBox.setSelectedIndex(3);

        okButton.doClick();

        verify(settings).setNetworkInterface("wlan0");
    }

    @Test
    public void okButtonShouldSaveSettings() {
        prepareClickOnOkButton();

        okButton.doClick();

        verify(settings).saveSettings();
    }

    @Test
    public void okButtonShouldHideDialog() {
        final SettingsDialog spyDialog = spy(settingsDialog);
        prepareClickOnOkButton();

        // okButton.doClick() won't work when using a spy
        spyDialog.actionPerformed(new ActionEvent(okButton, 0, ""));

        verify(spyDialog).setVisible(false);
    }

    @Test
    public void okButtonShouldAskToCheckNetwork() {
        prepareClickOnOkButton();

        okButton.doClick();

        verify(mediator).checkNetwork();
    }

    @Test
    public void okButtonShouldShowInfoMessageIfCurrentLookAndFeelIsDifferentFromSelected() {
        prepareClickOnOkButton();

        lookAndFeelComboBox.removeAllItems();

        final LookAndFeelWrapper stormy = createLookAndFeel("Stormy");
        lookAndFeelComboBox.addItem(createLookAndFeel("Sunny"));
        lookAndFeelComboBox.addItem(createLookAndFeel("Cloudy"));
        lookAndFeelComboBox.addItem(createLookAndFeel("Rainy"));
        lookAndFeelComboBox.addItem(stormy);

        lookAndFeelComboBox.setSelectedIndex(1); // Selected Cloudy
        when(uiTools.getCurrentLookAndFeel()).thenReturn(stormy.getLookAndFeelInfo()); // Currently Stormy

        okButton.doClick();

        verify(uiTools).showInfoMessage(
                "The new look and feel will be used the next time KouChat is started.",
                "Changed look and feel");
    }

    @Test
    public void okButtonShouldNotShowInfoMessageIfCurrentLookAndFeelIsSameAsSelected() {
        prepareClickOnOkButton();

        lookAndFeelComboBox.removeAllItems();

        final LookAndFeelWrapper stormy = createLookAndFeel("Stormy");
        lookAndFeelComboBox.addItem(createLookAndFeel("Sunny"));
        lookAndFeelComboBox.addItem(createLookAndFeel("Cloudy"));
        lookAndFeelComboBox.addItem(createLookAndFeel("Rainy"));
        lookAndFeelComboBox.addItem(stormy);

        lookAndFeelComboBox.setSelectedIndex(3); // Selected Stormy
        when(uiTools.getCurrentLookAndFeel()).thenReturn(stormy.getLookAndFeelInfo()); // Currently Stormy

        okButton.doClick();

        verify(uiTools, never()).showInfoMessage(anyString(), anyString());
    }

    @Test
    public void cancelButtonShouldHideDialog() {
        final SettingsDialog spyDialog = spy(settingsDialog);

        // cancelButton.doClick() won't work when using a spy
        spyDialog.actionPerformed(new ActionEvent(cancelButton, 0, ""));

        verify(spyDialog).setVisible(false);
    }

    @Test
    public void changeOwnColorButtonShouldOpenColorChooserWithSavedColorAndSetNewColorAsForegroundOnLabelOnOk() {
        final Color savedColor = Color.BLUE;
        final Color newColor = Color.RED;

        when(settings.getOwnColor()).thenReturn(savedColor.getRGB());
        when(uiTools.showColorChooser(anyString(), any(Color.class))).thenReturn(newColor); // Clicked ok in dialog

        changeOwnColorButton.doClick();

        verify(uiTools).showColorChooser("Choose color for own messages", savedColor);
        assertEquals(newColor, ownColorLabel.getForeground());
    }

    @Test
    public void changeOwnColorButtonShouldOpenColorChooserWithSavedColorAndDoNothingOnCancel() {
        final Color savedColor = Color.BLUE;
        final Color originalLabelColor = ownColorLabel.getForeground();

        when(settings.getOwnColor()).thenReturn(savedColor.getRGB());
        when(uiTools.showColorChooser(anyString(), any(Color.class))).thenReturn(null); // Clicked cancel in dialog

        changeOwnColorButton.doClick();

        verify(uiTools).showColorChooser("Choose color for own messages", savedColor);
        assertEquals(originalLabelColor, ownColorLabel.getForeground());
    }

    @Test
    public void changeSystemColorButtonShouldOpenColorChooserWithSavedColorAndSetNewColorAsForegroundOnLabelOnOk() {
        final Color savedColor = Color.CYAN;
        final Color newColor = Color.DARK_GRAY;

        when(settings.getSysColor()).thenReturn(savedColor.getRGB());
        when(uiTools.showColorChooser(anyString(), any(Color.class))).thenReturn(newColor); // Clicked ok in dialog

        changeSystemColorButton.doClick();

        verify(uiTools).showColorChooser("Choose color for info messages", savedColor);
        assertEquals(newColor, systemColorLabel.getForeground());
    }

    @Test
    public void changeSystemColorButtonShouldOpenColorChooserWithSavedColorAndDoNothingOnCancel() {
        final Color savedColor = Color.GREEN;
        final Color originalLabelColor = systemColorLabel.getForeground();

        when(settings.getSysColor()).thenReturn(savedColor.getRGB());
        when(uiTools.showColorChooser(anyString(), any(Color.class))).thenReturn(null); // Clicked cancel in dialog

        changeSystemColorButton.doClick();

        verify(uiTools).showColorChooser("Choose color for info messages", savedColor);
        assertEquals(originalLabelColor, systemColorLabel.getForeground());
    }

    @Test
    public void chooseBrowserButtonShouldSetAbsolutePathToSelectedFileInTextFieldOnOk() {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.APPROVE_OPTION);
        when(fileChooser.getSelectedFile()).thenReturn(new File("opera"));

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        chooseBrowserButton.doClick();

        assertEquals(new File("").getAbsolutePath() + File.separator + "opera", browserTextField.getText());
        verify(uiTools).createFileChooser("Choose browser");
    }

    @Test
    public void chooseBrowserButtonShouldNotDoAnythingOnCancel() {
        final JFileChooser fileChooser = mock(JFileChooser.class);
        when(fileChooser.showOpenDialog(null)).thenReturn(JFileChooser.CANCEL_OPTION);

        when(uiTools.createFileChooser(anyString())).thenReturn(fileChooser);

        chooseBrowserButton.doClick();

        assertEquals("", browserTextField.getText());
    }

    @Test
    public void testBrowserButtonShouldUseSpecifiedBrowser() throws IOException, URISyntaxException {
        browserTextField.setText("chrome");

        testBrowserButton.doClick();

        verify(uiTools).runCommand("chrome http://www.kouchat.net/");
        verifyZeroInteractions(errorHandler);
    }

    @Test
    public void testBrowserButtonShouldShowErrorIfSpecifiedBrowserIsUnableToLaunch() throws IOException, URISyntaxException {
        browserTextField.setText("iexplore.exe");
        when(uiTools.runCommand(anyString())).thenThrow(new IOException("Don't launch"));

        testBrowserButton.doClick();

        verify(uiTools).runCommand("iexplore.exe http://www.kouchat.net/");
        verify(errorHandler).showError("Could not open the browser 'iexplore.exe'. Try using the full path.");
    }

    @Test
    public void testBrowserButtonShouldUseDefaultBrowserIfNoBrowserSet() throws IOException, URISyntaxException {
        when(uiTools.isDesktopActionSupported(Desktop.Action.BROWSE)).thenReturn(true);

        testBrowserButton.doClick();

        verify(uiTools).browse("http://www.kouchat.net/");
        verifyZeroInteractions(errorHandler);
    }

    @Test
    public void testBrowserButtonShouldUseDefaultBrowserIfBrowserIsWhitespace() throws IOException, URISyntaxException {
        when(uiTools.isDesktopActionSupported(Desktop.Action.BROWSE)).thenReturn(true);
        browserTextField.setText(" ");

        testBrowserButton.doClick();

        verify(uiTools).browse("http://www.kouchat.net/");
        verifyZeroInteractions(errorHandler);
    }

    @Test
    public void testBrowserButtonShouldShowErrorIfOpeningDefaultBrowserWithInvalidUrl() throws IOException, URISyntaxException {
        when(uiTools.isDesktopActionSupported(Desktop.Action.BROWSE)).thenReturn(true);
        doThrow(new URISyntaxException("http://www.kouchat.net/", "Invalid url")).when(uiTools).browse(anyString());

        testBrowserButton.doClick();

        verify(uiTools).browse("http://www.kouchat.net/");
        verify(errorHandler).showError("That's strange, could not open http://www.kouchat.net/");
    }

    @Test
    public void testBrowserButtonShouldShowErrorIfUnableToOpenDefaultBrowser() throws IOException, URISyntaxException {
        when(uiTools.isDesktopActionSupported(Desktop.Action.BROWSE)).thenReturn(true);
        doThrow(new IOException("Wrong browser")).when(uiTools).browse(anyString());

        testBrowserButton.doClick();

        verify(errorHandler).showError("Could not open the default browser.");
    }

    @Test
    public void testBrowserButtonShouldShowErrorIfDefaultBrowserIsUnsupported() throws IOException, URISyntaxException {
        when(uiTools.isDesktopActionSupported(Desktop.Action.BROWSE)).thenReturn(false);

        testBrowserButton.doClick();

        verify(errorHandler).showError("Your system does not support a default browser. Please choose a browser manually.");
    }

    @Test
    public void showSettingsShouldSetNickNameFromSettings() {
        prepareShowSettings();

        settingsDialog.showSettings();

        assertEquals("Lisa", nickTextField.getText());
        verify(settings).getMe();
    }

    @Test
    public void showSettingsShouldSetBrowserFromSettings() {
        prepareShowSettings();
        when(settings.getBrowser()).thenReturn("Firefox");

        settingsDialog.showSettings();

        assertEquals("Firefox", browserTextField.getText());
        verify(settings).getBrowser();
    }

    @Test
    public void showSettingsShouldSetColorsFromSettings() {
        prepareShowSettings();

        when(settings.getOwnColor()).thenReturn(Color.BLUE.getRGB());
        when(settings.getSysColor()).thenReturn(Color.RED.getRGB());

        settingsDialog.showSettings();

        assertEquals(Color.BLUE.getRGB(), ownColorLabel.getForeground().getRGB());
        assertEquals(Color.RED.getRGB(), systemColorLabel.getForeground().getRGB());

        verify(settings).getOwnColor();
        verify(settings).getSysColor();
    }

    @Test
    public void showSettingsShouldSetMiscCheckBoxesFromSettings() {
        prepareShowSettings();

        when(settings.isSound()).thenReturn(true);
        when(settings.isLogging()).thenReturn(true);
        when(settings.isSmileys()).thenReturn(true);
        when(settings.isBalloons()).thenReturn(true);

        settingsDialog.showSettings();

        assertTrue(soundCheckBox.isSelected());
        assertTrue(loggingCheckBox.isSelected());
        assertTrue(smileysCheckBox.isSelected());
        assertTrue(balloonCheckBox.isSelected());

        verify(settings).isSound();
        verify(settings).isLogging();
        verify(settings).isSmileys();
        verify(settings).isBalloons();
    }

    @Test
    public void showSettingsShouldPutAllNetworkInterfacesAndAutoInComboBox() {
        prepareShowSettings();

        final NetworkInterfaceInfo net1 = createNetworkInterfaceInfo("eth0");
        final NetworkInterfaceInfo net2 = createNetworkInterfaceInfo("eth1");
        final NetworkInterfaceInfo net3 = createNetworkInterfaceInfo("eth2");
        when(networkUtils.getUsableNetworkInterfaces()).thenReturn(Arrays.asList(net1, net2, net3));

        settingsDialog.showSettings();

        assertEquals(4, networkInterfaceComboBox.getItemCount());

        checkNetworkChoiceAt(0, "Auto", "Let KouChat decide.");
        checkNetworkChoiceAt(1, "eth0", "Display name for eth0");
        checkNetworkChoiceAt(2, "eth1", "Display name for eth1");
        checkNetworkChoiceAt(3, "eth2", "Display name for eth2");

        verify(networkUtils).getUsableNetworkInterfaces();
    }

    @Test
    public void showSettingsShouldPutAllLookAndFeelsInComboBox() {
        prepareShowSettings();

        final LookAndFeelWrapper spungy = createLookAndFeel("Spungy", "net.usikkert.kouchat.lnf.spungy");
        final LookAndFeelWrapper kou = createLookAndFeel("Kou", "net.usikkert.kouchat.lnf.kou");
        final LookAndFeelWrapper goompa = createLookAndFeel("Goompa", "net.usikkert.kouchat.lnf.goompa");
        when(uiTools.getLookAndFeels()).thenReturn(new LookAndFeelWrapper[] {spungy, kou, goompa});

        settingsDialog.showSettings();

        assertEquals(3, lookAndFeelComboBox.getItemCount());

        assertSame(spungy, lookAndFeelComboBox.getItemAt(0));
        assertSame(kou, lookAndFeelComboBox.getItemAt(1));
        assertSame(goompa, lookAndFeelComboBox.getItemAt(2));

        verify(uiTools).getLookAndFeels();
    }

    @Test
    public void showSettingsShouldSetDialogVisible() {
        prepareShowSettings();

        settingsDialog.showSettings();

        verify(settingsDialog).setVisible(true);
    }

    @Test
    public void showSettingsShouldRequestFocusFromNickNameTextField() {
        prepareShowSettings();

        final JTextField nickTF = TestUtils.setFieldValueWithMock(settingsDialog, "nickTF", JTextField.class);

        settingsDialog.showSettings();

        verify(nickTF).requestFocusInWindow();
    }

    @Test
    public void showSettingsShouldSelectTheSavedLookAndFeelInTheComboBox() {
        prepareShowSettings();

        final LookAndFeelWrapper spungy = createLookAndFeel("Spungy", "net.usikkert.kouchat.lnf.spungy");
        final LookAndFeelWrapper kou = createLookAndFeel("Kou", "net.usikkert.kouchat.lnf.kou");
        final LookAndFeelWrapper goompa = createLookAndFeel("Goompa", "net.usikkert.kouchat.lnf.goompa");
        when(uiTools.getLookAndFeels()).thenReturn(new LookAndFeelWrapper[] {spungy, kou, goompa});

        when(settings.getLookAndFeel()).thenReturn("Kou");

        final UIManager.LookAndFeelInfo savedLookAndFeel = mock(UIManager.LookAndFeelInfo.class);
        when(savedLookAndFeel.getClassName()).thenReturn("net.usikkert.kouchat.lnf.kou");
        when(uiTools.getLookAndFeel("Kou")).thenReturn(savedLookAndFeel);

        settingsDialog.showSettings();

        assertEquals(kou, lookAndFeelComboBox.getSelectedItem());
    }

    @Test
    public void showSettingsShouldSelectTheCurrentLookAndFeelInTheComboBoxIfNoneIsSaved() {
        prepareShowSettings();

        final LookAndFeelWrapper spungy = createLookAndFeel("Spungy", "net.usikkert.kouchat.lnf.spungy");
        final LookAndFeelWrapper kou = createLookAndFeel("Kou", "net.usikkert.kouchat.lnf.kou");
        final LookAndFeelWrapper goompa = createLookAndFeel("Goompa", "net.usikkert.kouchat.lnf.goompa");
        when(uiTools.getLookAndFeels()).thenReturn(new LookAndFeelWrapper[] {spungy, kou, goompa});

        when(settings.getLookAndFeel()).thenReturn(null);

        final UIManager.LookAndFeelInfo currentLookAndFeel = mock(UIManager.LookAndFeelInfo.class);
        when(currentLookAndFeel.getClassName()).thenReturn("net.usikkert.kouchat.lnf.kou");
        when(uiTools.getCurrentLookAndFeel()).thenReturn(currentLookAndFeel);

        settingsDialog.showSettings();

        assertEquals(kou, lookAndFeelComboBox.getSelectedItem());
    }

    @Test
    public void showSettingsShouldSelectTheSavedNetworkInterfaceInTheComboBox() {
        prepareShowSettings();

        final NetworkInterfaceInfo net1 = createNetworkInterfaceInfo("eth0");
        final NetworkInterfaceInfo net2 = createNetworkInterfaceInfo("eth1");
        final NetworkInterfaceInfo net3 = createNetworkInterfaceInfo("eth2");
        when(networkUtils.getUsableNetworkInterfaces()).thenReturn(Arrays.asList(net1, net2, net3));

        when(settings.getNetworkInterface()).thenReturn("eth2");

        settingsDialog.showSettings();

        final int expectedPosition = 3;
        assertEquals(expectedPosition, networkInterfaceComboBox.getSelectedIndex()); // Auto is at position 0
        checkNetworkChoiceAt(expectedPosition, "eth2", "Display name for eth2");
    }

    @Test
    public void showSettingsShouldSelectAutoInTheComboBoxIfNoneIsSaved() {
        prepareShowSettings();

        final NetworkInterfaceInfo net1 = createNetworkInterfaceInfo("eth0");
        final NetworkInterfaceInfo net2 = createNetworkInterfaceInfo("eth1");
        final NetworkInterfaceInfo net3 = createNetworkInterfaceInfo("eth2");
        when(networkUtils.getUsableNetworkInterfaces()).thenReturn(Arrays.asList(net1, net2, net3));

        when(settings.getNetworkInterface()).thenReturn(null);

        settingsDialog.showSettings();

        final int expectedPosition = 0;
        assertEquals(expectedPosition, networkInterfaceComboBox.getSelectedIndex());
        checkNetworkChoiceAt(expectedPosition, "Auto", "Let KouChat decide.");
    }

    private void checkNetworkChoiceAt(final int position, final String deviceName, final String displayName) {
        final NetworkChoice itemAt = (NetworkChoice) networkInterfaceComboBox.getItemAt(position);

        assertEquals(deviceName, itemAt.getDeviceName());
        assertEquals(displayName, itemAt.getDisplayName());
    }

    private NetworkInterfaceInfo createNetworkInterfaceInfo(final String deviceName) {
        final NetworkInterfaceInfo networkInterfaceInfo = mock(NetworkInterfaceInfo.class);

        when(networkInterfaceInfo.getName()).thenReturn(deviceName);
        when(networkInterfaceInfo.getDisplayName()).thenReturn("Display name for " + deviceName);

        return networkInterfaceInfo;
    }

    private NetworkChoice createNetworkInterface(final String deviceName) {
        return new NetworkChoice(deviceName, "Display name for " + deviceName);
    }

    private LookAndFeelWrapper createLookAndFeel(final String name) {
        final UIManager.LookAndFeelInfo lookAndFeelInfo = mock(UIManager.LookAndFeelInfo.class);
        when(lookAndFeelInfo.getName()).thenReturn(name);

        return new LookAndFeelWrapper(lookAndFeelInfo);
    }

    private LookAndFeelWrapper createLookAndFeel(final String name, final String className) {
        final UIManager.LookAndFeelInfo lookAndFeelInfo = mock(UIManager.LookAndFeelInfo.class);
        when(lookAndFeelInfo.getName()).thenReturn(name);
        when(lookAndFeelInfo.getClassName()).thenReturn(className);

        return new LookAndFeelWrapper(lookAndFeelInfo);
    }

    private void prepareShowSettings() {
        settingsDialog = spy(settingsDialog);
        doNothing().when(settingsDialog).setVisible(anyBoolean());

        when(settings.getMe()).thenReturn(new User("Lisa", 1234));
        when(uiTools.getLookAndFeels()).thenReturn(new LookAndFeelWrapper[0]);
        when(uiTools.getCurrentLookAndFeel()).thenReturn(mock(UIManager.LookAndFeelInfo.class));
    }

    private void prepareClickOnOkButton() {
        when(mediator.changeNick(anyString())).thenReturn(true);
        when(uiTools.getCurrentLookAndFeel()).thenReturn(mock(UIManager.LookAndFeelInfo.class));
    }

    private SettingsDialog createFakeVisibleDialog() {
        final SettingsDialog dialog = new SettingsDialog(imageLoader, settings, errorHandler, messages) {

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
}
