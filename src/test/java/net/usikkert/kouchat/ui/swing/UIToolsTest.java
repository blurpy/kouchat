
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

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link UITools}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class UIToolsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UITools uiTools;

    @Before
    public void setUp() {
        uiTools = new UITools();
    }

    @Test
    public void browseShouldThrowExceptionIfUrlIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Url can not be empty");

        uiTools.browse(null, mock(Settings.class), mock(ErrorHandler.class));
    }

    @Test
    public void browseShouldThrowExceptionIfUrlIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Url can not be empty");

        uiTools.browse(" ", mock(Settings.class), mock(ErrorHandler.class));
    }

    @Test
    public void browseShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        uiTools.browse("url", null, mock(ErrorHandler.class));
    }

    @Test
    public void browseShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        uiTools.browse("url", mock(Settings.class), null);
    }
}
