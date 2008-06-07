
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.usikkert.kouchat.util.Validate;

/**
 * This is a JMX MBean for the network service.
 *
 * @author Christian Ihle
 */
public class NetworkInformation implements NetworkInformationMBean
{
	/** Information and control of the network. */
	private final ConnectionWorker connectionWorker;

	/**
	 * Constructor.
	 *
	 * @param connectionWorker To get information about the network, and control the network.
	 */
	public NetworkInformation( final ConnectionWorker connectionWorker )
	{
		Validate.notNull( connectionWorker, "Connection worker can not be null" );
		this.connectionWorker = connectionWorker;
	}

	/** {@inheritDoc} */
	@Override
	public String showCurrentNetwork() throws SocketException
	{
		NetworkInterface networkInterface = connectionWorker.getCurrentNetworkInterface();

		if ( networkInterface == null )
			return "No current network interface.";
		else
			return getNetworkInterfaceInfo( networkInterface );
	}

	/** {@inheritDoc} */
	@Override
	public String[] showUsableNetworks() throws SocketException
	{
		List<String> list = new ArrayList<String>();

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		if ( networkInterfaces == null )
			return new String[] { "No network interfaces detected." };

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();

			if ( connectionWorker.isUsable( netif ) )
				list.add( getNetworkInterfaceInfo( netif ) );
		}

		if ( list.size() == 0 )
			return new String[] { "No usable network interfaces detected." };

		return list.toArray( new String[0] );
	}

	/** {@inheritDoc} */
	@Override
	public String[] showAllNetworks() throws SocketException
	{
		List<String> list = new ArrayList<String>();

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		if ( networkInterfaces == null )
			return new String[] { "No network interfaces detected." };

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();
			list.add( getNetworkInterfaceInfo( netif ) );
		}

		return list.toArray( new String[0] );
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect()
	{
		connectionWorker.stop();
	}

	/** {@inheritDoc} */
	@Override
	public void connect()
	{
		connectionWorker.start();
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
				if ( i % 1 == 0 && i != address.length - 1 )
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
