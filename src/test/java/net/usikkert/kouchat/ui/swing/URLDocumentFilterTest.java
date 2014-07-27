
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
import org.mockito.ArgumentCaptor;

/**
 * Test of {@link URLDocumentFilter}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class URLDocumentFilterTest {

    private DefaultStyledDocument document;
    private UITools uiTools;

    @Before
    public void setUp() {
        final URLDocumentFilter filter = new URLDocumentFilter(true);

        document = new DefaultStyledDocument();
        document.setDocumentFilter(filter);

        uiTools = TestUtils.setFieldValueWithMock(filter, "uiTools", UITools.class);
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
    public void insertStringShouldDetectWwwUrlWithDifferentParameters() throws BadLocationException {
        document.insertString(0, "go to www.google.com/search#top?q=some+thing&hl=en_gb&type=1.2.3 to search\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 64, "www.google.com/search#top?q=some+thing&hl=en_gb&type=1.2.3");
        verifyText(paragraphElement.getElement(2), 64, 75, " to search\n");
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
    public void insertStringShouldOnlyDetectValidWwwUrls() throws BadLocationException {
        document.insertString(0, "go to www.kouchat www.kouchat.net www.kou www.kouchat www.kouchat.net\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(5, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 18, "go to www.kouchat ");
        verifyUrl(paragraphElement.getElement(1), 18, 33, "www.kouchat.net");
        verifyText(paragraphElement.getElement(2), 33, 54, " www.kou www.kouchat ");
        verifyUrl(paragraphElement.getElement(3), 54, 69, "www.kouchat.net");
        verifyText(paragraphElement.getElement(4), 69, 70, "\n");
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
    public void insertStringShouldDetectFtpUrlWithDifferentParameters() throws BadLocationException {
        document.insertString(0, "go to ftp.google.com/search#top?q=some+thing&hl=en_gb&type=1.2.3 to download\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 64, "ftp.google.com/search#top?q=some+thing&hl=en_gb&type=1.2.3");
        verifyText(paragraphElement.getElement(2), 64, 77, " to download\n");
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
    public void insertStringShouldOnlyDetectValidFtpUrls() throws BadLocationException {
        document.insertString(0, "go to ftp.cookie ftp.cookie.net ftp.coo ftp.cookie ftp.cookie.net\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(5, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 17, "go to ftp.cookie ");
        verifyUrl(paragraphElement.getElement(1), 17, 31, "ftp.cookie.net");
        verifyText(paragraphElement.getElement(2), 31, 51, " ftp.coo ftp.cookie ");
        verifyUrl(paragraphElement.getElement(3), 51, 65, "ftp.cookie.net");
        verifyText(paragraphElement.getElement(4), 65, 66, "\n");
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
    public void insertStringShouldDetectProtocolUrlWithDifferentParameters() throws BadLocationException {
        document.insertString(0, "go to http://google.com/search#top?q=some+thing&hl=en_gb&type=1.2.3 to search\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 67, "http://google.com/search#top?q=some+thing&hl=en_gb&type=1.2.3");
        verifyText(paragraphElement.getElement(2), 67, 78, " to search\n");
    }

    @Test
    public void insertStringShouldDetectMultipleDifferentUrlsWithWordsBetween() throws BadLocationException {
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

    @Test
    public void insertStringShouldDetectMultipleDifferentUrlsWithNoWordsBetween() throws BadLocationException {
        document.insertString(0, "go to http://cookie.net ftp://ftp.download.com http://www.upload.com\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(7, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 23, "http://cookie.net");
        verifyText(paragraphElement.getElement(2), 23, 24, " ");
        verifyUrl(paragraphElement.getElement(3), 24, 46, "ftp://ftp.download.com");
        verifyText(paragraphElement.getElement(4), 46, 47, " ");
        verifyUrl(paragraphElement.getElement(5), 47, 68, "http://www.upload.com");
        verifyText(paragraphElement.getElement(6), 68, 69, "\n");
    }

    @Test
    public void insertStringShouldOnlyDetectValidProtocolUrls() throws BadLocationException {
        document.insertString(0, "go to http://c http://cookie.net http:// http://c http://cookie.net\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(5, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 15, "go to http://c ");
        verifyUrl(paragraphElement.getElement(1), 15, 32, "http://cookie.net");
        verifyText(paragraphElement.getElement(2), 32, 50, " http:// http://c ");
        verifyUrl(paragraphElement.getElement(3), 50, 67, "http://cookie.net");
        verifyText(paragraphElement.getElement(4), 67, 68, "\n");
    }

    @Test
    public void insertStringShouldDetectMultipleDifferentUrlsAtTheSameTime() throws BadLocationException {
        document.insertString(0, "go to http://cookie.net or ftp.download.com or www.upload.com\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(7, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 23, "http://cookie.net");
        verifyText(paragraphElement.getElement(2), 23, 27, " or ");
        verifyUrl(paragraphElement.getElement(3), 27, 43, "ftp.download.com");
        verifyText(paragraphElement.getElement(4), 43, 47, " or ");
        verifyUrl(paragraphElement.getElement(5), 47, 61, "www.upload.com");
        verifyText(paragraphElement.getElement(6), 61, 62, "\n");
    }

    @Test
    public void insertStringShouldHandleEmptyString() throws BadLocationException {
        document.insertString(0, "\n", new SimpleAttributeSet());

        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(1, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 1, "\n");
    }

    @Test
    public void insertStringShouldCopyAttributesBeforeModificationToAvoidConcurrencyIssues() throws BadLocationException {
        // Don't run invokeLater() automatically in insertString()
        doNothing().when(uiTools).invokeLater(any(Runnable.class));

        final SimpleAttributeSet attributeSet = new SimpleAttributeSet();

        document.insertString(0, "go to www.kouchat.net for details\n", attributeSet);

        // Add a new attribute before running invokeLater() to simulate threads adding messages
        attributeSet.addAttribute("some", "thing");

        // Run runnable sent to invokeLater()
        final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(uiTools).invokeLater(runnableCaptor.capture());
        final Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        // Verify that there are no extra attributes added to the paragraph elements
        final Element paragraphElement = document.getParagraphElement(0);

        assertEquals(3, paragraphElement.getElementCount());

        verifyText(paragraphElement.getElement(0), 0, 6, "go to ");
        verifyUrl(paragraphElement.getElement(1), 6, 21, "www.kouchat.net");
        verifyText(paragraphElement.getElement(2), 21, 34, " for details\n");
    }

    // TODO standalone?

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
