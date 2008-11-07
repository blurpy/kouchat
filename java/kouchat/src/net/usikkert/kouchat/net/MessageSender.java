
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

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.Loggers;

/**
 * This is the class that sends multicast messages over the network.
 *
 * @author Christian Ihle
 */
public class MessageSender
{
	/** The logger. */
	private static final Logger LOG = Loggers.NETWORK_LOG;

	private MulticastSocket mcSocket;
	private InetAddress address;
	private boolean connected;
	private final ErrorHandler errorHandler;

	public MessageSender()
	{
		errorHandler = ErrorHandler.getErrorHandler();

		try
		{
			address = InetAddress.getByName( Constants.NETWORK_IP );
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
			errorHandler.showCriticalError( "Failed to initialize the network:\n" + e + "\n"
					+ Constants.APP_NAME + " will now shutdown." );
			System.exit( 1 );
		}
	}

	public void send( final String message )
	{
		if ( connected )
		{
			try
			{
				byte[] encodedMsg = message.getBytes( Constants.MESSAGE_CHARSET );
				int size = encodedMsg.length;

				if ( size > Constants.NETWORK_PACKET_SIZE )
				{
					LOG.log( Level.WARNING, "Message was " + size + " bytes, which is too large.\n"
							+ " The receiver might not get the complete message.\n'" + message + "'" );
				}

				DatagramPacket packet = new DatagramPacket( encodedMsg, size, address, Constants.NETWORK_CHAT_PORT );
				mcSocket.send( packet );
			}

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, "Could not send message: " + message );
			}
		}
	}

	public void stopSender()
	{
		LOG.log( Level.FINE, "Disconnecting..." );

		if ( !connected )
		{
			LOG.log( Level.FINE, "Not connected." );
		}

		else
		{
			connected = false;

			try
			{
				if ( !mcSocket.isClosed() )
				{
					mcSocket.leaveGroup( address );
				}
			}

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}

			if ( !mcSocket.isClosed() )
			{
				mcSocket.close();
				mcSocket = null;
			}

			LOG.log( Level.FINE, "Disconnected." );
		}
	}

	public boolean startSender( final NetworkInterface networkInterface )
	{
		LOG.log( Level.FINE, "Connecting..." );

		try
		{
			if ( connected )
			{
				LOG.log( Level.FINE, "Already connected." );
			}

			else if ( networkInterface != null )
			{
				if ( mcSocket == null )
					mcSocket = new MulticastSocket( Constants.NETWORK_CHAT_PORT );

				mcSocket.setNetworkInterface( networkInterface );
				mcSocket.joinGroup( address );
				mcSocket.setTimeToLive( 64 );
				connected = true;
				LOG.log( Level.FINE, "Connected to " + mcSocket.getNetworkInterface().getDisplayName() + "." );
			}

			else
			{
				LOG.log( Level.SEVERE, "No network interface found." );
			}
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, "Could not start sender: " + e.toString() );
		}

		return connected;
	}
}
