
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

import java.util.logging.Level;
import java.util.logging.Logger;

public class IdleThread extends Thread
{
	private static Logger log = Logger.getLogger( IdleThread.class.getName() );
	
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
				log.log( Level.SEVERE, e.getMessage(), e );
				run = false;
			}
		}
	}
	
	public void stopThread()
	{
		run = false;
	}
}
