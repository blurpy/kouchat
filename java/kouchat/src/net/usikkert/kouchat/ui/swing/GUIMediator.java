
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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.TopicDTO;
import net.usikkert.kouchat.misc.UserInterface;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.util.Tools;

/**
 * This class is a mediator for the gui, and gets all the events from the gui layer.
 * It is also a listener for events from the network layer.
 * 
 * @author Christian Ihle
 */
public class GUIMediator implements Mediator, UserInterface
{
	private SidePanel sideP;
	private SettingsFrame settingsFrame;
	private SysTray sysTray;
	private MenuBar menuBar;
	private ButtonPanel buttonP;
	private KouChatFrame gui;
	private MainPanel mainP;

	private Controller controller;
	private Settings settings;
	private NickDTO me;
	private TransferList tList;
	private CommandParser cmdParser;

	public GUIMediator()
	{
		controller = new Controller( this );
		tList = controller.getTransferList();
		settings = Settings.getSettings();
		me = settings.getMe();
		cmdParser = new CommandParser( controller, this );
	}

	@Override
	public void minimize()
	{
		gui.setVisible( false );
		mainP.getMsgTF().requestFocus();
	}

	@Override
	public void clearChat()
	{
		mainP.clearChat();
		mainP.getMsgTF().requestFocus();
	}

	@Override
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
				changeAway( false, null );
			}
		}

		else
		{
			String reason = JOptionPane.showInputDialog( null, "Reason for away?",
					Constants.APP_NAME + " - Away", JOptionPane.QUESTION_MESSAGE );

			if ( reason != null && reason.trim().length() > 0 )
			{
				changeAway( true, reason );
			}
		}

		mainP.getMsgTF().requestFocus();
	}

	@Override
	public void changeAway( boolean away, String reason )
	{
		if ( away )
		{
			controller.changeAwayStatus( me.getCode(), true, reason );
			sysTray.setAwayState();
			mainP.getMsgTF().setEnabled( false );
			menuBar.setAwayState( true );
			buttonP.setAwayState( true );
			mainP.appendSystemMessage( "You went away: " + me.getAwayMsg() );
			controller.sendAwayMessage();
		}

		else
		{
			controller.changeAwayStatus( me.getCode(), false, "" );
			sysTray.setNormalState();
			mainP.getMsgTF().setEnabled( true );
			menuBar.setAwayState( false );
			buttonP.setAwayState( false );
			mainP.appendSystemMessage( "You came back" );
			controller.sendBackMessage();
		}

		updateTitleAndTray();
	}

	@Override
	public void setTopic()
	{
		TopicDTO topic = controller.getTopic();

		Object objecttopic = JOptionPane.showInputDialog( null, "Change topic?", Constants.APP_NAME
				+ " - Topic", JOptionPane.QUESTION_MESSAGE, null, null, topic.getTopic() );

		if ( objecttopic != null )
		{
			String newTopic = objecttopic.toString();
			fixTopic( newTopic );
		}

		mainP.getMsgTF().requestFocus();
	}

	public void fixTopic( String newTopic )
	{
		TopicDTO topic = controller.getTopic();
		newTopic = newTopic.trim();

		if ( !newTopic.equals( topic.getTopic().trim() ) )
		{
			long time = System.currentTimeMillis();

			if ( newTopic.length() > 0 )
			{
				mainP.appendSystemMessage( "You changed the topic to: " + newTopic );
				topic.changeTopic( newTopic, me.getNick(), time );
			}

			else
			{
				mainP.appendSystemMessage( "You removed the topic..." );
				topic.changeTopic( "", me.getNick(), time );
			}

			controller.sendTopicMessage( topic );
			updateTitleAndTray();
		}
	}

	@Override
	public void start()
	{
		controller.logOn();
		updateTitleAndTray();
	}

	@Override
	public void quit()
	{
		Object[] options = { "Yes", "Cancel" };
		int choice = JOptionPane.showOptionDialog( null, "Are you sure you want to quit?",
				Constants.APP_NAME + " - Quit?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0] );

		if ( choice == JOptionPane.YES_OPTION )
		{
			System.exit( 0 );
		}
	}

	@Override
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

			if ( controller.getTopic().getTopic().length() > 0 )
				title += " - Topic: " + controller.getTopic();

			gui.setTitle( title );
			sysTray.setToolTip( tooltip );
		}
	}

	@Override
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

	@Override
	public void showSettings()
	{
		settingsFrame.showSettings();
	}

	@Override
	public void sendFile()
	{
		if ( me != sideP.getSelectedNick() )
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle( Constants.APP_NAME + " - Open" );
			int returnVal = chooser.showOpenDialog( null );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				File file = chooser.getSelectedFile().getAbsoluteFile();

				if ( file.exists() && file.isFile() )
				{
					NickDTO user = sideP.getSelectedNick();
					startFileSend( user, file );
				}
			}
		}

		else
		{
			JOptionPane.showMessageDialog( null, "No point in doing that!", Constants.APP_NAME
					+ " - Warning", JOptionPane.WARNING_MESSAGE );
		}
	}
	
	@Override
	public void startFileSend( NickDTO user, File file )
	{
		String size = Tools.byteToString( file.length() );

		FileSender fileSend = new FileSender( user, file );
		new TransferFrame( this, fileSend );
		tList.addFileSender( fileSend );

		controller.sendFile( user.getCode(), file.length(), file.hashCode(), file.getName() );
		mainP.appendSystemMessage( "Trying to send the file " + file.getName() + " [" + size + "] to " + user.getNick() );
	}

	@Override
	public void write()
	{
		String line = mainP.getMsgTF().getText();

		if ( line.trim().length() > 0 )
		{
			if ( line.startsWith( "/" ) )
			{
				cmdParser.parse( line );
			}

			else
			{
				sendMsg( line );
			}
		}

		mainP.getMsgTF().setText( "" );
	}

	private void sendMsg( String message )
	{
		mainP.appendOwnMessage( message );
		controller.sendChatMessage( message );
	}

	@Override
	public void showCommands()
	{
		cmdParser.showCommands();
	}

	@Override
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

	@Override
	public boolean changeNick( String nick )
	{
		nick = nick.trim();
		
		if ( !nick.equals( me.getNick() ) )
		{
			if ( controller.isNickInUse( nick ) )
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
				controller.changeNick( me.getCode(), nick );
				mainP.appendSystemMessage( "You changed nick to " + me.getNick() );
				updateTitleAndTray();
				
				return true;
			}
		}
		
		else
		{
			return true;
		}
		
		return false;
	}

	@Override
	public void setButtonP( ButtonPanel buttonP )
	{
		this.buttonP = buttonP;
	}

	@Override
	public void setKouChatFrame( KouChatFrame gui )
	{
		this.gui = gui;
	}

	@Override
	public void setMainP( MainPanel mainP )
	{
		this.mainP = mainP;
	}

	@Override
	public void setMenuBar( MenuBar menuBar )
	{
		this.menuBar = menuBar;
	}

	@Override
	public void setSettingsFrame( SettingsFrame settingsFrame )
	{
		this.settingsFrame = settingsFrame;
	}

	@Override
	public void setSideP( SidePanel sideP )
	{
		this.sideP = sideP;
		sideP.setNickList( controller.getNickList() );
	}

	@Override
	public void setSysTray( SysTray sysTray )
	{
		this.sysTray = sysTray;
	}

	@Override
	public void showUserMessage( String user, String message, int color )
	{
		mainP.appendUserMessage( user, message, color );

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
	public void showSystemMessage( String message )
	{
		mainP.appendSystemMessage( message );
	}
	
	@Override
	public void showOwnMessage( String message )
	{
		mainP.appendOwnMessage( message );
	}

	@Override
	public boolean askFileSave( String user, String fileName, String size )
	{
		Object[] options = { "Yes", "Cancel" };
		int choice = JOptionPane.showOptionDialog( null, user + " wants to send you the file "
				+ fileName + " (" + size + ")\nAccept?", Constants.APP_NAME + " - File send",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] );

		boolean answer = false;

		if ( choice == JOptionPane.YES_OPTION )
			answer = true;

		return answer;
	}

	@Override
	public File showFileSave( String fileName )
	{
		File returnFile = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle( Constants.APP_NAME + " - Save" );
		chooser.setSelectedFile( new File( fileName ) );
		boolean done = false;

		while ( !done )
		{
			done = true;
			int returnVal = chooser.showSaveDialog( null );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				File file = chooser.getSelectedFile().getAbsoluteFile();

				if ( file.exists() )
				{
					Object[] options = { "Yes", "Cancel" };
					int overwrite = JOptionPane.showOptionDialog( null, file.getName()
							+ " already exists.\nOverwrite?", Constants.APP_NAME + " - File exists",
							JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] );

					if ( overwrite != JOptionPane.YES_OPTION )
					{
						done = false;
					}
				}

				if ( done )
				{
					returnFile = file;
				}
			}
		}

		return returnFile;
	}

	@Override
	public void showTransfer( FileReceiver fileRes )
	{
		new TransferFrame( this, fileRes );
	}

	@Override
	public void showTopic()
	{
		updateTitleAndTray();
	}

	@Override
	public void transferCancelled( TransferFrame transferFrame )
	{
		if ( transferFrame.getCancelButtonText().equals( "Close" ) )
			transferFrame.dispose();

		else
		{
			transferFrame.setCancelButtonText( "Close" );
			FileTransfer fileTransfer = transferFrame.getFileTransfer();
			fileTransfer.cancel();

			if ( fileTransfer instanceof FileSender )
			{
				FileSender fs = (FileSender) fileTransfer;

				// This means that the other user has not answered yet
				if ( fs.isWaiting() )
				{
					showSystemMessage( "You cancelled sending of " + fs.getFileName() + " to " + fs.getNick() );
					tList.removeFileSender( fs );
				}
			}
		}
	}
}
