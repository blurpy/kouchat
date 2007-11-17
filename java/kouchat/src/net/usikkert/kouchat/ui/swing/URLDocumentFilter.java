
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

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This document filter is used to highlight urls added to a StyledDocument.
 * The current form of highlighting is underlining the url.
 * 
 * 4 different urls are recognized:<br />
 * http://url<br />
 * ftp://url<br />
 * www.url.domain<br />
 * ftp.url.domain<br />
 * 
 * @author Christian Ihle
 */
public class URLDocumentFilter extends DocumentFilter
{
	/**
	 * The url is saved as an attribute in the Document, so
	 * this attribute can be used to retrieve the url later.
	 */
	public static final String URL_ATTRIBUTE = "url.attribute";

	private Pattern httpProtPattern, ftpProtPattern, webDotPattern, ftpDotPattern;

	/**
	 * Constructor. Creates regex patterns to use for url checking.
	 */
	public URLDocumentFilter()
	{
		httpProtPattern = Pattern.compile( "http://.+" );
		ftpProtPattern = Pattern.compile( "ftp://.+" );
		webDotPattern = Pattern.compile( "www\\..+\\..+" );
		ftpDotPattern = Pattern.compile( "ftp\\..+\\..+" );
	}

	/**
	 * Inserts the text at the end of the Document, and checks if any parts
	 * of the text contains any urls. If a url is found, it is underlined
	 * and saved in an attribute.
	 */
	@Override
	public void insertString( FilterBypass fb, int offset, String text, AttributeSet attr ) throws BadLocationException
	{
		super.insertString( fb, offset, text, attr );

		int startPos = findURLPos( text, 0 );

		if ( startPos != -1 )
		{
			MutableAttributeSet urlAttr = (MutableAttributeSet) attr.copyAttributes();
			StyleConstants.setUnderline( urlAttr, true );
			StyledDocument doc = (StyledDocument) fb.getDocument();

			while ( startPos != -1 )
			{
				int stopPos = -1;

				stopPos = text.indexOf( " ", startPos );

				if ( stopPos == -1 )
					stopPos = text.indexOf( "\n", startPos );

				urlAttr.addAttribute( URL_ATTRIBUTE, text.substring( startPos, stopPos ) );
				doc.setCharacterAttributes( offset + startPos, stopPos - startPos, urlAttr, false );
				startPos = findURLPos( text, stopPos );
			}
		}
	}

	/**
	 * Returns the position the first matching
	 * url in the text, starting from the specified offset.
	 * 
	 * @param text The text to find urls in.
	 * @param offset Where in the text to begin the search.
	 * @return The position of the first character in the url, or -1
	 * if no url was found.
	 */
	private int findURLPos( String text, int offset )
	{
		int httpProt = text.indexOf( " http://", offset );
		int ftpProt = text.indexOf( " ftp://", offset );
		int wwwDot = text.indexOf( " www.", offset );
		int ftpDot = text.indexOf( " ftp.", offset );

		int firstMatch = -1;

		if ( httpProt != -1 && ( httpProt < firstMatch || firstMatch == -1 ) )
		{
			String t = text.substring( httpProt +1, text.length() -1 );

			if ( httpProtPattern.matcher( t ).matches() )
				firstMatch = httpProt +1;
		}

		if ( ftpProt != -1 && ( ftpProt < firstMatch || firstMatch == -1 ) )
		{
			String t = text.substring( ftpProt +1, text.length() -1 );

			if ( ftpProtPattern.matcher( t ).matches() )
				firstMatch = ftpProt +1;
		}

		if ( wwwDot != -1 && ( wwwDot < firstMatch || firstMatch == -1 ) )
		{
			String t = text.substring( wwwDot +1, text.length() -1 );

			if ( webDotPattern.matcher( t ).matches() )
				firstMatch = wwwDot +1;
		}

		if ( ftpDot != -1 && ( ftpDot < firstMatch || firstMatch == -1 ) )
		{
			String t = text.substring( ftpDot +1, text.length() -1 );

			if ( ftpDotPattern.matcher( t ).matches() )
				firstMatch = ftpDot +1;
		}

		return firstMatch;
	}
}
