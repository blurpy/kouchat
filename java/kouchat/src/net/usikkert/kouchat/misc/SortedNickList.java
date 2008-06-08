
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

/**
 * This is a sorted version of the nick list.
 *
 * <p>The users in the list are sorted by nick name,
 * as specified in {@link NickDTO#compareTo(NickDTO)}.</p>
 *
 * @author Christian Ihle
 */
public class SortedNickList implements NickList
{
	/** The list of users in the chat. */
	private final List<NickDTO> nickList;

	/** The list of listeners of changes to the user list. */
	private final List<NickListListener> listeners;

	/**
	 * Constructor.
	 */
	public SortedNickList()
	{
		nickList = new ArrayList<NickDTO>();
		listeners = new ArrayList<NickListListener>();
	}

	/**
	 * Adds the user, and then sorts the list.
	 *
	 * {@inheritDoc}
	 */
	@Override
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

	/** {@inheritDoc} */
	@Override
	public NickDTO get( final int pos )
	{
		if ( pos < nickList.size() )
			return nickList.get( pos );
		else
			return null;
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf( final NickDTO nick )
	{
		return nickList.indexOf( nick );
	}

	/** {@inheritDoc} */
	@Override
	public NickDTO remove( final int pos )
	{
		NickDTO nick = nickList.remove( pos );
		fireNickRemoved( pos );

		return nick;
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove( final NickDTO nick )
	{
		int pos = nickList.indexOf( nick );
		boolean success = nickList.remove( nick );
		fireNickRemoved( pos );

		return success;
	}

	/**
	 * Sets the user, and then sorts the list.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public NickDTO set( final int pos, final NickDTO nick )
	{
		NickDTO oldNick = nickList.set( pos, nick );
		Collections.sort( nickList );
		fireNickChanged( nickList.indexOf( nick ) );

		return oldNick;
	}

	/** {@inheritDoc} */
	@Override
	public int size()
	{
		return nickList.size();
	}

	/** {@inheritDoc} */
	@Override
	public void addNickListListener( final NickListListener listener )
	{
		listeners.add( listener );
	}

	/** {@inheritDoc} */
	@Override
	public void removeNickListListener( final NickListListener listener )
	{
		listeners.remove( listener );
	}

	/**
	 * Notifies the listeners that a user was added.
	 *
	 * @param pos The position where the user was added.
	 */
	private void fireNickAdded( final int pos )
	{
		for ( NickListListener listener : listeners )
		{
			listener.nickAdded( pos );
		}
	}

	/**
	 * Notifies the listeners that a user was changed.
	 *
	 * @param pos The position of the changed user.
	 */
	private void fireNickChanged( final int pos )
	{
		for ( NickListListener listener : listeners )
		{
			listener.nickChanged( pos );
		}
	}

	/**
	 * Notifies the listeners that a user was removed.
	 *
	 * @param pos The position of the removed user.
	 */
	private void fireNickRemoved( final int pos )
	{
		for ( NickListListener listener : listeners )
		{
			listener.nickRemoved( pos );
		}
	}
}
