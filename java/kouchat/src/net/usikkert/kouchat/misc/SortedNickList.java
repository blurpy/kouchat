
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

package net.usikkert.kouchat.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.usikkert.kouchat.event.NickListListener;

public class SortedNickList implements NickList
{
	private final List<NickDTO> nickList;
	private final List<NickListListener> listeners;

	public SortedNickList()
	{
		nickList = new ArrayList<NickDTO>();
		listeners = new ArrayList<NickListListener>();
	}

	public boolean add( final NickDTO nick )
	{
		boolean success = nickList.add( nick );

		if ( success )
		{
			Collections.sort( nickList );
			fireNickAdded( nickList.indexOf( nick ) );
		}

		return success;
	}

	public NickDTO get( final int pos )
	{
		if ( pos < nickList.size() )
			return nickList.get( pos );
		else
			return null;
	}

	public int indexOf( final NickDTO nick )
	{
		return nickList.indexOf( nick );
	}

	public NickDTO remove( final int pos )
	{
		NickDTO nick = nickList.remove( pos );
		fireNickRemoved( pos );

		return nick;
	}

	public boolean remove( final NickDTO nick )
	{
		int pos = nickList.indexOf( nick );
		boolean success = nickList.remove( nick );
		fireNickRemoved( pos );

		return success;
	}

	public NickDTO set( final int pos, final NickDTO nick )
	{
		NickDTO oldNick = nickList.set( pos, nick );
		Collections.sort( nickList );
		fireNickChanged( nickList.indexOf( nick ) );

		return oldNick;
	}

	public int size()
	{
		return nickList.size();
	}

	public void addNickListListener( final NickListListener listener )
	{
		listeners.add( listener );
	}

	public void removeNickListListener( final NickListListener listener )
	{
		listeners.remove( listener );
	}

	private void fireNickAdded( final int pos )
	{
		for ( NickListListener listener : listeners )
		{
			listener.nickAdded( pos );
		}
	}

	private void fireNickChanged( final int pos )
	{
		for ( NickListListener listener : listeners )
		{
			listener.nickChanged( pos );
		}
	}

	private void fireNickRemoved( final int pos )
	{
		for ( NickListListener listener : listeners )
		{
			listener.nickRemoved( pos );
		}
	}
}
