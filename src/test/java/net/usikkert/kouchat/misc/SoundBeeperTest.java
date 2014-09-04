
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

package net.usikkert.kouchat.misc;

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.ResourceLoader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link SoundBeeper}.
 *
 * @author Christian Ihle
 */
public class SoundBeeperTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SoundBeeper soundBeeper;

    private ErrorHandler errorHandler;
    private ResourceLoader resourceLoader;
    private Settings settings;

    @Before
    public void setUp() {
        settings = mock(Settings.class);
        resourceLoader = mock(ResourceLoader.class);
        errorHandler = mock(ErrorHandler.class);

        soundBeeper = new SoundBeeper(settings, resourceLoader, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new SoundBeeper(null, resourceLoader, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfResourceLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Resource loader can not be null");

        new SoundBeeper(settings, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new SoundBeeper(settings, resourceLoader, null);
    }
}
