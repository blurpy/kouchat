
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

public class NickController
{
	private NickList nickList;
	private NickDTO me;
	private Settings settings;

	public NickController()
	{
		settings = Settings.getSettings();
		nickList = new SortedNickList();
		me = settings.getMe();
		nickList.add( me );
	}

	public NickDTO getNick( final int code )
	{
		NickDTO dto = null;

		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				dto = temp;
				break;
			}
		}

		return dto;
	}

	public NickDTO getNick( final String nick )
	{
		NickDTO dto = null;

		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getNick().equalsIgnoreCase( nick ) )
			{
				dto = temp;
				break;
			}
		}

		return dto;
	}

	public void changeNick( final int code, final String nick )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				temp.setNick( nick );
				nickList.set( i, temp );
				break;
			}
		}
	}

	public void changeAwayStatus( final int code, final boolean away, final String awaymsg )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				temp.setAway( away );
				temp.setAwayMsg( awaymsg );
				nickList.set( i, temp );
				break;
			}
		}
	}

	public void changeWriting( final int code, final boolean writing )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				temp.setWriting( writing );
				nickList.set( i, temp );
				break;
			}
		}
	}

	public void changeNewMessage( final int code, final boolean newMsg )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				temp.setNewMsg( newMsg );
				nickList.set( i, temp );
				break;
			}
		}
	}

	public boolean isNickInUse( final String nick )
	{
		boolean inUse = false;

		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getNick().equalsIgnoreCase( nick ) && !temp.isMe() )
			{
				inUse = true;
				break;
			}
		}

		return inUse;
	}

	public boolean isNewUser( final int code )
	{
		boolean newUser = true;

		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getCode() == code )
			{
				newUser = false;
				break;
			}
		}

		return newUser;
	}

	public boolean isTimeoutUsers()
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getNick().equals( "" + temp.getCode() ) )
				return true;
		}

		return false;
	}

	public NickList getNickList()
	{
		return nickList;
	}
}
