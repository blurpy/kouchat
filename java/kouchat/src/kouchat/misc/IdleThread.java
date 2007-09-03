
package kouchat.misc;

public class IdleThread extends Thread
{
	private boolean run;
	private Controller controller;
	
	public IdleThread( Controller controller )
	{
		this.controller = controller;
		run = true;
	}

	public void run()
	{
		while ( run )
		{
			try
			{
				sleep( 15000 );
				controller.sendIdleMessage();
			}
			
			catch ( InterruptedException e )
			{
				e.printStackTrace();
				run = false;
			}
		}
	}
	
	public void stopThread()
	{
		run = false;
	}
}
