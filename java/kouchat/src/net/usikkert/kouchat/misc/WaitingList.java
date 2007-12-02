
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

import java.util.ArrayList;
import java.util.List;

/**
 * This waiting list is used to store unknown users while asking them to identify.
 * Usually it's users that timed out at some point, and are returning. By doing this,
 * messages from unknown users can be held back until they have identified themselves.
 *
 * @author Christian Ihle
 */
public class WaitingList
{
	private List<Integer> users;
	private boolean loggedOn;

	public WaitingList()
	{
		users = new ArrayList<Integer>();
	}

	public void addWaitingUser( int userCode )
	{
		users.add( userCode );
	}

	public boolean isWaitingUser( int userCode )
	{
		return users.contains( userCode );
	}

	public void removeWaitingUser( int userCode )
	{
		users.remove( new Integer( userCode ) );
	}

	public boolean isLoggedOn()
	{
		return loggedOn;
	}

	public void setLoggedOn( boolean loggedOn )
	{
		this.loggedOn = loggedOn;
	}
}
