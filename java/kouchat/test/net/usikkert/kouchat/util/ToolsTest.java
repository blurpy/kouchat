
/***************************************************************************
 *   Copyright 2006-2009 by Christian Ihle                                 *
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test of {@link Tools}.
 *
 * @author Christian Ihle
 */
public class ToolsTest
{
	/**
	 * Tests that capitalization of the first letter in a word works as expected.
	 */
	@Test
	public void testCapitalizeFirstLetter()
	{
		assertNull( Tools.capitalizeFirstLetter( null ) );
		assertEquals( "Monkey", Tools.capitalizeFirstLetter( "monkey" ) );
		assertEquals( "Kou", Tools.capitalizeFirstLetter( "kou" ) );
		assertEquals( "Up", Tools.capitalizeFirstLetter( "up" ) );
		assertEquals( "O", Tools.capitalizeFirstLetter( "o" ) );
		assertEquals( "-", Tools.capitalizeFirstLetter( "-" ) );
		assertEquals( "", Tools.capitalizeFirstLetter( "" ) );
		assertEquals( "CAKE", Tools.capitalizeFirstLetter( "CAKE" ) );
		assertEquals( "123", Tools.capitalizeFirstLetter( "123" ) );
	}

	/**
	 * Tests the shortening of words.
	 */
	@Test
	public void testShorten()
	{
		assertNull( Tools.shorten( null, 5 ) );
		assertEquals( "Monkey", Tools.shorten( "Monkey", 12 ) );
		assertEquals( "Monkey", Tools.shorten( "Monkey", 6 ) );
		assertEquals( "Monke", Tools.shorten( "Monkey", 5 ) );
		assertEquals( "M", Tools.shorten( "Monkey", 1 ) );
		assertEquals( "", Tools.shorten( "Monkey", 0 ) );
		assertEquals( "", Tools.shorten( "Monkey", -5 ) );
	}
}
