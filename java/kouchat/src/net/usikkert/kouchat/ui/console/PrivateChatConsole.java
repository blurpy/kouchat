
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

package net.usikkert.kouchat.ui.console;

import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.PrivateChatWindow;

/**
 * Very simple console support for private chat sessions.
 *
 * @author Christian Ihle
 */
public class PrivateChatConsole implements PrivateChatWindow
{
	private NickDTO user;

	/**
	 * Constructor
	 *
	 * @param user The user in this chat session.
	 */
	public PrivateChatConsole( final NickDTO user )
	{
		this.user = user;
	}

	/**
	 * Uses a simple System.out.println() to show messages,
	 * with (privmsg) in front of them.
	 */
	@Override
	public void appendToPrivateChat( final String message, final int color )
	{
		System.out.println( "(privmsg) " + message );
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void clearChatText()
	{

	}

	/**
	 * Will always return an empty string.
	 */
	@Override
	public String getChatText()
	{
		return "";
	}

	/**
	 * Returns the user in this private chat.
	 */
	@Override
	public NickDTO getUser()
	{
		return user;
	}

	/**
	 * Will always return true.
	 */
	@Override
	public boolean isVisible()
	{
		return true;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void setAway( final boolean away )
	{

	}

	/**
	 * Sets the user to null.
	 */
	@Override
	public void setLoggedOff()
	{
		user = null;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void setVisible( final boolean visible )
	{

	}

	/**
	 * Not implemented.
	 */
	@Override
	public void updateNick()
	{

	}
}
