
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.MessageListener;
import net.usikkert.kouchat.event.ReceiverEvent;
import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.Nick;
import net.usikkert.kouchat.misc.Settings;

public class MessageParser implements ReceiverListener
{
	private static Logger log = Logger.getLogger( MessageParser.class.getName() );
	
	private MessageReceiver receiver;
	private List<MessageListener> listeners;
	private Settings settings;
	private boolean loggedOn;
	
	public MessageParser()
	{
		settings = Settings.getSettings();
		listeners = new ArrayList<MessageListener>();
		receiver = new MessageReceiver();
		receiver.addReceiverListener( this );
		receiver.start();
	}
	
	public void addMessageListener( MessageListener listener )
	{
		listeners.add( listener );
	}
	
	public void removeMessageListener( MessageListener listener )
	{
		listeners.remove( listener );
	}
	
	private void fireMessageArrived( String msg, int color )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.messageArrived( msg, color );
		}
	}
	
	private void fireMeLogOn( String ipAddress )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.meLogOn( ipAddress );
		}
	}
	
	private void fireUserLogOn( Nick newUser )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.userLogOn( newUser );
		}
	}
	
	private void fireUserLogOff( int userCode )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.userLogOff( userCode );
		}
	}
	
	private void fireUserExposing( Nick user )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.userExposing( user );
		}
	}
	
	private void fireTopicChanged( String newTopic, String nick, long time )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.topicChanged( newTopic, nick, time );
		}
	}
	
	private void fireTopicRequested()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.topicRequested();
		}
	}

	private void fireAwayChanged( int userCode, boolean away, String awayMsg )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.awayChanged( userCode, away, awayMsg );
		}
	}
	
	private void fireWritingChanged( int msgCode, boolean writing )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.writingChanged( msgCode, writing );
		}
	}
	
	private void fireMeIdle()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.meIdle();
		}
	}
	
	private void fireUserIdle( int userCode )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.userIdle( userCode );
		}
	}
	
	private void fireNickCrash()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.nickCrash();
		}
	}
	
	private void fireExposeRequested()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.exposeRequested();
		}
	}
	
	private void fireNickChanged( int userCode, String newNick )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.nickChanged( userCode, newNick );
		}
	}
	
	private void fireSendFile( long byteSize, String fileName, String user, int fileHash, int fileCode, int userCode )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.fileSend( byteSize, fileName, user, fileHash, fileCode, userCode );
		}
	}
	
	private void fireSendFileAborted( int msgCode, String fileName, int fileHash )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.fileSendAborted( msgCode, fileName, fileHash );
		}
	}
	
	private void fireSendFileAccepted( int msgCode, String fileName, int fileHash, int port )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.fileSendAccepted( msgCode, fileName, fileHash, port );
		}
	}
	
	public void stop()
	{
		receiver.stopReceiver();
	}
	
	public void messageArrived( ReceiverEvent re )
	{
		String message = re.getMessage();
		String ipAddress = re.getIp();
		
		System.out.println( message );
		
		try
		{
			int exclamation = message.indexOf( "!" );
			int hash = message.indexOf( "#" );
			int colon = message.indexOf( ":" );
			
			int msgCode = Integer.parseInt( message.substring( 0, exclamation ) );
			String type = message.substring( exclamation +1, hash );
			String msgNick = message.substring( hash +1, colon );
			String msg = message.substring( colon +1, message.length() );
			
			Nick tempme = settings.getNick();
			
			if ( msgCode != tempme.getCode() && loggedOn )
			{
				if ( type.equals( "MSG" ) )
				{
					int leftBracket = msg.indexOf( "[" );
					int rightBracket = msg.indexOf( "]" );
					int rgb = Integer.parseInt( msg.substring( leftBracket +1, rightBracket ) );
					
					fireMessageArrived( "<" + msgNick + ">: " + msg.substring( rightBracket +1, msg.length() ), rgb);
				}
				
				else if ( type.equals( "LOGON" ) )
				{
					Nick newUser = new Nick( msgNick, msgCode );
					newUser.setIpAddress( ipAddress );
					newUser.setLastIdle( System.currentTimeMillis() );
					
					fireUserLogOn( newUser );
				}
				
				else if ( type.equals( "EXPOSING" ) )
				{
					Nick user = new Nick( msgNick, msgCode );
					user.setIpAddress( ipAddress );
					user.setAwayMsg( msg );
					
					if ( msg.length() > 0 )
						user.setAway( true );
					
					user.setLastIdle( System.currentTimeMillis() );
					fireUserExposing( user );
				}
				
				else if ( type.equals( "LOGOFF" ) )
				{
					fireUserLogOff( msgCode );
				}
				
				else if ( type.equals( "AWAY" ) )
				{
					fireAwayChanged( msgCode, true, msg );
				}
				
				else if ( type.equals( "BACK" ) )
				{
					fireAwayChanged( msgCode, false, "" );
				}
				
				else if ( type.equals( "EXPOSE" ) )
				{
					fireExposeRequested();
				}
				
				else if ( type.equals( "NICKCRASH" ) )
				{
					if ( tempme.getNick().equals( msg ) )
					{
						fireNickCrash();
					}
				}
				
				else if ( type.equals( "WRITING" ) )
				{
					fireWritingChanged( msgCode, true );
				}
				
				else if ( type.equals( "STOPPEDWRITING" ) )
				{
					fireWritingChanged( msgCode, false );
				}
				
				else if ( type.equals( "GETTOPIC" ) )
				{
					fireTopicRequested();
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
							
							fireTopicChanged( theTopic, theNick, theTime );
						}
					}
					
					catch ( StringIndexOutOfBoundsException e )
					{
						log.log( Level.SEVERE, e.getMessage(), e );
					}
				}
				
				else if ( type.equals( "NICK" ) )
				{
					fireNickChanged( msgCode, msgNick );
				}
				
				else if ( type.equals( "IDLE" ) )
				{
					fireUserIdle( msgCode );
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
						
						fireSendFileAccepted( msgCode, fileName, fileHash, port );
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
						
						fireSendFileAborted( msgCode, fileName, fileHash );
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
						
						fireSendFile( byteSize, fileName, msgNick, fileHash, fileCode, msgCode );
					}
				}
			}
			
			else if ( type.equals( "LOGON" ) )
			{
				fireMeLogOn( ipAddress );
				loggedOn = true;
			}
			
			else if ( type.equals( "IDLE" ) )
			{
				fireMeIdle();
			}
		}
		
		// This must not halt
		catch ( Exception e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}
}
