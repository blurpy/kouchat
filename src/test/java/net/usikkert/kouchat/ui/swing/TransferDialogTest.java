
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
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
@SuppressWarnings("HardCodedStringLiteral")
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

    @Test
    public void statusCompletedShouldSetGreenColorOnStatus() {
        final Color green = new Color(0, 176, 0);

        assertNotEquals(green, statusLabel.getForeground());

        transferDialog.statusCompleted();

        assertEquals(green, statusLabel.getForeground());
    }

    @Test
    public void statusCompletedWithReceiveShouldSetCorrectStatusTextAndEnableOpenButton() {
        when(fileTransfer.getDirection()).thenReturn(FileTransfer.Direction.RECEIVE);

        transferDialog.statusCompleted();

        assertEquals("File successfully received", statusLabel.getText());
        assertTrue(openButton.isEnabled());
    }

    @Test
    public void statusCompletedWithSendShouldSetCorrectStatusTextAndNotEnableOpenButton() {
        when(fileTransfer.getDirection()).thenReturn(FileTransfer.Direction.SEND);

        transferDialog.statusCompleted();

        assertEquals("File successfully sent", statusLabel.getText());
        assertFalse(openButton.isEnabled());
    }

    @Test
    public void statusCompletedShouldSetFieldClosableAndCloseTextOnCancelButton() {
        transferDialog.statusCompleted();

        assertTrue(transferDialog.isCloseable());
        assertEquals("Close", cancelButton.getText());

        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void statusConnectingShouldSetCorrectStatusText() {
        transferDialog.statusConnecting();

        assertEquals("Connecting...", statusLabel.getText());
        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void statusFailedShouldSetRedColorOnStatus() {
        assertNotEquals(Color.RED, statusLabel.getForeground());

        transferDialog.statusFailed();

        assertEquals(Color.RED, statusLabel.getForeground());
    }

    @Test
    public void statusFailedWithReceiveShouldSetCorrectStatusTextAndNotEnableOpenButton() {
        when(fileTransfer.getDirection()).thenReturn(FileTransfer.Direction.RECEIVE);

        transferDialog.statusFailed();

        assertEquals("Failed to receive file", statusLabel.getText());
        assertFalse(openButton.isEnabled());
    }

    @Test
    public void statusFailedWithSendShouldSetCorrectStatusTextAndNotEnableOpenButton() {
        when(fileTransfer.getDirection()).thenReturn(FileTransfer.Direction.SEND);

        transferDialog.statusFailed();

        assertEquals("Failed to send file", statusLabel.getText());
        assertFalse(openButton.isEnabled());
    }

    @Test
    public void statusFailedShouldSetFieldClosableAndCloseTextOnCancelButton() {
        transferDialog.statusFailed();

        assertTrue(transferDialog.isCloseable());
        assertEquals("Close", cancelButton.getText());

        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void statusTransferringWithReceiveShouldSetCorrectStatusText() {
        when(fileTransfer.getDirection()).thenReturn(FileTransfer.Direction.RECEIVE);

        transferDialog.statusTransferring();

        assertEquals("Receiving...", statusLabel.getText());
        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void statusTransferringWithSendShouldSetCorrectStatusText() {
        when(fileTransfer.getDirection()).thenReturn(FileTransfer.Direction.SEND);

        transferDialog.statusTransferring();

        assertEquals("Sending...", statusLabel.getText());
    }

    @Test
    public void statusWaitingShouldSetCorrectStatusTextAndCorrectFileNameAndZeroPercent() {
        when(fileTransfer.getFile()).thenReturn(new File("image.png"));
        when(fileTransfer.getFileSize()).thenReturn((long) (1024 * 1024 * 3.5)); // 3.5MB

        transferDialog.statusWaiting();

        assertEquals("Waiting...", statusLabel.getText());
        assertEquals("image.png", fileNameLabel.getText());
        assertEquals("0KB of 3.50MB at 0KB/s", transferredLabel.getText());
        assertEquals(0, progressBar.getValue());

        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void statusWaitingShouldSetToolTipOnFileNameIfLongerThanDialog() {
        when(fileTransfer.getFile()).thenReturn(new File("image.png"));
        when(uiTools.getTextWidth(anyString(), any(Graphics.class), any(Font.class))).thenReturn(500.0);

        transferDialog.statusWaiting();

        assertEquals("image.png", fileNameLabel.getToolTipText());

        // transferDialog.getGraphics() doesn't work in the verify for some reason
        verify(uiTools).getTextWidth(eq("image.png"), any(Graphics.class), eq(fileNameLabel.getFont()));
    }

    @Test
    public void statusWaitingShouldNotSetToolTipOnFileNameIfShorterThanDialog() {
        when(fileTransfer.getFile()).thenReturn(new File("image.png"));
        when(uiTools.getTextWidth(anyString(), any(Graphics.class), any(Font.class))).thenReturn(300.0);

        transferDialog.statusWaiting();

        assertNull(fileNameLabel.getToolTipText());

        verify(uiTools).getTextWidth(eq("image.png"), any(Graphics.class), eq(fileNameLabel.getFont()));
    }

    @Test
    public void statusWaitingWithReceiveShouldSetMeAsDestinationAndOpenButtonVisible() {
        final User me = new User("Me", 45678);
        me.setIpAddress("192.168.1.1");

        final User pedro = new User("Pedro", 12345);
        pedro.setIpAddress("192.168.1.2");

        when(settings.getMe()).thenReturn(me);
        when(fileTransfer.getUser()).thenReturn(pedro);
        when(fileTransfer.getFile()).thenReturn(new File("image.png"));
        when(fileTransfer.getDirection()).thenReturn(FileTransfer.Direction.RECEIVE);

        transferDialog.statusWaiting();

        assertTrue(openButton.isVisible());
        assertEquals("Pedro (192.168.1.2)", sourceLabel.getText());
        assertEquals("Me (192.168.1.1)", destinationLabel.getText());
    }

    @Test
    public void statusWaitingWithSendShouldSetMeAsSourceAndOpenButtonHidden() {
        final User me = new User("Me", 45678);
        me.setIpAddress("192.168.1.1");

        final User pedro = new User("Pedro", 12345);
        pedro.setIpAddress("192.168.1.2");

        when(settings.getMe()).thenReturn(me);
        when(fileTransfer.getUser()).thenReturn(pedro);
        when(fileTransfer.getFile()).thenReturn(new File("image.png"));
        when(fileTransfer.getDirection()).thenReturn(FileTransfer.Direction.SEND);

        transferDialog.statusWaiting();

        assertFalse(openButton.isVisible());
        assertEquals("Me (192.168.1.1)", sourceLabel.getText());
        assertEquals("Pedro (192.168.1.2)", destinationLabel.getText());
    }
}
