
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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.TopicDTO;
import net.usikkert.kouchat.misc.WaitingList;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;

/**
 * This class responds to events from the message parser.
 *
 * @author Christian Ihle
 */
public class DefaultMessageResponder implements MessageResponder
{
	private static final Logger LOG = Logger.getLogger( DefaultMessageResponder.class.getName() );

	private final Controller controller;
	private final NickDTO me;
	private final TransferList tList;
	private final WaitingList wList;
	private final UserInterface ui;
	private final MessageController msgController;

	/**
	 * Constructor.
	 *
	 * @param controller The controller to use for communication.
	 * @param ui The user interface to update.
	 */
	public DefaultMessageResponder( final Controller controller, final UserInterface ui )
	{
		this.controller = controller;
		this.ui = ui;

		msgController = ui.getMessageController();
		me = Settings.getSettings().getMe();
		tList = controller.getTransferList();
		wList = controller.getWaitingList();
	}

	/**
	 * Shows a message from a user in the user interface.
	 * If the user that sent the message does not yet exist in the user list,
	 * the user is asked to identify itself before the message is shown.
	 */
	@Override
	public void messageArrived( final int userCode, final String msg, final int color )
	{
		// A little hack to stop messages from showing before the user is logged on
		Thread t = new Thread( "DefaultMessageResponderMessageArrived" )
		{
			public void run()
			{
				if ( isAlive() )
				{
					int counter = 0;

					while ( wList.isWaitingUser( userCode ) && counter < 40 )
					{
						counter++;

						try
						{
							sleep( 50 );
						}

						catch ( final InterruptedException e )
						{
							LOG.log( Level.SEVERE, e.toString(), e );
						}
					}
				}

				if ( !controller.isNewUser( userCode ) )
				{
					NickDTO user = controller.getNick( userCode );

					if ( !user.isAway() )
					{
						msgController.showUserMessage( user.getNick(), msg, color );
						ui.notifyMessageArrived();
					}
				}

				else
				{
					LOG.log( Level.SEVERE, "Could not find user: " + userCode );
				}
			}
		};

		if ( controller.isNewUser( userCode ) )
		{
			wList.addWaitingUser( userCode );
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();

			t.start();
		}

		else
			t.run();
	}

	/**
	 * When a user logs off it is removed from the user list, and
	 * any open private chat window is notified.
	 */
	@Override
	public void userLogOff( final int userCode )
	{
		NickDTO user = controller.getNick( userCode );

		if ( user != null )
		{
			controller.getNickList().remove( user );
			msgController.showSystemMessage( user.getNick() + " logged off" );

			if ( user.getPrivchat() != null )
			{
				msgController.showPrivateSystemMessage( user, user.getNick() + " logged off" );
				user.getPrivchat().setLoggedOff();
			}
		}
	}

	/**
	 * When a user logs on, the user is added to the user list.
	 * If the user's nick name is not valid, it is reset, and reported if
	 * it is identical to the application user's nick.
	 */
	@Override
	public void userLogOn( final NickDTO newUser )
	{
		if ( me.getNick().trim().equalsIgnoreCase( newUser.getNick() ) )
		{
			controller.sendNickCrashMessage( newUser.getNick() );
			newUser.setNick( "" + newUser.getCode() );
		}

		else if ( controller.isNickInUse( newUser.getNick() ) )
		{
			newUser.setNick( "" + newUser.getCode() );
		}

		else if ( !Tools.isValidNick( newUser.getNick() ) )
		{
			newUser.setNick( "" + newUser.getCode() );
		}

		controller.getNickList().add( newUser );
		msgController.showSystemMessage( newUser.getNick() + " logged on from " + newUser.getIpAddress() );
	}

	/**
	 * If a user exposes itself without having sent a logon message first,
	 * and the expose is not part of the application startup, the user
	 * is checked and added to the user list.
	 *
	 * @param newUser The unknown user.
	 */
	private void userShowedUp( final NickDTO newUser )
	{
		if ( me.getNick().trim().equalsIgnoreCase( newUser.getNick() ) )
		{
			controller.sendNickCrashMessage( newUser.getNick() );
			newUser.setNick( "" + newUser.getCode() );
		}

		else if ( controller.isNickInUse( newUser.getNick() ) )
		{
			newUser.setNick( "" + newUser.getCode() );
		}

		else if ( !Tools.isValidNick( newUser.getNick() ) )
		{
			newUser.setNick( "" + newUser.getCode() );
		}

		controller.getNickList().add( newUser );
		msgController.showSystemMessage( newUser.getNick() + " showed up unexpectedly from " + newUser.getIpAddress() );
	}

	/**
	 * Updates the topic, and shows it.
	 */
	@Override
	public void topicChanged( final int userCode, final String newTopic, final String nick, final long time )
	{
		if ( controller.isNewUser( userCode ) )
		{
			wList.addWaitingUser( userCode );
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();
		}

		else
		{
			if ( time > 0 && nick.length() > 0 )
			{
				TopicDTO topic = controller.getTopic();

				if ( newTopic != null )
				{
					if ( !newTopic.equals( topic.getTopic() ) && time > topic.getTime() )
					{
						if ( wList.isLoggedOn() )
						{
							msgController.showSystemMessage( nick + " changed the topic to: " + newTopic );
						}

						// Shown during startup.
						else
						{
							String date = Tools.dateToString( new Date( time ), "HH:mm:ss, dd. MMM. yy" );
							msgController.showSystemMessage( "Topic is: " + newTopic + " (set by " + nick + " at " + date + ")" );
						}

						topic.changeTopic( newTopic, nick, time );
						ui.showTopic();
					}
				}

				else
				{
					if ( !topic.getTopic().equals( newTopic ) && time > topic.getTime() && wList.isLoggedOn() )
					{
						msgController.showSystemMessage( nick + " removed the topic" );
						topic.changeTopic( "", "", time );
						ui.showTopic();
					}
				}
			}
		}
	}

	/**
	 * Adds unknown users that are exposing themselves.
	 * This happens mostly during startup, but can also happen after a timeout.
	 */
	@Override
	public void userExposing( final NickDTO user )
	{
		if ( controller.isNewUser( user.getCode() ) )
		{
			// Usually this happens when someone returns from a timeout
			if ( wList.isLoggedOn() )
			{
				if ( wList.isWaitingUser( user.getCode() ) )
					wList.removeWaitingUser( user.getCode() );

				userShowedUp( user );
			}

			// This should ONLY happen during logon
			else
			{
				controller.getNickList().add( user );
			}
		}

		else
		{
			NickDTO orgUser = controller.getNick( user.getCode() );

			// When users timeout, there can become sync issues
			if ( !orgUser.getNick().equals( user.getNick() ) )
			{
				nickChanged( user.getCode(), user.getNick() );
			}
		}
	}

	/**
	 * When the user has logged on to the network, the application updates
	 * the status.
	 */
	@Override
	public void meLogOn( final String ipAddress )
	{
		controller.setConnected( true );
		me.setIpAddress( ipAddress );
		String date = Tools.dateToString( null, "EEEE, d MMMM yyyy" );
		msgController.showSystemMessage( "Today is " + date );
		msgController.showSystemMessage( "You logged on as " + me.getNick() + " from " + ipAddress );
		ui.showTopic();
	}

	/**
	 * Updates the writing status of the user.
	 */
	@Override
	public void writingChanged( final int userCode, final boolean writing )
	{
		controller.changeWriting( userCode, writing );
	}

	/**
	 * Updates the away status for the user, both in the main window
	 * and in the private chat window.
	 */
	@Override
	public void awayChanged( final int userCode, final boolean away, final String awayMsg )
	{
		if ( controller.isNewUser( userCode ) )
		{
			wList.addWaitingUser( userCode );
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();
		}

		else
		{
			try
			{
				NickDTO user = controller.getNick( userCode );
				controller.changeAwayStatus( userCode, away, awayMsg );

				if ( away )
					msgController.showSystemMessage( user.getNick() + " went away: " + awayMsg );
				else
					msgController.showSystemMessage( user.getNick() + " came back" );

				if ( user.getPrivchat() != null )
				{
					user.getPrivchat().setAway( away );

					if ( away )
						msgController.showPrivateSystemMessage( user, user.getNick() + " went away: " + user.getAwayMsg() );
					else
						msgController.showPrivateSystemMessage( user, user.getNick() + " came back" );
				}
			}

			catch ( final CommandException e )
			{
				LOG.log( Level.SEVERE, "Something very strange going on here...\n" + e );
			}
		}
	}

	/**
	 * Updates the idle time of the application user,
	 * and checks if the ip address has changed.
	 */
	@Override
	public void meIdle( final String ipAddress )
	{
		me.setLastIdle( System.currentTimeMillis() );

		if ( !me.getIpAddress().equals( ipAddress ) )
		{
			msgController.showSystemMessage( "You changed ip from " + me.getIpAddress() + " to " + ipAddress );
			me.setIpAddress( ipAddress );
		}
	}

	/**
	 * Updates the idle time of the user,
	 * and checks if the user's ip address has changed.
	 */
	@Override
	public void userIdle( final int userCode, final String ipAddress )
	{
		if ( controller.isNewUser( userCode ) )
		{
			wList.addWaitingUser( userCode );
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();
		}

		else
		{
			NickDTO user = controller.getNick( userCode );
			user.setLastIdle( System.currentTimeMillis() );

			if ( !user.getIpAddress().equals( ipAddress ) )
			{
				msgController.showSystemMessage( user.getNick() + " changed ip from " + user.getIpAddress() + " to " + ipAddress );
				user.setIpAddress( ipAddress );
			}
		}
	}

	/**
	 * Sends the current topic.
	 */
	@Override
	public void topicRequested()
	{
		controller.sendTopicMessage();
	}

	/**
	 * Someone sent a message that the application user's nick is
	 * in use, so the nick is reset.
	 */
	@Override
	public void nickCrash()
	{
		controller.changeNick( me.getCode(), "" + me.getCode() );
		msgController.showSystemMessage( "Nick crash, resetting nick to " + me.getNick() );
		ui.showTopic();
	}

	/**
	 * Sends information about this client to the other clients.
	 */
	@Override
	public void exposeRequested()
	{
		controller.sendExposingMessage();
		controller.sendClientInfo();
	}

	/**
	 * Changes the nick of a user, if valid.
	 */
	@Override
	public void nickChanged( final int userCode, final String newNick )
	{
		if ( controller.isNewUser( userCode ) )
		{
			wList.addWaitingUser( userCode );
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();
		}

		else
		{
			NickDTO user = controller.getNick( userCode );

			if ( !controller.isNickInUse( newNick ) && Tools.isValidNick( newNick ) )
			{
				String oldNick = user.getNick();
				controller.changeNick( userCode, newNick );
				msgController.showSystemMessage( oldNick + " changed nick to " + newNick );

				if ( user.getPrivchat() != null )
				{
					msgController.showPrivateSystemMessage( user, oldNick + " changed nick to " + user.getNick() );
					user.getPrivchat().updateNick();
				}
			}

			else
			{
				LOG.log( Level.SEVERE, user.getNick() + " tried to change nick to '" + newNick + "', which is invalid" );
			}
		}
	}

	/**
	 * Asks if the application user wants to receive a file from another user,
	 * and if so, starts a server listening for a file transfer.
	 *
	 * If the user does not exist in the user list, it's asked to identify
	 * itself first.
	 */
	@Override
	public void fileSend( final int userCode, final long byteSize, final String fileName, final String user, final int fileHash, final int fileCode )
	{
		if ( controller.isNewUser( userCode ) )
		{
			wList.addWaitingUser( userCode );
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();
		}

		new Thread( "DefaultMessageResponderFileSend" )
		{
			public void run()
			{
				int counter = 0;

				while ( wList.isWaitingUser( userCode ) && counter < 40 )
				{
					counter++;

					try
					{
						sleep( 50 );
					}

					catch ( final InterruptedException e )
					{
						LOG.log( Level.SEVERE, e.toString(), e );
					}
				}

				if ( !controller.isNewUser( userCode ) )
				{
					String size = Tools.byteToString( byteSize );
					msgController.showSystemMessage( user + " is trying to send the file " + fileName + " [" + size + "]" );

					if ( ui.askFileSave( user, fileName, size ) )
					{
						File file = ui.showFileSave( fileName );

						if ( file != null )
						{
							NickDTO tempnick = controller.getNick( userCode );
							FileReceiver fileRes = new FileReceiver( tempnick, file, byteSize );
							tList.addFileReceiver( fileRes );
							ui.showTransfer( fileRes );

							try
							{
								int port = fileRes.startServer();
								controller.sendFileAccept( userCode, port, fileHash, fileName );

								if ( fileRes.transfer() )
								{
									msgController.showSystemMessage( "Successfully received " + fileName
											+ " from " + user + ", and saved as " + file.getName() );
								}

								else
								{
									msgController.showSystemMessage( "Failed to receive " + fileName + " from " + user );
									fileRes.cancel();
								}
							}

							catch ( final ServerException e )
							{
								LOG.log( Level.SEVERE, e.toString(), e );
								msgController.showSystemMessage( "Failed to receive " + fileName + " from " + user );
								controller.sendFileAbort( userCode, fileHash, fileName );
								fileRes.cancel();
							}

							finally
							{
								tList.removeFileReceiver( fileRes );
							}
						}

						else
						{
							msgController.showSystemMessage( "You declined to receive " + fileName + " from " + user );
							controller.sendFileAbort( userCode, fileHash, fileName );
						}
					}

					else
					{
						msgController.showSystemMessage( "You declined to receive " + fileName + " from " + user );
						controller.sendFileAbort( userCode, fileHash, fileName );
					}
				}

				else
				{
					LOG.log( Level.SEVERE, "Could not find user: " + user );
				}
			}
		} .start();
	}

	/**
	 * The other user stopped a file transfer from the application user.
	 * Updates the status in the file sender.
	 */
	@Override
	public void fileSendAborted( final int userCode, final String fileName, final int fileHash )
	{
		NickDTO user = controller.getNick( userCode );
		FileSender fileSend = tList.getFileSender( user, fileName, fileHash );

		if ( fileSend != null )
		{
			fileSend.cancel();
			msgController.showSystemMessage( user.getNick() + " aborted sending of " + fileName );
			tList.removeFileSender( fileSend );
		}
	}

	/**
	 * The other user has accepted a file transfer. Will try to connect to the
	 * user to send the file.
	 */
	@Override
	public void fileSendAccepted( final int userCode, final String fileName, final int fileHash, final int port )
	{
		new Thread( "DefaultMessageResponderFileSendAccepted" )
		{
			public void run()
			{
				NickDTO user = controller.getNick( userCode );
				FileSender fileSend = tList.getFileSender( user, fileName, fileHash );

				if ( fileSend != null )
				{
					msgController.showSystemMessage( user.getNick() + " accepted sending of " + fileName );

					// Give the server some time to set up the connection first
					try
					{
						Thread.sleep( 200 );
					}

					catch ( final InterruptedException e )
					{
						LOG.log( Level.SEVERE, e.toString(), e );
					}

					if ( fileSend.transfer( port ) )
					{
						msgController.showSystemMessage( fileName + " successfully sent to " + user.getNick() );
					}

					else
					{
						msgController.showSystemMessage( "Failed to send " + fileName + " to " + user.getNick() );
					}

					tList.removeFileSender( fileSend );
				}
			}
		} .start();
	}

	/**
	 * Returns information about this client.
	 */
	@Override
	public void clientInfo( final int userCode, final String client, final long timeSinceLogon, final String operatingSystem, final int privateChatPort )
	{
		NickDTO user = controller.getNick( userCode );

		if ( user != null )
		{
			user.setClient( client );
			user.setLogonTime( System.currentTimeMillis() - timeSinceLogon );
			user.setOperatingSystem( operatingSystem );
			user.setPrivateChatPort( privateChatPort );
		}

		else
			LOG.log( Level.SEVERE, "Could not find user: " + userCode );
	}
}
