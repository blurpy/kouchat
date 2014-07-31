
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

import java.awt.Image;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.message.PropertyFileMessages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link ExceptionDialog}.
 *
 * @author Christian Ihle
 */
public class ExceptionDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ExceptionDialog exceptionDialog;

    private PropertyFileMessages messages;
    private ImageLoader imageLoader;

    private JLabel titleLabel;
    private JButton closebutton;
    private JLabel detailLabel;
    private JTextPaneWithoutWrap exceptionTextPane;

    @Before
    public void setUp() {
        messages = new PropertyFileMessages("messages.swing");
        imageLoader = new ImageLoader(mock(ErrorHandler.class), messages, new ResourceValidator(), new ResourceLoader());

        exceptionDialog = spy(new ExceptionDialog(imageLoader, messages));

        final JPanel titlePanel = (JPanel) exceptionDialog.getContentPane().getComponent(0);
        final JPanel buttonPanel = (JPanel) exceptionDialog.getContentPane().getComponent(1);
        final JPanel infoPanel = (JPanel) exceptionDialog.getContentPane().getComponent(2);

        titleLabel = (JLabel) titlePanel.getComponent(0);
        closebutton = (JButton) buttonPanel.getComponent(0);
        detailLabel = (JLabel) infoPanel.getComponent(0);

        final JScrollPane exceptionScroll = (JScrollPane) infoPanel.getComponent(1);
        exceptionTextPane = (JTextPaneWithoutWrap) exceptionScroll.getViewport().getView();
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new ExceptionDialog(null, mock(Messages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Messages can not be null");

        new ExceptionDialog(mock(ImageLoader.class), null);
    }

    @Test
    public void windowTitleShouldBeCorrect() {
        assertEquals("Unhandled error - KouChat", exceptionDialog.getTitle());
    }

    @Test
    public void topTextShouldHaveCorrectText() {
        assertEquals(" An unhandled error has occurred", titleLabel.getText());
    }

    @Test
    public void topTextShouldHaveCorrectIcon() {
        assertEquals(UIManager.getIcon("OptionPane.errorIcon"), titleLabel.getIcon());
    }

    @Test
    public void closeButtonShouldHaveCorrectText() {
        assertEquals("Close", closebutton.getText());
    }

    @Test
    public void closeButtonShouldDisposeOnClick() {
        final boolean[] disposed = {false};

        final ExceptionDialog exceptionDialog1 = new ExceptionDialog(imageLoader, messages) {
            @Override
            public void dispose() {
                disposed[0] = true;
            }
        };

        final JPanel buttonPanel = (JPanel) exceptionDialog1.getContentPane().getComponent(1);
        final JButton closeButton1 = (JButton) buttonPanel.getComponent(0);

        closeButton1.doClick();

        assertTrue(disposed[0]);
    }

    @Test
    public void detailMessageShouldHaveCorrectText() {
        assertEquals("<html>" + Constants.APP_NAME + " has experienced an unhandled error, " +
                "and may be in an inconsistent state. It's advised to restart the application " +
                "to make sure everything works as expected. Bugs can be reported at " +
                Constants.APP_WEB + ". Please describe what you did when " +
                "this error happened, and add the stack trace below to the report.</html>", detailLabel.getText());
    }

    @Test
    public void dialogShouldBeCorrectSize() {
        assertEquals(630, exceptionDialog.getWidth());
        assertEquals(450, exceptionDialog.getHeight());
    }

    @Test
    public void dialogShouldDisposeOnClose() {
        assertEquals(WindowConstants.DISPOSE_ON_CLOSE, exceptionDialog.getDefaultCloseOperation());
    }

    @Test
    public void dialogShouldHaveCorrectIcon() {
        final List<Image> iconImages = exceptionDialog.getIconImages();
        assertEquals(1, iconImages.size());

        final Image icon = iconImages.get(0);

        assertSame(imageLoader.getKouNormal32Icon().getImage(), icon);
    }

    @Test
    public void exceptionTextPaneShouldRegisterCopyPopup() {
        final JPopupMenu componentPopupMenu = exceptionTextPane.getComponentPopupMenu();

        assertEquals(CopyPopup.class, componentPopupMenu.getClass());
    }

    @Test
    public void exceptionTextPaneShouldNotBeEditable() {
        assertFalse(exceptionTextPane.isEditable());
    }

    @Test
    public void showDialogShouldSetLocationAndSetVisible() {
        doNothing().when(exceptionDialog).setVisible(anyBoolean());

        exceptionDialog.showDialog();

        verify(exceptionDialog).setLocationRelativeTo(exceptionDialog.getParent());
        verify(exceptionDialog).setVisible(true);
    }
}
