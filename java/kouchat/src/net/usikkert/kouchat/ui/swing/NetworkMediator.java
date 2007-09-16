
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

package net.usikkert.kouchat.ui.swing;

import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.MessageListener;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.NickList;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.TopicDTO;
import net.usikkert.kouchat.misc.WaitingList;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.ServerException;
import net.usikkert.kouchat.util.Tools;

/**
 * This class is a mediator for the gui, and gets all the events from the network layer.
 * 
 * @author Christian Ihle
 */
public class NetworkMediator implements MessageListener
{
	private static Logger log = Logger.getLogger( NetworkMediator.class.getName() );
	
	private SysTray sysTray;
	private MainPanel mainP;
	private KouChatFrame gui;
	
	private GUIMediator guiMediator;
	private Controller controller;
	private WaitingList wList;
	private NickDTO me;
	private Settings settings;
	
	public NetworkMediator( Controller controller, GUIMediator guiMediator )
	{
		this.controller = controller;
		this.guiMediator = guiMediator;
		
		wList = new WaitingList();
		settings = Settings.getSettings();
		me = settings.getNick();
		
		controller.setMessageListener( this );
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
				
				mainP.appendUserMessage( msg, color );

				if ( !gui.isVisible() && me.isAway() )
				{
					sysTray.setAwayActivityState();
				}

				else if ( !gui.isVisible() )
				{
					sysTray.setNormalActivityState();
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
		mainP.appendSystemMessage( "*** " + user.getNick() + " logged off..." );
	}

	@Override
	public void userLogOn( NickDTO newUser )
	{
		if ( me.getNick().equals( newUser.getNick() ) )
		{
			controller.sendNickCrashMessage( newUser.getNick() );
			newUser.setNick( "" + newUser.getCode() );
		}

		else if ( controller.isNickInUse( newUser.getNick() ) )
		{
			newUser.setNick( "" + newUser.getCode() );
		}

		controller.getNickList().add( newUser );
		mainP.appendSystemMessage( "*** " + newUser.getNick() + " logged on from " + newUser.getIpAddress() + "..." );
	}
	
	private void userShowedUp( NickDTO newUser )
	{
		if ( me.getNick().equals( newUser.getNick() ) )
		{
			controller.sendNickCrashMessage( newUser.getNick() );
			newUser.setNick( "" + newUser.getCode() );
		}

		else if ( controller.isNickInUse( newUser.getNick() ) )
		{
			newUser.setNick( "" + newUser.getCode() );
		}

		controller.getNickList().add( newUser );
		mainP.appendSystemMessage( "*** " + newUser.getNick() + " showed up unexpectedly from " + newUser.getIpAddress() + "..." );
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
						mainP.appendSystemMessage( "*** " + nick + " set the topic to: " + newTopic );
						topic.changeTopic( newTopic, nick, time );
						guiMediator.updateTitleAndTray();
					}
				}

				else
				{
					if ( !topic.getTopic().equals( newTopic ) && time > topic.getTime() )
					{
						mainP.appendSystemMessage( "*** " + nick + " removed the topic..." );
						topic.changeTopic( "", "", time );
						guiMediator.updateTitleAndTray();
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
			if ( wList.isWaitingUser( user.getCode() ) )
			{
				wList.removeWaitingUser( user.getCode() );
				userShowedUp( user );
			}
			
			else
				controller.getNickList().add( user );
		}
	}

	@Override
	public void meLogOn( String ipAddress )
	{
		me.setIpAddress( ipAddress );
		mainP.appendSystemMessage( "*** Today is " + Tools.dateToString( null, "EEEE, d MMMM yyyy" ) );
		mainP.appendSystemMessage( "*** You logged on as " + me.getNick() + " from " + ipAddress );
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
				mainP.appendSystemMessage( "*** " + user.getNick() + " went away: " + awayMsg );
			}

			else
			{
				mainP.appendSystemMessage( "*** " + user.getNick() + " came back..." );
			}
		}
	}

	@Override
	public void meIdle()
	{
		NickList nickList = controller.getNickList();

		for ( int i = 0; i < nickList.size(); i++ )
		{
			NickDTO temp = nickList.get( i );

			if ( temp.getCode() != me.getCode() && temp.getLastIdle() < System.currentTimeMillis() - 120000 )
			{
				nickList.remove( temp );
				mainP.appendSystemMessage( "*** " + temp.getNick() + " timed out..." );
				i--;
			}
		}
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
		mainP.appendSystemMessage( "*** " + "Nick crash, resetting nick to " + settings.getNick() );
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
			mainP.appendSystemMessage( "*** " + oldNick + " changed nick to " + newNick );
		}
	}

	@Override
	public void fileSend( int userCode, long byteSize, String fileName, String user, int fileHash, int fileCode )
	{
		final String fSize = Tools.byteToString( byteSize );
		final String fUser = user;
		final String fFileName = fileName;
		final int fUserCode = userCode;
		final int fFileHash = fileHash;
		final long fByteSize = byteSize;

		mainP.appendSystemMessage( "*** " + user + " is trying to send the file " + fileName + " [" + fSize + "]" );

		new Thread()
		{
			public void run()
			{
				Object[] options = { "Yes", "Cancel" };
				int choice = JOptionPane.showOptionDialog( gui, fUser + " wants to send you the file "
						+ fFileName + " (" + fSize + ")\nAccept?", Constants.APP_NAME + " - File send",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] );

				if ( choice == JOptionPane.YES_OPTION )
				{
					JFileChooser chooser = new JFileChooser();
					chooser.setSelectedFile( new File( fFileName ) );
					boolean done = false;

					while ( !done )
					{
						done = true;
						int returnVal = chooser.showSaveDialog( gui );

						if ( returnVal == JFileChooser.APPROVE_OPTION )
						{
							NickDTO tempnick = controller.getNick( fUserCode );
							File file = chooser.getSelectedFile().getAbsoluteFile();

							if ( file.exists() )
							{
								int overwrite = JOptionPane.showOptionDialog( gui, file.getName()
										+ " already exists.\nOverwrite?", Constants.APP_NAME + " - File exists",
										JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
										options, options[0] );

								if ( overwrite != JOptionPane.YES_OPTION )
								{
									done = false;
								}
							}

							if ( done )
							{
								FileReceiver fileRes = new FileReceiver( tempnick, file, fByteSize );
								TransferFrame fileStatus = new TransferFrame( fileRes );
								fileRes.registerListener( fileStatus );
								
								try
								{
									int port = fileRes.startServer();
									controller.sendFileAccept( fUserCode, port, fFileHash, fFileName );

									if ( fileRes.transfer() )
									{
										mainP.appendSystemMessage( "*** Successfully received " + fFileName
												+ " from " + fUser + ", and saved as " + file.getName() );
									}

									else
									{
										mainP.appendSystemMessage( "*** Failed to receive " + fFileName + " from " + fUser );
									}
								}
								
								catch ( ServerException e )
								{
									log.log( Level.SEVERE, e.getMessage(), e );
									
									mainP.appendSystemMessage( "*** Failed to receive " + fFileName + " from " + fUser );
									controller.sendFileAbort( fUserCode, fFileHash, fFileName );
									
									JOptionPane.showMessageDialog( fileStatus, "Could not connect...", Constants.APP_NAME
											+ " - File transfer", JOptionPane.ERROR_MESSAGE );
									
									fileRes.fail();
								}
							}
						}

						else
						{
							mainP.appendSystemMessage( "*** You declined to receive " + fFileName + " from " + fUser );
							controller.sendFileAbort( fUserCode, fFileHash, fFileName );
						}
					}
				}

				else
				{
					mainP.appendSystemMessage( "*** You declined to receive " + fFileName + " from " + fUser );
					controller.sendFileAbort( fUserCode, fFileHash, fFileName );
				}
			}
		}.start();
	}

	@Override
	public void fileSendAborted( int userCode, String fileName, int fileHash )
	{
		NickDTO user = controller.getNick( userCode );
		mainP.appendSystemMessage( "*** " + user.getNick() + " aborted sending of " + fileName );
		FileSender fileSend = controller.getTransferList().getFileSender( user, fileName, fileHash );
		fileSend.fail();
		controller.getTransferList().removeFileSender( fileSend );
	}

	@Override
	public void fileSendAccepted( int userCode, String fileName, int fileHash, int port )
	{
		final String fFileName = fileName;
		final int fFileHash = fileHash;
		final int fPort = port;
		final NickDTO fUser = controller.getNick( userCode );

		new Thread()
		{
			public void run()
			{
				FileSender fileSend = controller.getTransferList().getFileSender( fUser, fFileName, fFileHash );
				controller.getTransferList().removeFileSender( fileSend );

				if ( fileSend != null )
				{
					mainP.appendSystemMessage( "*** " + fUser.getNick() + " accepted sending of "	+ fFileName );

					// Give the server some time to set up the connection first
					try
					{
						Thread.sleep( 200 );
					}
					
					catch ( InterruptedException e )
					{
						log.log( Level.SEVERE, e.getMessage(), e );
					}

					if ( fileSend.transfer( fPort ) )
					{
						mainP.appendSystemMessage( "*** " + fFileName + " successfully sent to " + fUser.getNick() );
					}

					else
					{
						mainP.appendSystemMessage( "*** Failed to send " + fFileName + " to " + fUser.getNick() );
					}
				}

			}
		}.start();
	}

	public void setKouChatFrame( KouChatFrame gui )
	{
		this.gui = gui;
	}

	public void setMainP( MainPanel mainP )
	{
		this.mainP = mainP;
	}

	public void setSysTray( SysTray sysTray )
	{
		this.sysTray = sysTray;
	}
}
