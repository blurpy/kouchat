
/***************************************************************************
 *   Copyright 2006-2007 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouchat.ui.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Limits the number of characters a Document can contain.
 * Practical for use in the text field where users write
 * messages to send, so they know when a message is too
 * long before it is sent.
 * 
 * @author Christian Ihle
 */
public class SizeDocumentFilter extends DocumentFilter
{
	private int maxCharacters;

	/**
	 * Constructor.
	 * 
	 * @param maxCharacters The maximum number of characters the
	 * Document can contain.
	 */
	public SizeDocumentFilter( int maxCharacters )
	{
		this.maxCharacters = maxCharacters;
	}

	/**
	 * Replaces the parts of the text that fits within the character limit.
	 */
	@Override
	public void replace( FilterBypass fb, int offset, int length, String text, AttributeSet attrs ) throws BadLocationException
	{
		if ( text != null && text.length() > 0 )
		{
			if ( text.contains( "\n" ) )
				text = text.replace( '\n', ' ' );

			if ( ( fb.getDocument().getLength() + text.length() - length ) <= maxCharacters )
				super.replace( fb, offset, length, text, attrs );

			else
			{
				int allowedSize = maxCharacters - fb.getDocument().getLength();
				super.replace( fb, offset, length, text.substring( 0, allowedSize ), attrs );
			}
		}

		else
		{
			super.replace( fb, offset, length, text, attrs );
		}
	}
}
