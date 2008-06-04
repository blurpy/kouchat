
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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.util.Enumeration;

/**
 * This is a JMX MBean for getting information about the current network status.
 *
 * @author Christian Ihle
 */
public class NetworkInformation implements NetworkInformationMBean
{
	/** {@inheritDoc} */
	@Override
	public String showCurrentNetwork() throws SocketException
	{
		NetworkInterface networkInterface = NetworkSelector.getCurrentNetworkInterface();

		if ( networkInterface == null )
			return "No current network interface.";
		else
			return getNetworkInterfaceInfo( networkInterface );
	}

	/** {@inheritDoc} */
	@Override
	public String showUsableNetworks() throws SocketException
	{
		StringBuilder sb = new StringBuilder();

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		if ( networkInterfaces == null )
			return "No usable network interfaces detected.";

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();

			if ( NetworkSelector.isUsable( netif ) )
				sb.append( getNetworkInterfaceInfo( netif ) );
		}

		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public String showAllNetworks() throws SocketException
	{
		StringBuilder sb = new StringBuilder();

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		if ( networkInterfaces == null )
			return "No network interfaces detected.";

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();
			sb.append( getNetworkInterfaceInfo( netif ) );
		}

		return sb.toString();
	}

	/**
	 * Constructs a string with the information found on a {@link NetworkInterface}.
	 *
	 * @param netif The network interface to check.
	 * @return A string with information.
	 * @throws SocketException In case of network errors.
	 */
	private static String getNetworkInterfaceInfo( final NetworkInterface netif ) throws SocketException
	{
		if ( netif == null )
			return "Invalid network interface.";

		String ipaddr = "";
		Enumeration<InetAddress> inetAddresses = netif.getInetAddresses();

		while ( inetAddresses.hasMoreElements() )
		{
			InetAddress inetAddress = inetAddresses.nextElement();
			ipaddr += inetAddress.getHostAddress() + " ";
		}

		return "Interface name: " + netif.getDisplayName() + "\n"
				+ "Device: " + netif.getName() + "\n"
				+ "Is loopback: " + netif.isLoopback() + "\n"
				+ "Is up: " + netif.isUp() + "\n"
				+ "Is p2p: " + netif.isPointToPoint() + "\n"
				+ "Is virtual: " + netif.isVirtual() + "\n"
				+ "Supports multicast: " + netif.supportsMulticast() + "\n"
				+ "IP addresses: " + ipaddr + "\n\n";
	}
}
