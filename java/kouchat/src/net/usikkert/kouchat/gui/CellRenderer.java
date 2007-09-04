
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

package net.usikkert.kouchat.gui;

import java.awt.*;
import javax.swing.*;
import net.usikkert.kouchat.misc.Nick;

public class CellRenderer extends JLabel implements ListCellRenderer
{
	public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus )
	{
		Nick dto = (Nick) value;
		
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
		
		return this;
	}
}
