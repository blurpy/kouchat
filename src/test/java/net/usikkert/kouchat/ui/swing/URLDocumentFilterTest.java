
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

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link URLDocumentFilter}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class URLDocumentFilterTest {

    private DefaultStyledDocument document;

    @Before
    public void setUp() {
        final URLDocumentFilter filter = new URLDocumentFilter(true);

        document = new DefaultStyledDocument();
        document.setDocumentFilter(filter);

        final UITools uiTools = TestUtils.setFieldValueWithMock(filter, "uiTools", UITools.class);
        doAnswer(new RunArgumentAnswer()).when(uiTools).invokeLater(any(Runnable.class));
    }

    @Test
    public void insertStringShouldDetectWwwUrlInTheMiddle() throws BadLocationException {
        document.insertString(0, "go to www.kouchat.net for details", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 21, "www.kouchat.net");
        verifyText(paragraphElement.getElement(2), 21, 34, " for details"); // 33 characters, but "\n" gets appended
    }

    @Test
    public void insertStringShouldDetectFtpUrlInTheMiddle() throws BadLocationException {
        document.insertString(0, "go to ftp.download.com for details", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 22, "ftp.download.com");
        verifyText(paragraphElement.getElement(2), 22, 35, " for details");
    }

    @Test
    public void insertStringShouldDetectProtocolUrlInTheMiddle() throws BadLocationException {
        document.insertString(0, "go to http://google.com for details", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 23, "http://google.com");
        verifyText(paragraphElement.getElement(2), 23, 36, " for details");
    }

    private void verifyUrl(final Element element, final int expectedStartPosition, final int expectedEndPosition,
                           final String expectedUrl) throws BadLocationException {
        verifyPositionAndText(element, expectedStartPosition, expectedEndPosition, expectedUrl);

        assertEquals(2, element.getAttributes().getAttributeCount());
        assertEquals(expectedUrl, element.getAttributes().getAttribute(URLDocumentFilter.URL_ATTRIBUTE));
        assertTrue((Boolean) element.getAttributes().getAttribute(StyleConstants.Underline));
    }

    private void verifyText(final Element element, final int expectedStartPosition, final int expectedEndPosition,
                            final String expectedString) throws BadLocationException {
        verifyPositionAndText(element, expectedStartPosition, expectedEndPosition, expectedString);

        assertEquals(0, element.getAttributes().getAttributeCount());
    }

    private void verifyPositionAndText(final Element element, final int expectedStartPosition,
                                       final int expectedEndPosition, final String expectedUrl) throws BadLocationException {
        assertEquals(expectedStartPosition, element.getStartOffset());
        assertEquals(expectedEndPosition, element.getEndOffset());

        assertEquals(expectedUrl, element.getDocument().getText(expectedStartPosition, expectedUrl.length()));
    }
}
