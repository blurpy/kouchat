
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

import java.net.NetworkInterface;
import java.net.SocketException;

import java.util.Enumeration;

/**
 * This class is responsible for selecting the network interface to use.
 *
 * @author Christian Ihle
 */
public final class NetworkSelector
{
	/** The current network interface. */
	private static NetworkInterface networkInterface;

	/**
	 * Private constructor. Only static methods here.
	 */
	private NetworkSelector()
	{

	}

	/**
	 * Locates a network interface to use.
	 *
	 * @return The network interface found, or <code>null</code>.
	 * @throws SocketException In case of network issues.
	 */
	public static synchronized NetworkInterface selectNetworkInterface() throws SocketException
	{
		if ( isUsable( networkInterface ) )
			return networkInterface;

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		// Because null is returned if no network interfaces are found
		if ( networkInterfaces == null )
		{
			networkInterface = null;
			return networkInterface;
		}

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();

			if ( isUsable( netif ) )
			{
				networkInterface = netif;
				return networkInterface;
			}
		}

		networkInterface = null;
		return networkInterface;
	}

	/**
	 * Returns the current network interface.
	 *
	 * @return The current network interface.
	 */
	public static synchronized NetworkInterface getCurrentNetworkInterface()
	{
		return networkInterface;
	}

	/**
	 * Checks if the network interface is up, and usable for multicast.
	 *
	 * @param netif The network interface to check.
	 * @return True if the network interface is usable.
	 * @throws SocketException In case of network issues.
	 */
	public static synchronized boolean isUsable( final NetworkInterface netif ) throws SocketException
	{
		if ( netif == null )
			return false;
		else
			return netif.isUp() && !netif.isLoopback() && !netif.isPointToPoint()
					&& !netif.isVirtual() && netif.supportsMulticast();
	}
}
