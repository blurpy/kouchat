
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

package net.usikkert.kouchat.event;

/**
 * This interface can be used to be notified when
 * the nick list is updated.
 *
 * @author Christian Ihle
 */
public interface NickListListener
{
	/**
	 * A new user has been added to the nick list.
	 *
	 * @param pos The position in the nick list where
	 * the user was added.
	 */
	public void nickAdded( int pos );

	/**
	 * A user has updated some of its fields,
	 * so the ui needs to refresh.
	 *
	 * @param pos  The position of the changed user in the nick list.
	 */
	public void nickChanged( int pos );

	/**
	 * A user has been removed from the nick list.
	 *
	 * @param pos The position where the user used to be in the nick list.
	 */
	public void nickRemoved( int pos );
}
