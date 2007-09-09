
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.DayListener;
import net.usikkert.kouchat.event.MessageListener;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.Nick;
import net.usikkert.kouchat.misc.NickList;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.util.DayTimer;
import net.usikkert.kouchat.util.Tools;

public class ListenerMediator implements MessageListener
{
	private KouChatGUIFrame gui;
	private MainPanel mainP;
	private Settings settings;
	private SysTray sysTray;
	private MenuBar menuBar;
	private ButtonPanel buttonP;
	private Controller controller;
	private SidePanel sideP;
	private SettingsFrame settingsFrame;
	private Nick me;
	private DayTimer dayTimer;
	private List<File> fileList;
	
	public ListenerMediator( KouChatGUIFrame gui )
	{
		this.gui = gui;
		controller = new Controller();
		settings = Settings.getSettings();
		me = settings.getNick();
		controller.addMessageListener( this );
		dayTimer = new DayTimer();
		fileList = new ArrayList<File>();
	}
	
	public void setMainP( MainPanel mainP )
	{
		this.mainP = mainP;
	}

	public void setSysTray( SysTray sysTray )
	{
		this.sysTray = sysTray;
	}

	public void setMenuBar( MenuBar menuBar )
	{
		this.menuBar = menuBar;
	}

	public void setButtonP( ButtonPanel buttonP )
	{
		this.buttonP = buttonP;
	}

	public void setSideP( SidePanel sideP )
	{
		this.sideP = sideP;
	}

	public void setSettingsFrame( SettingsFrame settingsFrame )
	{
		this.settingsFrame = settingsFrame;
	}

	public Controller getController()
	{
		return controller;
	}
	
	public void minimize()
	{
		gui.setVisible( false );
		mainP.getMsgTF().requestFocus();
	}
	
	public void clearChat()
	{
		mainP.clearChat();
		mainP.getMsgTF().requestFocus();
	}
	
	public void setAway()
	{
		if ( me.isAway() )
		{
			Object[] options = { "Yes", "Cancel" };
			int choice = JOptionPane.showOptionDialog( null, "Back from '" + me.getAwayMsg()
					+ "'?", Constants.APP_NAME + " - Away", JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0] );
			
			if ( choice == JOptionPane.YES_OPTION )
			{
				controller.changeAwayStatus( me.getCode(), false, "" );
				sysTray.setNormalState();
				updateTitleAndTray();
				mainP.getMsgTF().setEnabled( true );
				menuBar.setAwayState( false );
				buttonP.setAwayState( false );
				mainP.appendSystemMessage( "*** You came back" );
				controller.sendBackMessage();
			}
		}
		
		else
		{
			String reason = JOptionPane.showInputDialog( null, "Reason for away?",
					Constants.APP_NAME + " - Away", JOptionPane.QUESTION_MESSAGE );
			
			if ( reason != null && reason.trim().length() > 0 )
			{
				controller.changeAwayStatus( me.getCode(), true, reason );
				sysTray.setAwayState();
				updateTitleAndTray();
				mainP.getMsgTF().setEnabled( false );
				menuBar.setAwayState( true );
				buttonP.setAwayState( true );
				mainP.appendSystemMessage( "*** You went away: " + me.getAwayMsg() );
				controller.sendAwayMessage();
			}
		}
		
		mainP.getMsgTF().requestFocus();
	}
	
	public void setTopic()
	{
		Topic topic = controller.getTopic();
		
		Object objecttopic = JOptionPane.showInputDialog( null, "Change topic?", Constants.APP_NAME
				+ " - Topic", JOptionPane.QUESTION_MESSAGE, null, null, topic.getTopic() );
		
		if ( objecttopic != null )
		{
			String newTopic = objecttopic.toString();
			
			if ( !newTopic.trim().equals( topic.getTopic().trim() ) )
			{
				long time = System.currentTimeMillis();
				
				if ( newTopic.trim().length() > 0 )
				{
					mainP.appendSystemMessage( "*** You changed the topic to: " + newTopic );
					topic.changeTopic( newTopic, me.getNick(), time );
				}
				
				else
				{
					mainP.appendSystemMessage( "*** You removed the topic..." );
					topic.changeTopic( "", "", time );
				}
				
				controller.sendTopicMessage( topic );
				updateTitleAndTray();
			}
		}
		
		mainP.getMsgTF().requestFocus();
	}
	
	public void start()
	{
		controller.logOn();
		updateTitleAndTray();
		
		dayTimer.addDayListener( new DayListener()
		{
			@Override
			public void dayChanged( Date date )
			{
				mainP.appendSystemMessage( "*** Day changed to " + Tools.dateToString( null, "EEEE, d MMMM yyyy" ) );
			}
		} );
	}
	
	public void quit()
	{
		Object[] options = { "Yes", "Cancel" };
		int choice = JOptionPane.showOptionDialog( null, "Are you sure you want to quit?",
				Constants.APP_NAME + " - Quit?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0] );
		
		if ( choice == JOptionPane.YES_OPTION )
		{
			controller.logOff();
			System.exit( 0 );
		}
	}
	
	public void updateTitleAndTray()
	{
		if ( me != null )
		{
			String title = Constants.APP_NAME + " v" + Constants.APP_VERSION + " - Nick: " + me.getNick();
			String tooltip = Constants.APP_NAME + " v" + Constants.APP_VERSION + " - " + me.getNick();
			
			if ( me.isAway() )
			{
				title += " (Away)";
				tooltip += " (Away)";
			}
			
			title += " - Topic: " + controller.getTopic();
			gui.setTitle( title );
			sysTray.setToolTip( tooltip );
		}
	}
	
	public void showWindow()
	{
		if ( gui.isVisible() )
		{
			gui.setVisible( false );
		}
		
		else
		{
			gui.setVisible( true );
			gui.repaint();
		}
	}
	
	public void showSettings()
	{
		settingsFrame.showSettings();
	}
	
	// TODO
	public void sendFile()
	{
		if ( me != sideP.getSelectedNick() )
		{
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog( gui );
			
			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				File file = chooser.getSelectedFile().getAbsoluteFile();
				
				if ( file.exists() && file.isFile() )
				{
					fileList.add( file );
					
					Nick tempnick = sideP.getSelectedNick();
					String size = Tools.byteToString( file.length() );
					
					controller.sendFile( tempnick.getCode(), file.length(), file.hashCode(), file.getName() );
					mainP.appendSystemMessage( "*** " + "Trying to send the file " + file.getName() + " [" + size + "] to " + tempnick.getNick() );
				}
			}
		}
		
		else
		{
			JOptionPane.showMessageDialog( gui, "No point in doing that!", Constants.APP_NAME
					+ " - Warning", JOptionPane.WARNING_MESSAGE );
		}
	}
	
	public void write()
	{
		String line = mainP.getMsgTF().getText();
		
		if ( line.trim().length() > 0 )
		{
			if ( line.startsWith( "/" ) )
			{
				mainP.appendSystemMessage( "*** No commands yet..." );
			}
			
			else
			{
				mainP.appendOwnMessage( "<" + me.getNick() + ">: " + line );
				controller.sendChatMessage( line );
			}
		}
		
		mainP.getMsgTF().setText( "" );
	}
	
	public void updateWriting()
	{
		if ( mainP.getMsgTF().getText().length() > 0 )
		{
			if ( !controller.isWrote() )
			{
				controller.changeWriting( me.getCode(), true );
			}
		}
		
		else
		{
			if ( controller.isWrote() )
			{
				controller.changeWriting( me.getCode(), false );
			}
		}
	}
	
	public void changeNick( String nick )
	{
		if ( !nick.equals( me.getNick() ) )
		{
			if ( controller.checkIfNickInUse( nick ) )
			{
				JOptionPane.showMessageDialog( null, "The nick is in use by someone else...", Constants.APP_NAME
						+ " - Change nick", JOptionPane.WARNING_MESSAGE );
			}
			
			else if ( !Tools.isValidNick( nick ) )
			{
				JOptionPane.showMessageDialog( null, "Not a valid nick name. (1-10 letters)", Constants.APP_NAME
						+ " - Change nick", JOptionPane.WARNING_MESSAGE );
			}
			
			else
			{
				nick = nick.trim();
				controller.changeNick( me.getCode(), nick );
				mainP.appendSystemMessage( "*** You changed nick to " + me.getNick() );
				updateTitleAndTray();
			}
		}
	}

	@Override
	public void messageArrived(  String msg, int color  )
	{
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

	@Override
	public void userLogOff( int userCode )
	{
		Nick user = controller.getNick( userCode );
		controller.getNickList().remove( user );
		mainP.appendSystemMessage( "*** " + user.getNick() + " logged off..." );
	}

	@Override
	public void userLogOn( Nick newUser )
	{
		if ( me.getNick().equals( newUser.getNick() ) )
		{
			controller.sendNickCrashMessage( newUser.getNick() );
			newUser.setNick( "" + newUser.getCode() );
		}
		
		else if ( !controller.checkIfNickInUse( newUser.getNick() ) )
		{
			newUser.setNick( "" + newUser.getCode() );
		}
		
		controller.getNickList().add( newUser );
		mainP.appendSystemMessage( "*** " + newUser.getNick() + " logged on from " + newUser.getIpAddress() + "..." );
	}

	@Override
	public void topicChanged( String newTopic, String nick, long time )
	{
		Topic topic = controller.getTopic();
		
		if ( newTopic != null )
		{
			if ( !newTopic.equals( topic.getTopic() ) )
			{
				mainP.appendSystemMessage( "*** " + nick + " changed topic to: " + newTopic );
				topic.changeTopic( newTopic, nick, time );
				updateTitleAndTray();
			}
		}
		
		else
		{
			if ( !topic.getTopic().equals( newTopic ) )
			{
				mainP.appendSystemMessage( "*** " + nick + " removed the topic..." );
				topic.changeTopic( "", "", time );
				updateTitleAndTray();
			}
		}
	}

	@Override
	public void userExposing( Nick user )
	{
		if ( controller.checkIfNewUser( user.getCode() ) )
		{
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
		Nick user = controller.getNick( userCode );
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

	@Override
	public void meIdle()
	{
		NickList nickList = controller.getNickList();
		
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
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
		if ( controller.checkIfNewUser( userCode ) )
		{
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
		Nick user = controller.getNick( userCode );
		String oldNick = user.getNick();
		controller.changeNick( userCode, newNick );
		mainP.appendSystemMessage( "*** " + oldNick + " changed nick to " + newNick );
	}

	@Override
	public void fileSend( long byteSize, String fileName, String user, int fileHash, int fileCode, int userCode )
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
							Nick tempnick = controller.getNick( fUserCode );
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
								controller.sendFileAccept( fUserCode, 50123, fFileHash, fFileName );
								
								// TODO
								FileReceiver fileRes = new FileReceiver( tempnick, 50123, file, fByteSize );
								TransferFrame fileStatus = new TransferFrame( fileRes );
								fileRes.registerListener( fileStatus );
								
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
	public void fileSendAborted( String user, String fileName, int fileHash )
	{
		mainP.appendSystemMessage( "*** " + user + " aborted sending of " + fileName );
		
		for ( int i = 0; i < fileList.size(); i++ )
		{
			if ( fileList.get( i ).hashCode() == fileHash )
			{
				fileList.remove( i );
			}
		}
	}

	@Override
	public void fileSendAccepted( String user, int userCode, String fileName, int fileHash, int port )
	{
		final String fUser = user;
		final String fFileName = fileName;
		final int fUserCode = userCode;
		final int fFileHash = fileHash;
		final int fPort = port;
		
		new Thread()
		{
			public void run()
			{
				mainP.appendSystemMessage( "*** " + fUser + " accepted sending of "	+ fFileName );
				File file = null;
				
				for ( int i = 0; i < fileList.size(); i++ )
				{
					if ( fileList.get( i ).hashCode() == fFileHash )
					{
						file = (File) fileList.get( i );
						fileList.remove( i );
						break;
					}
				}
				
				if ( file != null )
				{
					// Give the server some time to set up the connection first
					try { Thread.sleep( 200 ); }
					catch ( InterruptedException e ) {}
					
					Nick tempnick = controller.getNick( fUserCode );
					
					// TODO
					FileSender fileSend = new FileSender( tempnick, fPort, file );
					TransferFrame fileStatus = new TransferFrame( fileSend );
					fileSend.registerListener( fileStatus );
					
					if ( fileSend.transfer() )
					{
						mainP.appendSystemMessage( "*** " + fFileName + " successfully sent to " + fUser );
					}
					
					else
					{
						mainP.appendSystemMessage( "*** Failed to send " + fFileName + " to " + fUser );
					}
				}
			}
		}.start();
	}
}
