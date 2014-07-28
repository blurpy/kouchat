
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
import net.usikkert.kouchat.misc.Settings;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link MainPanel}.
 *
 * @author Christian Ihle
 */
public class MainPanelTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorShouldThrowExceptionIfSidePanelIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Side panel can not be null");

        new MainPanel(null, mock(ImageLoader.class), mock(Settings.class), mock(Messages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new MainPanel(mock(SidePanel.class), null, mock(Settings.class), mock(Messages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new MainPanel(mock(SidePanel.class), mock(ImageLoader.class), null, mock(Messages.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Messages can not be null");

        new MainPanel(mock(SidePanel.class), mock(ImageLoader.class), mock(Settings.class), null);
    }
}
