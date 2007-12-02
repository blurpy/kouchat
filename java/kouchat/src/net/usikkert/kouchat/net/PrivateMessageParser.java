
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

package net.usikkert.kouchat.net;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;

public class PrivateMessageParser implements ReceiverListener
{
	private static Logger log = Logger.getLogger( PrivateMessageParser.class.getName() );

	private UDPReceiver receiver;
	private Settings settings;
	private PrivateMessageResponder privmsgResponder;

	public PrivateMessageParser( PrivateMessageResponder privmsgResponder )
	{
		this.privmsgResponder = privmsgResponder;

		settings = Settings.getSettings();
		receiver = new UDPReceiver();
		receiver.registerReceiverListener( this );
	}

	public void start()
	{
		receiver.startReceiver();
	}

	public void stop()
	{
		receiver.stopReceiver();
	}

	@Override
	public void messageArrived( String message, String ipAddress )
	{
		if ( settings.isDebug() )
			System.out.println( message );

		try
		{
			int exclamation = message.indexOf( "!" );
			int hash = message.indexOf( "#" );
			int colon = message.indexOf( ":" );

			int fromCode = Integer.parseInt( message.substring( 0, exclamation ) );

			String type = message.substring( exclamation +1, hash );
			String msg = message.substring( colon +1, message.length() );

			int leftPara = msg.indexOf( "(" );
			int rightPara = msg.indexOf( ")" );
			int toCode = Integer.parseInt( msg.substring( leftPara +1, rightPara ) );

			NickDTO tempme = settings.getMe();

			if ( fromCode != tempme.getCode() && toCode == tempme.getCode() )
			{
				if ( type.equals( "PRIVMSG" ) )
				{
					int leftBracket = msg.indexOf( "[" );
					int rightBracket = msg.indexOf( "]" );
					int rgb = Integer.parseInt( msg.substring( leftBracket +1, rightBracket ) );
					String privmsg = msg.substring( rightBracket +1, msg.length() );

					privmsgResponder.messageArrived( fromCode, privmsg, rgb );
				}
			}
		}

		catch ( StringIndexOutOfBoundsException e )
		{
			log.log( Level.SEVERE, e.toString(), e );
		}

		catch ( NumberFormatException e )
		{
			log.log( Level.SEVERE, e.toString(), e );
		}
	}
}
