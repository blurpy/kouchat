
package net.usikkert.kouchat.misc;

import net.usikkert.kouchat.net.MessageListener;
import net.usikkert.kouchat.net.MessageParser;
import net.usikkert.kouchat.net.MessageSender;

public class Controller
{
	private ChatState chatState;
	private NickController nickController;
	private MessageSender msgSender;
	private MessageParser msgParser;
	private IdleThread idleThread;
	
	public Controller()
	{
		nickController = new NickController();
		chatState = new ChatState();
		msgParser = new MessageParser();
		msgSender = new MessageSender();
		idleThread = new IdleThread( this );
	}
	
	public Topic getTopic()
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
		Nick me = Settings.getSettings().getNick();
		
		if ( code == me.getCode() )
		{
			chatState.setWrote( writing );
			
			if ( writing )
				msgSender.sendWritingMessage();
			else
				msgSender.sendStoppedWritingMessage();
		}
	}
	
	public boolean checkIfValidNick( String tmp, boolean quiet )
	{
		return nickController.checkIfValidNick( tmp, quiet );
	}
	
	public boolean checkIfNewUser( int code )
	{
		return nickController.checkIfNewUser( code );
	}
	
	public void changeNick( int code, String nick )
	{
		nickController.changeNick( code, nick );
		Nick me = Settings.getSettings().getNick();
		
		if ( code == me.getCode() )
		{
			msgSender.sendNickMessage();
		}
	}
	
	public Nick getNick( int code )
	{
		return nickController.getNick( code );
	}
	
	public void addMessageListener( MessageListener listener )
	{
		msgParser.addMessageListener( listener );
	}
	
	public void logOn()
	{
		msgSender.sendLogonMessage();
		msgSender.sendExposeMessage();
		msgSender.sendGetTopicMessage();
		idleThread.start();
	}
	
	public void logOff()
	{
		idleThread.stopThread();
		msgParser.stop();
		msgSender.sendLogoffMessage();
		msgSender.stop();
	}
	
	public void sendExposeMessage()
	{
		msgSender.sendExposeMessage();
	}
	
	public void sendExposingMessage()
	{
		msgSender.sendExposingMessage();
	}
	
	public void sendGetTopicMessage()
	{
		msgSender.sendGetTopicMessage();
	}
	
	public void sendIdleMessage()
	{
		msgSender.sendIdleMessage();
	}
	
	public void sendChatMessage( String msg )
	{
		msgSender.sendChatMessage( msg );
	}
	
	public void sendTopicMessage( Topic topic )
	{
		msgSender.sendTopicMessage( topic );
	}
	
	public void sendAwayMessage()
	{
		msgSender.sendAwayMessage();
	}
	
	public void sendBackMessage()
	{
		msgSender.sendBackMessage();
	}
	
	public void sendNickCrashMessage( String nick )
	{
		msgSender.sendNickCrashMessage( nick );
	}
	
	public void changeAwayStatus( int code, boolean away, String awaymsg )
	{
		nickController.changeAwayStatus( code, away, awaymsg );
	}
}
