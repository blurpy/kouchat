
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
 * This class keeps some information about the current state of the chat.
 *
 * @author Christian Ihle
 */
public class ChatState
{
	/**
	 * This keeps information about the current topic in the chat.
	 */
	private final TopicDTO topic;

	/**
	 * Whether the application user was writing at the moment this was updated.
	 */
	private boolean wrote;

	/**
	 * Whether the application is connected to the network.
	 */
	private boolean connected;

	/**
	 * Constructor.
	 */
	public ChatState()
	{
		topic = new TopicDTO();
		wrote = false;
		connected = false;
	}

	/**
	 * Returns if the application user wrote the last time this was updated.
	 *
	 * @return True if the application user wrote the last time this was updated.
	 */
	public boolean isWrote()
	{
		return wrote;
	}

	/**
	 * Sets if the application user is writing at this moment.
	 *
	 * @param wrote True if the application user is writing at this moment.
	 */
	public void setWrote( final boolean wrote )
	{
		this.wrote = wrote;
	}

	/**
	 * Gets the object containing the current topic information.
	 *
	 * @return The current topic.
	 */
	public TopicDTO getTopic()
	{
		return topic;
	}

	/**
	 * Returns if the application is connected to the network.
	 *
	 * @return True if the application is connected to the network.
	 */
	public boolean isConnected()
	{
		return connected;
	}

	/**
	 * Sets if the application is connected to the network.
	 *
	 * @param connected If the application is connected to the network.
	 */
	public void setConnected( final boolean connected )
	{
		this.connected = connected;
	}
}
