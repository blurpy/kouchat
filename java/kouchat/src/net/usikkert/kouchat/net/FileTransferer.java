
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

import java.io.*;
import java.net.*;
import net.usikkert.kouchat.gui.FileStatus;
import net.usikkert.kouchat.misc.Nick;

public class FileTransferer
{
	public boolean send( Nick nick, int port, File file )
	{
		boolean sent = false;
		
		FileInputStream fis = null;
		OutputStream os = null;
		Socket sock = null;
		FileStatus fs = null;
		
		try
		{
			int counter = 0;
			
			while ( sock == null && counter < 10 )
			{
				counter++;
				
				try
				{
					sock = new Socket( InetAddress.getByName( nick.getIpAddress() ), port );
				}
				
				catch ( UnknownHostException e1 )
				{
					e1.printStackTrace();
				}
				
				catch ( IOException e1 )
				{
					e1.printStackTrace();
				}
				
				try
				{
					Thread.sleep( 100 );
				}
				
				catch ( InterruptedException e ) {}
			}
		
			if ( sock != null )
			{
				fis = new FileInputStream( file );
				os = sock.getOutputStream();
				
				byte b[] = new byte[1024];
				long transferred = 0;
				int tmpTransferred = 0;
				int percent = 0;
				int tmpPercent = 0;
				
				fs = new FileStatus( "Sending " + file.getName() + " to " + nick.getNick() + "...",
						( transferred / 1024 ) + "KB of " + ( file.length() / 1024 ) + "KB are transferred..." );
				
				while ( ( tmpTransferred = fis.read( b ) ) != -1 && !fs.isCancel() )
				{
					os.write( b, 0, tmpTransferred );
					transferred += tmpTransferred;
					percent = (int) ( ( transferred * 100 ) / file.length() );
					
					if ( percent > tmpPercent )
					{
						tmpPercent = percent;
						fs.setStatus( percent );
						fs.setSendingLabelText( ( transferred / 1024 ) + "KB of " + ( file.length() / 1024 )
								+ "KB are transferred..." );
					}
				}
				
				if ( !fs.isCancel() && transferred == file.length() )
					sent = true;
			}
		}
		
		catch ( UnknownHostException e )
		{
			e.printStackTrace();
		}
		
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if ( fis != null )
					fis.close();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( os != null )
					os.flush();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( os != null )
					os.close();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( sock != null )
					sock.close();
			}
			catch ( IOException e ) {}
			
			if ( fs != null )
				fs.close();
		}
		
		return sent;
	}
	
	public boolean receive( Nick nick, int port, File file, long size )
	{
		boolean received = false;
		
		ServerSocket sSock = null;
		Socket sock = null;
		FileOutputStream fos = null;
		InputStream is = null;
		FileStatus fs = null;
		
		try
		{
			sSock = new ServerSocket( port );
			
			ReceiveTimeoutThread rtt = new ReceiveTimeoutThread( sSock, sock );
			rtt.start();
			
			sock = sSock.accept();
			fos = new FileOutputStream( file );
			is = sock.getInputStream();
		
			byte b[] = new byte[1024];
			long transferred = 0;
			int tmpTransferred = 0;
			int percent = 0;
			int tmpPercent = 0;
			
			fs = new FileStatus( "Receiving " + file.getName() + " from " + nick.getNick() + "...",
					( transferred / 1024 ) + "KB of " + ( size / 1024 ) + "KB are transferred..." );
			
			while ( ( tmpTransferred = is.read( b ) ) != -1 && !fs.isCancel())
			{
				fos.write( b, 0, tmpTransferred );
				transferred += tmpTransferred;
				percent = (int) ( ( transferred * 100 ) / size );
				
				if ( percent > tmpPercent )
				{
					tmpPercent = percent;
					fs.setStatus( percent );
					fs.setSendingLabelText( ( transferred / 1024 ) + "KB of " + ( size / 1024 ) + "KB are transferred..." );
				}
			}
			
			if ( !fs.isCancel() && transferred == size )
				received = true;
		}
		
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if ( is != null )
					is.close();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( fos != null )
					fos.flush();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( fos != null )
					fos.close();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( sock != null )
					sock.close();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( sSock != null )
					sSock.close();
			}
			
			catch ( IOException e ) {}
			
			if ( fs != null )
				fs.close();
		}
		
		return received;
	}
	
	// No point in waiting for a connection forever
	private class ReceiveTimeoutThread extends Thread
	{
		private ServerSocket sSock;
		private Socket sock;
		
		public ReceiveTimeoutThread( ServerSocket sSock, Socket sock )
		{
			this.sSock = sSock;
			this.sock = sock;
		}
		
		public void run()
		{
			try
			{
				sleep( 15000 );
			}
			
			catch ( InterruptedException e ) {}
			
			if ( sock == null )
			{
				try
				{
					sSock.close();
				}
				
				catch ( IOException e ) {}
			}
		}
	}
}
