
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

import net.usikkert.kouchat.util.Tools;

/**
 * Formats different kind of messages for display in a chat window,
 * and logs them to file.
 * 
 * @author Christian Ihle
 */
public class MessageController
{
	private Settings settings;
	private NickDTO me;
	private ChatWindow chat;
	private ChatLogger cLog;
	
	/**
	 * Initializes log and loads settings.
	 * 
	 * @param chat The user interface object to write the formatted messages to.
	 */
	public MessageController( ChatWindow chat )
	{
		this.chat = chat;
		
		settings = Settings.getSettings();
		me = settings.getMe();
		cLog = new ChatLogger();
	}
	
	/**
	 * This is a message from another user. The result will look like this:<br />
	 * [hour:min:sec] &lt;user&gt; message<br />
	 * The message will be shown in the color spesified.
	 * 
	 * @param user The user who wrote the message.
	 * @param message The text the user write.
	 * @param color The color the user chose for the message.
	 */
	public void showUserMessage( String user, String message, int color )
	{
		String msg = Tools.getTime() + " <" + user + ">: " + message;
		chat.appendToChat( msg, color );
		cLog.append( msg );
	}

	/**
	 * This is an information message from the system. The result
	 * will look like this:<br />
	 * [hour:min:sec] *** message<br />
	 * The message will be shown in the color spesified in the settings.
	 * 
	 * @param message The system message to show.
	 */
	public void showSystemMessage( String message )
	{
		String msg = Tools.getTime() + " *** " + message;
		chat.appendToChat( msg, settings.getSysColor() );
		cLog.append( msg );
	}

	/**
	 * This is a normal message written by the application user,
	 * meant to be seen by all other users. It will look like this:<br />
	 * [hour:min:sec] &lt;nick&gt; message<br />
	 * The message will be shown in the color spesified in the settings.
	 * 
	 * @param message The message written by the application user.
	 */
	public void showOwnMessage( String message )
	{
		String msg = Tools.getTime() + " <" + me.getNick() + ">: " + message;
		chat.appendToChat( msg, settings.getOwnColor() );
		cLog.append( msg );
	}
}
