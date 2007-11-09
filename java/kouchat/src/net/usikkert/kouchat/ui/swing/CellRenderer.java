
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

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.NickDTO;

public class CellRenderer extends JLabel implements ListCellRenderer
{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger( CellRenderer.class.getName() );
	
	private ImageIcon envelope, dot;
	
	public CellRenderer()
	{
		ErrorHandler errorHandler = ErrorHandler.getErrorHandler();
		
		URL envelope_url = getClass().getResource( "/icons/envelope.png" );
		URL dot_url = getClass().getResource( "/icons/dot.png" );
		
		if ( envelope_url == null || dot_url == null )
		{
			String error = "Missing images in icons folder. Quitting...";
			log.log( Level.SEVERE, error );
			errorHandler.showExitError( error );
			System.exit( 1 );
		}
		
		envelope = new ImageIcon( envelope_url );
		dot = new ImageIcon( dot_url );
	}
	
	public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus )
	{
		NickDTO dto = (NickDTO) value;

		if ( dto.isMe() )
		{
			if ( dto.isAway() )
			{
				setFont( new Font( list.getFont().getName(), Font.BOLD, list.getFont().getSize() ) );
				setForeground( Color.GRAY );
			}

			else
			{
				setFont( new Font( list.getFont().getName(), Font.BOLD, list.getFont().getSize() ) );
				setForeground( Color.BLACK );
			}
		}

		else
		{
			if ( dto.isAway() )
			{
				setFont( new Font( list.getFont().getName(), Font.PLAIN, list.getFont().getSize() ) );
				setForeground( Color.GRAY );
			}

			else
			{
				setFont( new Font( list.getFont().getName(), Font.PLAIN, list.getFont().getSize() ) );
				setForeground( Color.BLACK );
			}
		}
		
		if ( dto.isNewMsg() )
			setIcon( envelope );
		else
			setIcon( dot );

		if ( dto.isWriting() )
			setText( dto.getNick() + " *" );
		else
			setText( dto.getNick() );

		if ( isSelected )
			setBackground( list.getSelectionBackground() );
		else
			setBackground( list.getBackground() );

		setEnabled( list.isEnabled() );
		setOpaque( true );
		
		setBorder( BorderFactory.createEmptyBorder( 2, 4, 2, 4 ) );

		return this;
	}
}
