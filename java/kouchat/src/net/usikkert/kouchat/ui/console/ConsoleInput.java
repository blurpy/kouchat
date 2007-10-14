
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

package net.usikkert.kouchat.ui.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.misc.AwayException;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.UIMessages;
import net.usikkert.kouchat.misc.UserInterface;

public class ConsoleInput
{
	private static Logger log = Logger.getLogger( ConsoleInput.class.getName() );

	private BufferedReader stdin;
	private Controller controller;
	private CommandParser cmdParser;
	private UIMessages uiMsg;
	private NickDTO me;

	public ConsoleInput( Controller controller, UserInterface ui )
	{
		this.controller = controller;

		uiMsg = ui.getUIMessages();
		stdin = new BufferedReader( new InputStreamReader( System.in ) );
		cmdParser = new CommandParser( controller, ui );
		me = Settings.getSettings().getMe();

		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			public void run()
			{
				System.out.println( "Quitting... Bye!" );
			}
		} );
	}

	public void input()
	{
		String input = "";

		while ( !input.startsWith( "/quit" ) )
		{
			try
			{
				input = stdin.readLine();

				if ( input.trim().length() > 0 )
				{
					if ( input.startsWith( "/" ) )
					{
						if ( !input.startsWith( "/quit" ) )
						{
							cmdParser.parse( input );
						}
					}

					else
					{
						if ( !me.isAway() )
						{
							try
							{
								controller.sendChatMessage( input );
								uiMsg.showOwnMessage( input );
							}

							catch ( AwayException e )
							{
								log.log( Level.WARNING, e.getMessage() );
								uiMsg.showActionNotAllowed();
							}
						}

						else
						{
							uiMsg.showActionNotAllowed();
						}
					}
				}
			}

			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}

		System.exit( 0 );
	}
}
