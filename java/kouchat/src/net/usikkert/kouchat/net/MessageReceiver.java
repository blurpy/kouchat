
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
import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.ErrorHandler;

public class MessageReceiver implements Runnable
{
	private static final Logger LOG = Logger.getLogger( MessageReceiver.class.getName() );

	private MulticastSocket mcSocket;
	private InetAddress address;
	private ReceiverListener listener;
	private boolean connected;
	private Thread worker;
	private ErrorHandler errorHandler;

	public MessageReceiver()
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

	public void run()
	{
		while ( connected )
		{
			try
			{
				DatagramPacket packet = new DatagramPacket(
						new byte[Constants.NETWORK_PACKET_SIZE], Constants.NETWORK_PACKET_SIZE );

				mcSocket.receive( packet );
				String ip = packet.getAddress().getHostAddress();
				String message = new String( packet.getData(), Constants.MESSAGE_CHARSET ).trim();

				if ( listener != null )
					listener.messageArrived( message, ip );
			}

			catch ( IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}
		}
	}

	private void startThread()
	{
		worker = new Thread( this, "MessageReceiverWorker" );
		worker.start();
	}

	public void startReceiver()
	{
		if ( connected )
		{
			stopReceiver();
		}

		try
		{
			mcSocket.joinGroup( address );
			connected = true;
			startThread();
		}

		catch ( IOException e )
		{
			LOG.log( Level.SEVERE, "Could not start receiver: " + e.toString() );
		}
	}

	public void stopReceiver()
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

	public boolean restartReceiver()
	{
		LOG.log( Level.WARNING, "Restarting receiver." );

		boolean success = false;

		try
		{
			mcSocket.leaveGroup( address );
		}

		catch ( IOException e )
		{
			LOG.log( Level.WARNING, "Leaving group: " + e.toString() );
		}

		try
		{
			mcSocket.joinGroup( address );
			success = true;
		}

		catch ( IOException e )
		{
			LOG.log( Level.WARNING, "Joining group: " + e.toString() );
		}

		if ( success && ( worker == null || !worker.isAlive() ) )
		{
			LOG.log( Level.SEVERE, "Thread is dead. Restarting." );
			connected = true;
			startThread();
		}

		return success;
	}

	public void registerReceiverListener( ReceiverListener listener )
	{
		this.listener = listener;
	}
}
