
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

/**
 * This is the controller responsible for the nick list.
 *
 * It contains methods for getting information about users,
 * and updating the state of users.
 *
 * @author Christian Ihle
 */
public class NickController
{
	private final UserList nickList;
	private final User me;
	private final Settings settings;

	/**
	 * Constructor.
	 * Initializes the nick list and puts <code>me</code> in the list.
	 */
	public NickController()
	{
		settings = Settings.getSettings();
		nickList = new SortedUserList();
		me = settings.getMe();
		nickList.add( me );
	}

	/**
	 * Gets a user by the user's unique code.
	 *
	 * @param code The unique code of the user to get.
	 * @return The user, or <code>null</code> if the user was not found.
	 */
	public User getNick( final int code )
	{
		User user = null;

		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				user = temp;
				break;
			}
		}

		return user;
	}

	/**
	 * Gets a user by the user's unique nick name.
	 *
	 * @param nick The unique nick name of the user to get.
	 * @return The user, or <code>null</code> if the user was not found.
	 */
	public User getNick( final String nick )
	{
		User user = null;

		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getNick().equalsIgnoreCase( nick ) )
			{
				user = temp;
				break;
			}
		}

		return user;
	}

	/**
	 * Changes the nick name of a user.
	 *
	 * @param code The unique code of the user to change the nick name of.
	 * @param nick The new nick name of the user.
	 */
	public void changeNick( final int code, final String nick )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				temp.setNick( nick );
				nickList.set( i, temp );
				break;
			}
		}
	}

	/**
	 * Changes the away status of a user.
	 *
	 * @param code The unique code of the user.
	 * @param away If the user is away.
	 * @param awaymsg The new away message.
	 */
	public void changeAwayStatus( final int code, final boolean away, final String awaymsg )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				temp.setAway( away );
				temp.setAwayMsg( awaymsg );
				nickList.set( i, temp );
				break;
			}
		}
	}

	/**
	 * Changes if the user is writing or not.
	 *
	 * @param code The unique code of the user.
	 * @param writing If the user is writing.
	 */
	public void changeWriting( final int code, final boolean writing )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				temp.setWriting( writing );
				nickList.set( i, temp );
				break;
			}
		}
	}

	/**
	 * Changes if the user has new private messages.
	 *
	 * @param code The unique code of the user.
	 * @param newMsg If the user has new private messages.
	 */
	public void changeNewMessage( final int code, final boolean newMsg )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				temp.setNewPrivMsg( newMsg );
				nickList.set( i, temp );
				break;
			}
		}
	}

	/**
	 * Checks if the nick name is in use by any other users.
	 *
	 * @param nick The nick name to check.
	 * @return If the nick name is in use.
	 */
	public boolean isNickInUse( final String nick )
	{
		boolean inUse = false;

		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getNick().equalsIgnoreCase( nick ) && !temp.isMe() )
			{
				inUse = true;
				break;
			}
		}

		return inUse;
	}

	/**
	 * Checks if the user already exists in the nick list.
	 *
	 * @param code The unique code of the user.
	 * @return If the user is new, which means it is not in the nick list.
	 */
	public boolean isNewUser( final int code )
	{
		boolean newUser = true;

		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				newUser = false;
				break;
			}
		}

		return newUser;
	}

	/**
	 * Checks if the nick list contains <em>timeout users</em>.
	 *
	 * <p>A timeout user is a user which disconnected from the chat without
	 * logging off, and then logging on the chat again before the original
	 * user has timed out from the chat. The user will then get a nick name
	 * which is identical to the user's unique code to avoid nick crash.</p>
	 *
	 * @return If there are any timeout users.
	 */
	public boolean isTimeoutUsers()
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			User temp = nickList.get( i );

			if ( temp.getNick().equals( "" + temp.getCode() ) )
				return true;
		}

		return false;
	}

	/**
	 * Gets the nick list.
	 *
	 * @return The nick list.
	 */
	public UserList getNickList()
	{
		return nickList;
	}
}
