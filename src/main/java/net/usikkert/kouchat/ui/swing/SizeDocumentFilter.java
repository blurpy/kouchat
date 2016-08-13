
/***************************************************************************
 *   Copyright 2006-2016 by Christian Ihle                                 *
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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import net.usikkert.kouchat.util.Tools;

/**
 * Limits the number of bytes a Document can contain.
 * Practical for use in the text field where users write
 * messages to send, so they know when a message is too
 * long before it is sent.
 *
 * @author Christian Ihle
 */
public class SizeDocumentFilter extends DocumentFilter {

    private final int maxBytes;

    /**
     * Constructor.
     *
     * @param maxBytes The maximum number of bytes the
     * Document can contain.
     */
    public SizeDocumentFilter(final int maxBytes) {
        this.maxBytes = maxBytes;
    }

    /**
     * Replaces the parts of the text that fits within the byte limit.
     * This got a bit more complicated than I thought, because of the varying
     * size of characters in different character sets.
     *
     * Also removes tabs and newlines.
     *
     * {@inheritDoc}
     */
    @Override
    public void replace(final FilterBypass fb, final int offset, final int length,
            final String text, final AttributeSet attrs) throws BadLocationException {
        if (text != null && text.length() > 0) {
            String newText = text;

            // Replace newlines with space
            if (newText.contains("\n")) {
                newText = newText.replace('\n', ' ');
            }

            // Replace tabs with space
            if (newText.contains("\t")) {
                newText = newText.replace('\t', ' ');
            }

            final String docText = fb.getDocument().getText(0, fb.getDocument().getLength());
            final int textLength = Tools.getBytes(newText);
            final int docLength = Tools.getBytes(docText);
            final int removedLength = Tools.getBytes(docText.substring(offset, offset + length));

            // Everything OK, insert the text as it is.
            if ((docLength + textLength - removedLength) <= maxBytes) {
                super.replace(fb, offset, length, newText, attrs);
            }

            // Text too big to fit. Will need to find out which
            // characters that can be inserted without going over the limit.
            else {
                String replaceText = "";
                int replaceTextSize = 0;
                final int allowedSize = maxBytes - docLength;

                for (int i = 0; i < newText.length(); i++) {
                    if (replaceTextSize < allowedSize) {
                        final String tmpChar = "" + newText.charAt(i);
                        final int tmpCharSize = Tools.getBytes(tmpChar);

                        if (replaceTextSize + tmpCharSize <= allowedSize) {
                            replaceText += tmpChar;
                            replaceTextSize += tmpCharSize;
                        }

                        else {
                            break;
                        }
                    }

                    else {
                        break;
                    }
                }

                super.replace(fb, offset, length, replaceText, attrs);
            }
        }

        // Empty text, just continue normally.
        else {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
