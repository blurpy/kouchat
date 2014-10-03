
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
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.TestUtils;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;

/**
 * Test of {@link ExceptionDialog}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ExceptionDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ExceptionDialog exceptionDialog;

    private SwingMessages messages;
    private ImageLoader imageLoader;
    private UITools uiTools;

    private JLabel titleLabel;
    private JButton closeButton;
    private JLabel detailLabel;
    private JTextPaneWithoutWrap exceptionTextPane;

    @Before
    public void setUp() {
        messages = new SwingMessages();
        imageLoader = new ImageLoader(mock(ErrorHandler.class), messages, new ResourceValidator(), new ResourceLoader());

        exceptionDialog = spy(new ExceptionDialog(imageLoader, messages));

        final JPanel titlePanel = (JPanel) exceptionDialog.getContentPane().getComponent(0);
        final JPanel buttonPanel = (JPanel) exceptionDialog.getContentPane().getComponent(1);
        final JPanel infoPanel = (JPanel) exceptionDialog.getContentPane().getComponent(2);

        titleLabel = (JLabel) titlePanel.getComponent(0);
        closeButton = (JButton) buttonPanel.getComponent(0);
        detailLabel = (JLabel) infoPanel.getComponent(0);

        final JScrollPane exceptionScroll = (JScrollPane) infoPanel.getComponent(1);
        exceptionTextPane = (JTextPaneWithoutWrap) exceptionScroll.getViewport().getView();

        uiTools = TestUtils.setFieldValueWithMock(exceptionDialog, "uiTools", UITools.class);
        doAnswer(new RunArgumentAnswer()).when(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new ExceptionDialog(null, mock(SwingMessages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        new ExceptionDialog(mock(ImageLoader.class), null);
    }

    @Test
    public void windowTitleShouldBeCorrect() {
        assertEquals("Unhandled error - KouChat", exceptionDialog.getTitle());
    }

    @Test
    public void topTextShouldHaveCorrectText() {
        assertEquals("An unhandled error has occurred", titleLabel.getText());
    }

    @Test
    public void topTextShouldHaveCorrectIcon() {
        assertEquals(UIManager.getIcon("OptionPane.errorIcon"), titleLabel.getIcon());
    }

    @Test
    public void closeButtonShouldHaveCorrectText() {
        assertEquals("Close", closeButton.getText());
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

    @Test
    public void timestampShouldCreateCorrectFormat() {
        final Date date = new LocalDateTime()
                .withDate(2014, 5, 23)
                .withTime(10, 55, 12, 0)
                .toDate();

        assertEquals("23.May.2014 10:55:12", exceptionDialog.timestamp(date));
    }

    @Test
    public void uncaughtExceptionShouldShowDialog() {
        doNothing().when(exceptionDialog).showDialog();

        exceptionDialog.uncaughtException(Thread.currentThread(), new RuntimeException("Test"));

        verify(exceptionDialog).showDialog();
        verify(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void uncaughtExceptionShouldSetCaretAtBeginning() {
        doNothing().when(exceptionDialog).showDialog();

        exceptionDialog.uncaughtException(Thread.currentThread(), new RuntimeException("Test"));

        assertEquals(0, exceptionTextPane.getCaretPosition());
    }

    @Test
    public void uncaughtExceptionShouldCreateTimestampForNow() {
        doNothing().when(exceptionDialog).showDialog();

        exceptionDialog.uncaughtException(Thread.currentThread(), new RuntimeException("Test"));

        verify(exceptionDialog).timestamp(argThat(new ArgumentMatcher<Date>() {
            @Override
            public boolean matches(final Object argument) {
                final Date dateForTimestamp = (Date) argument;

                // Add some slack, to avoid failing test on slow build servers
                final Interval nowPlusMinus5Seconds = new Interval(new DateTime().minusSeconds(5),
                                                                   new DateTime().plusSeconds(5));

                return nowPlusMinus5Seconds.contains(dateForTimestamp.getTime());
            }
        }));
    }

    @Test
    public void uncaughtExceptionShouldShowDetailsAboutException() {
        doNothing().when(exceptionDialog).showDialog();
        when(exceptionDialog.timestamp(any(Date.class))).thenReturn("31.Jul.2014 13:58:14");

        final RuntimeException firstException = new RuntimeException("First");
        firstException.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("FirstClass", "secondMethod", "FirstClass.java", 12),
                new StackTraceElement("FirstClass", "firstMethod", "FirstClass.java", 10),
        });

        final RuntimeException secondException = new RuntimeException("Second", firstException);
        secondException.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("SecondClass", "secondMethod", "SecondClass.java", 12),
                new StackTraceElement("SecondClass", "firstMethod", "SecondClass.java", 10),
        });

        exceptionDialog.uncaughtException(Thread.currentThread(), secondException);

        assertEquals("31.Jul.2014 13:58:14 UncaughtException in thread: main (id 1, priority 5)\n" +
                        "java.lang.RuntimeException: Second\n" +
                        "\tat SecondClass.secondMethod(SecondClass.java:12)\n" +
                        "\tat SecondClass.firstMethod(SecondClass.java:10)\n" +
                        "Caused by: java.lang.RuntimeException: First\n" +
                        "\tat FirstClass.secondMethod(FirstClass.java:12)\n" +
                        "\tat FirstClass.firstMethod(FirstClass.java:10)\n",
                     exceptionTextPane.getText().replaceAll("\\r\\n", "\\\n")); // Replace Windows newlines
    }

    @Test
    public void uncaughtExceptionShouldPrependExceptions() {
        doNothing().when(exceptionDialog).showDialog();
        when(exceptionDialog.timestamp(any(Date.class))).thenReturn("31.Jul.2014 13:58:14",
                                                                    "31.Jul.2014 14:33:01");

        final RuntimeException firstException = new RuntimeException("First");
        firstException.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("FirstClass", "secondMethod", "FirstClass.java", 12),
                new StackTraceElement("FirstClass", "firstMethod", "FirstClass.java", 10),
        });

        exceptionDialog.uncaughtException(Thread.currentThread(), firstException);

        final RuntimeException secondException = new RuntimeException("Second");
        secondException.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("SecondClass", "secondMethod", "SecondClass.java", 12),
                new StackTraceElement("SecondClass", "firstMethod", "SecondClass.java", 10),
        });

        exceptionDialog.uncaughtException(Thread.currentThread(), secondException);

        assertEquals("31.Jul.2014 14:33:01 UncaughtException in thread: main (id 1, priority 5)\n" +
                             "java.lang.RuntimeException: Second\n" +
                             "\tat SecondClass.secondMethod(SecondClass.java:12)\n" +
                             "\tat SecondClass.firstMethod(SecondClass.java:10)\n" +
                             "\n" +
                             "31.Jul.2014 13:58:14 UncaughtException in thread: main (id 1, priority 5)\n" +
                             "java.lang.RuntimeException: First\n" +
                             "\tat FirstClass.secondMethod(FirstClass.java:12)\n" +
                             "\tat FirstClass.firstMethod(FirstClass.java:10)\n",
                     exceptionTextPane.getText().replaceAll("\\r\\n", "\\\n")); // Replace Windows newlines
    }
}
