
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.NickDTO;

/**
 * This class renders the rows in the nick list.
 *
 * @author Christian Ihle
 */
public class NickListCellRenderer extends JLabel implements ListCellRenderer
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger( NickListCellRenderer.class.getName() );
	private static final String IMG_ENVELOPE = "/icons/envelope.png";
	private static final String IMG_DOT = "/icons/dot.png";

	private final ImageIcon envelope, dot;
	private final Border selectedBorder, normalBorder;

	/**
	 * Default constructor.
	 *
	 * Initializes resources, and shuts down the application
	 * if this fails.
	 */
	public NickListCellRenderer()
	{
		ErrorHandler errorHandler = ErrorHandler.getErrorHandler();

		URL envelopeURL = getClass().getResource( IMG_ENVELOPE );
		URL dotURL = getClass().getResource( IMG_DOT );

		if ( envelopeURL == null || dotURL == null )
		{
			String missing = "";

			if ( envelopeURL == null && dotURL == null )
				missing = "* " + IMG_ENVELOPE + "\n* " + IMG_DOT;
			else if ( envelopeURL == null )
				missing = "* " + IMG_ENVELOPE;
			else if ( dotURL == null )
				missing = "* " + IMG_DOT;

			String error = "These images were expected, but not found:\n\n" + missing + "\n\n"
					+ Constants.APP_NAME + " will now shutdown.";

			LOG.log( Level.SEVERE, error );
			errorHandler.showCriticalError( error );
			System.exit( 1 );
		}

		envelope = new ImageIcon( envelopeURL );
		dot = new ImageIcon( dotURL );
		normalBorder = BorderFactory.createEmptyBorder( 2, 4, 2, 4 );
		selectedBorder = BorderFactory.createCompoundBorder(
				UIManager.getBorder( "List.focusCellHighlightBorder" ),
				BorderFactory.createEmptyBorder( 1, 3, 1, 3 ) );

		setOpaque( true );
	}

	/**
	 * Displays an icon and the user's nick name.
	 *
	 * If the user is away, the nick name is shown in gray.
	 * If the user is "me", the nick name is shown in bold.
	 * If the user has a new message, the icon changes to an envelope.
	 * If the user is writing, the nick name will have a star next to it.
	 */
	@Override
	public Component getListCellRendererComponent( final JList list, final Object value,
			final int index, final boolean isSelected, final boolean cellHasFocus )
	{
		if ( isSelected )
		{
			setBackground( list.getSelectionBackground() );
			setForeground( list.getSelectionForeground() );
			setBorder( selectedBorder );
		}

		else
		{
			setBackground( list.getBackground() );
			setForeground( list.getForeground() );
			setBorder( normalBorder );
		}

		NickDTO dto = (NickDTO) value;

		if ( dto != null )
		{
			if ( dto.isMe() )
				setFont( list.getFont().deriveFont( Font.BOLD ) );
			else
				setFont( list.getFont().deriveFont( Font.PLAIN ) );

			if ( dto.isAway() )
				setForeground( Color.GRAY );

			if ( dto.isNewPrivMsg() )
				setIcon( envelope );
			else
				setIcon( dot );

			if ( dto.isWriting() )
				setText( dto.getNick() + " *" );
			else
				setText( dto.getNick() );
		}

		else
			LOG.log( Level.WARNING, "Got a null list element." );

		setEnabled( list.isEnabled() );
		setComponentOrientation( list.getComponentOrientation() );

		return this;
	}
}
