
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

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;

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

    @Before
    public void setUp() {
        final ImageLoader imageLoader =
                new ImageLoader(mock(ErrorHandler.class), mock(Messages.class), new ResourceValidator(), new ResourceLoader());

        transferDialog = spy(new TransferDialog(mock(Mediator.class), mock(FileTransfer.class), imageLoader, mock(Settings.class)));

        doNothing().when(transferDialog).setVisible(anyBoolean());
    }

    @Test
    public void constructorShouldThrowExceptionIfMediatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Mediator can not be null");

        new TransferDialog(null, mock(FileTransfer.class), mock(ImageLoader.class), mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfFileTransferIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File transfer can not be null");

        new TransferDialog(mock(Mediator.class), null, mock(ImageLoader.class), mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new TransferDialog(mock(Mediator.class), mock(FileTransfer.class), null, mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new TransferDialog(mock(Mediator.class), mock(FileTransfer.class), mock(ImageLoader.class), null);
    }

    @Test
    public void openShouldSetTheDialogVisible() {
        transferDialog.open();

        verify(transferDialog).setVisible(true);
    }
}
