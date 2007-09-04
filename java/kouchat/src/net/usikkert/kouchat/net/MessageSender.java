
package net.usikkert.kouchat.net;

import net.usikkert.kouchat.misc.Nick;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.Topic;

public class MessageSender
{
	private Sender sender;
	private Nick me;
	private Settings settings;
	
	public MessageSender()
	{
		sender = new Sender();
		settings = Settings.getSettings();
		me = settings.getNick();
	}
	
	public void sendIdleMessage()
	{
		sender.send( me.getCode() + "!IDLE#" + me.getNick() + ":" );
	}
	
	public void sendTopicMessage( Topic topic )
	{
		sender.send( me.getCode() + "!TOPIC#" + me.getNick() + ":" + "(" + topic.getNick()	+ ")" + "[" + topic.getTime() + "]" + topic.getTopic() );
	}
	
	public void sendAwayMessage()
	{
		sender.send( me.getCode() + "!AWAY#" + me.getNick() + ":" + me.getAwayMsg() );
	}
	
	public void sendBackMessage()
	{
		sender.send( me.getCode() + "!BACK#" + me.getNick() + ":" );
	}
	
	public void sendChatMessage( String msg )
	{
		sender.send( me.getCode() + "!MSG#" + me.getNick() + ":[" + settings.getOwnColor() + "]" + msg );
	}
	
	public void sendLogonMessage()
	{
		sender.send( me.getCode() + "!LOGON#" + me.getNick() + ":" );
	}
	
	public void sendLogoffMessage()
	{
		sender.send( me.getCode() + "!LOGOFF#" + me.getNick() + ":" );
	}
	
	public void sendExposeMessage()
	{
		sender.send( me.getCode() + "!EXPOSE#" + me.getNick() + ":" );
	}
	
	public void sendExposingMessage()
	{
		sender.send( me.getCode() + "!EXPOSING#" + me.getNick() + ":" + me.getAwayMsg() );
	}
	
	public void sendGetTopicMessage()
	{
		sender.send( me.getCode() + "!GETTOPIC#" + me.getNick() + ":" );
	}
	
	public void sendWritingMessage()
	{
		sender.send( me.getCode() + "!WRITING#" + me.getNick() + ":" );
	}
	
	public void sendStoppedWritingMessage()
	{
		sender.send( me.getCode() + "!STOPPEDWRITING#" + me.getNick() + ":" );
	}
	
	public void sendNickMessage()
	{
		sender.send( me.getCode() + "!NICK#" + me.getNick() + ":" );
	}
	
	public void sendNickCrashMessage( String nick )
	{
		sender.send( me.getCode() + "!NICKCRASH#" + me.getNick() + ":" + nick );
	}
	
	public void stop()
	{
		sender.stopSender();
	}
}
