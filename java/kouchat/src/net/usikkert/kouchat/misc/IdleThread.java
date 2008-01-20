
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

package net.usikkert.kouchat.misc;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.ui.UserInterface;

/**
 * This thread is responsible for sending a special "idle"
 * message every IDLE_TIME milliseconds to inform other clients
 * that this client is still online. It will also check if
 * other clients have stopped sending these messages,
 * and if that is the case, remove them and show a message
 * in the user interface.
 *
 * @author Christian Ihle
 */
public class IdleThread extends Thread
{
	private static final Logger LOG = Logger.getLogger( IdleThread.class.getName() );

	/**
	 * Number of milliseconds to wait before the next
	 * idle message will be sent.
	 */
	private static final int IDLE_TIME = 15000;

	/**
	 * When the network is working correctly this client will
	 * receive its own idle messages as well. If the client
	 * does not receive one of its own idle messages after
	 * this number of milliseconds, then something is wrong,
	 * and a restart of the network will be initiated.
	 */
	private static final int RESTART_TIME = 20000;

	/**
	 * If an idle message has not been received from another
	 * client in this number of milliseconds, then it's not
	 * on the network anymore and must be removed.
	 */
	private static final int TIMEOUT = 120000;

	private final Controller controller;
	private final NickList nickList;
	private final NickDTO me;
	private final TransferList tList;
	private final ErrorHandler errorHandler;
	private final MessageController msgController;

	private boolean run;

	/**
	 * Constructor. Makes sure the thread is ready to start.
	 *
	 * @param controller The controller.
	 * @param ui The user interface.
	 */
	public IdleThread( final Controller controller, final UserInterface ui )
	{
		this.controller = controller;

		nickList = controller.getNickList();
		me = Settings.getSettings().getMe();
		tList = controller.getTransferList();
		errorHandler = ErrorHandler.getErrorHandler();
		msgController = ui.getMessageController();

		run = true;
		setName( "IdleThread" );
	}

	/**
	 * This is where most of the action is.
	 *
	 * <li>Sends idle messages
	 * <li>Restarts the network if there are problems
	 * <li>Removes timed out clients
	 *
	 * The application will exit if this thread is interrupted.
	 */
	public void run()
	{
		// In case of any error messages during startup
		me.setLastIdle( System.currentTimeMillis() );

		while ( run )
		{
			try
			{
				sleep( IDLE_TIME );

				controller.sendIdleMessage();

				if ( me.getLastIdle() < System.currentTimeMillis() - RESTART_TIME )
				{
					if ( controller.restart() )
						me.setLastIdle( System.currentTimeMillis() );
				}

				boolean timeout = false;

				for ( int i = 0; i < nickList.size(); i++ )
				{
					NickDTO temp = nickList.get( i );

					if ( temp.getCode() != me.getCode() && temp.getLastIdle() < System.currentTimeMillis() - TIMEOUT )
					{
						nickList.remove( temp );
						userTimedOut( temp );
						timeout = true;
						i--;
					}
				}

				if ( timeout )
					controller.updateAfterTimeout();
			}

			catch ( final InterruptedException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
				run = false;
				errorHandler.showCriticalError( "The idle thread failed:\n" + e + "\n"
						+ Constants.APP_NAME + " will now shutdown." );
				System.exit( 1 );
			}
		}
	}

	/**
	 * When a user times out, all current file transfers must
	 * be cancelled, and messages must be shown in the normal
	 * chat window, and the private chat window.
	 *
	 * @param user The user which timed out.
	 */
	private void userTimedOut( final NickDTO user )
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

		msgController.showSystemMessage( user.getNick() + " timed out" );

		if ( user.getPrivchat() != null )
		{
			msgController.showPrivateSystemMessage( user, user.getNick() + " timed out" );
			user.getPrivchat().setLoggedOff();
		}
	}

	/**
	 * Shuts down the thread in a controlled manner.
	 */
	public void stopThread()
	{
		run = false;
	}
}
