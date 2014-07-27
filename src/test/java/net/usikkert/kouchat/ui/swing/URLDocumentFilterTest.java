
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
import org.junit.Ignore;
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
    @Ignore("Not implemented")
    public void insertStringShouldDetectWwwUrlAtTheBeginning() throws BadLocationException {
        document.insertString(0, "www.kouchat.net is the place to be\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(2, paragraphElement.getElementCount());

        verifyUrl(paragraphElement.getElement(0), 0, 15, "www.kouchat.net");
        verifyText(paragraphElement.getElement(1), 15, 35, " is the place to be\n");
    }

    @Test
    public void insertStringShouldDetectWwwUrlInTheMiddle() throws BadLocationException {
        document.insertString(0, "go to www.kouchat.net for details\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 21, "www.kouchat.net");
        verifyText(paragraphElement.getElement(2), 21, 34, " for details\n");
    }

    @Test
    public void insertStringShouldDetectWwwUrlAtTheEnd() throws BadLocationException {
        document.insertString(0, "go to www.kouchat.net\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 21, "www.kouchat.net");
        verifyText(paragraphElement.getElement(2), 21, 22, "\n");
    }

    @Test
    public void insertStringShouldDetectMultipleWwwUrls() throws BadLocationException {
        document.insertString(0, "go to www.kouchat.net or www.google.com or www.cnn.com\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(7, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 21, "www.kouchat.net");
        verifyText(paragraphElement.getElement(2), 21, 25, " or ");
        verifyUrl(paragraphElement.getElement(3), 25, 39, "www.google.com");
        verifyText(paragraphElement.getElement(4), 39, 43, " or ");
        verifyUrl(paragraphElement.getElement(5), 43, 54, "www.cnn.com");
        verifyText(paragraphElement.getElement(6), 54, 55, "\n");
    }

    @Test
    @Ignore("Not implemented")
    public void insertStringShouldDetectFtpUrlAtTheBeginning() throws BadLocationException {
        document.insertString(0, "ftp.download.com has good stuff\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(2, paragraphElement.getElementCount());

        verifyUrl(paragraphElement.getElement(0), 0, 16, "ftp.download.com");
        verifyText(paragraphElement.getElement(1), 16, 32, " has good stuff\n");
    }

    @Test
    public void insertStringShouldDetectFtpUrlInTheMiddle() throws BadLocationException {
        document.insertString(0, "go to ftp.download.com for details\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 22, "ftp.download.com");
        verifyText(paragraphElement.getElement(2), 22, 35, " for details\n");
    }

    @Test
    public void insertStringShouldDetectFtpUrlAtTheEnd() throws BadLocationException {
        document.insertString(0, "go to ftp.download.com\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 22, "ftp.download.com");
        verifyText(paragraphElement.getElement(2), 22, 23, "\n");
    }

    @Test
    public void insertStringShouldDetectMultipleFtpUrls() throws BadLocationException {
        document.insertString(0, "go to ftp.cookie.net or ftp.download.com or ftp.upload.com\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(7, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 20, "ftp.cookie.net");
        verifyText(paragraphElement.getElement(2), 20, 24, " or ");
        verifyUrl(paragraphElement.getElement(3), 24, 40, "ftp.download.com");
        verifyText(paragraphElement.getElement(4), 40, 44, " or ");
        verifyUrl(paragraphElement.getElement(5), 44, 58, "ftp.upload.com");
        verifyText(paragraphElement.getElement(6), 58, 59, "\n");
    }

    @Test
    public void insertStringShouldDetectProtocolUrlAtTheBeginning() throws BadLocationException {
        document.insertString(0, "http://google.com can search\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(2, paragraphElement.getElementCount());

        verifyUrl(paragraphElement.getElement(0), 0, 17, "http://google.com");
        verifyText(paragraphElement.getElement(1), 17, 29, " can search\n");
    }

    @Test
    public void insertStringShouldDetectProtocolUrlInTheMiddle() throws BadLocationException {
        document.insertString(0, "go to http://google.com for details\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 23, "http://google.com");
        verifyText(paragraphElement.getElement(2), 23, 36, " for details\n");
    }

    @Test
    public void insertStringShouldDetectProtocolUrlAtTheEnd() throws BadLocationException {
        document.insertString(0, "go to http://google.com\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 23, "http://google.com");
        verifyText(paragraphElement.getElement(2), 23, 24, "\n");
    }

    @Test
    public void insertStringShouldDetectMultipleProtocolUrls() throws BadLocationException {
        document.insertString(0, "go to http://cookie.net or ftp://ftp.download.com or http://www.upload.com\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(7, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 23, "http://cookie.net");
        verifyText(paragraphElement.getElement(2), 23, 27, " or ");
        verifyUrl(paragraphElement.getElement(3), 27, 49, "ftp://ftp.download.com");
        verifyText(paragraphElement.getElement(4), 49, 53, " or ");
        verifyUrl(paragraphElement.getElement(5), 53, 74, "http://www.upload.com");
        verifyText(paragraphElement.getElement(6), 74, 75, "\n");
    }

    // TODO long url with different characters
    // TODO multiple urls of different type on same line
    // TODO standalone?
    // TODO copy attributes?
    // TODO failed regex match

    private void verifyUrl(final Element element, final int expectedStartPosition, final int expectedEndPosition,
                           final String expectedUrl) throws BadLocationException {
        verifyPositionAndText(element, expectedStartPosition, expectedEndPosition, expectedUrl);

        assertEquals(2, element.getAttributes().getAttributeCount());
        assertEquals(expectedUrl, element.getAttributes().getAttribute(URLDocumentFilter.URL_ATTRIBUTE));
        assertTrue((Boolean) element.getAttributes().getAttribute(StyleConstants.Underline));
    }

    private void verifyText(final Element element, final int expectedStartPosition, final int expectedEndPosition,
                            final String expectedText) throws BadLocationException {
        verifyPositionAndText(element, expectedStartPosition, expectedEndPosition, expectedText);

        assertEquals(0, element.getAttributes().getAttributeCount());
    }

    private void verifyPositionAndText(final Element element, final int expectedStartPosition,
                                       final int expectedEndPosition, final String expectedText) throws BadLocationException {
        assertEquals(expectedStartPosition, element.getStartOffset());
        assertEquals(expectedEndPosition, element.getEndOffset());

        final int expectedTextLength = expectedEndPosition - expectedStartPosition;
        assertEquals(expectedText, element.getDocument().getText(expectedStartPosition, expectedTextLength));
    }
}
