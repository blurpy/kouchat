
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
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.NickList;
import net.usikkert.kouchat.misc.PrivateChatWindow;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.SoundBeeper;
import net.usikkert.kouchat.misc.TopicDTO;
import net.usikkert.kouchat.misc.UIMessages;
import net.usikkert.kouchat.misc.UserInterface;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.util.Tools;

/**
 * This class is a mediator for the gui, and gets all the events from the gui layer
 * that needs access to other components, or classes in lower layers. It is also
 * the interface for classes in lower layers to update the gui.
 * 
 * @author Christian Ihle
 */
public class SwingMediator implements Mediator, UserInterface
{
	private static Logger log = Logger.getLogger( SwingMediator.class.getName() );
	
	private SidePanel sideP;
	private SettingsDialog settingsDialog;
	private KouChatFrame gui;
	private MainPanel mainP;
	private SysTray sysTray;
	private MenuBar menuBar;
	private ButtonPanel buttonP;

	private Controller controller;
	private Settings settings;
	private NickDTO me;
	private TransferList tList;
	private CommandParser cmdParser;
	private UIMessages uiMsg;
	private SoundBeeper beeper;

	public SwingMediator( ComponentHandler compHandler )
	{
		sideP = compHandler.getSidePanel();
		settingsDialog = compHandler.getSettingsDialog();
		gui = compHandler.getGui();
		mainP = compHandler.getMainPanel();
		sysTray = compHandler.getSysTray();
		menuBar = compHandler.getMenuBar();
		buttonP = compHandler.getButtonPanel();
		
		uiMsg = new UIMessages( new MessageController( mainP, this ) );
		controller = new Controller( this );

		tList = controller.getTransferList();
		settings = Settings.getSettings();
		me = settings.getMe();
		cmdParser = new CommandParser( controller, this );

		uiMsg.showWelcomeMsg();
		beeper = new SoundBeeper();
		
		if ( !sysTray.isSystemTraySupport() )
		{
			buttonP.disableMinimize();
			menuBar.disableMinimize();
		}

		sideP.setNickList( controller.getNickList() );
	}

	@Override
	public void minimize()
	{
		if ( sysTray.isSystemTraySupport() )
			gui.setVisible( false );
	}

	@Override
	public void clearChat()
	{
		mainP.clearChat();
		mainP.getMsgTF().requestFocusInWindow();
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
				try
				{
					controller.changeAwayStatus( me.getCode(), false, "" );
					controller.sendBackMessage();
					changeAway( false );
					uiMsg.showUserBack( "You" );
				}
				
				catch ( CommandException e )
				{
					log.log( Level.WARNING, e.toString() );
					JOptionPane.showMessageDialog( null, "You are not allowed to change away mode at this time...",
							Constants.APP_NAME + " - Change away", JOptionPane.WARNING_MESSAGE );
				}
			}
		}

		else
		{
			String reason = JOptionPane.showInputDialog( null, "Reason for away?",
					Constants.APP_NAME + " - Away", JOptionPane.QUESTION_MESSAGE );

			if ( reason != null && reason.trim().length() > 0 )
			{
				if ( controller.isWrote() )
				{
					controller.changeWriting( me.getCode(), false );
					mainP.getMsgTF().setText( "" );
				}

				try
				{
					controller.changeAwayStatus( me.getCode(), true, reason );
					controller.sendAwayMessage();
					changeAway( true );
					uiMsg.showUserAway( "You", me.getAwayMsg() );
				}
				
				catch ( CommandException e )
				{
					log.log( Level.WARNING, e.toString() );
					JOptionPane.showMessageDialog( null, "You are not allowed to change away mode at this time...",
							Constants.APP_NAME + " - Change away", JOptionPane.WARNING_MESSAGE );
				}
			}
		}

		mainP.getMsgTF().requestFocusInWindow();
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
			cmdParser.fixTopic( newTopic );
		}

		mainP.getMsgTF().requestFocusInWindow();
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

			if ( !controller.isConnected() )
			{
				title += " - (Not connected)";
				tooltip += " - (Not connected)";
			}
			
			else
			{
				if ( me.isAway() )
				{
					title += " (Away)";
					tooltip += " (Away)";
				}
				
				if ( controller.getTopic().getTopic().length() > 0 )
					title += " - Topic: " + controller.getTopic();
			}

			gui.setTitle( title );
			sysTray.setToolTip( tooltip );
		}
	}

	@Override
	public void showWindow()
	{
		if ( gui.isVisible() )
			gui.setVisible( false );

		else
		{
			if ( gui.getExtendedState() == JFrame.ICONIFIED )
				gui.setExtendedState( JFrame.NORMAL );
			
			gui.setVisible( true );
			gui.toFront();
		}
	}

	@Override
	public void showSettings()
	{
		settingsDialog.showSettings();
	}

	@Override
	public void sendFile( NickDTO user, File selectedFile )
	{
		if ( me == user )
		{
			JOptionPane.showMessageDialog( null, "No point in doing that!", Constants.APP_NAME
					+ " - Warning", JOptionPane.WARNING_MESSAGE );
		}
		
		else if ( user != null && !user.isAway() && !me.isAway() )
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle( Constants.APP_NAME + " - Open" );
			
			if ( selectedFile != null && selectedFile.exists() )
				chooser.setSelectedFile( selectedFile );
			
			int returnVal = chooser.showOpenDialog( null );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				File file = chooser.getSelectedFile().getAbsoluteFile();

				if ( file.exists() && file.isFile() )
				{
					cmdParser.sendFile( user, file );
				}
			}
		}
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
				try
				{
					controller.sendChatMessage( line );
					uiMsg.showOwnMessage( line );
				}
				
				catch ( CommandException e )
				{
					log.log( Level.WARNING, e.getMessage() );
					uiMsg.showActionNotAllowed();
				}
			}
		}

		mainP.getMsgTF().setText( "" );
	}
	
	@Override
	public void writePrivate( PrivateChatWindow privchat )
	{
		String line = privchat.getChatText();
		NickDTO user = privchat.getUser();
		
		if ( line.trim().length() > 0 )
		{
			try
			{
				controller.sendPrivateMessage( line, user.getIpAddress(), user.getPrivateChatPort(), user.getCode() );
				uiMsg.showPrivateOwnMessage( user, line );
			}
			
			catch ( CommandException e )
			{
				log.log( Level.WARNING, e.getMessage() );
				uiMsg.showActionNotAllowed();
			}
		}

		privchat.clearChatText();
	}

	@Override
	public void showCommands()
	{
		uiMsg.showCommands();
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
				JOptionPane.showMessageDialog( null, "'" + nick + "' is not a valid nick name.\n\n" +
						"A nick name can have between 1 and 10 characters.\nLegal characters are 'a-z'," +
						" '0-9', '-' and '_'.", Constants.APP_NAME + " - Change nick", JOptionPane.WARNING_MESSAGE );
			}

			else
			{
				try
				{
					controller.changeMyNick( nick );
					uiMsg.showNickChanged( "You", me.getNick() );
					updateTitleAndTray();
					return true;
				}
				
				catch ( CommandException e )
				{
					log.log( Level.SEVERE, e.toString() );
					JOptionPane.showMessageDialog( null, "You are not allowed to change nick at this time...",
							Constants.APP_NAME + " - Change nick", JOptionPane.WARNING_MESSAGE );
				}
			}
		}

		else
		{
			return true;
		}

		return false;
	}

	@Override
	public void transferCancelled( TransferDialog transferDialog)
	{
		if ( transferDialog.getCancelButtonText().equals( "Close" ) )
			transferDialog.dispose();

		else
		{
			transferDialog.setCancelButtonText( "Close" );
			FileTransfer fileTransfer = transferDialog.getFileTransfer();
			fileTransfer.cancel();

			if ( fileTransfer instanceof FileSender )
			{
				FileSender fs = (FileSender) fileTransfer;

				// This means that the other user has not answered yet
				if ( fs.isWaiting() )
				{
					uiMsg.showSendCancelled( fs.getFileName(), fs.getNick().getNick() );
					tList.removeFileSender( fs );
				}
			}
		}
	}

	@Override
	public void notifyMessageArrived()
	{
		if ( !gui.isVisible() && me.isAway() )
		{
			sysTray.setAwayActivityState();
		}

		else if ( !gui.isVisible() )
		{
			sysTray.setNormalActivityState();
			beeper.beep();
		}
	}

	@Override
	public boolean askFileSave( String user, String fileName, String size )
	{
		Object[] options = { "Yes", "Cancel" };
		int choice = JOptionPane.showOptionDialog( null, user + " wants to send you the file "
				+ fileName + " (" + size + ")\nAccept?", Constants.APP_NAME + " - File send",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] );

		if ( choice == JOptionPane.YES_OPTION )
			return true;
		else
			return false;
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
	public void showTopic()
	{
		updateTitleAndTray();
	}

	@Override
	public void showTransfer( FileReceiver fileRes )
	{
		new TransferDialog( this, fileRes );
	}
	
	@Override
	public void showTransfer( FileSender fileSend )
	{
		new TransferDialog( this, fileSend );
	}

	@Override
	public void changeAway( boolean away )
	{
		if ( away )
		{
			sysTray.setAwayState();
			mainP.getMsgTF().setEnabled( false );
			menuBar.setAwayState( true );
			buttonP.setAwayState( true );
		}

		else
		{
			sysTray.setNormalState();
			mainP.getMsgTF().setEnabled( true );
			menuBar.setAwayState( false );
			buttonP.setAwayState( false );
		}

		updateAwayInPrivChats( away );
		updateTitleAndTray();
	}
	
	private void updateAwayInPrivChats( boolean away )
	{
		NickList list = controller.getNickList();
		
		for ( int i = 0; i < list.size(); i++ )
		{
			NickDTO user = list.get( i );
			
			if ( user.getPrivchat() != null && !user.isAway() )
			{
				user.getPrivchat().setAway( away );
			}
		}
	}

	@Override
	public UIMessages getUIMessages()
	{
		return uiMsg;
	}

	@Override
	public void createPrivChat( NickDTO user )
	{
		if ( user.getPrivchat() == null )
			user.setPrivchat( new PrivateChatFrame( this, user ) );
	}

	@Override
	public void showPrivChat( NickDTO user )
	{
		if ( user.getPrivchat() == null )
			user.setPrivchat( new PrivateChatFrame( this, user ) );
		
		user.getPrivchat().setVisible( true );
		controller.changeNewMessage( user.getCode(), false );
	}
}
