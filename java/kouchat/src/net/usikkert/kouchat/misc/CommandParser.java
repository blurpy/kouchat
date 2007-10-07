
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

import java.io.File;

import java.util.Date;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.util.Tools;

public class CommandParser
{
	private Controller controller;
	private UserInterface ui;
	private NickDTO me;
	
	public CommandParser( Controller controller, UserInterface ui )
	{
		this.controller = controller;
		this.ui = ui;
	}
	
	private void cmdTopic( String args )
	{
		if ( args.length() == 0 )
		{
			TopicDTO topic = controller.getTopic();

			if ( topic.getTopic().equals( "" ) )
			{
				ui.showSystemMessage( "No topic set" );
			}

			else
			{
				ui.showSystemMessage( "Topic is: " + topic.getTopic() + " (set by " + 
						topic.getNick() + " at " + Tools.dateToString( 
								new Date( topic.getTime() ), "HH:mm:ss, dd. MMM. yy" ) + ")" );
			}
		}

		else
		{
			fixTopic( args );
		}
	}
	
	private void cmdAway( String args )
	{
		if ( me.isAway() )
		{
			ui.showSystemMessage( "You are already away: '" + me.getAwayMsg() + "'" );
		}

		else
		{
			if ( args.trim().length() == 0 )
				ui.showSystemMessage( "/away - missing argument <away message>" );
			else
				ui.changeAway( true, args.trim() );
		}
	}
	
	private void cmdClear()
	{
		ui.clearChat();
	}
	
	private void cmdAbout()
	{
		ui.showSystemMessage( "This is " + Constants.APP_NAME + " v" + Constants.APP_VERSION +
				", by " + Constants.AUTHOR_NAME + " - " + Constants.AUTHOR_MAIL + 
				" - " + Constants.AUTHOR_WEB );
	}
	
	private void cmdHelp()
	{
		showCommands();
	}
	
	private void cmdWhois( String args )
	{
		if ( args.trim().length() == 0 )
		{
			ui.showSystemMessage( "/whois - missing argument <nick>" );
		}

		else
		{
			String[] argsArray = args.split( "\\s" );
			String nick = argsArray[1].trim();

			NickDTO user = controller.getNick( nick );

			if ( user == null )
			{
				ui.showSystemMessage( "/whois - no such user '" + nick + "'" );
			}

			else
			{
				String info = "/whois - " + user.getNick() + " lives at " + user.getIpAddress();

				if ( user.isAway() )
					info += ", but is away and '" + user.getAwayMsg() + "'";

				ui.showSystemMessage( info );
			}
		}
	}
	
	private void cmdSend( String args )
	{
		String[] argsArray = args.split( "\\s" );

		if ( argsArray.length <= 2 )
		{
			ui.showSystemMessage( "/send - missing arguments <nick> <file>" );
		}
		
		else
		{
			String nick = argsArray[1];
			NickDTO user = controller.getNick( nick );
			
			if ( user != me )
			{
				if ( user == null )
				{
					ui.showSystemMessage( "/send - no such user '" + nick + "'" );
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
						ui.startFileSend( user, sendFile );
					}
					
					else
					{
						ui.showSystemMessage( "/send - no such file '" + file + "'" );
					}
				}
			}
			
			else
			{
				ui.showSystemMessage( "/send - no point in doing that!" );
			}
		}
	}
	
	private void cmdNick( String args )
	{
		if ( args.trim().length() == 0 )
		{
			ui.showSystemMessage( "/nick - missing argument <nick>" );
		}

		else
		{
			String[] argsArray = args.split( "\\s" );
			String nick = argsArray[1].trim();

			if ( !nick.equals( me.getNick() ) )
			{
				if ( controller.isNickInUse( nick ) )
				{
					ui.showSystemMessage( "/nick - '" + nick + "' is in use by someone else..." );
				}

				else if ( !Tools.isValidNick( nick ) )
				{
					ui.showSystemMessage( "/nick - '" + nick + "' is not a valid nick name. (1-10 letters)" );
				}

				else
				{
					controller.changeNick( me.getCode(), nick );
					ui.showSystemMessage( "/nick - you changed nick to '" + me.getNick() + "'" );
					ui.showTopic();
				}
			}

			else
			{
				ui.showSystemMessage( "/nick - you are already called '" + nick + "'" );
			}
		}
	}
	
	private void cmdSlash( String line )
	{
		String message = line.replaceFirst( "/", "" );
		ui.showOwnMessage( message );
		controller.sendChatMessage( message );
	}
	
	private void cmdUnknown( String command )
	{
		ui.showSystemMessage( "Unknown command '" + command + "'. Type /help for a list of commands." );
	}
	
	public void parse( String line )
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
			else if ( command.equals( "nick" ) )
				cmdNick( args );
			else if ( command.startsWith( "/" ) )
				cmdSlash( line );
			else
				cmdUnknown( command );
		}
	}
	
	public void showCommands()
	{
		ui.showSystemMessage( Constants.APP_NAME + " commands:\n" +
				"/help - show this help message\n" +
				"/about - information about " + Constants.APP_NAME + "\n" +
				"/clear - clear all the text from the chat\n" +
				"/whois <nick> - show information about a user\n" +
				"/away <away message> - set status to away\n" +
				"/send <nick> <file> - send a file to a user\n" +
				"/topic <optional new topic> - prints the current topic, or changes the topic\n" +
				"//<text> - send the text as a normal message, with a single slash" );
	}
}
