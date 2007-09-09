
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.Nick;
import net.usikkert.kouchat.util.ByteCounter;

public class FileSender implements FileTransfer
{
	private Nick nick;
	private int percent;
	private long transferred;
	private File file;
	private boolean sent, cancel;
	private FileTransferListener listener;
	private Direction direction;
	private ByteCounter bCounter;
	
	public FileSender( Nick nick, File file )
	{
		this.nick = nick;
		this.file = file;
		
		direction = Direction.SEND;
		bCounter = new ByteCounter();
	}
	
	public boolean transfer( int port )
	{
		if ( !cancel )
		{
			listener.statusConnecting();
		}
		
		sent = false;
		FileInputStream fis = null;
		OutputStream os = null;
		Socket sock = null;

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

			if ( sock != null && !cancel )
			{
				listener.statusTransferring();
				fis = new FileInputStream( file );
				os = sock.getOutputStream();

				byte b[] = new byte[1024];
				transferred = 0;
				percent = 0;
				int tmpTransferred = 0;
				int tmpPercent = 0;
				int transCounter = 0;
				bCounter.reset();

				while ( ( tmpTransferred = fis.read( b ) ) != -1 && !cancel )
				{
					os.write( b, 0, tmpTransferred );
					transferred += tmpTransferred;
					percent = (int) ( ( transferred * 100 ) / file.length() );
					bCounter.update( tmpTransferred );
					transCounter++;
					
					if ( percent > tmpPercent || transCounter >= 250 )
					{
						transCounter = 0;
						tmpPercent = percent;
						listener.transferUpdate();
					}
				}

				if ( !cancel && transferred == file.length() )
				{
					sent = true;
					listener.statusCompleted();
				}

				else
				{
					listener.statusFailed();
				}
			}

			else
			{
				listener.statusFailed();
			}
		}

		catch ( UnknownHostException e )
		{
			listener.statusFailed();
		}

		catch ( IOException e )
		{
			listener.statusFailed();
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
		}

		return sent;
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
		listener.statusFailed();
	}

	@Override
	public boolean isTransferred()
	{
		return sent;
	}

	@Override
	public int getPercent()
	{
		return percent;
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
		return file.length();
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
	
	public File getFile()
	{
		return file;
	}

	@Override
	public void registerListener( FileTransferListener listener )
	{
		this.listener = listener;
		listener.statusWaiting();
	}

	public void fail()
	{
		listener.statusFailed();
	}
}
