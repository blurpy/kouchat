
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

public class MessageController
{
	private Settings settings;
	private NickDTO me;
	private ChatWindow chat;
	
	public MessageController( ChatWindow chat )
	{
		this.chat = chat;
		
		settings = Settings.getSettings();
		me = settings.getMe();
	}
	
	public void showUserMessage( String user, String message, int color )
	{
		chat.appendToChat( Tools.getTime() + " <" + user + ">: " + message, color );
	}

	public void showSystemMessage( String message )
	{
		chat.appendToChat( Tools.getTime() + " *** " + message, settings.getSysColor() );
	}

	public void showOwnMessage( String message )
	{
		chat.appendToChat( Tools.getTime() + " <" + me.getNick() + ">: " + message, settings.getOwnColor() );
	}
}
