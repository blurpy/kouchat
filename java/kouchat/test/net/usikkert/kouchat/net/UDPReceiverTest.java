
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

import static org.junit.Assert.*;

import net.usikkert.kouchat.event.ReceiverListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UDPReceiverTest implements ReceiverListener
{
	private UDPReceiver res;

	@Before
	public void setUp() throws Exception
	{
		res = new UDPReceiver();
	}

	@After
	public void tearDown() throws Exception
	{
		res.stopReceiver();
	}

	@Test
	public void testUDPReceiver()
	{
		assertNotNull( res );
	}

	@Test
	public void testRun()
	{
		// Don't test the run method of a thread...
	}

	@Test
	public void testStartReceiver()
	{
		res.startReceiver();
	}

	@Test
	public void testStopReceiver()
	{
		res.stopReceiver();
	}

	@Test
	public void testRegisterReceiverListener() throws InterruptedException
	{
		res.registerReceiverListener( this );
		testStartReceiver();
		Thread.sleep( 4000 );
	}

	@Override
	public void messageArrived( String message, String ipAddress )
	{
		// Not part of the testing. Just to see if it works.
		System.out.println( message );
	}
}
