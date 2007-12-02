
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

package net.usikkert.kouchat.net;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;

/**
 * Sends UDP packets directly to a user. Useful for private chat,
 * where not everyone should get the packets.
 *
 * @author Christian Ihle
 */
public class UDPSender
{
	private static Logger log = Logger.getLogger( UDPSender.class.getName() );

	private DatagramSocket udpSocket;
	private boolean started;
	private ErrorHandler errorHandler;

	/**
	 * Default constructor.
	 */
	public UDPSender()
	{
		errorHandler = ErrorHandler.getErrorHandler();
	}

	/**
	 * Sends a packet with a message to a user.
	 *
	 * @param message The message to send.
	 * @param ip The ip address of the user.
	 * @param port The port to send the message to.
	 */
	public void send( String message, String ip, int port )
	{
		if ( started )
		{
			try
			{
				InetAddress address = InetAddress.getByName( ip );
				DatagramPacket packet = new DatagramPacket( message.getBytes( Constants.NETWORK_CHARSET ),
						message.length(), address, port );
				udpSocket.send( packet );
			}

			catch ( IOException e )
			{
				log.log( Level.SEVERE, "Could not send message: " + message );
			}
		}
	}

	/**
	 * Closes the UDP socket.
	 */
	public void stopSender()
	{
		started = false;

		if ( udpSocket != null && !udpSocket.isClosed() )
		{
			udpSocket.close();
		}
	}

	/**
	 * Creates a new UDP socket.
	 */
	public void startSender()
	{
		try
		{
			udpSocket = new DatagramSocket();
			started = true;
		}

		catch ( IOException e )
		{
			log.log( Level.SEVERE, e.toString(), e );
			errorHandler.showError( "Failed to initialize network:\n" + e +
					"\n\nYou will not be able to send private messages!" );
		}
	}
}
