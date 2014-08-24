
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link TransferDialog}.
 *
 * @author Christian Ihle
 */
public class TransferDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TransferDialog transferDialog;
    private TransferDialog transferDialogSpy;

    private Mediator mediator;
    private Settings settings;
    private UITools uiTools;
    private FileTransfer fileTransfer;
    private StatusIcons statusIcons;

    private JButton cancelButton;
    private JButton openButton;

    private JLabel transferredHeaderLabel;
    private JLabel transferredLabel;
    private JLabel fileNameHeaderLabel;
    private JLabel fileNameLabel;
    private JLabel statusHeaderLabel;
    private JLabel statusLabel;
    private JLabel sourceHeaderLabel;
    private JLabel sourceLabel;
    private JLabel destinationHeaderLabel;
    private JLabel destinationLabel;
    private JProgressBar progressBar;

    @Before
    public void setUp() {
        final ImageLoader imageLoader =
                new ImageLoader(mock(ErrorHandler.class), mock(Messages.class), new ResourceValidator(), new ResourceLoader());

        mediator = mock(Mediator.class);
        settings = mock(Settings.class);
        fileTransfer = mock(FileTransfer.class);

        transferDialog = new TransferDialog(mediator, fileTransfer, imageLoader, settings);

        uiTools = TestUtils.setFieldValueWithMock(transferDialog, "uiTools", UITools.class);
        doAnswer(new RunArgumentAnswer()).when(uiTools).invokeLater(any(Runnable.class));

        statusIcons = new StatusIcons(imageLoader);

        final JPanel topPanel = (JPanel) transferDialog.getContentPane().getComponent(0);

        final JPanel statusPanel = (JPanel) topPanel.getComponent(0);
        statusHeaderLabel = (JLabel) statusPanel.getComponent(0);
        statusLabel = (JLabel) statusPanel.getComponent(1);

        final JPanel sourcePanel = (JPanel) topPanel.getComponent(1);
        sourceHeaderLabel = (JLabel) sourcePanel.getComponent(0);
        sourceLabel = (JLabel) sourcePanel.getComponent(1);

        final JPanel destinationPanel = (JPanel) topPanel.getComponent(2);
        destinationHeaderLabel = (JLabel) destinationPanel.getComponent(0);
        destinationLabel = (JLabel) destinationPanel.getComponent(1);

        final JPanel fileNamePanel = (JPanel) topPanel.getComponent(3);
        fileNameHeaderLabel = (JLabel) fileNamePanel.getComponent(0);
        fileNameLabel = (JLabel) fileNamePanel.getComponent(1);

        final JPanel progressPanel = (JPanel) topPanel.getComponent(4);
        progressBar = (JProgressBar) progressPanel.getComponent(0);

        final JPanel transferredPanel = (JPanel) topPanel.getComponent(5);
        transferredHeaderLabel = (JLabel) transferredPanel.getComponent(0);
        transferredLabel = (JLabel) transferredPanel.getComponent(1);

        final JPanel bottomPanel = (JPanel) transferDialog.getContentPane().getComponent(1);
        openButton = (JButton) bottomPanel.getComponent(1);
        cancelButton = (JButton) bottomPanel.getComponent(3);

        transferDialogSpy = spy(transferDialog);
        doNothing().when(transferDialogSpy).setVisible(anyBoolean());
    }

    @Test
    public void constructorShouldThrowExceptionIfMediatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Mediator can not be null");

        new TransferDialog(null, fileTransfer, mock(ImageLoader.class), settings);
    }

    @Test
    public void constructorShouldThrowExceptionIfFileTransferIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File transfer can not be null");

        new TransferDialog(mediator, null, mock(ImageLoader.class), settings);
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new TransferDialog(mediator, fileTransfer, null, settings);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new TransferDialog(mediator, fileTransfer, mock(ImageLoader.class), null);
    }

    @Test
    public void constructorShouldRegisterListenerOnFileTransfer() {
        verify(fileTransfer).registerListener(transferDialog);
    }

    @Test
    public void dialogTitleShouldBeZeroPercentByDefault() {
        assertEquals("0% - File transfer - KouChat", transferDialog.getTitle());
    }

    @Test
    public void dialogShouldDoNothingOnClose() {
        assertEquals(WindowConstants.DO_NOTHING_ON_CLOSE, transferDialog.getDefaultCloseOperation());
    }

    @Test
    public void dialogShouldSetNormalIcon() {
        assertSame(statusIcons.getNormalIcon(), transferDialog.getIconImages().get(0));
    }

    @Test
    public void dialogShouldNotBeResizable() {
        assertFalse(transferDialog.isResizable());
    }

    @Test
    public void cancelButtonShouldHaveCorrectText() {
        assertEquals("Cancel", cancelButton.getText());
    }

    @Test
    public void cancelButtonShouldBeDefaultButton() {
        assertSame(cancelButton, transferDialog.getRootPane().getDefaultButton());
    }

    @Test
    public void cancelButtonShouldNotifyMediatorOnClick() {
        cancelButton.doClick();

        verify(mediator).transferCancelled(transferDialog);
    }

    @Test
    public void openButtonShouldHaveCorrectText() {
        assertEquals("Open folder", openButton.getText());
    }

    @Test
    public void openButtonShouldBeHiddenAndDisabledByDefault() {
        assertFalse(openButton.isEnabled());
        assertFalse(openButton.isVisible());
    }

    @Test
    public void openButtonShouldUseUiToolsToOpenFileFromFileTransfer() {
        final File file = new File("files/something.txt");
        when(fileTransfer.getFile()).thenReturn(file);

        openButton.setEnabled(true); // Must be enabled for actionPerformed() to run

        openButton.doClick();

        verify(uiTools).open(file.getParentFile(), settings);
    }

    @Test
    public void transferredHeaderLabelShouldHaveCorrectText() {
        assertEquals("Transferred:", transferredHeaderLabel.getText());
    }

    @Test
    public void transferredLabelShouldHaveCorrectText() {
        assertEquals("0KB of 0KB at 0KB/s", transferredLabel.getText());
    }

    @Test
    public void fileNameHeaderLabelShouldHaveCorrectText() {
        assertEquals("Filename:", fileNameHeaderLabel.getText());
    }

    @Test
    public void fileNameLabelShouldHaveCorrectText() {
        assertEquals("(No file)", fileNameLabel.getText());
    }

    @Test
    public void statusHeaderLabelShouldHaveCorrectText() {
        assertEquals("Status:", statusHeaderLabel.getText());
    }

    @Test
    public void statusLabelShouldHaveCorrectText() {
        assertEquals("Waiting...", statusLabel.getText());
    }

    @Test
    public void sourceHeaderLabelShouldHaveCorrectText() {
        assertEquals("Source:", sourceHeaderLabel.getText());
    }

    @Test
    public void sourceLabelShouldHaveCorrectText() {
        assertEquals("Source (No IP)", sourceLabel.getText());
    }

    @Test
    public void destinationHeaderLabelShouldHaveCorrectText() {
        assertEquals("Destination:", destinationHeaderLabel.getText());
    }

    @Test
    public void destinationLabelShouldHaveCorrectText() {
        assertEquals("Destination (No IP)", destinationLabel.getText());
    }

    @Test
    public void progressBarShouldGoFromZeroToHundred() {
        assertEquals(0, progressBar.getMinimum());
        assertEquals(100, progressBar.getMaximum());

        assertTrue(progressBar.isStringPainted());
    }

    @Test
    public void openShouldSetTheDialogVisible() {
        transferDialogSpy.open();

        verify(transferDialogSpy).setVisible(true);
    }

    @Test
    public void registerAsCloseableShouldSetFieldClosableAndCloseTextOnCancelButton() {
        assertFalse(transferDialog.isCloseable());
        assertEquals("Cancel", cancelButton.getText());

        transferDialog.registerAsCloseable();

        assertTrue(transferDialog.isCloseable());
        assertEquals("Close", cancelButton.getText());

        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void getFileTransferShouldReturnFileTransferFromConstructor() {
        assertSame(fileTransfer, transferDialog.getFileTransfer());
    }
}
