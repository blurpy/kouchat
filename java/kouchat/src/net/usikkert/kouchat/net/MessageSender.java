
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

	/** The multicast socket used for sending messages. */
	private MulticastSocket mcSocket;

	/** The inetaddress object with the multicast ip address to send messages to. */
	private InetAddress address;

	/** If connected to the network or not. */
	private boolean connected;

	/** The error handler for registering important messages. */
	private final ErrorHandler errorHandler;

	/** The port to send messages to. */
	private final int port;

	/**
	 * Default constructor.
	 *
	 * <p>Initializes the network with the default ip address and port.</p>
	 *
	 * @see Constants#NETWORK_IP
	 * @see Constants#NETWORK_CHAT_PORT
	 */
	public MessageSender()
	{
		this( Constants.NETWORK_IP, Constants.NETWORK_CHAT_PORT );
	}

	/**
	 * Alternative constructor.
	 *
	 * <p>Initializes the network with the given ip address and port.</p>
	 *
	 * @param ipAddress Multicast ip address to connect to.
	 * @param port Port to connect to.
	 */
	public MessageSender( final String ipAddress, final int port )
	{
		this.port = port;
		errorHandler = ErrorHandler.getErrorHandler();

		try
		{
			address = InetAddress.getByName( ipAddress );
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
			errorHandler.showCriticalError( "Failed to initialize the network:\n" + e + "\n"
					+ Constants.APP_NAME + " will now shutdown." );
			System.exit( 1 );
		}
	}

	/**
	 * Sends a multicast packet to other clients over the network.
	 *
	 * @param message The message to send in the packet.
	 * @see Constants#MESSAGE_CHARSET
	 * @see Constants#NETWORK_PACKET_SIZE
	 */
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

				DatagramPacket packet = new DatagramPacket( encodedMsg, size, address, port );
				mcSocket.send( packet );
			}

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, "Could not send message: " + message );
			}
		}
	}

	/**
	 * Disconnects from the network and closes the multicast socket.
	 */
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

	/**
	 * Connects to the network with the given network interface, or gives
	 * the control to the operating system to choose if <code>null</code>
	 * is given.
	 *
	 * @param networkInterface The network interface to use, or <code>null</code>.
	 * @return If connected to the network or not.
	 */
	public boolean startSender( final NetworkInterface networkInterface )
	{
		LOG.log( Level.FINE, "Connecting..." );

		try
		{
			if ( connected )
			{
				LOG.log( Level.FINE, "Already connected." );
			}

			else
			{
				if ( mcSocket == null )
					mcSocket = new MulticastSocket( port );

				if ( networkInterface != null )
					mcSocket.setNetworkInterface( networkInterface );

				mcSocket.joinGroup( address );
				mcSocket.setTimeToLive( 64 );
				connected = true;
				LOG.log( Level.FINE, "Connected to " + mcSocket.getNetworkInterface().getDisplayName() + "." );
			}
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, "Could not start sender: " + e.toString() );

			if ( mcSocket != null )
			{
				if ( !mcSocket.isClosed() )
					mcSocket.close();

				mcSocket = null;
			}
		}

		return connected;
	}
}
