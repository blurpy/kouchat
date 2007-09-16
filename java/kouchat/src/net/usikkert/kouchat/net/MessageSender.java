
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

public class MessageSender
{
	private static Logger log = Logger.getLogger( MessageSender.class.getName() );
	
	private MulticastSocket mcSocket;
	private InetAddress address;
	
	public MessageSender()
	{
		try
		{
			mcSocket = new MulticastSocket( Constants.NETWORK_PORT );
			address = InetAddress.getByName( Constants.NETWORK_IP );
			mcSocket.joinGroup( address );
		}
		
		catch ( IOException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}
	
	public void send( String message )
	{
		try
		{
			DatagramPacket packet = new DatagramPacket( message.getBytes( "ISO-8859-15" ), message.length(), address, Constants.NETWORK_PORT );
			mcSocket.send( packet );
		}
		
		catch ( IOException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}
	
	public void stopSender()
	{
		try
		{
			mcSocket.leaveGroup( address );
			mcSocket.close();
		}
		
		catch ( IOException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}
}
