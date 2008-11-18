
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

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.misc.User;

/**
 * This class keeps a list of all the ongoing file transfers.
 *
 * @author Christian Ihle
 */
public class TransferList
{
	private final List<FileSender> senders;
	private final List<FileReceiver> receivers;

	public TransferList()
	{
		senders = new ArrayList<FileSender>();
		receivers = new ArrayList<FileReceiver>();
	}

	public void addFileSender( final FileSender fileSender )
	{
		senders.add( fileSender );
	}

	public void removeFileSender( final FileSender fileSender )
	{
		senders.remove( fileSender );
	}

	public FileSender getFileSender( final User user, final String fileName, final int fileHash )
	{
		FileSender fileSender = null;

		for ( FileSender fs : senders )
		{
			if ( fs.getNick() == user && fs.getFile().getName().equals( fileName ) && fs.getFile().hashCode() == fileHash )
			{
				fileSender = fs;
				break;
			}
		}

		return fileSender;
	}

	public List<FileSender> getFileSenders( final User user )
	{
		List<FileSender> list = new ArrayList<FileSender>();

		for ( FileSender fs : senders )
		{
			if ( fs.getNick() == user )
			{
				list.add( fs );
			}
		}

		return list;
	}

	public List<FileSender> getFileSenders()
	{
		List<FileSender> list = new ArrayList<FileSender>();

		for ( FileSender fs : senders )
		{
			list.add( fs );
		}

		return list;
	}

	public void addFileReceiver( final FileReceiver fileReceiver )
	{
		receivers.add( fileReceiver );
	}

	public void removeFileReceiver( final FileReceiver fileReceiver )
	{
		receivers.remove( fileReceiver );
	}

	public List<FileReceiver> getFileReceivers( final User user )
	{
		List<FileReceiver> list = new ArrayList<FileReceiver>();

		for ( FileReceiver fr : receivers )
		{
			if ( fr.getNick() == user )
			{
				list.add( fr );
			}
		}

		return list;
	}

	public List<FileReceiver> getFileReceivers()
	{
		List<FileReceiver> list = new ArrayList<FileReceiver>();

		for ( FileReceiver fr : receivers )
		{
			list.add( fr );
		}

		return list;
	}
}
