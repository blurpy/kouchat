
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

package net.usikkert.kouchat.net;

import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.TopicDTO;

public class Messages
{
	private UDPSender udpSender;
	private MessageSender sender;
	private NickDTO me;
	private Settings settings;

	public Messages()
	{
		udpSender = new UDPSender();
		sender = new MessageSender();
		settings = Settings.getSettings();
		me = settings.getMe();
	}

	public void sendIdleMessage()
	{
		sender.send( me.getCode() + "!IDLE#" + me.getNick() + ":" );
	}

	public void sendTopicMessage( TopicDTO topic )
	{
		sender.send( me.getCode() + "!TOPIC#" + me.getNick() + ":" + "(" + topic.getNick()	+ ")" + "[" + topic.getTime() + "]" + topic.getTopic() );
	}

	public void sendAwayMessage()
	{
		sender.send( me.getCode() + "!AWAY#" + me.getNick() + ":" + me.getAwayMsg() );
	}

	public void sendBackMessage()
	{
		sender.send( me.getCode() + "!BACK#" + me.getNick() + ":" );
	}

	public void sendChatMessage( String msg )
	{
		sender.send( me.getCode() + "!MSG#" + me.getNick() + ":[" + settings.getOwnColor() + "]" + msg );
	}

	public void sendLogonMessage()
	{
		sender.send( me.getCode() + "!LOGON#" + me.getNick() + ":" );
	}

	public void sendLogoffMessage()
	{
		sender.send( me.getCode() + "!LOGOFF#" + me.getNick() + ":" );
	}

	public void sendExposeMessage()
	{
		sender.send( me.getCode() + "!EXPOSE#" + me.getNick() + ":" );
	}

	public void sendExposingMessage()
	{
		sender.send( me.getCode() + "!EXPOSING#" + me.getNick() + ":" + me.getAwayMsg() );
	}

	public void sendGetTopicMessage()
	{
		sender.send( me.getCode() + "!GETTOPIC#" + me.getNick() + ":" );
	}

	public void sendWritingMessage()
	{
		sender.send( me.getCode() + "!WRITING#" + me.getNick() + ":" );
	}

	public void sendStoppedWritingMessage()
	{
		sender.send( me.getCode() + "!STOPPEDWRITING#" + me.getNick() + ":" );
	}

	public void sendNickMessage()
	{
		sender.send( me.getCode() + "!NICK#" + me.getNick() + ":" );
	}

	public void sendNickCrashMessage( String nick )
	{
		sender.send( me.getCode() + "!NICKCRASH#" + me.getNick() + ":" + nick );
	}

	public void sendFileAbort( int msgCode, int fileHash, String fileName )
	{
		sender.send( me.getCode() + "!SENDFILEABORT#" + me.getNick() + ":(" + msgCode + "){" + fileHash + "}" + fileName );
	}

	public void sendFileAccept( int msgCode, int port, int fileHash, String fileName )
	{
		sender.send( me.getCode() + "!SENDFILEACCEPT#" + me.getNick() + ":(" + msgCode + ")[" + port + "]{" + fileHash + "}" + fileName );
	}

	public void sendFile( int sendToUserCode, long fileLength, int fileHash, String fileName )
	{
		sender.send( me.getCode() + "!SENDFILE#" + me.getNick() + ":(" + sendToUserCode + ")" + "["
				+ fileLength + "]{" + fileHash + "}" + fileName );
	}
	
	public void sendClient()
	{
		sender.send( me.getCode() + "!CLIENT#" + me.getNick() + ":(" + me.getClient() +
				")[" + ( System.currentTimeMillis() - me.getLogonTime() ) +
				"]{" + me.getOperatingSystem() + "}<" + me.getPrivateChatPort() + ">" );
	}
	
	public void sendPrivateMessage( String privmsg, String userIP, int userPort, int userCode )
	{
		udpSender.send( me.getCode() + "!PRIVMSG#" + me.getNick() + ":(" + userCode + ")" +
				"[" + settings.getOwnColor() + "]" + privmsg, userIP, userPort );
	}
	
	public void start()
	{
		sender.startSender();
		udpSender.startSender();
	}

	public void stop()
	{
		sender.stopSender();
		udpSender.stopSender();
	}
}
