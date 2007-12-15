
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UDPSenderTest
{
	private UDPSender sender;

	@Before
	public void setUp() throws Exception
	{
		sender = new UDPSender();
	}

	@After
	public void tearDown() throws Exception
	{
		sender.stopSender();
	}

	@Test
	public void testUDPSender()
	{
		assertNotNull( sender );
	}

	@Test
	public void testSend()
	{
		testStartSender();
		sender.send( "testing testing", "192.168.1.4", 1234 );
	}

	@Test
	public void testStopSender()
	{
		sender.stopSender();
	}

	@Test
	public void testStartSender()
	{
		sender.startSender();
	}
}
