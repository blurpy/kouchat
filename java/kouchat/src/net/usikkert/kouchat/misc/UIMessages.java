
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

package net.usikkert.kouchat.misc;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.util.Tools;

/**
 * This class contains all the system messages that can appear
 * in the chat window. This is to make it easy to keep the
 * messages consistent and quick to change. It also means that
 * these messages do not need to be reimplemented by different
 * user interfaces.
 * 
 * @author Christian Ihle
 *
 */
public class UIMessages
{
	private MessageController msgController;
	
	/**
	 * Default constructor
	 * 
	 * @param msgController The controller that decides how to deal with messages
	 */
	public UIMessages( MessageController msgController )
	{
		this.msgController = msgController;
	}
	
	/**
	 * Shows a users message, with the users color.
	 * 
	 * @param user The user that sent the message
	 * @param message The message
	 * @param color The color to show the message with
	 */
	public void showUserMessage( String user, String message, int color )
	{
		msgController.showUserMessage( user, message, color );
	}
	
	/**
	 * Shows your own message
	 * 
	 * @param message The message to show
	 */
	public void showOwnMessage( String message )
	{
		msgController.showOwnMessage( message );
	}
	
	/**
	 * Shows "user logged off..."
	 * 
	 * @param user The user that logged off
	 */
	public void showLoggedOff( String user )
	{
		msgController.showSystemMessage( user + " logged off..." );
	}
	
	/**
	 * Shows "user removed the topic..."
	 * 
	 * @param user The user that removed the topic
	 */
	public void showTopicRemoved( String user )
	{
		msgController.showSystemMessage( user + " removed the topic..." );
	}
	
	/**
	 * Shows "user changed the topic to: topic"
	 * 
	 * @param user The user that changed the topic
	 * @param topic The new topic
	 */
	public void showTopicChanged( String user, String topic )
	{
		msgController.showSystemMessage( user + " changed the topic to: " + topic );
	}
	
	/**
	 * Shows information about the commands /help, /about, /clear,
	 * /whois, /names, /nick, /away, /back, /send, /msg, /transfers, /topic and //text
	 */
	public void showCommands()
	{
		msgController.showSystemMessage( Constants.APP_NAME + " commands:\n" +
				"/help - show this help message\n" +
				"/about - information about " + Constants.APP_NAME + "\n" +
				"/clear - clear all the text from the chat\n" +
				"/whois <nick> - show information about a user\n" +
				"/names - show the user list\n" +
				"/nick <new nick> - changes your nick name\n" +
				"/away <away message> - set status to away\n" +
				"/back - set status to not away\n" +
				"/send <nick> <file> - send a file to a user\n" +
				"/msg <nick> <msg> - send a private message to a user\n" +
				"/transfers - shows a list of all transfers and their status\n" +
				"/topic <optional new topic> - prints the current topic, or changes the topic\n" +
				"//<text> - send the text as a normal message, with a single slash" );
	}
	
	/**
	 * Shows "user went away: awayMsg"
	 * 
	 * @param user The user that went away
	 * @param awayMsg The away message the user set
	 */
	public void showUserAway( String user, String awayMsg )
	{
		msgController.showSystemMessage( user + " went away: " + awayMsg );
	}
	
	/**
	 * Shows "user came back..."
	 * 
	 * @param user The user that came back
	 */
	public void showUserBack( String user )
	{
		msgController.showSystemMessage( user + " came back..." );
	}

	/**
	 * Shows "This is APP_NAME vAPP_VERSION, by AUTHOR_NAME - AUTHOR_MAIL - AUTHOR_WEB"
	 */
	public void showAbout()
	{
		msgController.showSystemMessage( "This is " + Constants.APP_NAME + " v" + Constants.APP_VERSION +
				", by " + Constants.AUTHOR_NAME + " - " + Constants.AUTHOR_MAIL + 
				" - " + Constants.APP_WEB );
	}

	/**
	 * Shows "no topic set..."
	 */
	public void showNoTopic()
	{
		msgController.showSystemMessage( "No topic set..." );
	}

	/**
	 * Shows "user logged on from ipAddress..."
	 * 
	 * @param user The user that logged on
	 * @param ipAddress The ip address of the user that logged on
	 */
	public void showUserLoggedOn( String user, String ipAddress )
	{
		msgController.showSystemMessage( user + " logged on from " + ipAddress + "..." );
	}

	/**
	 * Shows "Today is date"
	 * 
	 * @param date The time and date right now
	 */
	public void showTodayIs( String date )
	{
		msgController.showSystemMessage( "Today is " + date );
	}

	/**
	 * Shows "You logged on as nick from ipAddress"
	 * 
	 * @param nick The nick you use
	 * @param ipAddress You ip address
	 */
	public void showMeLoggedOn( String nick, String ipAddress )
	{
		msgController.showSystemMessage( "You logged on as " + nick + " from " + ipAddress );
	}

	/**
	 * Shows "user changed ip from oldIp to newIp"
	 * 
	 * @param user The user that changed ip
	 * @param oldIP The old ip address
	 * @param newIP The new ip address
	 */
	public void showChangedIp( String user, String oldIP, String newIP )
	{
		msgController.showSystemMessage( user + " changed ip from " + oldIP + " to " + newIP );
	}

	/**
	 * Shows "Nick crash, resetting nick to newNick"
	 * 
	 * @param newNick The new nick
	 */
	public void showNickCrash( String newNick )
	{
		msgController.showSystemMessage( "Nick crash, resetting nick to " + newNick );
	}

	/**
	 * Shows "oldNick changed nick to newNick"
	 * 
	 * @param oldNick The old nick
	 * @param newNick The new nick
	 */
	public void showNickChanged( String oldNick, String newNick )
	{
		msgController.showSystemMessage( oldNick + " changed nick to " + newNick );
	}

	/**
	 * Shows "user showed up unexpectedly from ipAddress"
	 * 
	 * @param user The user that shows up
	 * @param ipAddress The ip of that user
	 */
	public void showShowedUnexpectedly( String user, String ipAddress )
	{
		msgController.showSystemMessage( user + " showed up unexpectedly from " + ipAddress + "..." );
	}

	/**
	 * Shows "Topic is: topic (set by user at date)"
	 * 
	 * @param topic The topic to show
	 * @param user The user that set that topic
	 * @param date The time and date when that topic was set
	 */
	public void showTopic( String topic, String user, String date )
	{
		msgController.showSystemMessage( "Topic is: " + topic + " (set by " + user + " at " + date + ")" );
	}

	/**
	 * Shows "Unknown command 'command'. Type /help for a list of commands."
	 * 
	 * @param command
	 */
	public void showUnknownCommand( String command )
	{
		msgController.showSystemMessage( "Unknown command '" + command + "'. Type /help for a list of commands." );
	}

	/**
	 * Shows "user timed out..."
	 * 
	 * @param user The user that timed out
	 */
	public void showUserTimedOut( String user )
	{
		msgController.showSystemMessage( user + " timed out..." );
	}

	/**
	 * Shows "Day changed to date"
	 * 
	 * @param date The time and date when the day changed
	 */
	public void showDayChanged( String date )
	{
		msgController.showSystemMessage( "Day changed to " + date );
	}

	/**
	 * Shows "user accepted sending of fileName"
	 * 
	 * @param user The user that accepted the file
	 * @param fileName The name of the file that was accepted
	 */
	public void showSendAccepted( String user, String fileName )
	{
		msgController.showSystemMessage( user + " accepted sending of " + fileName );
	}

	/**
	 * Shows "fileName successfully sent to user"
	 * 
	 * @param fileName The file that was sent
	 * @param user The that got the file
	 */
	public void showSendSuccess( String fileName, String user )
	{
		msgController.showSystemMessage( fileName + " successfully sent to " + user );
	}

	/**
	 * Shows "Failed to send fileName to user"
	 * 
	 * @param fileName The file that failed to be sent
	 * @param user The user that did not get the file
	 */
	public void showSendFailed( String fileName, String user )
	{
		msgController.showSystemMessage( "Failed to send " + fileName + " to " + user );
	}

	/**
	 * Shows "Failed to receive fileName from user"
	 * 
	 * @param fileName The file that failed to be received
	 * @param user The user that tried to send that file
	 */
	public void showReceiveFailed( String fileName, String user )
	{
		msgController.showSystemMessage( "Failed to receive " + fileName + " from " + user );
	}

	/**
	 * Shows "Successfully received orgFileName from user, and saved as newFileName"
	 * 
	 * @param orgFileName The file name sent by user
	 * @param user The user that sent the file
	 * @param newFileName The file name the file is saved as
	 */
	public void showReceiveSuccess( String orgFileName, String user, String newFileName )
	{
		msgController.showSystemMessage( "Successfully received " + orgFileName 
				+ " from " + user + ", and saved as " + newFileName );
	}

	/**
	 * Shows "You declined to receive fileName from user"
	 * 
	 * @param fileName The file you declined
	 * @param user The user that tried to send that file
	 */
	public void showReceiveDeclined( String fileName, String user )
	{
		msgController.showSystemMessage( "You declined to receive " + fileName + " from " + user );
	}

	/**
	 * Shows "Welcome to APP_NAME vAPP_VERSION!"
	 */
	public void showWelcomeMsg()
	{
		msgController.showSystemMessage( "Welcome to " + Constants.APP_NAME + " v" + Constants.APP_VERSION + "!" );
	}

	/**
	 * Shows "Trying to send the file fileName [fileSize] to user"
	 * 
	 * @param fileName The name of the file to send
	 * @param fileSize The size of the file to send
	 * @param user The user that will get the file
	 */
	public void showSendRequest( String fileName, String fileSize, String user )
	{
		msgController.showSystemMessage( "Trying to send the file " + fileName + " [" + fileSize + "] to " + user );
	}

	/**
	 * Shows "You cancelled sending of fileName to user"
	 * 
	 * @param fileName The name of the file that was sent
	 * @param user The user that got the file
	 */
	public void showSendCancelled( String fileName, String user )
	{
		msgController.showSystemMessage( "You cancelled sending of " + fileName + " to " + user );
	}

	/**
	 * Shows "user is trying to send the file fileName [fileSize]"
	 * 
	 * @param user The user trying to send you a file
	 * @param fileName The name of the file
	 * @param fileSize The size of the file
	 */
	public void showReceiveRequest( String user, String fileName, String fileSize )
	{
		msgController.showSystemMessage( user + " is trying to send the file " + fileName + " [" + fileSize + "]" );
	}

	/**
	 * Shows "user aborted sending of fileName"
	 * 
	 * @param user The user that aborted the sending
	 * @param fileName The name of the file
	 */
	public void showSendAborted( String user, String fileName )
	{
		msgController.showSystemMessage( user + " aborted sending of " + fileName );
	}

	/**
	 * Shows "/away - you are already away: 'awayMsg'"
	 * 
	 * @param awayMsg The away message
	 */
	public void showCmdAwayAlready( String awayMsg )
	{
		msgController.showSystemMessage( "/away - you are already away: '" + awayMsg + "'" );
	}

	/**
	 * Shows "/away - missing argument &lt;away message&gt;"
	 */
	public void showCmdAwayMissingArgs()
	{
		msgController.showSystemMessage( "/away - missing argument <away message>" );
	}

	/**
	 * Shows "/whois - missing argument &lt;nick&gt;"
	 */
	public void showCmdWhoisMissingArgs()
	{
		msgController.showSystemMessage( "/whois - missing argument <nick>" );
	}

	/**
	 * Shows "/whois - no such user 'user'"
	 * 
	 * @param user The user that was not found
	 */
	public void showCmdWhoisNoUser( String user )
	{
		msgController.showSystemMessage( "/whois - no such user '" + user + "'" );
	}

	/**
	 * Shows "/whois - user:<br />
	 * IP address:<br />
	 * Client:<br />
	 * Operating System:<br />
	 * Online:<br />
	 * Away message:"
	 * 
	 * @param user The user to show info about
	 */
	public void showCmdWhois( NickDTO user )
	{
		String info = "/whois - " + user.getNick();
		
		if ( user.isAway() )
			info += " (Away)";
		
		info += ":\nIP address: " + user.getIpAddress() +
				"\nClient: " + user.getClient() +
				"\nOperating System: " + user.getOperatingSystem() +
				"\nOnline: " + Tools.howLongFromNow( user.getLogonTime() );

		if ( user.isAway() )
			info += "\nAway message: " + user.getAwayMsg();

		msgController.showSystemMessage( info );
	}

	/**
	 * Shows "/send - missing arguments &lt;nick&gt; &lt;file&gt;"
	 */
	public void showCmdSendMissingArgs()
	{
		msgController.showSystemMessage( "/send - missing arguments <nick> <file>" );
	}

	/**
	 * Shows "/send - no such user 'user'"
	 * 
	 * @param user The user that was not found
	 */
	public void showCmdSendNoUser( String user )
	{
		msgController.showSystemMessage( "/send - no such user '" + user + "'" );
	}

	/**
	 * Shows "/send - no such file 'file'"
	 * 
	 * @param file The file that was not found
	 */
	public void showCmdSendNoFile( String file )
	{
		msgController.showSystemMessage( "/send - no such file '" + file + "'" );
	}

	/**
	 * Shows "/send - no point in doing that!"
	 */
	public void showCmdSendNoPoint()
	{
		msgController.showSystemMessage( "/send - no point in doing that!" );
	}

	/**
	 * Shows "/nick - missing argument &lt;nick&gt;"
	 */
	public void showCmdNickMissingArgs()
	{
		msgController.showSystemMessage( "/nick - missing argument <nick>" );
	}

	/**
	 * Shows "/nick - 'nick' is in use by someone else..."
	 * 
	 * @param nick The nick in use
	 */
	public void showCmdNickInUse( String nick )
	{
		msgController.showSystemMessage( "/nick - '" + nick + "' is in use by someone else..." );
	}

	/**
	 * Shows "/nick - 'nick' is not a valid nick name. (1-10 letters)"
	 * 
	 * @param nick The nick that is not valid
	 */
	public void showCmdNickNotValid( String nick )
	{
		msgController.showSystemMessage( "/nick - '" + nick + "' is not a valid nick name. (1-10 letters)" );
	}

	/**
	 * Show "/nick - you are already called 'nick'"
	 * 
	 * @param nick Your nick
	 */
	public void showCmdNickAlreadyCalled( String nick )
	{
		msgController.showSystemMessage( "/nick - you are already called '" + nick + "'" );
	}

	/**
	 * Shows "Users: nickList"
	 * 
	 * @param nickList A list of nick names
	 */
	public void showNickList( String nickList )
	{
		msgController.showSystemMessage( "Users: " + nickList );
	}
	
	/**
	 * Shows "/back - you are not away..."
	 */
	public void showCmdBackNotAway()
	{
		msgController.showSystemMessage( "/back - you are not away..." );
	}

	/**
	 * Shows "Action not allowed at this time..."
	 */
	public void showActionNotAllowed()
	{
		msgController.showSystemMessage( "Action not allowed at this time..." );
	}

	/**
	 * Shows "Operation not supported..."
	 */
	public void showNotSupported()
	{
		msgController.showSystemMessage( "Operation not supported..." );
	}

	/**
	 * Shows "File transfers: senders receivers"
	 * 
	 * @param senders A list of file send transfer
	 * @param receivers A list of file receive transfers
	 */
	public void showTransfers( String senders, String receivers )
	{
		msgController.showSystemMessage( "File transfers:\n" + senders + receivers );
	}

	/**
	 * Show "To save the file, use /receive."
	 */
	public void showSaveWith()
	{
		msgController.showSystemMessage( "To save the file, use /receive" );
	}
	
	/**
	 * Shows "/msg - missing arguments &lt;nick&gt; &lt;msg&gt;"
	 */
	public void showCmdMsgMissingArgs()
	{
		msgController.showSystemMessage( "/msg - missing arguments <nick> <msg>" );
	}
	
	/**
	 * Shows "/msg - no point in doing that!"
	 */
	public void showCmdMsgNoPoint()
	{
		msgController.showSystemMessage( "/msg - no point in doing that!" );
	}
	
	/**
	 * Shows "/msg - no such user 'user'"
	 * 
	 * @param user The user that was not found
	 */
	public void showCmdMsgNoUser( String user )
	{
		msgController.showSystemMessage( "/msg - no such user '" + user + "'" );
	}
	
	/**
	 * Shows "/msg - user can't receive private chat messages..."
	 * 
	 * @param user The user who could not receive private chat messages.
	 */
	public void showCmdMsgNoPort( String user )
	{
		msgController.showSystemMessage( "/msg - " + user + " can't receive private chat messages..." );
	}
	
	/**
	 * Shows a users private message, with the user's color.
	 * 
	 * @param user The user that sent the private message
	 * @param privmsg The private message
	 * @param color The color to show the message with
	 */
	public void showPrivateUserMessage( NickDTO user, String privmsg, int color )
	{
		msgController.showPrivateUserMessage( user, privmsg, color );
	}
	
	/**
	 * Shows your own private message
	 * 
	 * @param user The user that got you sent the message to
	 * @param privmsg The private message to show
	 */
	public void showPrivateOwnMessage( NickDTO user, String privmsg )
	{
		msgController.showPrivateOwnMessage( user, privmsg );
	}
	
	/**
	 * Shows "user logged off..."
	 * 
	 * @param user The user that logged off
	 */
	public void showPrivateLoggedOff( NickDTO user )
	{
		msgController.showPrivateSystemMessage( user, user.getNick() + " logged off..." );
	}

	/**
	 * Shows "user timed out..."
	 * 
	 * @param user The user that timed out
	 */
	public void showPrivateUserTimedOut( NickDTO user )
	{
		msgController.showPrivateSystemMessage( user, user.getNick() + " timed out..." );
	}

	/**
	 * Shows "oldNick changed nick to newNick"
	 * 
	 * @param oldNick The old nick
	 * @param user The user that changed nick
	 */
	public void showPrivateNickChanged( NickDTO user, String oldNick )
	{
		msgController.showPrivateSystemMessage( user, oldNick + " changed nick to " + user.getNick() );
	}

	/**
	 * Shows "user went away: awayMsg"
	 * 
	 * @param user The user that went away
	 */
	public void showPrivateUserAway( NickDTO user )
	{
		msgController.showPrivateSystemMessage( user, user.getNick() + " went away: " + user.getAwayMsg() );
	}

	/**
	 * Shows "user came back..."
	 * 
	 * @param user The user that came back
	 */
	public void showPrivateUserBack( NickDTO user )
	{
		msgController.showPrivateSystemMessage( user, user.getNick() + " came back..." );
	}
}
