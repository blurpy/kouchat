
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
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;

public class MessageSender
{
	private static final Logger LOG = Logger.getLogger( MessageSender.class.getName() );

	private MulticastSocket mcSocket;
	private InetAddress address;
	private boolean connected;
	private ErrorHandler errorHandler;

	public MessageSender()
	{
		errorHandler = ErrorHandler.getErrorHandler();

		try
		{
			mcSocket = new MulticastSocket( Constants.NETWORK_CHAT_PORT );
			address = InetAddress.getByName( Constants.NETWORK_IP );
		}

		catch ( IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
			errorHandler.showCriticalError( "Failed to initialize the network:\n" + e + "\n"
					+ Constants.APP_NAME + " will now shutdown." );
			System.exit( 1 );
		}
	}

	public void send( String message )
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

			catch ( IOException e )
			{
				LOG.log( Level.WARNING, "Could not send message: " + message );
			}
		}
	}

	public void stopSender()
	{
		connected = false;

		try
		{
			if ( !mcSocket.isClosed() )
			{
				mcSocket.leaveGroup( address );
			}
		}

		catch ( IOException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		if ( !mcSocket.isClosed() )
		{
			mcSocket.close();
		}
	}

	public void startSender()
	{
		try
		{
			mcSocket.joinGroup( address );
			connected = true;
		}

		catch ( IOException e )
		{
			LOG.log( Level.SEVERE, "Could not start sender: " + e.toString() );
		}
	}

	public void restartSender()
	{
		if ( !connected )
		{
			LOG.log( Level.WARNING, "Restarting sender." );
			startSender();
		}
	}
}
