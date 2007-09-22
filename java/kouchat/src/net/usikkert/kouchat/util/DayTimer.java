
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

package net.usikkert.kouchat.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.usikkert.kouchat.event.DayListener;

/**
 * Notifies a listener when the day changes.
 * Checks every hour, in case daylight saving changes the time.
 * 
 * @author Christian Ihle
 */
public class DayTimer extends TimerTask
{
	private boolean done;
	private DayListener listener;
	private static final int NOTIFY_HOUR = 0;

	public DayTimer()
	{
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
			if ( listener != null )
				listener.dayChanged( new Date() );

			done = true;
		}

		else if ( hour != NOTIFY_HOUR && done )
		{
			done = false;
		}
	}

	public void registerDayListener( DayListener listener )
	{
		this.listener = listener;
	}
}
