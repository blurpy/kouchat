
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

import net.usikkert.kouchat.event.MessageListener;
import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;

public class MessageParser implements ReceiverListener
{
	private static Logger log = Logger.getLogger( MessageParser.class.getName() );
	
	private MessageReceiver receiver;
	private MessageListener listener;
	private Settings settings;
	private boolean loggedOn;
	
	public MessageParser( MessageListener listener )
	{
		this.listener = listener;
		
		settings = Settings.getSettings();
		receiver = new MessageReceiver();
		receiver.registerReceiverListener( this );
		receiver.start();
	}
	
	public void stop()
	{
		receiver.stopReceiver();
	}
	
	public boolean restart()
	{
		return receiver.restartReceiver();
	}
	
	public void messageArrived( String message, String ipAddress )
	{
		System.out.println( message ); // TODO
		
		try
		{
			int exclamation = message.indexOf( "!" );
			int hash = message.indexOf( "#" );
			int colon = message.indexOf( ":" );
			
			int msgCode = Integer.parseInt( message.substring( 0, exclamation ) );
			String type = message.substring( exclamation +1, hash );
			String msgNick = message.substring( hash +1, colon );
			String msg = message.substring( colon +1, message.length() );
			
			NickDTO tempme = settings.getMe();
			
			if ( msgCode != tempme.getCode() && loggedOn )
			{
				if ( type.equals( "MSG" ) )
				{
					int leftBracket = msg.indexOf( "[" );
					int rightBracket = msg.indexOf( "]" );
					int rgb = Integer.parseInt( msg.substring( leftBracket +1, rightBracket ) );
					
					listener.messageArrived( msgCode, "<" + msgNick + ">: " + msg.substring( rightBracket +1, msg.length() ), rgb);
				}
				
				else if ( type.equals( "LOGON" ) )
				{
					NickDTO newUser = new NickDTO( msgNick, msgCode );
					newUser.setIpAddress( ipAddress );
					newUser.setLastIdle( System.currentTimeMillis() );
					
					listener.userLogOn( newUser );
				}
				
				else if ( type.equals( "EXPOSING" ) )
				{
					NickDTO user = new NickDTO( msgNick, msgCode );
					user.setIpAddress( ipAddress );
					user.setAwayMsg( msg );
					
					if ( msg.length() > 0 )
						user.setAway( true );
					
					user.setLastIdle( System.currentTimeMillis() );
					listener.userExposing( user );
				}
				
				else if ( type.equals( "LOGOFF" ) )
				{
					listener.userLogOff( msgCode );
				}
				
				else if ( type.equals( "AWAY" ) )
				{
					listener.awayChanged( msgCode, true, msg );
				}
				
				else if ( type.equals( "BACK" ) )
				{
					listener.awayChanged( msgCode, false, "" );
				}
				
				else if ( type.equals( "EXPOSE" ) )
				{
					listener.exposeRequested();
				}
				
				else if ( type.equals( "NICKCRASH" ) )
				{
					if ( tempme.getNick().equals( msg ) )
					{
						listener.nickCrash();
					}
				}
				
				else if ( type.equals( "WRITING" ) )
				{
					listener.writingChanged( msgCode, true );
				}
				
				else if ( type.equals( "STOPPEDWRITING" ) )
				{
					listener.writingChanged( msgCode, false );
				}
				
				else if ( type.equals( "GETTOPIC" ) )
				{
					listener.topicRequested();
				}
				
				else if ( type.equals( "TOPIC" ) )
				{
					try
					{
						int leftBracket = msg.indexOf( "[" );
						int rightBracket = msg.indexOf( "]" );
						int leftPara = msg.indexOf( "(" );
						int rightPara = msg.indexOf( ")" );
						
						if ( rightBracket != -1 && leftBracket != -1 )
						{
							String theNick = msg.substring( leftPara +1, rightPara );
							long theTime = Long.parseLong( msg.substring( leftBracket +1, rightBracket ) );
							String theTopic = null;
							
							if ( msg.length() > rightBracket + 1 )
							{
								theTopic = msg.substring( rightBracket +1, msg.length() );
							}
							
							listener.topicChanged( msgCode, theTopic, theNick, theTime );
						}
					}
					
					catch ( StringIndexOutOfBoundsException e )
					{
						log.log( Level.SEVERE, e.getMessage(), e );
					}
				}
				
				else if ( type.equals( "NICK" ) )
				{
					listener.nickChanged( msgCode, msgNick );
				}
				
				else if ( type.equals( "IDLE" ) )
				{
					listener.userIdle( msgCode );
				}
				
				else if ( type.equals( "SENDFILEACCEPT" ) )
				{
					int leftPara = msg.indexOf( "(" );
					int rightPara = msg.indexOf( ")" );
					int fileCode = Integer.parseInt( msg.substring( leftPara +1, rightPara ) );
					
					if ( fileCode == tempme.getCode() )
					{
						int leftCurly = msg.indexOf( "{" );
						int rightCurly = msg.indexOf( "}" );
						int leftBracket = msg.indexOf( "[" );
						int rightBracket = msg.indexOf( "]" );
						int port = Integer.parseInt( msg.substring( leftBracket +1, rightBracket ) );
						int fileHash = Integer.parseInt( msg.substring( leftCurly +1, rightCurly ) );
						String fileName = msg.substring( rightCurly +1, msg.length() );
						
						listener.fileSendAccepted( msgCode, fileName, fileHash, port );
					}
				}
				
				else if ( type.equals( "SENDFILEABORT" ) )
				{
					int leftPara = msg.indexOf( "(" );
					int rightPara = msg.indexOf( ")" );
					int fileCode = Integer.parseInt( msg.substring( leftPara +1, rightPara ) );
					
					if ( fileCode == tempme.getCode() )
					{
						int leftCurly = msg.indexOf( "{" );
						int rightCurly = msg.indexOf( "}" );
						String fileName = msg.substring( rightCurly +1, msg.length() );
						int fileHash = Integer.parseInt( msg.substring( leftCurly +1, rightCurly ) );
						
						listener.fileSendAborted( msgCode, fileName, fileHash );
					}
				}
				
				else if ( type.equals( "SENDFILE" ) )
				{
					int leftPara = msg.indexOf( "(" );
					int rightPara = msg.indexOf( ")" );
					int fileCode = Integer.parseInt( msg.substring( leftPara +1, rightPara ) );
					
					if ( fileCode == tempme.getCode() )
					{
						int leftCurly = msg.indexOf( "{" );
						int rightCurly = msg.indexOf( "}" );
						int leftBracket = msg.indexOf( "[" );
						int rightBracket = msg.indexOf( "]" );
						long byteSize = Long.parseLong( msg.substring( leftBracket +1, rightBracket ) );
						String fileName = msg.substring( rightCurly +1, msg.length() );
						int fileHash = Integer.parseInt( msg.substring( leftCurly +1, rightCurly ) );
						
						listener.fileSend( msgCode, byteSize, fileName, msgNick, fileHash, fileCode );
					}
				}
			}
			
			else if ( type.equals( "LOGON" ) )
			{
				listener.meLogOn( ipAddress );
				loggedOn = true;
			}
			
			else if ( type.equals( "IDLE" ) )
			{
				listener.meIdle();
			}
		}
		
		// This must not halt
		catch ( Exception e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}
}
