
package net.usikkert.kouchat.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.usikkert.kouchat.event.DayListener;

public class DayTimer extends TimerTask
{
	private boolean done;
	private List<DayListener> listeners;
	private static final int NOTIFY_HOUR = 0;
	
	public DayTimer()
	{
		listeners = new ArrayList<DayListener>();
		
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		
		long interval = 1000 * 60 * 60; // 1 hour
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate( this, new Date( cal.getTimeInMillis() ), interval );
	}
	
	@Override
	public void run()
	{
		int hour = Calendar.getInstance().get( Calendar.HOUR_OF_DAY );
		
		if ( hour == NOTIFY_HOUR && !done )
		{
			fireDayChanged( new Date() );
			done = true;
		}
		
		else if ( hour != NOTIFY_HOUR && done )
		{
			done = false;
		}
	}
	
	private void fireDayChanged( Date date )
	{
		for ( DayListener dl : listeners )
		{
			dl.dayChanged( date );
		}
	}
	
	public void addDayListener( DayListener listener )
	{
		listeners.add( listener );
	}
	
	public void removeDayListener( DayListener listener )
	{
		listeners.remove( listener );
	}
}
