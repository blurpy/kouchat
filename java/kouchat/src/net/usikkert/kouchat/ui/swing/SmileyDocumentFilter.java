
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

import net.usikkert.kouchat.util.Validate;

/**
 * This is a document filter that checks for text smiley codes added to
 * a {@link StyledDocument}, and replaces them with images.
 *
 * @author Christian Ihle
 */
public class SmileyDocumentFilter extends DocumentFilter
{
	/**
	 * If this document filter is the only document filter used.
	 * This must be true if it is, or the text will not be visible.
	 * If this is not the only filter, then this must be false, or
	 * the same text will be shown several times.
	 */
	private final boolean standAlone;

	/** The available smileys. */
	private final SmileyLoader smileyLoader;

	/**
	 * Constructor.
	 *
	 * @param standAlone If this is the only document filter used.
	 */
	public SmileyDocumentFilter( final boolean standAlone )
	{
		this.standAlone = standAlone;
		smileyLoader = new SmileyLoader();
	}

	/**
	 * Checks if any text smiley codes are in the text, and replaces them
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
		final MutableAttributeSet smileyAttr = (MutableAttributeSet) attr.copyAttributes();

		// Do this in the background so the text wont lag
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				Smiley smiley = findSmiley( text, 0 );

				if ( smiley != null )
				{
					StyledDocument doc = (StyledDocument) fb.getDocument();

					while ( smiley != null )
					{
						int stopPos = smiley.getStopPosition();
						int startPos = smiley.getStartPosition();

						if ( !smileyAttr.containsAttribute( StyleConstants.IconAttribute, smiley.getIcon() ) )
							StyleConstants.setIcon( smileyAttr, smiley.getIcon() );

						doc.setCharacterAttributes( offset + startPos, stopPos - startPos, smileyAttr, false );
						smiley = findSmiley( text, stopPos );
					}
				}
			}
		} );
	}

	/**
	 * Returns the first matching smiley in the text, starting from the specified offset.
	 *
	 * @param text The text to find smileys in.
	 * @param offset Where in the text to begin the search.
	 * @return The first matching smiley in the text, or <code>null</code> if
	 *         none were found.
	 */
	private Smiley findSmiley( final String text, final int offset )
	{
		int firstMatch = -1;
		Smiley smiley = null;

		for ( String smileyText : smileyLoader.getTextSmileys() )
		{
			int smileyPos = text.indexOf( smileyText, offset );

			if ( smileyPos != -1 && ( smileyPos < firstMatch || firstMatch == -1 ) )
			{
				smiley = new Smiley( smileyPos, smileyLoader.getSmiley( smileyText ), smileyText );
				firstMatch = smileyPos;
			}
		}

		return smiley;
	}

	/**
	 * This class represents a smiley in the text with position,
	 * the text code, and the icon for the smiley.
	 *
	 * @author Christian Ihle
	 */
	private class Smiley
	{
		/** The position of the first character in the smiley. */
		private final int startPosition;

		/** The position of the last character in the smiley. */
		private final int stopPosition;

		/** The icon replacing the text smiley code. */
		private final ImageIcon icon;

		/** The text smiley code. */
		private final String code;

		/**
		 * Constructor.
		 *
		 * @param startPosition The position of the first character in the smiley.
		 * @param icon The icon replacing the text smiley code.
		 * @param code The text smiley code.
		 */
		public Smiley( final int startPosition, final ImageIcon icon, final String code )
		{
			Validate.notNull( icon, "Icon can not be null" );
			Validate.notEmpty( code, "Code can not be empty" );

			this.startPosition = startPosition;
			this.icon = icon;
			this.code = code;

			stopPosition = startPosition + code.length();
		}

		/**
		 * Gets the position of the first character in the smiley.
		 *
		 * @return The position of the first character in the smiley.
		 */
		public int getStartPosition()
		{
			return startPosition;
		}

		/**
		 * Gets the position of the last character in the smiley.
		 *
		 * @return The position of the last character in the smiley.
		 */
		public int getStopPosition()
		{
			return stopPosition;
		}

		/**
		 * Gets the icon replacing the text smiley.
		 *
		 * @return The icon replacing the text smiley.
		 */
		public ImageIcon getIcon()
		{
			return icon;
		}

		/**
		 * Gets the text smiley code.
		 *
		 * @return The text smiley code.
		 */
		public String getCode()
		{
			return code;
		}
	}
}
