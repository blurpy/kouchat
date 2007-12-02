
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

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.misc.NickDTO;

public class TransferList
{
	private List<FileSender> senders;
	private List<FileReceiver> receivers;

	public TransferList()
	{
		senders = new ArrayList<FileSender>();
		receivers = new ArrayList<FileReceiver>();
	}

	public void addFileSender( FileSender fileSender )
	{
		senders.add( fileSender );
	}

	public void removeFileSender( FileSender fileSender )
	{
		senders.remove( fileSender );
	}

	public FileSender getFileSender( NickDTO user, String fileName, int fileHash )
	{
		FileSender fileSender = null;

		for ( FileSender fs : senders )
		{
			if ( fs.getNick() == user && fs.getFileName().equals( fileName ) && fs.getFile().hashCode() == fileHash )
			{
				fileSender = fs;
				break;
			}
		}

		return fileSender;
	}

	public List<FileSender> getFileSenders( NickDTO user )
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

	public void addFileReceiver( FileReceiver fileReceiver )
	{
		receivers.add( fileReceiver );
	}

	public void removeFileReceiver( FileReceiver fileReceiver )
	{
		receivers.remove( fileReceiver );
	}

	public List<FileReceiver> getFileReceivers( NickDTO user )
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
