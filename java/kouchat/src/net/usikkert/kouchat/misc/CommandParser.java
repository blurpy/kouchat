
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

import java.io.File;

import java.util.Date;
import java.util.List;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;

/**
 * Parses and executes commands. A command starts with a slash, and can
 * have arguments.
 *
 * @author Christian Ihle
 */
public class CommandParser
{
	private final Controller controller;
	private final UserInterface ui;
	private final MessageController msgController;
	private final NickDTO me;

	/**
	 * Constructor.
	 *
	 * @param controller The controller.
	 * @param ui The user interface.
	 */
	public CommandParser( final Controller controller, final UserInterface ui )
	{
		this.controller = controller;
		this.ui = ui;

		msgController = ui.getMessageController();
		me = Settings.getSettings().getMe();
	}

	/**
	 * Command: <em>/topic &lt;optional new topic&gt;</em>.
	 * Prints the current topic if no arguments are supplied,
	 * or changes the topic. To remove the topic, use a space as the argument.
	 *
	 * @param args Nothing, or the new topic.
	 */
	private void cmdTopic( final String args )
	{
		if ( args.length() == 0 )
		{
			TopicDTO topic = controller.getTopic();

			if ( topic.getTopic().equals( "" ) )
			{
				msgController.showSystemMessage( "No topic set" );
			}

			else
			{
				String date = Tools.dateToString( new Date( topic.getTime() ), "HH:mm:ss, dd. MMM. yy" );
				msgController.showSystemMessage( "Topic is: " + topic.getTopic() + " (set by " + topic.getNick() + " at " + date + ")" );
			}
		}

		else
		{
			fixTopic( args );
		}
	}

	/**
	 * Command: <em>/away &lt;away message&gt;</em>.
	 * Set status to away.
	 *
	 * @param args The away message.
	 */
	private void cmdAway( final String args )
	{
		if ( me.isAway() )
		{
			msgController.showSystemMessage( "/away - you are already away: '" + me.getAwayMsg() + "'" );
		}

		else
		{
			if ( args.trim().length() == 0 )
			{
				msgController.showSystemMessage( "/away - missing argument <away message>" );
			}

			else
			{
				try
				{
					controller.changeAwayStatus( me.getCode(), true, args.trim() );
					controller.sendAwayMessage();
					ui.changeAway( true );
					msgController.showSystemMessage( "You went away: " + me.getAwayMsg() );
				}

				catch ( final CommandException e )
				{
					msgController.showSystemMessage( e.getMessage() );
				}
			}
		}
	}

	/**
	 * Command: <em>/back</em>.
	 * Set status to not away.
	 */
	private void cmdBack()
	{
		if ( me.isAway() )
		{
			try
			{
				controller.changeAwayStatus( me.getCode(), false, "" );
				controller.sendBackMessage();
				ui.changeAway( false );
				msgController.showSystemMessage( "You came back" );
			}

			catch ( final CommandException e )
			{
				msgController.showSystemMessage( e.getMessage() );
			}
		}

		else
		{
			msgController.showSystemMessage( "/back - you are not away" );
		}
	}

	/**
	 * Command: <em>/clear</em>.
	 * Clear all the text from the chat.
	 */
	private void cmdClear()
	{
		ui.clearChat();
	}

	/**
	 * Command: <em>/about</em>.
	 * Show information about the application.
	 */
	private void cmdAbout()
	{
		msgController.showSystemMessage( "This is " + Constants.APP_NAME + " v" + Constants.APP_VERSION
				+ ", by " + Constants.AUTHOR_NAME + " - " + Constants.AUTHOR_MAIL
				+ " - " + Constants.APP_WEB );
	}

	/**
	 * Command: <em>/help</em>.
	 * Shows a list of commands.
	 */
	private void cmdHelp()
	{
		showCommands();
	}

	/**
	 * Command: <em>/whois &lt;nick&gt;</em>.
	 * Show information about a user.
	 *
	 * @param args The user to show information about.
	 */
	private void cmdWhois( final String args )
	{
		if ( args.trim().length() == 0 )
		{
			msgController.showSystemMessage( "/whois - missing argument <nick>" );
		}

		else
		{
			String[] argsArray = args.split( "\\s" );
			String nick = argsArray[1].trim();

			NickDTO user = controller.getNick( nick );

			if ( user == null )
			{
				msgController.showSystemMessage( "/whois - no such user '" + nick + "'" );
			}

			else
			{
				String info = "/whois - " + user.getNick();

				if ( user.isAway() )
					info += " (Away)";

				info += ":\nIP address: " + user.getIpAddress()
						+ "\nClient: " + user.getClient()
						+ "\nOperating System: " + user.getOperatingSystem()
						+ "\nOnline: " + Tools.howLongFromNow( user.getLogonTime() );

				if ( user.isAway() )
					info += "\nAway message: " + user.getAwayMsg();

				msgController.showSystemMessage( info );
			}
		}
	}

	/**
	 * Command: <em>/send &lt;nick&gt; &lt;file&gt;</em>.
	 * Send a file to a user.
	 *
	 * @param args First argument is the user to send to, and the second is
	 * the file to send to the user.
	 */
	private void cmdSend( final String args )
	{
		String[] argsArray = args.split( "\\s" );

		if ( argsArray.length <= 2 )
		{
			msgController.showSystemMessage( "/send - missing arguments <nick> <file>" );
		}

		else
		{
			String nick = argsArray[1];
			NickDTO user = controller.getNick( nick );

			if ( user != me )
			{
				if ( user == null )
				{
					msgController.showSystemMessage( "/send - no such user '" + nick + "'" );
				}

				else
				{
					String file = "";

					for ( int i = 2; i < argsArray.length; i++ )
					{
						file += argsArray[i] + " ";
					}

					file = file.trim();
					File sendFile = new File( file );

					if ( sendFile.exists() && sendFile.isFile() )
					{
						sendFile( user, sendFile );
					}

					else
					{
						msgController.showSystemMessage( "/send - no such file '" + file + "'" );
					}
				}
			}

			else
			{
				msgController.showSystemMessage( "/send - no point in doing that!" );
			}
		}
	}

	/**
	 * Command: <em>/msg &lt;nick&gt; &lt;msg&gt;</em>.
	 * Send a private message to a user.
	 *
	 * @param args The first argument is the user to send to, and the
	 * second is the private message to the user.
	 */
	private void cmdMsg( final String args )
	{
		String[] argsArray = args.split( "\\s" );

		if ( argsArray.length <= 2 )
		{
			msgController.showSystemMessage( "/msg - missing arguments <nick> <msg>" );
		}

		else
		{
			String nick = argsArray[1];
			NickDTO user = controller.getNick( nick );

			if ( user == null )
			{
				msgController.showSystemMessage( "/msg - no such user '" + nick + "'" );
			}

			else if ( user == me )
			{
				msgController.showSystemMessage( "/msg - no point in doing that!" );
			}

			else if ( user.getPrivateChatPort() == 0 )
			{
				msgController.showSystemMessage( "/msg - " + user.getNick() + " can't receive private chat messages" );
			}

			else
			{
				String privmsg = "";

				for ( int i = 2; i < argsArray.length; i++ )
				{
					privmsg += argsArray[i] + " ";
				}

				privmsg = privmsg.trim();

				try
				{
					controller.sendPrivateMessage( privmsg, user.getIpAddress(), user.getPrivateChatPort(), user.getCode() );
					msgController.showPrivateOwnMessage( user, privmsg );
				}

				catch ( final CommandException e )
				{
					msgController.showSystemMessage( e.getMessage() );
				}
			}
		}
	}

	/**
	 * Command: <em>/nick &lt;new nick&gt;</em>.
	 * Changes your nick name.
	 *
	 * @param args The nick to change to.
	 */
	private void cmdNick( final String args )
	{
		if ( args.trim().length() == 0 )
		{
			msgController.showSystemMessage( "/nick - missing argument <nick>" );
		}

		else
		{
			String[] argsArray = args.split( "\\s" );
			String nick = argsArray[1].trim();

			if ( !nick.equals( me.getNick() ) )
			{
				if ( controller.isNickInUse( nick ) )
				{
					msgController.showSystemMessage( "/nick - '" + nick + "' is in use by someone else" );
				}

				else if ( !Tools.isValidNick( nick ) )
				{
					msgController.showSystemMessage( "/nick - '" + nick + "' is not a valid nick name. (1-10 letters)" );
				}

				else
				{
					try
					{
						controller.changeMyNick( nick );
						msgController.showSystemMessage( "You changed nick to " + me.getNick() );
						ui.showTopic();
					}

					catch ( final CommandException e )
					{
						msgController.showSystemMessage( e.getMessage() );
					}
				}
			}

			else
			{
				msgController.showSystemMessage( "/nick - you are already called '" + nick + "'" );
			}
		}
	}

	/**
	 * Command: <em>/names</em>.
	 * Shows a list of connected users.
	 */
	private void cmdNames()
	{
		NickList list = controller.getNickList();
		String nickList = "";

		for ( int i = 0; i < list.size(); i++ )
		{
			NickDTO nick = list.get( i );
			nickList += nick.getNick();

			if ( i < list.size() - 1 )
				nickList += ", ";
		}

		msgController.showSystemMessage( "Users: " + nickList );
	}

	/**
	 * Command: <em>/transfers</em>.
	 * Shows a list of all transfers and their status.
	 */
	private void cmdTransfers()
	{
		List<FileSender> fsList = controller.getTransferList().getFileSenders();
		List<FileReceiver> frList = controller.getTransferList().getFileReceivers();

		String senders = "Sending:";
		String receivers = "\nReceiving:";

		for ( FileSender fs : fsList )
		{
			senders += "\n" + fs.getFileName() + " [" + Tools.byteToString( fs.getFileSize() ) + "] (" + fs.getPercent() + "%) to " + fs.getNick().getNick();
		}

		for ( FileReceiver fr : frList )
		{
			receivers += "\n" + fr.getFileName() + " [" + Tools.byteToString( fr.getFileSize() ) + "] (" + fr.getPercent() + "%) from " + fr.getNick().getNick();
		}

		msgController.showSystemMessage( "File transfers:\n" + senders + receivers );
	}

	/**
	 * Command: <em>//&lt;text&gt;</em>.
	 * Sends the text as a message, instead of parsing it as a command.
	 *
	 * @param line The text starting with a slash.
	 */
	private void cmdSlash( final String line )
	{
		String message = line.replaceFirst( "/", "" );

		try
		{
			controller.sendChatMessage( message );
			msgController.showOwnMessage( message );
		}

		catch ( final CommandException e )
		{
			msgController.showSystemMessage( e.getMessage() );
		}
	}

	/**
	 * Command: <em>/'anything'</em>.
	 * The command was not recognized by the parser.
	 *
	 * @param command The unknown command.
	 */
	private void cmdUnknown( final String command )
	{
		msgController.showSystemMessage( "Unknown command '" + command + "'. Type /help for a list of commands" );
	}

	/**
	 * Updates the topic. If the new topic is empty, the topic will be removed.
	 *
	 * @param newTopic The new topic to use.
	 */
	public void fixTopic( final String newTopic )
	{
		TopicDTO topic = controller.getTopic();
		String trimTopic = newTopic.trim();

		if ( !trimTopic.equals( topic.getTopic().trim() ) )
		{
			try
			{
				controller.changeTopic( trimTopic );

				if ( trimTopic.length() > 0 )
					msgController.showSystemMessage( "You changed the topic to: " + trimTopic );
				else
					msgController.showSystemMessage( "You removed the topic" );

				ui.showTopic();
			}

			catch ( final CommandException e )
			{
				msgController.showSystemMessage( e.getMessage() );
			}
		}
	}

	/**
	 * Sends a file to a user.
	 *
	 * @param user The user to send to.
	 * @param file The file to send to the user.
	 */
	public void sendFile( final NickDTO user, final File file )
	{
		try
		{
			controller.sendFile( user.getCode(), file.length(), file.hashCode(), file.getName() );
			FileSender fileSend = new FileSender( user, file );
			ui.showTransfer( fileSend );
			controller.getTransferList().addFileSender( fileSend );
			String size = Tools.byteToString( file.length() );
			msgController.showSystemMessage( "Trying to send the file "
					+ file.getName() + " [" + size + "] to " + user.getNick() );
		}

		catch ( final CommandException e )
		{
			msgController.showSystemMessage( e.getMessage() );
		}
	}

	/**
	 * Shows a list of all the supported commands, with a short description.
	 */
	public void showCommands()
	{
		msgController.showSystemMessage( Constants.APP_NAME + " commands:\n"
				+ "/help - show this help message\n"
				+ "/about - information about " + Constants.APP_NAME + "\n"
				+ "/clear - clear all the text from the chat\n"
				+ "/whois <nick> - show information about a user\n"
				+ "/names - show the user list\n"
				+ "/nick <new nick> - changes your nick name\n"
				+ "/away <away message> - set status to away\n"
				+ "/back - set status to not away\n"
				+ "/send <nick> <file> - send a file to a user\n"
				+ "/msg <nick> <msg> - send a private message to a user\n"
				+ "/transfers - shows a list of all transfers and their status\n"
				+ "/topic <optional new topic> - prints the current topic, or changes the topic\n"
				+ "//<text> - send the text as a normal message, with a single slash" );
	}

	/**
	 * Parses the line to split the command from the arguments.
	 * The command is then checked against valid options and redirected
	 * to the appropriate method.
	 *
	 * @param line The command in its raw form.
	 */
	public void parse( final String line )
	{
		String command = "";

		if ( line.contains( " " ) )
			command = line.substring( 1, line.indexOf( ' ' ) );
		else
			command = line.substring( 1, line.length() );

		if ( command.length() > 0 )
		{
			String args = line.replaceFirst( "/" + command, "" );

			if ( command.equals( "topic" ) )
				cmdTopic( args );
			else if ( command.equals( "away" ) )
				cmdAway( args );
			else if ( command.equals( "back" ) )
				cmdBack();
			else if ( command.equals( "clear" ) )
				cmdClear();
			else if ( command.equals( "about" ) )
				cmdAbout();
			else if ( command.equals( "help" ) )
				cmdHelp();
			else if ( command.equals( "whois" ) )
				cmdWhois( args );
			else if ( command.equals( "send" ) )
				cmdSend( args );
			else if ( command.equals( "msg" ) )
				cmdMsg( args );
			else if ( command.equals( "nick" ) )
				cmdNick( args );
			else if ( command.equals( "names" ) )
				cmdNames();
			else if ( command.equals( "transfers" ) )
				cmdTransfers();
			else if ( command.startsWith( "/" ) )
				cmdSlash( line );
			else
				cmdUnknown( command );
		}

		else
			cmdUnknown( command );
	}
}
