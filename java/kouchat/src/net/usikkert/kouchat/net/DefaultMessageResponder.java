
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

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.TopicDTO;
import net.usikkert.kouchat.misc.UIMessages;
import net.usikkert.kouchat.misc.UserInterface;
import net.usikkert.kouchat.misc.WaitingList;
import net.usikkert.kouchat.util.Tools;

/**
 * This class responds to events from the message parser.
 * 
 * @author Christian Ihle
 */
public class DefaultMessageResponder implements MessageResponder
{
	private static Logger log = Logger.getLogger( DefaultMessageResponder.class.getName() );

	private Controller controller;
	private NickDTO me;
	private Settings settings;
	private TransferList tList;
	private WaitingList wList;
	private UserInterface ui;
	private UIMessages uiMsg;

	public DefaultMessageResponder( Controller controller, UserInterface ui )
	{
		this.controller = controller;
		this.ui = ui;

		uiMsg = ui.getUIMessages();
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

					if ( !user.isAway() )
					{
						uiMsg.showUserMessage( user.getNick(), msg, color );
						ui.notifyMessageArrived();
					}
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

		if ( user != null )
		{
			controller.getNickList().remove( user );
			uiMsg.showLoggedOff( user.getNick() );
			
			if ( user.getPrivchat() != null )
			{
				user.getPrivchat().setLoggedOff();
				uiMsg.showPrivateLoggedOff( user );
			}
		}
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
		uiMsg.showUserLoggedOn( newUser.getNick(), newUser.getIpAddress() );
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
		uiMsg.showShowedUnexpectedly( newUser.getNick(), newUser.getIpAddress() );
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
							uiMsg.showTopicChanged( nick, newTopic );
						}

						else
						{
							String date = Tools.dateToString( new Date( time ), "HH:mm:ss, dd. MMM. yy" );
							uiMsg.showTopic( newTopic, nick, date );
						}

						topic.changeTopic( newTopic, nick, time );
						ui.showTopic();
					}
				}

				else
				{
					if ( !topic.getTopic().equals( newTopic ) && time > topic.getTime() && wList.isLoggedOn() )
					{
						uiMsg.showTopicRemoved( nick );
						topic.changeTopic( "", "", time );
						ui.showTopic();
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
		String date = Tools.dateToString( null, "EEEE, d MMMM yyyy" );
		uiMsg.showTodayIs( date );
		uiMsg.showMeLoggedOn( me.getNick(), ipAddress );
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
				uiMsg.showUserAway( user.getNick(), awayMsg );
			else
				uiMsg.showUserBack( user.getNick() );
			
			if ( user.getPrivchat() != null )
				user.getPrivchat().setAway( away );
		}
	}

	@Override
	public void meIdle( String ipAddress )
	{
		me.setLastIdle( System.currentTimeMillis() );

		if ( !me.getIpAddress().equals( ipAddress ) )
		{
			uiMsg.showChangedIp( "You", me.getIpAddress(), ipAddress );
			me.setIpAddress( ipAddress );
		}
	}

	@Override
	public void userIdle( int userCode, String ipAddress )
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
				uiMsg.showChangedIp( user.getNick(), user.getIpAddress(), ipAddress );
				user.setIpAddress( ipAddress );
			}
		}
	}

	@Override
	public void topicRequested()
	{
		controller.sendTopicMessage();
	}

	@Override
	public void nickCrash()
	{
		me.setNick( "" + me.getCode() );
		uiMsg.showNickCrash( settings.getMe().getNick() );
		ui.showTopic();
	}

	@Override
	public void exposeRequested()
	{
		controller.sendExposingMessage();
		controller.sendClientInfo();
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
			uiMsg.showNickChanged( oldNick, newNick );
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

				if ( !controller.isNewUser( userCode ) )
				{
					String size = Tools.byteToString( byteSize );
					uiMsg.showReceiveRequest( user, fileName, size );

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
									uiMsg.showReceiveSuccess( fileName, user, file.getName() );
								}

								else
								{
									uiMsg.showReceiveFailed( fileName, user );
									fileRes.cancel();
								}
							}

							catch ( ServerException e )
							{
								log.log( Level.SEVERE, e.getMessage(), e );
								uiMsg.showReceiveFailed( fileName, user );
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
							uiMsg.showReceiveDeclined( fileName, user );
							controller.sendFileAbort( userCode, fileHash, fileName );
						}
					}

					else
					{
						uiMsg.showReceiveDeclined( fileName, user );
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
			uiMsg.showSendAborted( user.getNick(), fileName );
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
					uiMsg.showSendAccepted( fUser.getNick(), fileName );

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
						uiMsg.showSendSuccess( fileName, fUser.getNick() );
					}

					else
					{
						uiMsg.showSendFailed( fileName, fUser.getNick() );
					}

					tList.removeFileSender( fileSend );
				}
			}
		}.start();
	}

	@Override
	public void clientInfo( int userCode, String client, long timeSinceLogon, String operatingSystem )
	{
		NickDTO user = controller.getNick( userCode );
		
		user.setClient( client );
		user.setLogonTime( System.currentTimeMillis() - timeSinceLogon );
		user.setOperatingSystem( operatingSystem );
	}
}
