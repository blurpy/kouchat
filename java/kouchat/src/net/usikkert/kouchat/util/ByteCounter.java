
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

package net.usikkert.kouchat.util;

/**
 * Used for calculating number of bytes per second.
 *
 * @author Christian Ihle
 */
public class ByteCounter
{
	private long lastTime, spentTime, bytesPerSec, bytesPerSecCounter;

	public void reset()
	{
		lastTime = System.currentTimeMillis();
		spentTime = 0;
		bytesPerSec = 0;
		bytesPerSecCounter = 0;
	}

	public void update( long bytes )
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

	public long getBytesPerSec()
	{
		return bytesPerSec;
	}
}
