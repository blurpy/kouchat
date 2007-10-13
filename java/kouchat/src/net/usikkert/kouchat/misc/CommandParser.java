
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

import net.usikkert.kouchat.util.Tools;

public class CommandParser
{
	private Controller controller;
	private UserInterface ui;
	private NickDTO me;
	private UIMessages uiMsg;
	
	public CommandParser( Controller controller, UserInterface ui )
	{
		this.controller = controller;
		this.ui = ui;
		
		me = Settings.getSettings().getMe();
		uiMsg = ui.getUIMessages();
	}
	
	private void cmdTopic( String args )
	{
		if ( args.length() == 0 )
		{
			TopicDTO topic = controller.getTopic();

			if ( topic.getTopic().equals( "" ) )
			{
				uiMsg.showNoTopic();
			}

			else
			{
				String date = Tools.dateToString( new Date( topic.getTime() ), "HH:mm:ss, dd. MMM. yy" );
				uiMsg.showTopic( topic.getTopic(), topic.getNick(), date );
			}
		}

		else
		{
			ui.fixTopic( args );
		}
	}
	
	private void cmdAway( String args )
	{
		if ( me.isAway() )
		{
			uiMsg.showCmdAwayAlready( me.getAwayMsg() );
		}

		else
		{
			if ( args.trim().length() == 0 )
			{
				uiMsg.showCmdAwayMissingArgs();
			}
			
			else
			{
				controller.changeAwayStatus( me.getCode(), true, args.trim() );
				controller.sendAwayMessage();
				ui.changeAway( true );
				uiMsg.showUserAway( "You", me.getAwayMsg() );
			}
		}
	}
	
	private void cmdClear()
	{
		ui.clearChat();
	}
	
	private void cmdAbout()
	{
		uiMsg.showAbout();
	}
	
	private void cmdHelp()
	{
		uiMsg.showCommands();
	}
	
	private void cmdWhois( String args )
	{
		if ( args.trim().length() == 0 )
		{
			uiMsg.showCmdWhoisMissingArgs();
		}

		else
		{
			String[] argsArray = args.split( "\\s" );
			String nick = argsArray[1].trim();

			NickDTO user = controller.getNick( nick );

			if ( user == null )
			{
				uiMsg.showCmdWhoisNoUser( nick );
			}

			else
			{
				uiMsg.showCmdWhois( user.getNick(), user.getIpAddress(), user.getAwayMsg() );
			}
		}
	}
	
	private void cmdSend( String args )
	{
		String[] argsArray = args.split( "\\s" );

		if ( argsArray.length <= 2 )
		{
			uiMsg.showCmdSendMissingArgs();
		}
		
		else
		{
			String nick = argsArray[1];
			NickDTO user = controller.getNick( nick );
			
			if ( user != me )
			{
				if ( user == null )
				{
					uiMsg.showCmdSendNoUser( nick );
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
						uiMsg.showCmdSendNoFile( file );
					}
				}
			}
			
			else
			{
				uiMsg.showCmdSendNoPoint();
			}
		}
	}
	
	private void cmdNick( String args )
	{
		if ( args.trim().length() == 0 )
		{
			uiMsg.showCmdNickMissingArgs();
		}

		else
		{
			String[] argsArray = args.split( "\\s" );
			String nick = argsArray[1].trim();

			if ( !nick.equals( me.getNick() ) )
			{
				if ( controller.isNickInUse( nick ) )
				{
					uiMsg.showCmdNickInUse( nick );
				}

				else if ( !Tools.isValidNick( nick ) )
				{
					uiMsg.showCmdNickNotValid( nick );
				}

				else
				{
					controller.changeNick( me.getCode(), nick );
					uiMsg.showNickChanged( "You", me.getNick() );
					ui.showTopic();
				}
			}

			else
			{
				uiMsg.showCmdNickAlreadyCalled( nick );
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
		uiMsg.showUnknownCommand( command );
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
}
