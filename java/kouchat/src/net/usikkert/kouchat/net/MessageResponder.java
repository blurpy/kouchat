
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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.MessageListener;
import net.usikkert.kouchat.event.NetworkListener;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.TopicDTO;
import net.usikkert.kouchat.misc.WaitingList;
import net.usikkert.kouchat.util.Tools;

/**
 * This class responds to events from the message parser.
 * 
 * @author Christian Ihle
 */
public class MessageResponder implements MessageListener
{
	private static Logger log = Logger.getLogger( MessageResponder.class.getName() );

	private Controller controller;
	private NickDTO me;
	private Settings settings;
	private TransferList tList;
	private WaitingList wList;
	private NetworkListener listener;

	public MessageResponder( Controller controller, NetworkListener listener )
	{
		this.controller = controller;
		this.listener = listener;

		settings = Settings.getSettings();
		me = settings.getMe();
		tList = controller.getTransferList();
		wList = controller.getWaitingList();
	}

	@Override
	public void messageArrived( final int userCode, final String msg, final int color )
	{
		// A little hack to stop messages from showing before the user is logged on
		Thread t = new Thread()
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

						catch ( InterruptedException e )
						{
							log.log( Level.SEVERE, e.getMessage(), e );
						}
					}
				}

				if ( !controller.isNewUser( userCode ) )
				{
					NickDTO user = controller.getNick( userCode );
					listener.showUserMessage( user.getNick(), msg, color );
				}

				else
				{
					log.log( Level.SEVERE, "Could not find user: " + userCode );
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

	@Override
	public void userLogOff( int userCode )
	{
		NickDTO user = controller.getNick( userCode );
		controller.getNickList().remove( user );
		listener.showSystemMessage( user.getNick() + " logged off..." );
	}

	@Override
	public void userLogOn( NickDTO newUser )
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

		controller.getNickList().add( newUser );
		listener.showSystemMessage( newUser.getNick() + " logged on from " + newUser.getIpAddress() + "..." );
	}

	private void userShowedUp( NickDTO newUser )
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

		controller.getNickList().add( newUser );
		listener.showSystemMessage( newUser.getNick() + " showed up unexpectedly from " + newUser.getIpAddress() + "..." );
	}

	@Override
	public void topicChanged( int userCode, String newTopic, String nick, long time )
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
							listener.showSystemMessage( nick + " changed the topic to: " + newTopic );
						}
						
						else
						{
							listener.showSystemMessage( "Topic is: " + newTopic + " (set by " + 
									nick + " at " + Tools.dateToString( 
											new Date( time ), "HH:mm:ss, dd. MMM. yy" ) + ")" );
						}
						
						topic.changeTopic( newTopic, nick, time );
						listener.showTopic();
					}
				}

				else
				{
					if ( !topic.getTopic().equals( newTopic ) && time > topic.getTime() && wList.isLoggedOn() )
					{
						listener.showSystemMessage( nick + " removed the topic..." );
						topic.changeTopic( "", "", time );
						listener.showTopic();
					}
				}
			}
		}
	}

	@Override
	public void userExposing( NickDTO user )
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
	}

	@Override
	public void meLogOn( String ipAddress )
	{
		me.setIpAddress( ipAddress );
		listener.showSystemMessage( "Today is " + Tools.dateToString( null, "EEEE, d MMMM yyyy" ) );
		listener.showSystemMessage( "You logged on as " + me.getNick() + " from " + ipAddress );
	}

	@Override
	public void writingChanged( int userCode, boolean writing )
	{
		controller.changeWriting( userCode, writing );
	}

	@Override
	public void awayChanged( int userCode, boolean away, String awayMsg )
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
			controller.changeAwayStatus( userCode, away, awayMsg );

			if ( away )
			{
				listener.showSystemMessage( user.getNick() + " went away: " + awayMsg );
			}

			else
			{
				listener.showSystemMessage( user.getNick() + " came back..." );
			}
		}
	}

	@Override
	public void meIdle( String ipAddress )
	{
		me.setLastIdle( System.currentTimeMillis() );

		if ( !me.getIpAddress().equals( ipAddress ) )
			me.setIpAddress( ipAddress );
	}

	@Override
	public void userIdle( int userCode )
	{
		if ( controller.isNewUser( userCode ) )
		{
			wList.addWaitingUser( userCode );
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();
		}

		else
		{
			controller.updateLastIdle( userCode, System.currentTimeMillis() );
		}
	}

	@Override
	public void topicRequested()
	{
		controller.sendTopicMessage( controller.getTopic() );
	}

	@Override
	public void nickCrash()
	{
		me.setNick( "" + me.getCode() );
		listener.showSystemMessage( "Nick crash, resetting nick to " + settings.getMe() );
		listener.showTopic();
	}

	@Override
	public void exposeRequested()
	{
		controller.sendExposingMessage();
	}

	@Override
	public void nickChanged( int userCode, String newNick )
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
			String oldNick = user.getNick();
			controller.changeNick( userCode, newNick );
			listener.showSystemMessage( oldNick + " changed nick to " + newNick );
		}
	}

	@Override
	public void fileSend( final int userCode, final long byteSize, final String fileName, final String user, final int fileHash, final int fileCode )
	{
		if ( controller.isNewUser( userCode ) )
		{
			wList.addWaitingUser( userCode );
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();
		}

		new Thread()
		{
			public void run()
			{
				int counter = 0;

				while ( wList.isWaitingUser( userCode ) && counter < 40 )
				{
					try
					{
						sleep( 50 );
					}

					catch ( InterruptedException e )
					{
						log.log( Level.SEVERE, e.getMessage(), e );
					}
				}

				if ( !controller.isNewUser( userCode ) )
				{
					String size = Tools.byteToString( byteSize );
					listener.showSystemMessage( user + " is trying to send the file " + fileName + " [" + size + "]" );

					if ( listener.askFileSave( user, fileName, size ) )
					{
						File file = listener.showFileSave( fileName );

						if ( file != null )
						{
							NickDTO tempnick = controller.getNick( userCode );
							FileReceiver fileRes = new FileReceiver( tempnick, file, byteSize );
							tList.addFileReceiver( fileRes );
							listener.showTransfer( fileRes );

							try
							{
								int port = fileRes.startServer();
								controller.sendFileAccept( userCode, port, fileHash, fileName );

								if ( fileRes.transfer() )
								{
									listener.showSystemMessage( "Successfully received " + fileName
											+ " from " + user + ", and saved as " + file.getName() );
								}

								else
								{
									listener.showSystemMessage( "Failed to receive " + fileName + " from " + user );
									fileRes.cancel();
								}
							}

							catch ( ServerException e )
							{
								log.log( Level.SEVERE, e.getMessage(), e );
								listener.showSystemMessage( "Failed to receive " + fileName + " from " + user );
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
							listener.showSystemMessage( "You declined to receive " + fileName + " from " + user );
							controller.sendFileAbort( userCode, fileHash, fileName );
						}
					}

					else
					{
						listener.showSystemMessage( "You declined to receive " + fileName + " from " + user );
						controller.sendFileAbort( userCode, fileHash, fileName );
					}
				}

				else
				{
					log.log( Level.SEVERE, "Could not find user: " + user );
				}
			}
		}.start();
	}

	@Override
	public void fileSendAborted( int userCode, String fileName, int fileHash )
	{
		NickDTO user = controller.getNick( userCode );
		FileSender fileSend = tList.getFileSender( user, fileName, fileHash );

		if ( fileSend != null )
		{
			fileSend.cancel();
			listener.showSystemMessage( user.getNick() + " aborted sending of " + fileName );
			tList.removeFileSender( fileSend );
		}
	}

	@Override
	public void fileSendAccepted( final int userCode, final String fileName, final int fileHash, final int port )
	{
		new Thread()
		{
			public void run()
			{
				NickDTO fUser = controller.getNick( userCode );
				FileSender fileSend = tList.getFileSender( fUser, fileName, fileHash );

				if ( fileSend != null )
				{
					listener.showSystemMessage( fUser.getNick() + " accepted sending of "	+ fileName );

					// Give the server some time to set up the connection first
					try
					{
						Thread.sleep( 200 );
					}

					catch ( InterruptedException e )
					{
						log.log( Level.SEVERE, e.getMessage(), e );
					}

					if ( fileSend.transfer( port ) )
					{
						listener.showSystemMessage( fileName + " successfully sent to " + fUser.getNick() );
					}

					else
					{
						listener.showSystemMessage( "Failed to send " + fileName + " to " + fUser.getNick() );
					}

					tList.removeFileSender( fileSend );
				}
			}
		}.start();
	}
}
