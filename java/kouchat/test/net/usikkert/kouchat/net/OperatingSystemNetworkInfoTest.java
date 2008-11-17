
/***************************************************************************
 *   Copyright 2006-2008 by Christian Ihle                                 *
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.Test;

/**
 * Test of {@link OperatingSystemNetworkInfo}.
 *
 * @author Christian Ihle
 */
public class OperatingSystemNetworkInfoTest
{
	/**
	 * Tests if the network interface for the operating system can be found.
	 *
	 * <p>But only if there are usable network interfaces available.</p>
	 *
	 * @throws SocketException In case of network issues.
	 */
	@Test
	public void testFindingTheOSNetworkInterface() throws SocketException
	{
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		if ( networkInterfaces == null )
		{
			System.out.println( "Skipping test, no network interfaces found." );
			return;
		}

		boolean validNetworkAvailable = false;

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface networkInterface = networkInterfaces.nextElement();

			if ( NetworkUtils.isUsable( networkInterface ) )
			{
				validNetworkAvailable = true;
				break;
			}
		}

		if ( !validNetworkAvailable )
		{
			System.out.println( "Skipping test, no usable network interfaces found." );
			return;
		}

		OperatingSystemNetworkInfo osNicInfo = new OperatingSystemNetworkInfo();
		NetworkInterface osInterface = osNicInfo.getOperatingSystemNetworkInterface();
		assertNotNull( osInterface );
		assertTrue( NetworkUtils.isUsable( osInterface ) );

		System.out.println( NetworkUtils.getNetworkInterfaceInfo( osInterface ) );
	}
}
