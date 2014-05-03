
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

import net.usikkert.kouchat.argument.ArgumentParser;

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

    @Test
    public void constructorShouldThrowExceptionIfArgumentParserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Argument parser can not be null");

        new UIFactory(null);
    }

    @Test
    public void loadUIWithConsoleAsArgumentShouldLoadConsoleUserInterface() throws UIException {
        final UIFactory uiFactory = createFactoryWithArguments("--console");

        uiFactory.loadUI();

        verify(uiFactory).loadConsoleUserInterface();
    }

    @Test
    public void loadUIWithConsoleAsArgumentShouldLoadConsoleUserInterfaceIfHeadless() throws UIException {
        final UIFactory uiFactory = createFactoryWithArguments("--console");
        when(uiFactory.isHeadless()).thenReturn(true);

        uiFactory.loadUI();

        verify(uiFactory).loadConsoleUserInterface();
    }

    @Test
    public void loadUIWithNoArgumentShouldLoadSwingUserInterface() throws UIException {
        final UIFactory uiFactory = createFactoryWithArguments();

        uiFactory.loadUI();

        verify(uiFactory).loadSwingUserInterface();
    }

    @Test
    public void loadUIWithNoArgumentShouldThrowExceptionIfHeadless() throws UIException {
        expectedException.expect(UIException.class);
        expectedException.expectMessage("Error: The Swing User Interface could not be loaded because a " +
                "graphical environment could not be detected.");

        final UIFactory uiFactory = createFactoryWithArguments();
        when(uiFactory.isHeadless()).thenReturn(true);

        uiFactory.loadUI();
    }

    @Test
    public void loadUIWithOtherArgumentsShouldLoadSwingUserInterface() throws UIException {
        final UIFactory uiFactory = createFactoryWithArguments("--debug", "--always-log");

        uiFactory.loadUI();

        verify(uiFactory).loadSwingUserInterface();
    }

    @Test
    public void loadUIMoreThanOnceShouldThrowException() throws UIException {
        expectedException.expect(UIException.class);
        expectedException.expectMessage("Error: A User Interface has already been loaded.");

        final UIFactory uiFactory = createFactoryWithArguments();

        uiFactory.loadUI();
        uiFactory.loadUI();
    }

    private UIFactory createFactoryWithArguments(final String... arguments) {
        final ArgumentParser argumentParser = new ArgumentParser(arguments);
        final UIFactory uiFactory = spy(new UIFactory(argumentParser));

        doNothing().when(uiFactory).loadConsoleUserInterface();
        doNothing().when(uiFactory).loadSwingUserInterface();
        when(uiFactory.isHeadless()).thenReturn(false);

        return uiFactory;
    }
}
