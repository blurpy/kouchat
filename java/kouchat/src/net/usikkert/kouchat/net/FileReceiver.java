
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.util.ByteCounter;
import net.usikkert.kouchat.util.Loggers;

public class FileReceiver implements FileTransfer
{
	private static final Logger LOG = Loggers.NETWORK_LOG;

	private final NickDTO nick;
	private final long size;
	private final File file;
	private final Direction direction;
	private final ByteCounter bCounter;

	private int percent;
	private long transferred;
	private boolean received, cancel;
	private FileTransferListener listener;
	private ServerSocket sSock;
	private Socket sock;
	private FileOutputStream fos;
	private InputStream is;

	public FileReceiver( final NickDTO nick, final File file, final long size )
	{
		this.nick = nick;
		this.file = file;
		this.size = size;

		direction = Direction.RECEIVE;
		bCounter = new ByteCounter();
	}

	public int startServer() throws ServerException
	{
		int port = Constants.NETWORK_FILE_TRANSFER_PORT;
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

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, "Could not open " + port, e );
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

		try
		{
			if ( sSock != null )
			{
				sock = sSock.accept();
				listener.statusTransferring();
				fos = new FileOutputStream( file );
				is = sock.getInputStream();

				byte[] b = new byte[1024];
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

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString() );
			listener.statusFailed();
		}

		finally
		{
			stopReceiver();
		}

		return received;
	}

	private void stopReceiver()
	{
		try
		{
			if ( is != null )
			{
				is.close();
				is = null;
			}
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		try
		{
			if ( fos != null )
				fos.flush();
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		try
		{
			if ( fos != null )
			{
				fos.close();
				fos = null;
			}
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		try
		{
			if ( sock != null )
			{
				sock.close();
				sock = null;
			}
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		try
		{
			if ( sSock != null )
			{
				sSock.close();
				sSock = null;
			}
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}
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
		stopReceiver();
		listener.statusFailed();
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
	public NickDTO getNick()
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

	@Override
	public void registerListener( final FileTransferListener listener )
	{
		this.listener = listener;
		listener.statusWaiting();
	}

	// No point in waiting for a connection forever
	private class TimeoutThread extends Thread
	{
		public TimeoutThread()
		{
			setName( "TimeoutThread" );
		}

		@Override
		public void run()
		{
			try
			{
				sleep( 15000 );
			}

			catch ( final InterruptedException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}

			try
			{
				if ( sSock != null )
				{
					sSock.close();
					sSock = null;
				}
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}
		}
	}
}
