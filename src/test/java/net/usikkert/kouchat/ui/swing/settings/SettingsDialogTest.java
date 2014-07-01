
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import net.usikkert.kouchat.misc.Settings;
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

    @Before
    public void setUp() {
        settingsDialog = new SettingsDialog(new ImageLoader(), mock(Settings.class));

        settingsDialog.setMediator(mock(Mediator.class));

        final JPanel mainPanel = (JPanel) settingsDialog.getContentPane().getComponent(0);
        final JPanel centerPanel = (JPanel) mainPanel.getComponent(1);

        chooseNickPanel = (JPanel) mainPanel.getComponent(0);
        nickLabel = (JLabel) chooseNickPanel.getComponent(0);

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
    }

    @Test
    @Ignore("Run manually to see the settings dialog")
    public void showSettings() {
        final SettingsDialog dialog = new SettingsDialog(new ImageLoader(), new Settings());

        final Mediator mediator = mock(Mediator.class);
        when(mediator.changeNick(anyString())).thenReturn(true);
        dialog.setMediator(mediator);

        dialog.showSettings();
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new SettingsDialog(null, mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new SettingsDialog(mock(ImageLoader.class), null);
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

    // TODO misc panel
    // TODO action listeners
    // TODO drop down content
    // TODO copy paste popup
    // TODO ++
}
