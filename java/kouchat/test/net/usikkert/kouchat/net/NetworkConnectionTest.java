
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import net.usikkert.kouchat.event.ReceiverListener;

import org.junit.Test;

/**
 * Testing network connections.
 *
 * @author Christian Ihle
 */
public class NetworkConnectionTest
{
	private String messageIP;

	@Test
	public void testFindOperatingSystemNetworkInterface() throws Exception
	{
		MessageSender sender = new MessageSender();
		sender.startSender( null );

		MessageReceiver receiver = new MessageReceiver();
		receiver.startReceiver( null );

		receiver.registerReceiverListener( new ReceiverListener()
		{
			@Override
			public void messageArrived( final String message, final String ipAddress )
			{
				messageIP = ipAddress;
			}
		} );

		sender.send( "Test message :)" );

		Thread.sleep( 100 );

		sender.stopSender();
		receiver.stopReceiver();

		assertNotNull( messageIP );

		InetAddress messageAddress = InetAddress.getByName( messageIP );
		assertNotNull( messageAddress );

		NetworkInterface messageInterface = NetworkInterface.getByInetAddress( messageAddress );
		assertNotNull( messageInterface );
		assertTrue( NetworkUtils.isUsable( messageInterface ) );

		Enumeration<InetAddress> messageAddresses = messageInterface.getInetAddresses();

		boolean ipFound = false;

		while ( messageAddresses.hasMoreElements() )
		{
			InetAddress inetAddress = messageAddresses.nextElement();

			if ( inetAddress.getHostAddress().equals( messageIP ) )
			{
				ipFound = true;
				break;
			}
		}

		assertTrue( ipFound );

		System.out.println( NetworkUtils.getNetworkInterfaceInfo( messageInterface ) );
	}
}
