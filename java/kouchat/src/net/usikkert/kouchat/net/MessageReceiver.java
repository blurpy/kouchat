
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

public class MessageReceiver implements Runnable
{
	private static Logger log = Logger.getLogger( MessageReceiver.class.getName() );
	private static final int BYTESIZE = 1024;

	private MulticastSocket mcSocket;
	private InetAddress address;
	private ReceiverListener listener;
	private boolean run;
	private Thread worker;

	public MessageReceiver()
	{
		try
		{
			mcSocket = new MulticastSocket( Constants.NETWORK_PORT );
			address = InetAddress.getByName( Constants.NETWORK_IP );
		}

		catch ( IOException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	public void run()
	{
		while ( run )
		{
			try
			{
				DatagramPacket packet = new DatagramPacket( new byte[BYTESIZE], BYTESIZE );

				mcSocket.receive( packet );
				String ip = packet.getAddress().getHostAddress();
				String message = new String( packet.getData(), "ISO-8859-15" ).trim();

				if ( listener != null )
					listener.messageArrived( message, ip );
			}

			catch ( IOException e )
			{
				log.log( Level.WARNING, e.getMessage() );
			}
		}
	}

	public void startReceiver()
	{
		if ( run )
		{
			stopReceiver();
		}

		try
		{
			mcSocket.joinGroup( address );
			run = true;
			worker = new Thread( this );
			worker.start();
		}

		catch ( IOException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	public void stopReceiver()
	{
		try
		{
			run = false;

			if ( !mcSocket.isClosed() )
			{
				mcSocket.leaveGroup( address );
				mcSocket.close();
			}
		}

		catch ( IOException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	public boolean restartReceiver()
	{
		log.log( Level.WARNING, "Restarting receiver..." );

		boolean success = false;

		try
		{
			mcSocket.leaveGroup( address );
		}

		catch ( IOException e )
		{
			log.log( Level.WARNING, e.getMessage() );
		}

		try
		{
			mcSocket.joinGroup( address );
			success = true;
		}

		catch ( IOException e )
		{
			log.log( Level.WARNING, e.getMessage() );
		}

		if ( !worker.isAlive() )
		{
			log.log( Level.SEVERE, "Thread died. Restarting..." );
			startReceiver();
		}

		return success;
	}

	public void registerReceiverListener( ReceiverListener listener )
	{
		this.listener = listener;
	}
}
