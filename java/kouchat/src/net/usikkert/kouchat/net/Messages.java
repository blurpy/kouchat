
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

package net.usikkert.kouchat.net;

import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.TopicDTO;
import net.usikkert.kouchat.util.Validate;

public class Messages
{
	private final NetworkService networkService;
	private final NickDTO me;
	private final Settings settings;

	public Messages( final NetworkService networkService )
	{
		Validate.notNull( networkService, "Network service can not be null" );
		this.networkService = networkService;
		settings = Settings.getSettings();
		me = settings.getMe();
	}

	public void sendIdleMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!IDLE#" + me.getNick() + ":" );
	}

	public void sendTopicMessage( final TopicDTO topic )
	{
		networkService.sendMulticastMsg( me.getCode() + "!TOPIC#" + me.getNick() + ":" + "(" + topic.getNick()
				+ ")" + "[" + topic.getTime() + "]" + topic.getTopic() );
	}

	public void sendAwayMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!AWAY#" + me.getNick() + ":" + me.getAwayMsg() );
	}

	public void sendBackMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!BACK#" + me.getNick() + ":" );
	}

	public void sendChatMessage( final String msg )
	{
		networkService.sendMulticastMsg( me.getCode() + "!MSG#" + me.getNick() + ":[" + settings.getOwnColor() + "]" + msg );
	}

	public void sendLogonMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!LOGON#" + me.getNick() + ":" );
	}

	public void sendLogoffMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!LOGOFF#" + me.getNick() + ":" );
	}

	public void sendExposeMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!EXPOSE#" + me.getNick() + ":" );
	}

	public void sendExposingMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!EXPOSING#" + me.getNick() + ":" + me.getAwayMsg() );
	}

	public void sendGetTopicMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!GETTOPIC#" + me.getNick() + ":" );
	}

	public void sendWritingMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!WRITING#" + me.getNick() + ":" );
	}

	public void sendStoppedWritingMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!STOPPEDWRITING#" + me.getNick() + ":" );
	}

	public void sendNickMessage()
	{
		networkService.sendMulticastMsg( me.getCode() + "!NICK#" + me.getNick() + ":" );
	}

	public void sendNickCrashMessage( final String nick )
	{
		networkService.sendMulticastMsg( me.getCode() + "!NICKCRASH#" + me.getNick() + ":" + nick );
	}

	public void sendFileAbort( final int msgCode, final int fileHash, final String fileName )
	{
		networkService.sendMulticastMsg( me.getCode() + "!SENDFILEABORT#" + me.getNick() + ":(" + msgCode
				+ "){" + fileHash + "}" + fileName );
	}

	public void sendFileAccept( final int msgCode, final int port,
			final int fileHash, final String fileName )
	{
		networkService.sendMulticastMsg( me.getCode() + "!SENDFILEACCEPT#" + me.getNick() + ":("
				+ msgCode + ")[" + port + "]{" + fileHash + "}" + fileName );
	}

	public void sendFile( final int sendToUserCode, final long fileLength,
			final int fileHash, final String fileName )
	{
		networkService.sendMulticastMsg( me.getCode() + "!SENDFILE#" + me.getNick() + ":(" + sendToUserCode + ")" + "["
				+ fileLength + "]{" + fileHash + "}" + fileName );
	}

	public void sendClient()
	{
		networkService.sendMulticastMsg( me.getCode() + "!CLIENT#" + me.getNick() + ":(" + me.getClient()
				+ ")[" + ( System.currentTimeMillis() - me.getLogonTime() )
				+ "]{" + me.getOperatingSystem() + "}<" + me.getPrivateChatPort() + ">" );
	}

	public void sendPrivateMessage( final String privmsg, final String userIP,
			final int userPort, final int userCode )
	{
		networkService.sendUDPMsg( me.getCode() + "!PRIVMSG#" + me.getNick() + ":(" + userCode + ")"
				+ "[" + settings.getOwnColor() + "]" + privmsg, userIP, userPort );
	}
}
