
/***************************************************************************
 *   Copyright 2006-2008 by Christian Ihle                                 *
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

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This is a document filter that checks for text smileys added to
 * a {@link StyledDocument}, and replaces them with images.
 *
 * @author Christian Ihle
 */
public class SmileyDocumentFilter extends DocumentFilter
{
	/** Temporary field for the smiley. */
	private final ImageIcon smiley;

	/**
	 * If this document filter is the only document filter used.
	 * This must be true if it is, or the text will not be visible.
	 * If this is not the only filter, then this must be false, or
	 * the same text will be shown several times.
	 */
	private final boolean standAlone;

	/**
	 * Constructor.
	 *
	 * @param standAlone If this is the only document filter used.
	 */
	public SmileyDocumentFilter( final boolean standAlone )
	{
		this.standAlone = standAlone;
		smiley = new ImageIcon( getClass().getResource( "/icons/smile.png" ) );
	}

	/**
	 * Checks if any text smileys are in the text, and replaces them
	 * with the corresponding image.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void insertString( final FilterBypass fb, final int offset, final String text, final AttributeSet attr )
			throws BadLocationException
	{
		if ( standAlone )
			super.insertString( fb, offset, text, attr );

		// Make a copy now, or else it could change if another message comes
		final MutableAttributeSet urlAttr = (MutableAttributeSet) attr.copyAttributes();

		// TODO
		// add support for more smileys
		// find out why text directly after (no space) icon is missing
		// only add icon attribute if not exists
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				int startPos = text.indexOf( ":)", 0 );

				if ( startPos != -1 )
				{
					StyleConstants.setIcon( urlAttr, smiley );
					StyledDocument doc = (StyledDocument) fb.getDocument();

					while ( startPos != -1 )
					{
						int stopPos = -1;

						stopPos = text.indexOf( " ", startPos );

						if ( stopPos == -1 )
							stopPos = text.indexOf( "\n", startPos );

						doc.setCharacterAttributes( offset + startPos, stopPos - startPos, urlAttr, false );
						startPos = text.indexOf( ":)", stopPos );
					}
				}
			}
		} );
	}
}
