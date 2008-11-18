
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.autocomplete.AutoCompleter;
import net.usikkert.kouchat.autocomplete.CommandAutoCompleteList;
import net.usikkert.kouchat.autocomplete.UserAutoCompleteList;
import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.net.DefaultPrivateMessageResponder;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.MessageParser;
import net.usikkert.kouchat.net.DefaultMessageResponder;
import net.usikkert.kouchat.net.MessageResponder;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.NetworkService;
import net.usikkert.kouchat.net.PrivateMessageParser;
import net.usikkert.kouchat.net.PrivateMessageResponder;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.DayTimer;
import net.usikkert.kouchat.util.JMXAgent;
import net.usikkert.kouchat.util.Loggers;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This controller gives access to the network and the state of the
 * application, like the user list and the topic.
 * <br><br>
 * When changing state, use the methods available <strong>here</strong> instead
 * of doing it manually, to make sure the state is consistent.
 * <br><br>
 * To connect to the network, use {@link #logOn()}.
 *
 * @author Christian Ihle
 */
public class Controller implements NetworkConnectionListener
{
	/** The logger. */
	private static final Logger LOG = Loggers.MISC_LOG;

	private final ChatState chatState;
	private final NickController nickController;
	private final NetworkService networkService;
	private final Messages messages;
	private final MessageParser msgParser;
	private final PrivateMessageParser privmsgParser;
	private final MessageResponder msgResponder;
	private final PrivateMessageResponder privmsgResponder;
	private final IdleThread idleThread;
	private final TransferList tList;
	private final WaitingList wList;
	private final User me;
	private final UserInterface ui;
	private final MessageController msgController;

	/**
	 * Constructor. Initializes the controller, but does not log on to
	 * the network.
	 *
	 * @param ui The active user interface object.
	 */
	public Controller( final UserInterface ui )
	{
		Validate.notNull( ui, "User interface can not be null" );
		this.ui = ui;

		Runtime.getRuntime().addShutdownHook( new Thread( "ControllerShutdownHook" )
		{
			@Override
			public void run()
			{
				logOff( false );
				shutdown();
			}
		} );

		me = Settings.getSettings().getMe();

		nickController = new NickController();
		chatState = new ChatState();
		tList = new TransferList();
		wList = new WaitingList();
		idleThread = new IdleThread( this, ui );
		networkService = new NetworkService();
		msgResponder = new DefaultMessageResponder( this, ui );
		privmsgResponder = new DefaultPrivateMessageResponder( this, ui );
		msgParser = new MessageParser( msgResponder );
		networkService.registerMessageReceiverListener( msgParser );
		privmsgParser = new PrivateMessageParser( privmsgResponder );
		networkService.registerUDPReceiverListener( privmsgParser );
		messages = new Messages( networkService );
		networkService.registerNetworkConnectionListener( this );
		msgController = ui.getMessageController();

		new JMXAgent( this, networkService.getConnectionWorker() );
		new DayTimer( ui );
		idleThread.start();

		msgController.showSystemMessage( "Welcome to " + Constants.APP_NAME + " v" + Constants.APP_VERSION + "!" );
		String date = Tools.dateToString( null, "EEEE, d MMMM yyyy" );
		msgController.showSystemMessage( "Today is " + date );
	}

	/**
	 * Gets the current topic.
	 *
	 * @return The current topic.
	 */
	public Topic getTopic()
	{
		return chatState.getTopic();
	}

	/**
	 * Gets the list of online users.
	 *
	 * @return The user list.
	 */
	public UserList getNickList()
	{
		return nickController.getNickList();
	}

	/**
	 * Returns if the application user wrote the last time
	 * {@link #changeWriting(int, boolean)} was called.
	 *
	 * @return If the user wrote.
	 * @see ChatState#isWrote()
	 */
	public boolean isWrote()
	{
		return chatState.isWrote();
	}

	/**
	 * Updates the write state for the user. This is useful to see which
	 * users are currently writing.
	 *
	 * If the user is the application user, messages will be sent to the
	 * other clients to notify of changes.
	 *
	 * @param code The user code for the user to update.
	 * @param writing True if the user is writing.
	 */
	public void changeWriting( final int code, final boolean writing )
	{
		nickController.changeWriting( code, writing );

		if ( code == me.getCode() )
		{
			chatState.setWrote( writing );

			if ( writing )
				messages.sendWritingMessage();
			else
				messages.sendStoppedWritingMessage();
		}
	}

	/**
	 * Updates the away status and the away message for the user.
	 *
	 * @param code The user code for the user to update.
	 * @param away If the user is away or not.
	 * @param awaymsg The away message for that user.
	 * @throws CommandException If there is no connection to the network,
	 * 		or the user tries to set an away message that is to long.
	 */
	public void changeAwayStatus( final int code, final boolean away, final String awaymsg ) throws CommandException
	{
		if ( code == me.getCode() && !isConnected() )
			throw new CommandException( "You can not change away mode without being connected." );
		else if ( Tools.getBytes( awaymsg ) > Constants.MESSAGE_MAX_BYTES )
			throw new CommandException( "You can not set an away message with more than " + Constants.MESSAGE_MAX_BYTES + " bytes." );
		else
			nickController.changeAwayStatus( code, away, awaymsg );
	}

	/**
	 * Checks if the nick is in use by another user.
	 *
	 * @param nick The nick to check.
	 * @return True if the nick is already in use.
	 */
	public boolean isNickInUse( final String nick )
	{
		return nickController.isNickInUse( nick );
	}

	/**
	 * Checks if the user with that user code is already in the user list.
	 *
	 * @param code The user code of the user to check.
	 * @return True if the user is not in the user list.
	 */
	public boolean isNewUser( final int code )
	{
		return nickController.isNewUser( code );
	}

	/**
	 * Changes the nick for the application user, sends a message over the
	 * network to notify the other clients of the change, and saves the changes.
	 *
	 * @param nick The new nick for the application user.
	 * @throws CommandException If the user is away.
	 */
	public void changeMyNick( final String nick ) throws CommandException
	{
		if ( me.isAway() )
			throw new CommandException( "You can not change nick while away." );

		else
		{
			changeNick( me.getCode(), nick );
			messages.sendNickMessage();
			Settings.getSettings().saveSettings();
		}
	}

	/**
	 * Changes the nick of the user.
	 *
	 * @param code The user code for the user.
	 * @param nick The new nick for the user.
	 */
	public void changeNick( final int code, final String nick )
	{
		nickController.changeNick( code, nick );
	}

	/**
	 * Gets the user with the specified user code.
	 *
	 * @param code The user code for the user.
	 * @return The user with the specified user code, or <em>null</em> if not found.
	 */
	public User getNick( final int code )
	{
		return nickController.getNick( code );
	}

	/**
	 * Gets the user with the specified nick name.
	 *
	 * @param nick The nick name to check for.
	 * @return The user with the specified nick name, or <em>null</em> if not found.
	 */
	public User getNick( final String nick )
	{
		return nickController.getNick( nick );
	}

	/**
	 * Sends the necessary network messages to log the user onto the network
	 * and query for the users and state.
	 */
	private void sendLogOn()
	{
		messages.sendLogonMessage();
		messages.sendClient();
		messages.sendExposeMessage();
		messages.sendGetTopicMessage();
	}

	/**
	 * This should be run after a successful logon, to update the connection state.
	 */
	private void runDelayedLogon()
	{
		Timer delayedLogonTimer = new Timer( "DelayedLogonTimer" );
		delayedLogonTimer.schedule( new DelayedLogonTask(), 0 );
	}

	/**
	 * Logs this client onto the network.
	 */
	public void logOn()
	{
		if ( !networkService.isConnectionWorkerAlive() )
			networkService.connect();
	}

	/**
	 * Logs this client off the network.
	 *
	 * <br /><br />
	 *
	 * <strong>Note:</strong> removeUsers should not be true when called
	 * from a ShutdownHook, as that will lead to a deadlock. See
	 * http://bugs.sun.com/bugdatabase/view_bug.do;?bug_id=6261550 for details.
	 *
	 * @param removeUsers Set to true to remove users from the nick list.
	 */
	public void logOff( final boolean removeUsers )
	{
		messages.sendLogoffMessage();
		chatState.setLoggedOn( false );
		chatState.setLogonCompleted( false );
		networkService.disconnect();
		getTopic().resetTopic();
		if ( removeUsers )
			removeAllUsers();
		me.reset();
	}

	/**
	 * Cancels all file transfers, sets all users as logged off,
	 * and removes them from the nick list.
	 */
	private void removeAllUsers()
	{
		UserList nickList = getNickList();

		for ( int i = 0; i < nickList.size(); i++ )
		{
			User user = nickList.get( i );

			if ( !user.isMe() )
			{
				user.setOnline( false );
				cancelFileTransfers( user );
				nickList.remove( user );

				if ( user.getPrivchat() != null )
				{
					msgController.showPrivateSystemMessage( user, "You logged off" );
					user.getPrivchat().setLoggedOff();
				}

				i--;
			}
		}
	}

	/**
	 * Cancels all file transfers for that user.
	 *
	 * @param user The user to cancel for.
	 */
	public void cancelFileTransfers( final User user )
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
	}

	/**
	 * Prepares the application for shutdown.
	 * Should <strong>only</strong> be called when the application shuts down.
	 */
	private void shutdown()
	{
		idleThread.stopThread();
	}

	/**
	 * Sends a message over the network, asking the other clients to identify
	 * themselves.
	 */
	public void sendExposeMessage()
	{
		messages.sendExposeMessage();
	}

	/**
	 * Sends a message over the network to identify this client.
	 */
	public void sendExposingMessage()
	{
		messages.sendExposingMessage();
	}

	/**
	 * Sends a message over the network to ask for the current topic.
	 */
	public void sendGetTopicMessage()
	{
		messages.sendGetTopicMessage();
	}

	/**
	 * Sends a message over the network to notify other clients that this
	 * client is still alive.
	 */
	public void sendIdleMessage()
	{
		messages.sendIdleMessage();
	}

	/**
	 * Sends a chat message over the network, to all the other users.
	 *
	 * @param msg The message to send.
	 * @throws CommandException If there is no connection to the network,
	 * 		or the application user is away,
	 * 		or the message is empty,
	 * 		or the message is too long.
	 */
	public void sendChatMessage( final String msg ) throws CommandException
	{
		if ( !isConnected() )
			throw new CommandException( "You can not send a chat message without being connected." );
		else if ( me.isAway() )
			throw new CommandException( "You can not send a chat message while away." );
		else if ( msg.trim().length() == 0 )
			throw new CommandException( "You can not send an empty chat message." );
		else if ( Tools.getBytes( msg ) > Constants.MESSAGE_MAX_BYTES )
			throw new CommandException( "You can not send a chat message with more than " + Constants.MESSAGE_MAX_BYTES + " bytes." );
		else
			messages.sendChatMessage( msg );
	}

	/**
	 * Sends a message over the network with the current topic.
	 */
	public void sendTopicMessage()
	{
		messages.sendTopicMessage( getTopic() );
	}

	/**
	 * Changes the topic, and sends a notification to the other clients.
	 *
	 * @param newTopic The new topic to set.
	 * @throws CommandException If there is no connection to the network,
	 * 		or the application user is away,
	 * 		or the topic is too long.
	 */
	public void changeTopic( final String newTopic ) throws CommandException
	{
		if ( !isConnected() )
			throw new CommandException( "You can not change the topic without being connected." );
		else if ( me.isAway() )
			throw new CommandException( "You can not change the topic while away." );
		else if ( Tools.getBytes( newTopic ) > Constants.MESSAGE_MAX_BYTES )
			throw new CommandException( "You can not set a topic with more than " + Constants.MESSAGE_MAX_BYTES + " bytes." );

		else
		{
			long time = System.currentTimeMillis();
			Topic topic = getTopic();
			topic.changeTopic( newTopic, me.getNick(), time );
			sendTopicMessage();
		}
	}

	/**
	 * Sends a message over the network with the current away message, to
	 * notify the other clients that the application user has gone away.
	 */
	public void sendAwayMessage()
	{
		messages.sendAwayMessage();
	}

	/**
	 * Sends a message over the network to notify the other clients that
	 * the application user is back from away.
	 */
	public void sendBackMessage()
	{
		messages.sendBackMessage();
	}

	/**
	 * Sends a message over the network to notify the other clients that
	 * a client has tried to logon using the nick name of the
	 * application user.
	 *
	 * @param nick The nick that is already in use by the application user.
	 */
	public void sendNickCrashMessage( final String nick )
	{
		messages.sendNickCrashMessage( nick );
	}

	/**
	 * Sends a message over the network to notify the file sender that you
	 * aborted the file transfer.
	 *
	 * @param userCode The user code of the user sending a file.
	 * @param fileHash The unique hash code of the file.
	 * @param fileName The name of the file.
	 */
	public void sendFileAbort( final int userCode, final int fileHash, final String fileName )
	{
		messages.sendFileAbort( userCode, fileHash, fileName );
	}

	/**
	 * Sends a message over the network to notify the file sender that you
	 * accepted the file transfer.
	 *
	 * @param userCode The user code of the user sending a file.
	 * @param port The port the file sender can connect to on this client
	 * 		to start the file transfer.
	 * @param fileHash The unique hash code of the file.
	 * @param fileName The name of the file.
	 */
	public void sendFileAccept( final int userCode, final int port, final int fileHash, final String fileName )
	{
		messages.sendFileAccept( userCode, port, fileHash, fileName );
	}

	/**
	 * Sends a message over the network to notify another user that the
	 * application user wants to send a file.
	 *
	 * @param sendToUserCode The user code of the user asked to receive a file.
	 * @param fileLength The size of the file, in bytes.
	 * @param fileHash The unique hash code of the file.
	 * @param fileName The name of the file.
	 * @throws CommandException If there is no connection to the network,
	 * 		or the application user is away,
	 * 		or the file name is too long.
	 */
	public void sendFile( final int sendToUserCode, final long fileLength, final int fileHash, final String fileName ) throws CommandException
	{
		if ( !isConnected() )
			throw new CommandException( "You can not send a file without being connected." );
		else if ( me.isAway() )
			throw new CommandException( "You can not send a file while away." );
		else if ( Tools.getBytes( fileName ) > Constants.MESSAGE_MAX_BYTES )
			throw new CommandException( "You can not send a file with a name with more than " + Constants.MESSAGE_MAX_BYTES + " bytes." );
		else
			messages.sendFile( sendToUserCode, fileLength, fileHash, fileName );
	}

	/**
	 * Gets the list of current transfers.
	 *
	 * @return The list of transfers.
	 */
	public TransferList getTransferList()
	{
		return tList;
	}

	/**
	 * Gets the list of unidentified users.
	 *
	 * @return The list of unidentified users.
	 */
	public WaitingList getWaitingList()
	{
		return wList;
	}

	/**
	 * If any users have timed out because of missed idle messages, then
	 * send a message over the network to ask all clients to identify
	 * themselves again.
	 */
	public void updateAfterTimeout()
	{
		if ( nickController.isTimeoutUsers() )
			messages.sendExposeMessage();
	}

	/**
	 * Sends a message over the network with more information about this client.
	 */
	public void sendClientInfo()
	{
		messages.sendClient();
	}

	/**
	 * Sends a private chat message over the network, to the specified user.
	 *
	 * @param privmsg The private message to send.
	 * @param userIP The ip address of the specified user.
	 * @param userPort The port to send the private message to.
	 * @param userCode The user code of the user to send the private message to.
	 * @throws CommandException If there is no connection to the network,
	 * 		or the application user is away,
	 * 		or the private message is empty,
	 * 		or the private message is too long,
	 * 		or the specified user has no port to send the private message to.
	 */
	public void sendPrivateMessage( final String privmsg, final String userIP, final int userPort, final int userCode ) throws CommandException
	{
		if ( !isConnected() )
			throw new CommandException( "You can not send a private chat message without being connected." );
		else if ( me.isAway() )
			throw new CommandException( "You can not send a private chat message while away." );
		else if ( privmsg.trim().length() == 0 )
			throw new CommandException( "You can not send an empty private chat message." );
		else if ( Tools.getBytes( privmsg ) > Constants.MESSAGE_MAX_BYTES )
			throw new CommandException( "You can not send a private chat message with more than " + Constants.MESSAGE_MAX_BYTES + " bytes." );
		else if ( userPort == 0 )
			throw new CommandException( "You can not send a private chat message to a user with no available port number." );
		else
			messages.sendPrivateMessage( privmsg, userIP, userPort, userCode );
	}

	/**
	 * Updates if the user has unread private messages for the
	 * application user.
	 *
	 * @param code The user code for the user to update.
	 * @param newMsg True if the user has unread private messages.
	 */
	public void changeNewMessage( final int code, final boolean newMsg )
	{
		nickController.changeNewMessage( code, newMsg );
	}

	/**
	 * Returns if the client is logged on to the network.
	 *
	 * @return True if the client is logged on to the network.
	 */
	public boolean isConnected()
	{
		return networkService.isNetworkUp() && chatState.isLoggedOn();
	}

	/**
	 * This timer task sleeps for 1.5 seconds before updating the
	 * {@link WaitingList} to set the status to logged on if the
	 * client was successful in connecting to the network.
	 *
	 * @author Christian Ihle
	 */
	private class DelayedLogonTask extends TimerTask
	{
		/**
		 * The task runs as a thread.
		 */
		@Override
		public void run()
		{
			try
			{
				Thread.sleep( 1500 );
			}

			catch ( final InterruptedException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}

			if ( networkService.isNetworkUp() )
			{
				chatState.setLogonCompleted( true );
				// To stop the timer from running in the background
				cancel();
			}
		}
	}

	/**
	 * Creates a new instance of the {@link AutoCompleter}, with
	 * a {@link CommandAutoCompleteList} and a {@link UserAutoCompleteList}.
	 *
	 * @return A new instance of a ready-to-use AutoCompleter.
	 */
	public AutoCompleter getAutoCompleter()
	{
		AutoCompleter autoCompleter = new AutoCompleter();
		autoCompleter.addAutoCompleteList( new CommandAutoCompleteList() );
		autoCompleter.addAutoCompleteList( new UserAutoCompleteList( getNickList() ) );

		return autoCompleter;
	}

	/**
	 * Makes sure the application reacts when the network is available.
	 */
	@Override
	public void networkCameUp()
	{
		// Network came up after a logon
		if ( !chatState.isLoggedOn() )
		{
			runDelayedLogon();
			sendLogOn();
		}

		// Network came up after a timeout
		else
		{
			ui.showTopic();
			msgController.showSystemMessage( "You are connected to the network again" );
			messages.sendGetTopicMessage();
			messages.sendExposeMessage();
		}
	}

	/**
	 * Makes sure the application reacts when the network is unavailable.
	 */
	@Override
	public void networkWentDown()
	{
		ui.showTopic();

		if ( chatState.isLoggedOn() )
			msgController.showSystemMessage( "You lost contact with the network" );
		else
			msgController.showSystemMessage( "You logged off" );
	}

	/**
	 * Gets the chat state.
	 *
	 * @return The chat state.
	 */
	public ChatState getChatState()
	{
		return chatState;
	}
}
