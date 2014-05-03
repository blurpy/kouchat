
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

package net.usikkert.kouchat.ui;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link UIFactory}.
 *
 * @author Christian Ihle
 */
public class UIFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UIFactory uiFactory;

    @Before
    public void setUp() {
        uiFactory = spy(new UIFactory());

        doNothing().when(uiFactory).loadConsoleUserInterface();
        doNothing().when(uiFactory).loadSwingUserInterface();
        when(uiFactory.isHeadless()).thenReturn(false);
    }

    @Test
    public void loadUIWithConsoleAsArgumentShouldLoadConsoleUserInterface() throws UIException {
        uiFactory.loadUI(UIChoice.CONSOLE);

        verify(uiFactory).loadConsoleUserInterface();
    }

    @Test
    public void loadUIWithConsoleAsArgumentShouldLoadConsoleUserInterfaceIfHeadless() throws UIException {
        when(uiFactory.isHeadless()).thenReturn(true);

        uiFactory.loadUI(UIChoice.CONSOLE);

        verify(uiFactory).loadConsoleUserInterface();
    }

    @Test
    public void loadUIWithSwingAsArgumentShouldLoadSwingUserInterface() throws UIException {
        uiFactory.loadUI(UIChoice.SWING);

        verify(uiFactory).loadSwingUserInterface();
    }

    @Test
    public void loadUIWithSwingAsArgumentShouldThrowExceptionIfHeadless() throws UIException {
        expectedException.expect(UIException.class);
        expectedException.expectMessage("The Swing User Interface could not be loaded because a " +
                "graphical environment could not be detected.");

        when(uiFactory.isHeadless()).thenReturn(true);

        uiFactory.loadUI(UIChoice.SWING);
    }

    @Test
    public void loadUIMoreThanOnceShouldThrowException() throws UIException {
        expectedException.expect(UIException.class);
        expectedException.expectMessage("A User Interface has already been loaded.");

        uiFactory.loadUI(UIChoice.SWING);
        uiFactory.loadUI(UIChoice.SWING);
    }
}
