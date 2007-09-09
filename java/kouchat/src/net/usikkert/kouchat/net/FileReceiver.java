
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.ServerSocket;
import java.net.Socket;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.Nick;
import net.usikkert.kouchat.util.ByteCounter;

public class FileReceiver implements FileTransfer
{	
	private Nick nick;
	private int percent;
	private long transferred, size;
	private File file;
	private boolean received, cancel;
	private FileTransferListener listener;
	private Direction direction;
	private ByteCounter bCounter;
	private ServerSocket sSock;
	
	public FileReceiver( Nick nick, File file, long size )
	{
		this.nick = nick;
		this.file = file;
		this.size = size;
		
		direction = Direction.RECEIVE;
		bCounter = new ByteCounter();
	}
	
	public int startServer() throws ServerException
	{
		int port = 50123;
		boolean done = false;
		int counter = 0;
		
		while ( !done && counter < 10 )
		{
			try
			{
				sSock = new ServerSocket( port );
				TimeoutThread tt = new TimeoutThread();
				tt.start();
				done = true;
			}
			
			catch ( IOException e )
			{
				System.err.println( e + " (" + port + ")" );
				port++;
			}
			
			finally
			{
				counter++;
			}
		}
		
		if ( !done )
			throw new ServerException( "Could not start server" );
		
		return port;
	}
	
	public boolean transfer()
	{
		listener.statusConnecting();
		
		received = false;
		cancel = false;
		
		Socket sock = null;
		FileOutputStream fos = null;
		InputStream is = null;
		
		try
		{
			if ( sSock != null )
			{
				sock = sSock.accept();
				listener.statusTransferring();
				fos = new FileOutputStream( file );
				is = sock.getInputStream();
			
				byte b[] = new byte[1024];
				transferred = 0;
				percent = 0;
				int tmpTransferred = 0;
				int tmpPercent = 0;
				int transCounter = 0;
				bCounter.reset();
				
				while ( ( tmpTransferred = is.read( b ) ) != -1 && !cancel )
				{
					fos.write( b, 0, tmpTransferred );
					transferred += tmpTransferred;
					percent = (int) ( ( transferred * 100 ) / size );
					bCounter.update( tmpTransferred );
					transCounter++;
					
					if ( percent > tmpPercent || transCounter >= 250 )
					{
						transCounter = 0;
						tmpPercent = percent;
						listener.transferUpdate();
					}
				}
				
				if ( !cancel && transferred == size )
				{
					received = true;
					listener.statusCompleted();
				}
				
				else
				{
					listener.statusFailed();
				}
			}
		}
		
		catch ( IOException e )
		{
			listener.statusFailed();
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
				{
					sSock.close();
					sSock = null;
				}
			}
			
			catch ( IOException e ) {}
		}
		
		return received;
	}

	@Override
	public boolean isCanceled()
	{
		return cancel;
	}
	
	@Override
	public void cancel()
	{
		cancel = true;
	}

	@Override
	public int getPercent()
	{
		return percent;
	}

	@Override
	public boolean isTransferred()
	{
		return received;
	}

	@Override
	public String getFileName()
	{
		return file.getName();
	}

	@Override
	public Nick getNick()
	{
		return nick;
	}

	@Override
	public long getTransferred()
	{
		return transferred;
	}
	
	@Override
	public long getFileSize()
	{
		return size;
	}
	
	@Override
	public Direction getDirection()
	{
		return direction;
	}
	
	@Override
	public long getSpeed()
	{
		return bCounter.getBytesPerSec();
	}
	
	public void fail()
	{
		listener.statusFailed();
	}

	@Override
	public void registerListener( FileTransferListener listener )
	{
		this.listener = listener;
		listener.statusWaiting();
	}
	
	// No point in waiting for a connection forever
	private class TimeoutThread extends Thread
	{
		public void run()
		{
			try { sleep( 15000 ); }
			catch ( InterruptedException e ) {}

			try
			{
				if ( sSock != null )
				{
					sSock.close();
					sSock = null;
				}
			}

			catch ( IOException e ) {}
		}
	}
}
