
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
import java.net.UnknownHostException;
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
			return NetworkUtils.getNetworkInterfaceInfo( networkInterface );
	}

	/** {@inheritDoc} */
	@Override
	public String showOperatingSystemNetwork()
			throws SocketException, UnknownHostException, InterruptedException
	{
		final String ipAddress = "224.168.5.250";
		final int port = 50050;

		MessageReceiver receiver = new MessageReceiver( ipAddress, port );
		receiver.startReceiver( null );

		SimpleReceiverListener listener = new SimpleReceiverListener();
		receiver.registerReceiverListener( listener );

		MessageSender sender = new MessageSender( ipAddress, port );
		sender.startSender( null );

		sender.send( "showOperatingSystemNetwork" );

		for ( int i = 0; i < 20; i++ )
		{
			if ( listener.getIpAddress() == null )
				Thread.sleep( 50 );
			else
				break;
		}

		sender.stopSender();
		receiver.stopReceiver();

		if ( listener.getIpAddress() == null )
			return "Could not find the operating system network interface.";

		InetAddress messageAddress = InetAddress.getByName( listener.getIpAddress() );
		NetworkInterface messageInterface = NetworkInterface.getByInetAddress( messageAddress );

		return NetworkUtils.getNetworkInterfaceInfo( messageInterface );
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

			if ( NetworkUtils.isUsable( netif ) )
				list.add( NetworkUtils.getNetworkInterfaceInfo( netif ) );
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
			list.add( NetworkUtils.getNetworkInterfaceInfo( netif ) );
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
}
