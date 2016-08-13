
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

import javax.swing.JButton;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link ButtonPanel}.
 *
 * @author Christian Ihle
 */
public class ButtonPanelTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ButtonPanel buttonPanel;

    private JButton clearButton;
    private JButton awayButton;
    private JButton topicButton;
    private JButton minimizeButton;
    private Mediator mediator;

    @Before
    public void setUp() {
        buttonPanel = new ButtonPanel(new SwingMessages());

        clearButton = TestUtils.getFieldValue(buttonPanel, JButton.class, "clearB");
        awayButton = TestUtils.getFieldValue(buttonPanel, JButton.class, "awayB");
        topicButton = TestUtils.getFieldValue(buttonPanel, JButton.class, "topicB");
        minimizeButton = TestUtils.getFieldValue(buttonPanel, JButton.class, "minimizeB");

        mediator = mock(Mediator.class);
        buttonPanel.setMediator(mediator);

        final UITools uiTools = TestUtils.setFieldValueWithMock(buttonPanel, "uiTools", UITools.class);
        doAnswer(new RunArgumentAnswer()).when(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        new ButtonPanel(null);
    }

    @Test
    public void clearButtonShouldHaveCorrectText() {
        assertEquals("Clear", clearButton.getText());
        assertEquals("Clear all the text in the chat area.", clearButton.getToolTipText());
    }

    @Test
    public void awayButtonShouldHaveCorrectText() {
        assertEquals("Away", awayButton.getText());
        assertEquals("Set/unset your user as away.", awayButton.getToolTipText());
    }

    @Test
    public void topicButtonShouldHaveCorrectText() {
        assertEquals("Topic", topicButton.getText());
        assertEquals("Change the topic of this chat.", topicButton.getToolTipText());
    }

    @Test
    public void minimizeButtonShouldHaveCorrectText() {
        assertEquals("Minimize", minimizeButton.getText());
        assertEquals("Minimize to the system tray.", minimizeButton.getToolTipText());
    }

    @Test
    public void layoutShouldIncludeAllButtons() {
        final Component[] components = buttonPanel.getComponents();
        assertEquals(4, components.length);

        assertSame(clearButton, components[0]);
        assertSame(awayButton, components[1]);
        assertSame(topicButton, components[2]);
        assertSame(minimizeButton, components[3]);
    }

    @Test
    public void setMediatorShouldThrowExceptionIfMediatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Mediator can not be null");

        buttonPanel.setMediator(null);
    }

    @Test
    public void setAwayStateShouldDisableTopicButtonWhenAway() {
        assertTrue(topicButton.isEnabled());

        buttonPanel.setAwayState(true);
        assertFalse(topicButton.isEnabled());

        buttonPanel.setAwayState(false);
        assertTrue(topicButton.isEnabled());
    }

    @Test
    public void clickOnMinimizeShouldMinimize() {
        minimizeButton.doClick();

        verify(mediator).minimize();
    }

    @Test
    public void clickOnClearShouldClearChat() {
        clearButton.doClick();

        verify(mediator).clearChat();
    }

    @Test
    public void clickOnAwayShouldSetAway() {
        awayButton.doClick();

        verify(mediator).setAway();
    }

    @Test
    public void clickOnTopicShouldSetTopic() {
        topicButton.doClick();

        verify(mediator).setTopic();
    }
}
