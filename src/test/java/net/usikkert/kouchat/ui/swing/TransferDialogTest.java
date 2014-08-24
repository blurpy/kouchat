
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
import javax.swing.JPanel;
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

    @Before
    public void setUp() {
        final ImageLoader imageLoader =
                new ImageLoader(mock(ErrorHandler.class), mock(Messages.class), new ResourceValidator(), new ResourceLoader());

        mediator = mock(Mediator.class);
        settings = mock(Settings.class);
        fileTransfer = mock(FileTransfer.class);

        transferDialog = new TransferDialog(mediator, fileTransfer, imageLoader, settings);
        uiTools = TestUtils.setFieldValueWithMock(transferDialog, "uiTools", UITools.class);

        statusIcons = new StatusIcons(imageLoader);

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
    public void openShouldSetTheDialogVisible() {
        transferDialogSpy.open();

        verify(transferDialogSpy).setVisible(true);
    }
}
