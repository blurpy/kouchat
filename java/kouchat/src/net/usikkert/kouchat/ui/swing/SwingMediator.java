
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

package net.usikkert.kouchat.ui.swing;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.SoundBeeper;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This class is a mediator for the gui, and gets all the events from the gui layer
 * that needs access to other components, or classes in lower layers. It is also
 * the interface for classes in lower layers to update the gui.
 *
 * @author Christian Ihle
 */
public class SwingMediator implements Mediator, UserInterface
{
	private final SidePanel sideP;
	private final SettingsDialog settingsDialog;
	private final KouChatFrame gui;
	private final MainPanel mainP;
	private final SysTray sysTray;
	private final MenuBar menuBar;
	private final ButtonPanel buttonP;

	private final Controller controller;
	private final Settings settings;
	private final User me;
	private final TransferList tList;
	private final CommandParser cmdParser;
	private final SoundBeeper beeper;
	private final MessageController msgController;

	/**
	 * Constructor. Initializes the lower layers.
	 *
	 * @param compHandler An object with references to all
	 * the gui components this mediator works with.
	 */
	public SwingMediator( final ComponentHandler compHandler )
	{
		Validate.notNull( compHandler, "Component handler can not be null" );
		Validate.notNull( compHandler.getSidePanel(), "Side panel can not be null" );
		Validate.notNull( compHandler.getSettingsDialog(), "Settings dialog can not be null" );
		Validate.notNull( compHandler.getGui(), "GUI can not be null" );
		Validate.notNull( compHandler.getMainPanel(), "Main panel can not be null" );
		Validate.notNull( compHandler.getSysTray(), "System tray can not be null" );
		Validate.notNull( compHandler.getMenuBar(), "Menu bar can not be null" );
		Validate.notNull( compHandler.getButtonPanel(), "Button panel can not be null" );

		sideP = compHandler.getSidePanel();
		settingsDialog = compHandler.getSettingsDialog();
		gui = compHandler.getGui();
		mainP = compHandler.getMainPanel();
		sysTray = compHandler.getSysTray();
		menuBar = compHandler.getMenuBar();
		buttonP = compHandler.getButtonPanel();

		msgController = new MessageController( mainP, this );
		controller = new Controller( this );
		tList = controller.getTransferList();
		settings = Settings.getSettings();
		me = settings.getMe();
		cmdParser = new CommandParser( controller, this );
		beeper = new SoundBeeper();

		if ( !sysTray.isSystemTraySupport() )
		{
			buttonP.disableMinimize();
			menuBar.disableMinimize();
		}

		sideP.setUserList( controller.getNickList() );
		mainP.setAutoCompleter( controller.getAutoCompleter() );
	}

	/**
	 * Hides the main window in the system tray,
	 * if a system tray is supported.
	 */
	@Override
	public void minimize()
	{
		if ( sysTray.isSystemTraySupport() )
			gui.setVisible( false );
	}

	/**
	 * Clears all the text from the main chat area.
	 */
	@Override
	public void clearChat()
	{
		mainP.clearChat();
		mainP.getMsgTF().requestFocusInWindow();
	}

	/**
	 * If the user is not away, asks for an away reason,
	 * and sets the user as away.
	 *
	 * If user is away, asks if the user wants to come back.
	 */
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
					changeAway( false );
					msgController.showSystemMessage( "You came back" );
				}

				catch ( final CommandException e )
				{
					JOptionPane.showMessageDialog( null, e.getMessage(),
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
					changeAway( true );
					msgController.showSystemMessage( "You went away: " + me.getAwayMsg() );
				}

				catch ( final CommandException e )
				{
					JOptionPane.showMessageDialog( null, e.getMessage(),
							Constants.APP_NAME + " - Change away", JOptionPane.WARNING_MESSAGE );
				}
			}
		}

		mainP.getMsgTF().requestFocusInWindow();
	}

	/**
	 * Asks for the new topic, and changes it.
	 */
	@Override
	public void setTopic()
	{
		Topic topic = controller.getTopic();

		Object objecttopic = JOptionPane.showInputDialog( null, "Change topic?", Constants.APP_NAME
				+ " - Topic", JOptionPane.QUESTION_MESSAGE, null, null, topic.getTopic() );

		if ( objecttopic != null )
		{
			String newTopic = objecttopic.toString();
			cmdParser.fixTopic( newTopic );
		}

		mainP.getMsgTF().requestFocusInWindow();
	}

	/**
	 * Logs on to the network.
	 */
	@Override
	public void start()
	{
		controller.logOn();
		updateTitleAndTray();
	}

	/**
	 * Asks if the user wants to quit.
	 */
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

	/**
	 * Updates the titlebar and the system tray tooltip with
	 * current information about the application and the user.
	 */
	@Override
	public void updateTitleAndTray()
	{
		if ( me != null )
		{
			String title = Constants.APP_NAME + " v" + Constants.APP_VERSION + " - ";
			String tooltip = title + me.getNick();

			if ( me.isNewMsg() )
				title += "[!!] ";

			title += me.getNick();

			if ( !controller.isConnected() )
			{
				if ( controller.getChatState().isLoggedOn() )
				{
					title += " - (Connection lost)";
					tooltip += " - (Connection lost)";
				}

				else
				{
					title += " - (Not connected)";
					tooltip += " - (Not connected)";
				}
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

	/**
	 * Shows or hides the main window.
	 * The window will always be brought to front when shown.
	 */
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

	/**
	 * Opens the settings dialog window.
	 */
	@Override
	public void showSettings()
	{
		settingsDialog.showSettings();
	}

	/**
	 * Opens a file chooser, where the user can select a file to send to
	 * another user.
	 */
	@Override
	public void sendFile( final User user, final File selectedFile )
	{
		if ( user == null )
			return;

		else if ( user.isMe() )
		{
			JOptionPane.showMessageDialog( null, "You cannot send files to yourself.", Constants.APP_NAME
					+ " - Warning", JOptionPane.WARNING_MESSAGE );
		}

		else if ( me.isAway() )
		{
			JOptionPane.showMessageDialog( null, "You cannot send files while you are away.",
					Constants.APP_NAME + " - Warning", JOptionPane.WARNING_MESSAGE );
		}

		else if ( user.isAway() )
		{
			JOptionPane.showMessageDialog( null, "You cannot send files to " + user.getNick() + ", which is away.",
					Constants.APP_NAME + " - Warning", JOptionPane.WARNING_MESSAGE );
		}

		else if ( !user.isOnline() )
		{
			JOptionPane.showMessageDialog( null, "You cannot send files to " + user.getNick()
					+ ", which is not online anymore.",
					Constants.APP_NAME + " - Warning", JOptionPane.WARNING_MESSAGE );
		}

		else
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

	/**
	 * Gets the text written in the input field and either sends it to
	 * the command parser or sends it as a message.
	 */
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
					msgController.showOwnMessage( line );
				}

				catch ( final CommandException e )
				{
					msgController.showSystemMessage( e.getMessage() );
				}
			}
		}

		mainP.getMsgTF().setText( "" );
	}

	/**
	 * Gets the text from the input field of the private chat, and
	 * sends it as a message to the user.
	 */
	@Override
	public void writePrivate( final PrivateChatWindow privchat )
	{
		String line = privchat.getChatText();
		User user = privchat.getUser();

		if ( line.trim().length() > 0 )
		{
			try
			{
				controller.sendPrivateMessage( line, user );
				msgController.showPrivateOwnMessage( user, line );
			}

			catch ( final CommandException e )
			{
				msgController.showPrivateSystemMessage( user, e.getMessage() );
			}
		}

		privchat.clearChatText();
	}

	/**
	 * Shows a list of the supported commands and their syntax.
	 */
	@Override
	public void showCommands()
	{
		cmdParser.showCommands();
	}

	/**
	 * Checks if the user is currently writing, and updates the status.
	 */
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

	/**
	 * Changes the nick name of the user, if the nick is valid.
	 */
	@Override
	public boolean changeNick( final String nick )
	{
		String trimNick = nick.trim();

		if ( !trimNick.equals( me.getNick() ) )
		{
			if ( controller.isNickInUse( trimNick ) )
			{
				JOptionPane.showMessageDialog( null, "The nick is in use by someone else.", Constants.APP_NAME
						+ " - Change nick", JOptionPane.WARNING_MESSAGE );
			}

			else if ( !Tools.isValidNick( trimNick ) )
			{
				JOptionPane.showMessageDialog( null, "'" + trimNick + "' is not a valid nick name.\n\n"
						+ "A nick name can have between 1 and 10 characters.\nLegal characters are 'a-z',"
						+ " '0-9', '-' and '_'.", Constants.APP_NAME + " - Change nick", JOptionPane.WARNING_MESSAGE );
			}

			else
			{
				try
				{
					controller.changeMyNick( trimNick );
					msgController.showSystemMessage( "You changed nick to " + me.getNick() );
					updateTitleAndTray();
					return true;
				}

				catch ( final CommandException e )
				{
					JOptionPane.showMessageDialog( null, e.getMessage(),
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

	/**
	 * Runs when the user presses the cancel/close button in the
	 * transfer dialog. If the button's text is close, the dialog should
	 * close. If the text is cancel, the file transfer should stop,
	 * and the button should change text to close.
	 */
	@Override
	public void transferCancelled( final TransferDialog transferDialog )
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
					msgController.showSystemMessage( "You cancelled sending of "
							+ fs.getFile().getName() + " to " + fs.getNick().getNick() );
					tList.removeFileSender( fs );
				}
			}
		}
	}

	/**
	 * Notifies the user of a new message in different ways,
	 * depending on the state of the main chat window.
	 *
	 * <ul>
	 *   <li><i>Main chat in focus</i> - do nothing</li>
	 *   <li><i>Main chat out of focus</i> - beep, update main chat title</li>
	 *   <li><i>Main chat hidden</i> - beep, update systray</li>
	 * </ul>
	 */
	@Override
	public void notifyMessageArrived()
	{
		// Main chat hidden - beep, update systray
		if ( !gui.isVisible() )
		{
			if ( me.isAway() )
				sysTray.setAwayActivityState();

			else
			{
				sysTray.setNormalActivityState();
				beeper.beep();
			}
		}

		// Main chat out of focus - beep, update main chat title
		else if ( !gui.isFocused() )
		{
			updateTitleAndTray();

			if ( !me.isAway() )
				beeper.beep();
		}
	}

	/**
	 * Notifies the user of new private message in different ways,
	 * depending on the state of the main chat window and the private
	 * chat window.
	 *
	 * <br /><br />
	 *
	 * A private message can never be sent while the sender
	 * or receiver is away, so this method assumes that is the case.
	 *
	 * <ul>
	 *   <li><b>Main chat in focus</b></li>
	 *   <ul>
	 *     <li><i>Private chat in focus</i> - not possible</li>
	 *     <li><i>Private chat out of focus</i> - update privchat title</li>
	 *     <li><i>Private chat hidden</i> - do nothing</li>
	 *   </ul>
	 *
	 *   <li><b>Main chat out of focus</b></li>
	 *	 <ul>
	 *     <li><i>Private chat in focus</i> - do nothing</li>
	 *	   <li><i>Private chat out of focus</i> - beep, update privchat title</li>
	 *	   <li><i>Private chat hidden</i> - beep, update main chat title</li>
	 *   </ul>
	 *
	 *   <li><b>Main chat hidden</b></li>
	 *   <ul>
	 *	   <li><i>Private chat in focus</i> - do nothing</li>
	 *	   <li><i>Private chat out of focus</i> - beep, update privchat title</li>
	 *	   <li><i>Private chat hidden</i> - beep, update systray</li>
	 *   </ul>
	 * </ul>
	 *
	 * @param user The user that sent the private message.
	 */
	@Override
	public void notifyPrivateMessageArrived( final User user )
	{
		PrivateChatWindow privchat = user.getPrivchat();

		// Main chat hidden
		if ( !gui.isVisible() )
		{
			// Private chat hidden - beep, update systray
			if ( !privchat.isVisible() )
			{
				sysTray.setNormalActivityState();
				beeper.beep();
			}

			// Private chat out of focus - beep, update privchat title
			else if ( !privchat.isFocused() )
			{
				privchat.updateNick();
				beeper.beep();
			}
		}

		// Main chat out of focus
		else if ( !gui.isFocused() )
		{
			// Private chat hidden - beep, update main chat title
			if ( !privchat.isVisible() )
			{
				me.setNewMsg( true );
				updateTitleAndTray();
				beeper.beep();
			}

			// Private chat out of focus - beep, update privchat title
			else if ( !privchat.isFocused() )
			{
				privchat.updateNick();
				beeper.beep();
			}
		}

		// Main chat in focus
		else if ( gui.isFocused() )
		{
			// Private chat out of focus - update privchat title
			if ( privchat.isVisible() && !privchat.isFocused() )
				privchat.updateNick();
		}
	}

	/**
	 * Gives a notification beep, and opens a dialog box asking if the user
	 * wants to accept a file transfer from another user.
	 */
	@Override
	public boolean askFileSave( final String user, final String fileName, final String size )
	{
		beeper.beep();

		Object[] options = { "Yes", "Cancel" };
		int choice = JOptionPane.showOptionDialog( null, user + " wants to send you the file "
				+ fileName + " (" + size + ")\nAccept?", Constants.APP_NAME + " - File send",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] );

		return choice == JOptionPane.YES_OPTION;
	}

	/**
	 * Opens a file chooser so the user can choose where to save a file
	 * another user is trying to send. Warns if the file name chosen
	 * already exists.
	 */
	@Override
	public File showFileSave( final String fileName )
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

	/**
	 * Updates the titlebar and tray tooltip with current information.
	 */
	@Override
	public void showTopic()
	{
		updateTitleAndTray();
	}

	/**
	 * Creates a new {@link TransferDialog} for that {@link FileReceiver}.
	 */
	@Override
	public void showTransfer( final FileReceiver fileRes )
	{
		new TransferDialog( this, fileRes );
	}

	/**
	 * Creates a new {@link TransferDialog} for that {@link FileSender}.
	 */
	@Override
	public void showTransfer( final FileSender fileSend )
	{
		new TransferDialog( this, fileSend );
	}

	/**
	 * Updates the gui components depending on the away state.
	 */
	@Override
	public void changeAway( final boolean away )
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

	/**
	 * Notifies the open private chat windows that the away state has changed.
	 *
	 * @param away If the user is away.
	 */
	private void updateAwayInPrivChats( final boolean away )
	{
		UserList list = controller.getNickList();

		for ( int i = 0; i < list.size(); i++ )
		{
			User user = list.get( i );

			if ( user.getPrivchat() != null )
			{
				if ( !user.isAway() )
				{
					user.getPrivchat().setAway( away );
				}

				if ( away )
				{
					msgController.showPrivateSystemMessage( user, "You went away: " + me.getAwayMsg() );
				}

				else
				{
					msgController.showPrivateSystemMessage( user, "You came back" );
				}
			}
		}
	}

	/**
	 * If the user does not have a private chat window already,
	 * one is created.
	 */
	@Override
	public void createPrivChat( final User user )
	{
		if ( user.getPrivchat() == null )
			user.setPrivchat( new PrivateChatFrame( this, user ) );
	}

	/**
	 * Shows the user's private chat window.
	 */
	@Override
	public void showPrivChat( final User user )
	{
		createPrivChat( user );
		user.getPrivchat().setVisible( true );
		controller.changeNewMessage( user.getCode(), false );
	}

	/**
	 * Resets the new private message field of the user.
	 */
	@Override
	public void activatedPrivChat( final User user )
	{
		if ( user.isNewPrivMsg() )
			controller.changeNewMessage( user.getCode(), false );
	}

	/**
	 * Returns the message controller for swing.
	 */
	@Override
	public MessageController getMessageController()
	{
		return msgController;
	}

	/**
	 * Returns if the main chat is in focus.
	 */
	@Override
	public boolean isFocused()
	{
		return gui.isFocused();
	}

	/**
	 * Returns if the main chat is visible.
	 */
	@Override
	public boolean isVisible()
	{
		return gui.isVisible();
	}
}
