
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

package net.usikkert.kouchat.misc;

import javax.swing.JOptionPane;
import net.usikkert.kouchat.Constants;

public class NickController
{
	private NickList nickList;
	private Nick me;
	private Settings settings;
	
	public NickController()
	{
		settings = Settings.getSettings();
		nickList = new SortedNickList();
		me = settings.getNick();
		nickList.add( me );
	}
	
	public Nick getNick( int code )
	{
		Nick dto = null;
		
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getCode() == code )
			{
				dto = temp;
				break;
			}
		}
		
		return dto;
	}
	
	public void updateLastIdle( int code, long lastIdle )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getCode() == code )
			{
				temp.setLastIdle( lastIdle );
				nickList.set( i, temp );
				break;
			}
		}
	}
	
	public void changeNick( int code, String nick )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getCode() == code )
			{
				temp.setNick( nick );
				nickList.set( i, temp );
				break;
			}
		}
	}
	
	public void changeIP( int code, String ip )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getCode() == code )
			{
				temp.setIpAddress( ip );
				nickList.set( i, temp );
				break;
			}
		}
	}
	
	public void changeAwayStatus( int code, boolean away, String awaymsg )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getCode() == code )
			{
				temp.setAway( away );
				temp.setAwayMsg( awaymsg );
				nickList.set( i, temp );
				break;
			}
		}
	}
	
	public void changeWriting( int code, boolean writing )
	{
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getCode() == code )
			{
				temp.setWriting( writing );
				nickList.set( i, temp );
				break;
			}
		}
	}
	
	//TODO fix gui stuff
	public boolean checkIfValidNick( String tmp, boolean quiet )
	{
		boolean ok = true;
		
		if ( tmp == null || tmp.trim().equals( "" ) )
		{
			ok = false;
			
			if ( !quiet )
				JOptionPane.showMessageDialog( null, "You can not have an empty nick...", Constants.APP_NAME
						+ " - Change nick", JOptionPane.WARNING_MESSAGE );
		}
		
		else if ( tmp.length() > 10 )
		{
			ok = false;
			
			if ( !quiet )
				JOptionPane.showMessageDialog( null, "Max 10 letters allowed in the nick...", Constants.APP_NAME
						+ " - Change nick", JOptionPane.WARNING_MESSAGE );
		}
		
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getNick().equalsIgnoreCase( tmp ) && !temp.isMe() )
			{
				ok = false;
				
				if ( !quiet )
					JOptionPane.showMessageDialog( null, "The nick is in use by someone else...", Constants.APP_NAME
							+ " - Change nick", JOptionPane.WARNING_MESSAGE );
				
				break;
			}
		}
		
		return ok;
	}
	
	public boolean checkIfNewUser( int code )
	{
		boolean newUser = true;
		
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getCode() == code )
			{
				newUser = false;
				break;
			}
		}
		
		return newUser;
	}

	public NickList getNickList()
	{
		return nickList;
	}
}
