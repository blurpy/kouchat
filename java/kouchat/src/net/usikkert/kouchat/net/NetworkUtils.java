
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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Class containing utility methods for network operations.
 *
 * @author Christian Ihle
 */
public final class NetworkUtils
{
	/**
	 * Private constructor. Only static methods here.
	 */
	private NetworkUtils()
	{

	}

	/**
	 * Checks if the network interface is up, and usable.
	 *
	 * <p>A network interface is usable when it:</p>
	 *
	 * <ul>
	 *   <li>Is up.</li>
	 *   <li>Supports multicast.</li>
	 *   <li>Is not a loopback device, like localhost.</li>
	 *   <li>Is not a point to point device, like a modem.</li>
	 *   <li>Is not virtual, like <code>eth0:1</code>.</li>
	 *   <li>Has an IPv4 address.</li>
	 * </ul>
	 *
	 * @param netif The network interface to check.
	 * @return True if the network interface is usable.
	 * @throws SocketException In case of network issues.
	 */
	public static boolean isUsable( final NetworkInterface netif ) throws SocketException
	{
		if ( netif == null )
			return false;

		else if ( netif.isUp() && !netif.isLoopback() && !netif.isPointToPoint()
				&& !netif.isVirtual() && netif.supportsMulticast() )
		{
			Enumeration<InetAddress> inetAddresses = netif.getInetAddresses();

			while ( inetAddresses.hasMoreElements() )
			{
				InetAddress inetAddress = inetAddresses.nextElement();

				if ( inetAddress instanceof Inet4Address )
					return true;
			}
		}

		return false;
	}

	/**
	 * Constructs a string with the information found on a {@link NetworkInterface}.
	 *
	 * @param netif The network interface to check.
	 * @return A string with information.
	 * @throws SocketException In case of network errors.
	 */
	public static String getNetworkInterfaceInfo( final NetworkInterface netif ) throws SocketException
	{
		if ( netif == null )
			return "Invalid network interface.";

		String ipaddr = "";
		Enumeration<InetAddress> inetAddresses = netif.getInetAddresses();

		while ( inetAddresses.hasMoreElements() )
		{
			InetAddress inetAddress = inetAddresses.nextElement();
			if ( inetAddress instanceof Inet4Address )
				ipaddr += inetAddress.getHostAddress() + " ";
		}

		String hwaddress = "";
		byte[] address = netif.getHardwareAddress();

		// Convert byte array to hex format
		if ( address != null )
		{
			for ( int i = 0; i < address.length; i++ )
			{
				hwaddress += String.format( "%02x", address[i] );
				if ( i != address.length - 1 )
					hwaddress += "-";
			}
		}

		return "Interface name: " + netif.getDisplayName() + "\n"
				+ "Device: " + netif.getName() + "\n"
				+ "Is loopback: " + netif.isLoopback() + "\n"
				+ "Is up: " + netif.isUp() + "\n"
				+ "Is p2p: " + netif.isPointToPoint() + "\n"
				+ "Is virtual: " + netif.isVirtual() + "\n"
				+ "Supports multicast: " + netif.supportsMulticast() + "\n"
				+ "MAC address: " + hwaddress.toUpperCase() + "\n"
				+ "IP addresses: " + ipaddr;
	}
}
