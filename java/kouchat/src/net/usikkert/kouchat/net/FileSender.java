
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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.util.ByteCounter;

public class FileSender implements FileTransfer
{
	private static final Logger LOG = Logger.getLogger( FileSender.class.getName() );

	private final NickDTO nick;
	private final File file;
	private final ByteCounter bCounter;
	private final Direction direction;

	private int percent;
	private long transferred;
	private boolean sent, cancel, waiting;
	private FileTransferListener listener;
	private FileInputStream fis;
	private OutputStream os;
	private Socket sock;

	public FileSender( final NickDTO nick, final File file )
	{
		this.nick = nick;
		this.file = file;

		direction = Direction.SEND;
		bCounter = new ByteCounter();
		waiting = true;
	}

	public boolean transfer( final int port )
	{
		if ( !cancel )
		{
			listener.statusConnecting();

			waiting = false;
			sent = false;

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

					catch ( final UnknownHostException e )
					{
						LOG.log( Level.SEVERE, e.toString(), e );
					}

					catch ( final IOException e )
					{
						LOG.log( Level.SEVERE, e.toString(), e );
					}

					try
					{
						Thread.sleep( 100 );
					}

					catch ( final InterruptedException e )
					{
						LOG.log( Level.SEVERE, e.toString(), e );
					}
				}

				if ( sock != null && !cancel )
				{
					listener.statusTransferring();
					fis = new FileInputStream( file );
					os = sock.getOutputStream();

					byte[] b = new byte[1024];
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

			catch ( final UnknownHostException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
				listener.statusFailed();
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString() );
				listener.statusFailed();
			}

			finally
			{
				stopSender();
			}
		}

		return sent;
	}

	private void stopSender()
	{
		try
		{
			if ( fis != null )
			{
				fis.close();
				fis = null;
			}
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		try
		{
			if ( os != null )
				os.flush();
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		try
		{
			if ( os != null )
			{
				os.close();
				os = null;
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
		stopSender();
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

	public boolean isWaiting()
	{
		return waiting;
	}

	@Override
	public void registerListener( final FileTransferListener listener )
	{
		this.listener = listener;
		listener.statusWaiting();
	}
}
