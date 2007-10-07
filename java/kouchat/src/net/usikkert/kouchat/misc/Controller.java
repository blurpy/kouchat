
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

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.net.MessageParser;
import net.usikkert.kouchat.net.DefaultMessageResponder;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.util.DayTimer;

public class Controller
{
	private static Logger log = Logger.getLogger( Controller.class.getName() );

	private ChatState chatState;
	private NickController nickController;
	private Messages messages;
	private MessageParser msgParser;
	private DefaultMessageResponder msgResponder;
	private IdleThread idleThread;
	private TransferList tList;
	private WaitingList wList;

	public Controller( UserInterface ui )
	{
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			public void run()
			{
				logOff();
			}
		} );

		nickController = new NickController();
		chatState = new ChatState();
		idleThread = new IdleThread( this, ui );
		tList = new TransferList();
		wList = new WaitingList();
		msgResponder = new DefaultMessageResponder( this, ui );
		msgParser = new MessageParser( msgResponder );
		messages = new Messages();
		
		new DayTimer( ui );
	}

	public TopicDTO getTopic()
	{
		return chatState.getTopic();
	}

	public NickList getNickList()
	{
		return nickController.getNickList();
	}

	public boolean isWrote()
	{
		return chatState.isWrote();
	}

	public void updateLastIdle( int code, long lastIdle )
	{
		nickController.updateLastIdle( code, lastIdle );
	}

	public void changeWriting( int code, boolean writing )
	{
		nickController.changeWriting( code, writing );
		NickDTO me = Settings.getSettings().getMe();

		if ( code == me.getCode() )
		{
			chatState.setWrote( writing );

			if ( writing )
				messages.sendWritingMessage();
			else
				messages.sendStoppedWritingMessage();
		}
	}

	public boolean isNickInUse( String nick )
	{
		return nickController.isNickInUse( nick );
	}

	public boolean isNewUser( int code )
	{
		return nickController.isNewUser( code );
	}

	public void changeNick( int code, String nick )
	{
		nickController.changeNick( code, nick );
		NickDTO me = Settings.getSettings().getMe();

		if ( code == me.getCode() )
		{
			messages.sendNickMessage();
		}
	}

	public NickDTO getNick( int code )
	{
		return nickController.getNick( code );
	}
	
	public NickDTO getNick( String nick )
	{
		return nickController.getNick( nick );
	}

	public void logOn()
	{
		messages.sendLogonMessage();
		messages.sendExposeMessage();
		messages.sendGetTopicMessage();
		idleThread.start();

		// Not sure if this is the best way to set if I'm logged on or not
		TimerTask delayedLogon = new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( 800 );
				}

				catch ( InterruptedException e )
				{
					log.log( Level.SEVERE, e.getMessage(), e );
				}

				wList.setLoggedOn( true );
			}
		};

		Timer timer = new Timer();
		timer.schedule( delayedLogon, 0 );
	}

	public void logOff()
	{
		idleThread.stopThread();
		messages.sendLogoffMessage();
		messages.stop();
		msgParser.stop();
	}

	public void sendExposeMessage()
	{
		messages.sendExposeMessage();
	}

	public void sendExposingMessage()
	{
		messages.sendExposingMessage();
	}

	public void sendGetTopicMessage()
	{
		messages.sendGetTopicMessage();
	}

	public void sendIdleMessage()
	{
		messages.sendIdleMessage();
	}

	public void sendChatMessage( String msg )
	{
		messages.sendChatMessage( msg );
	}

	public void sendTopicMessage( TopicDTO topic )
	{
		messages.sendTopicMessage( topic );
	}

	public void sendAwayMessage()
	{
		messages.sendAwayMessage();
	}

	public void sendBackMessage()
	{
		messages.sendBackMessage();
	}

	public void sendNickCrashMessage( String nick )
	{
		messages.sendNickCrashMessage( nick );
	}

	public void sendFileAbort( int msgCode, int fileHash, String fileName )
	{
		messages.sendFileAbort( msgCode, fileHash, fileName );
	}

	public void sendFileAccept( int msgCode, int port, int fileHash, String fileName )
	{
		messages.sendFileAccept( msgCode, port, fileHash, fileName );
	}

	public void sendFile( int sendToUserCode, long fileLength, int fileHash, String fileName )
	{
		messages.sendFile( sendToUserCode, fileLength, fileHash, fileName );
	}

	public void changeAwayStatus( int code, boolean away, String awaymsg )
	{
		nickController.changeAwayStatus( code, away, awaymsg );
	}

	public TransferList getTransferList()
	{
		return tList;
	}

	public WaitingList getWaitingList()
	{
		return wList;
	}

	public boolean restartMsgReceiver()
	{
		return msgParser.restart();
	}
}
