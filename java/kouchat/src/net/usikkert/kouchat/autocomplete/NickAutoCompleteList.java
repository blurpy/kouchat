
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

package net.usikkert.kouchat.autocomplete;

import net.usikkert.kouchat.event.UserListListener;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.Tools;

/**
 * This autocompleter has a list of all the users currently online.
 *
 * @author Christian Ihle
 */
public class NickAutoCompleteList implements AutoCompleteList, UserListListener
{
	private final UserList nickList;
	private String[] nicks;

	/**
	 * Constructor. Registers itself as a nicklist listener.
	 *
	 * @param nickList The list of online users.
	 */
	public NickAutoCompleteList( final UserList nickList )
	{
		this.nickList = nickList;
		nickList.addUserListListener( this );
		updateWords();
	}

	/**
	 * Updates the list of nicknames.
	 */
	@Override
	public void userAdded( final int pos )
	{
		updateWords();
	}

	/**
	 * Updates the list of nicknames.
	 */
	@Override
	public void userChanged( final int pos )
	{
		updateWords();
	}

	/**
	 * Updates the list of nicknames.
	 */
	@Override
	public void userRemoved( final int pos )
	{
		updateWords();
	}

	/**
	 * Iterates through the nicklist, and adds all the nick names to the
	 * list of words.
	 */
	private void updateWords()
	{
		nicks = new String[nickList.size()];

		for ( int i = 0; i < nickList.size(); i++ )
		{
			nicks[i] = nickList.get( i ).getNick();
		}
	}

	/**
	 * Checks if the word is a valid nick.
	 */
	@Override
	public boolean acceptsWord( final String word )
	{
		return Tools.isValidNick( word );
	}

	/**
	 * Returns a list of all the users.
	 */
	@Override
	public String[] getWordList()
	{
		return nicks;
	}
}
