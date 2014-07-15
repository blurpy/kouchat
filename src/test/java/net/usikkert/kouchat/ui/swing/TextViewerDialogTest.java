
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
import net.usikkert.kouchat.util.ResourceLoader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link TextViewerDialog}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class TextViewerDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ImageLoader imageLoader;
    private ResourceLoader resourceLoader;
    private Settings settings;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        imageLoader = mock(ImageLoader.class);
        resourceLoader = mock(ResourceLoader.class);
        settings = mock(Settings.class);
        errorHandler = mock(ErrorHandler.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfTextFileIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Text file can not be empty");

        new TextViewerDialog(null, "title", false, imageLoader, resourceLoader, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfTextFileIsBlank() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Text file can not be empty");

        new TextViewerDialog(" ", "title", false, imageLoader, resourceLoader, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfTitleIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Title can not be empty");

        new TextViewerDialog("file", null, false, imageLoader, resourceLoader, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfTitleIsBlank() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Title can not be empty");

        new TextViewerDialog("file", " ", false, imageLoader, resourceLoader, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfImageLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image loader can not be null");

        new TextViewerDialog("file", "title", false, null, resourceLoader, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfResourceLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Resource loader can not be null");

        new TextViewerDialog("file", "title", false, imageLoader, null, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new TextViewerDialog("file", "title", false, imageLoader, resourceLoader, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new TextViewerDialog("file", "title", false, imageLoader, resourceLoader, settings, null);
    }
}
