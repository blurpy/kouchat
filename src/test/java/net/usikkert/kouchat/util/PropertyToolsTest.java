
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

package net.usikkert.kouchat.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.jetbrains.annotations.NonNls;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link PropertyTools}.
 *
 * @author Christian Ihle
 */
public class PropertyToolsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PropertyTools propertyTools;

    private IOTools ioTools;

    @Before
    public void setUp() {
        propertyTools = new PropertyTools();

        ioTools = TestUtils.setFieldValueWithMock(propertyTools, "ioTools", IOTools.class);
    }

    @Test
    public void loadPropertiesShouldThrowExceptionIfFilePathIsNull() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File path can not be empty");

        propertyTools.loadProperties(null);
    }

    @Test
    public void loadPropertiesShouldThrowExceptionIfFilePathIsEmpty() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File path can not be empty");

        propertyTools.loadProperties(" ");
    }

    @Test
    public void loadPropertiesShouldThrowExceptionIfFileNotFound() throws IOException {
        expectedException.expect(FileNotFoundException.class);
        expectedException.expectMessage("unknown.properties (No such file or directory)");

        propertyTools.loadProperties("unknown.properties");
    }

    @Test
    public void loadPropertiesShouldSuccessfullyLoadAllPropertiesInFileFromFullFileSystemPath() throws IOException {
        final File filePath = getPathTo("test-messages.properties");

        final Properties properties = propertyTools.loadProperties(filePath.getAbsolutePath());

        assertEquals(3, properties.size());

        assertEquals("This is the first string", properties.getProperty("test.string1"));
        assertEquals("This is the second string", properties.getProperty("test.string2"));
        assertEquals("Say hello to {0} from {1}!", properties.getProperty("test.hello"));
    }

    @Test
    public void loadPropertiesShouldCloseInputStreamWhenDoneLoading() throws IOException {
        final File filePath = getPathTo("test-messages.properties");

        propertyTools.loadProperties(filePath.getAbsolutePath());

        verify(ioTools).close(any(InputStream.class));
    }

    @Test
    public void loadPropertiesShouldCloseInputStreamEvenOnException() {
        try {
            propertyTools.loadProperties("nothing");
            fail("Should fail to load properties");
        }

        catch (final IOException e) {
            verify(ioTools).close(any(InputStream.class));
        }
    }

    private File getPathTo(@NonNls final String fileName) {
        final URL classpathUrl = getClass().getResource("/" + fileName);

        return new File(classpathUrl.getPath());
    }
}
