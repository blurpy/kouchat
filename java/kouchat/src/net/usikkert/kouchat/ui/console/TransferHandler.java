
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

package net.usikkert.kouchat.ui.console;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.net.FileTransfer;

public class TransferHandler implements FileTransferListener
{
	public TransferHandler( FileTransfer fileTransfer )
	{
		fileTransfer.registerListener( this );
	}
	
	@Override
	public void statusCompleted()
	{

	}

	@Override
	public void statusConnecting()
	{

	}

	@Override
	public void statusFailed()
	{

	}

	@Override
	public void statusTransferring()
	{

	}

	@Override
	public void statusWaiting()
	{

	}

	@Override
	public void transferUpdate()
	{

	}
}
