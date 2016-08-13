
/***************************************************************************
 *   Copyright 2006-2016 by Christian Ihle                                 *
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

import java.awt.Component;
import java.awt.ComponentOrientation;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.border.Border;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link UserListCellRenderer}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class UserListCellRendererTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserListCellRenderer cellRenderer;

    private JList jList;
    private User user;

    private ImageIcon envelope;
    private ImageIcon dot;

    @Before
    public void setUp() {
        envelope = mock(ImageIcon.class);
        dot = mock(ImageIcon.class);

        final ImageLoader imageLoader = mock(ImageLoader.class);
        when(imageLoader.getEnvelopeIcon()).thenReturn(envelope);
        when(imageLoader.getDotIcon()).thenReturn(dot);

        cellRenderer = new UserListCellRenderer(imageLoader, new SwingMessages());

        jList = new JList();
        user = new User("Test", 123);
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new UserListCellRenderer(null, mock(SwingMessages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        new UserListCellRenderer(mock(ImageLoader.class), null);
    }

    @Test
    public void getListCellRendererComponentShouldSetTextAndToolTipToNickNameByDefault() {
        cellRenderer.getListCellRendererComponent(jList, user, 0, true, true);

        assertEquals("Test", cellRenderer.getText());
        assertEquals("Test", cellRenderer.getToolTipText());
    }

    @Test
    public void getListCellRendererComponentShouldAddWritingDetailsToTextAndToolTipWhenWriting() {
        user.setWriting(true);

        cellRenderer.getListCellRendererComponent(jList, user, 0, true, true);

        assertEquals("Test *", cellRenderer.getText());
        assertEquals("Test is writing", cellRenderer.getToolTipText());
    }

    @Test
    public void getListCellRendererComponentShouldAddAwayDetailsToToolTipWhenAway() {
        user.setAway(true);

        cellRenderer.getListCellRendererComponent(jList, user, 0, true, true);

        assertEquals("Test", cellRenderer.getText());
        assertEquals("Test is away", cellRenderer.getToolTipText());
    }

    @Test
    public void getListCellRendererComponentShouldUsePlainFontWhenNotMe() {
        cellRenderer.getListCellRendererComponent(jList, user, 0, true, true);

        assertTrue(cellRenderer.getFont().isPlain());
        assertFalse(cellRenderer.getFont().isBold());
    }

    @Test
    public void getListCellRendererComponentShouldUseBoldFontWhenMe() {
        user.setMe(true);

        cellRenderer.getListCellRendererComponent(jList, user, 0, true, true);

        assertFalse(cellRenderer.getFont().isPlain());
        assertTrue(cellRenderer.getFont().isBold());
    }

    @Test
    public void getListCellRendererComponentShouldSetDotIconWhenNoPrivateMessages() {
        cellRenderer.getListCellRendererComponent(jList, user, 0, true, true);

        assertSame(dot, cellRenderer.getIcon());
    }

    @Test
    public void getListCellRendererComponentShouldSetEnvelopeIconWhenNewPrivateMessages() {
        user.setNewPrivMsg(true);

        cellRenderer.getListCellRendererComponent(jList, user, 0, true, true);

        assertSame(envelope, cellRenderer.getIcon());
    }

    @Test
    public void getListCellRendererComponentShouldHandleMissingUser() {
        user.setNewPrivMsg(true);

        cellRenderer.getListCellRendererComponent(jList, null, 0, true, true);

        assertEquals("", cellRenderer.getText());
    }

    @Test
    public void getListCellRendererComponentShouldSetCorrectColorsAndBorderWhenSelected() {
        cellRenderer.getListCellRendererComponent(jList, null, 0, true, true);

        assertEquals(jList.getSelectionForeground(), cellRenderer.getForeground());
        assertEquals(jList.getSelectionBackground(), cellRenderer.getBackground());
        assertEquals(TestUtils.getFieldValue(cellRenderer, Border.class, "selectedBorder"), cellRenderer.getBorder());
    }

    @Test
    public void getListCellRendererComponentShouldSetCorrectColorsAndBorderWhenUnselected() {
        cellRenderer.getListCellRendererComponent(jList, null, 0, false, true);

        assertEquals(jList.getForeground(), cellRenderer.getForeground());
        assertEquals(jList.getBackground(), cellRenderer.getBackground());
        assertEquals(TestUtils.getFieldValue(cellRenderer, Border.class, "normalBorder"), cellRenderer.getBorder());
    }

    @Test
    public void getListCellRendererComponentShouldSetEnabledAndOrientationFromJList() {
        cellRenderer.getListCellRendererComponent(jList, null, 0, false, true);

        assertTrue(cellRenderer.isEnabled());
        assertEquals(ComponentOrientation.UNKNOWN, cellRenderer.getComponentOrientation());

        jList.setEnabled(false);
        jList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        cellRenderer.getListCellRendererComponent(jList, null, 0, false, true);

        assertFalse(cellRenderer.isEnabled());
        assertEquals(ComponentOrientation.RIGHT_TO_LEFT, cellRenderer.getComponentOrientation());
    }

    @Test
    public void getListCellRendererComponentShouldReturnSelf() {
        final Component rendererComponent = cellRenderer.getListCellRendererComponent(jList, null, 0, false, true);

        assertSame(cellRenderer, rendererComponent);
    }
}
