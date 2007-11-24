
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

import javax.swing.AbstractListModel;

import net.usikkert.kouchat.event.NickListListener;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.NickList;

public class NickListModel extends AbstractListModel implements NickListListener
{
	private static final long serialVersionUID = 1L;
	
	private NickList nickList;

	public void setNickList( NickList nickList )
	{
		this.nickList = nickList;
		nickList.addNickListListener( this );
	}

	@Override
	public NickDTO getElementAt( int index )
	{
		return nickList.get( index );
	}

	@Override
	public int getSize()
	{
		if ( nickList == null )
			return 0;
		else
			return nickList.size();
	}

	@Override
	public void nickAdded( int pos )
	{
		fireIntervalAdded( this, pos, pos );
	}

	@Override
	public void nickChanged( int pos )
	{
		fireContentsChanged( this, pos, pos );
	}

	@Override
	public void nickRemoved( int pos )
	{
		fireIntervalRemoved( this, pos, pos );
	}
}
