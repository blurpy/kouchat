
/***************************************************************************
 *   Copyright 2006-2009 by Christian Ihle                                 *
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

package net.usikkert.kouchat.util;

/**
 * Used for calculating number of bytes per second.
 *
 * @author Christian Ihle
 */
public class ByteCounter
{
	private long lastTime, spentTime, bytesPerSec, bytesPerSecCounter;

	/**
	 * Resets the byte counter to zero.
	 */
	public void reset()
	{
		lastTime = System.currentTimeMillis();
		spentTime = 0;
		bytesPerSec = 0;
		bytesPerSecCounter = 0;
	}

	/**
	 * Updates the byte counter and time spent.
	 * If the time spent is a second or more, the number of
	 * bytes is saved and the counters are reset.
	 *
	 * @param bytes Number of bytes to add to the counter.
	 */
	public void update( final long bytes )
	{
		long currentTime = System.currentTimeMillis();
		spentTime += currentTime - lastTime;
		lastTime = currentTime;
		bytesPerSecCounter += bytes;

		if ( spentTime >= 1000 )
		{
			spentTime %= 1000;
			bytesPerSec = bytesPerSecCounter;
			bytesPerSecCounter = 0;
		}
	}

	/**
	 * Gets the current number of bytes per seconds.
	 *
	 * @return The current number of bytes per second.
	 */
	public long getBytesPerSec()
	{
		return bytesPerSec;
	}
}
