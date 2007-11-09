
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

package net.usikkert.kouchat.misc;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.TransferList;

public class IdleThread extends Thread
{
	private static Logger log = Logger.getLogger( IdleThread.class.getName() );

	private boolean run;
	private Controller controller;
	private NickList nickList;
	private NickDTO me;
	private UserInterface ui;
	private TransferList tList;
	private ErrorHandler errorHandler;

	public IdleThread( Controller controller, UserInterface ui )
	{
		this.controller = controller;
		this.ui = ui;

		nickList = controller.getNickList();
		me = Settings.getSettings().getMe();
		tList = controller.getTransferList();
		errorHandler = ErrorHandler.getErrorHandler();
		
		run = true;
	}

	public void run()
	{
		while ( run )
		{
			try
			{
				sleep( 15000 );

				controller.sendIdleMessage();

				if ( me.getLastIdle() < System.currentTimeMillis() - 20000 )
				{
					if ( controller.restartMsgReceiver() )
						me.setLastIdle( System.currentTimeMillis() );
				}

				for ( int i = 0; i < nickList.size(); i++ )
				{
					NickDTO temp = nickList.get( i );

					if ( temp.getCode() != me.getCode() && temp.getLastIdle() < System.currentTimeMillis() - 120000 )
					{
						nickList.remove( temp );
						userTimedOut( temp );
						i--;
					}
				}
			}

			catch ( InterruptedException e )
			{
				log.log( Level.SEVERE, e.getMessage(), e );
				run = false;
				errorHandler.showError( "The idle thread failed:\n" + e );
			}
		}
	}
	
	private void userTimedOut( NickDTO user )
	{
		List<FileSender> fsList = tList.getFileSenders( user );
		List<FileReceiver> frList = tList.getFileReceivers( user );

		for ( FileSender fs : fsList )
		{
			fs.cancel();
		}

		for ( FileReceiver fr : frList )
		{
			fr.cancel();
		}

		ui.getUIMessages().showUserTimedOut( user.getNick() );
		
		if ( user.getPrivchat() != null )
		{
			user.getPrivchat().setLoggedOff();
			ui.getUIMessages().showPrivateUserTimedOut( user );
		}
	}

	public void stopThread()
	{
		run = false;
	}
}
