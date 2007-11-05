
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

public class NickDTO implements Comparable<NickDTO>
{
	private String nick, awayMsg, ipAddress, operatingSystem, client;
	private int code, privateChatPort;
	private long lastIdle, logonTime;
	private boolean writing, away, me, newMsg;
	private PrivateChatWindow privchat;

	public NickDTO( String nick, int code )
	{
		this.nick = nick;
		this.code = code;
		
		lastIdle = 0;
		awayMsg = "";
		writing = false;
		away = false;
		ipAddress = "<unknown>";
		me = false;
		logonTime = 0;
		operatingSystem = "<unknown>";
		client = "<unknown>";
		newMsg = false;
		privateChatPort = 0;
		privchat = null;
	}

	public boolean isMe()
	{
		return me;
	}

	public void setMe( boolean me )
	{
		this.me = me;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode( int code )
	{
		this.code = code;
	}

	public String getNick()
	{
		return nick;
	}

	public void setNick( String nick )
	{
		this.nick = nick;
	}

	public long getLastIdle()
	{
		return lastIdle;
	}

	public void setLastIdle( long lastIdle )
	{
		this.lastIdle = lastIdle;
	}

	public boolean isAway()
	{
		return away;
	}

	public void setAway( boolean away )
	{
		this.away = away;
	}

	public String getAwayMsg()
	{
		return awayMsg;
	}

	public void setAwayMsg( String awayMsg )
	{
		this.awayMsg = awayMsg;
	}

	public boolean isWriting()
	{
		return writing;
	}

	public void setWriting( boolean writing )
	{
		this.writing = writing;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress( String ipAddress )
	{
		this.ipAddress = ipAddress;
	}

	public String getOperatingSystem()
	{
		return operatingSystem;
	}

	public void setOperatingSystem( String operatingSystem )
	{
		this.operatingSystem = operatingSystem;
	}

	public long getLogonTime()
	{
		return logonTime;
	}

	public void setLogonTime( long logonTime )
	{
		this.logonTime = logonTime;
	}

	public String getClient()
	{
		return client;
	}

	public void setClient( String client )
	{
		this.client = client;
	}

	public PrivateChatWindow getPrivchat()
	{
		return privchat;
	}

	public void setPrivchat( PrivateChatWindow privchat )
	{
		this.privchat = privchat;
	}

	public boolean isNewMsg()
	{
		return newMsg;
	}

	public void setNewMsg( boolean newMsg )
	{
		this.newMsg = newMsg;
	}

	public int getPrivateChatPort()
	{
		return privateChatPort;
	}

	public void setPrivateChatPort( int privateChatPort )
	{
		this.privateChatPort = privateChatPort;
	}

	@Override
	public String toString()
	{
		return nick;
	}

	@Override
	public int compareTo( NickDTO compNick )
	{
		return nick.compareToIgnoreCase( compNick.getNick() );
	}
}
