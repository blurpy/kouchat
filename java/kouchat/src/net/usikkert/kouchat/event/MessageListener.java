
package net.usikkert.kouchat.event;

import net.usikkert.kouchat.misc.Nick;

public interface MessageListener
{
	public void messageArrived( String msg, int color );
	public void topicChanged( String newTopic, String nick, long time );
	public void topicRequested();
	public void awayChanged( int userCode, boolean away, String awayMsg );
	public void nickChanged( int userCode, String newNick );
	public void nickCrash();
	public void meLogOn( String ipAddress );
	public void userLogOn( Nick newUser );
	public void userLogOff( int userCode );
	public void userExposing( Nick user );
	public void exposeRequested();
	public void writingChanged( int userCode, boolean writing );
	public void meIdle();
	public void userIdle( int userCode );
//	public void fileSend();
}
