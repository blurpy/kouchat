
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

package net.usikkert.kouchat.ui.swing;

import javax.swing.AbstractListModel;

import net.usikkert.kouchat.event.NickListListener;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.NickList;

/**
 * This is the list model for the user list. It's just a facade above
 * the real list containing the users, so it can deliver events on changes.
 * 
 * @author Christian Ihle
 */
public class NickListModel extends AbstractListModel implements NickListListener
{
	private static final long serialVersionUID = 1L;
	
	private NickList nickList;

	/**
	 * Constructor. Adds this list model as a listener for events
	 * from the real user list.
	 * 
	 * @param nickList The list where the real users are.
	 */
	public NickListModel( NickList nickList )
	{
		System.out.println( "NickListModel" );
		this.nickList = nickList;
		nickList.addNickListListener( this );
	}

	/**
	 * Returns the user at the specified index position.
	 */
	@Override
	public NickDTO getElementAt( int index )
	{
		System.out.println( "getElementAt: " + index );
		return nickList.get( index );
	}

	/**
	 * Returns the number of users in the user list.
	 */
	@Override
	public int getSize()
	{
		System.out.println( "getSize: " + nickList.size() );
		return nickList.size();
	}

	/**
	 * Sends a fireIntervalAdded() event.
	 */
	@Override
	public void nickAdded( int pos )
	{
		System.out.println( "nickAdded: " + pos );
		fireIntervalAdded( this, pos, pos );
	}

	/**
	 * Sends a fireContentsChanged() event.
	 */
	@Override
	public void nickChanged( int pos )
	{
		System.out.println( "nickChanged: " + pos );
		fireContentsChanged( this, pos, pos );
	}

	/**
	 * Sends a fireIntervalRemoved() event.
	 */
	@Override
	public void nickRemoved( int pos )
	{
		System.out.println( "nickRemoved: " + pos );
		fireIntervalRemoved( this, pos, pos );
	}
}
