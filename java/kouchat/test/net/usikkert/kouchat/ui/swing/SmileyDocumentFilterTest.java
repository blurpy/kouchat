
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

package net.usikkert.kouchat.ui.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.swing.ImageIcon;

import org.junit.Test;

/**
 * Test of {@link SmileyDocumentFilter}.
 *
 * @author Christian Ihle
 */
public class SmileyDocumentFilterTest
{
	/**
	 * Tests that a smiley is detected when the smiley has no text before or after.
	 */
	@Test
	public void testSmileyHasWhitespace1()
	{
		SmileyDocumentFilter filter = new SmileyDocumentFilter( true );
		Smiley smiley = new Smiley( 0, new ImageIcon( "" ), ":)" );
		assertTrue( filter.smileyHasWhitespace( smiley, ":)" ) );
	}

	/**
	 * Tests that a smiley is detected when the smiley has no text before.
	 */
	@Test
	public void testSmileyHasWhitespace2()
	{
		SmileyDocumentFilter filter = new SmileyDocumentFilter( true );
		Smiley smiley = new Smiley( 1, new ImageIcon( "" ), ":)" );
		assertTrue( filter.smileyHasWhitespace( smiley, " :)" ) );
	}

	/**
	 * Tests that a smiley is detected when the smiley has no text after.
	 */
	@Test
	public void testSmileyHasWhitespace3()
	{
		SmileyDocumentFilter filter = new SmileyDocumentFilter( true );
		Smiley smiley = new Smiley( 0, new ImageIcon( "" ), ":)" );
		assertTrue( filter.smileyHasWhitespace( smiley, ":) " ) );
	}

	/**
	 * Tests that a smiley is detected when the smiley has whitespace before and after.
	 */
	@Test
	public void testSmileyHasWhitespace4()
	{
		SmileyDocumentFilter filter = new SmileyDocumentFilter( true );
		Smiley smiley = new Smiley( 1, new ImageIcon( "" ), ":)" );
		assertTrue( filter.smileyHasWhitespace( smiley, " :) " ) );
	}

	/**
	 * Tests that a smiley is not detected when the smiley has non-whitespace text around.
	 */
	@Test
	public void testSmileyHasNoWhitespace()
	{
		SmileyDocumentFilter filter = new SmileyDocumentFilter( true );
		Smiley smiley = new Smiley( 0, new ImageIcon( "" ), ":)" );
		assertFalse( filter.smileyHasWhitespace( smiley, ":):)" ) );
	}

	/**
	 * Tests that the correct smiley is found when there are several,
	 * but only one with whitespace.
	 */
	@Test
	public void testFindSmiley()
	{
		SmileyDocumentFilter filter = new SmileyDocumentFilter( true );
		Smiley smiley = filter.findSmiley( "Test :):) :) :):) Test", 0 );
		assertNotNull( smiley );
		assertEquals( 10, smiley.getStartPosition() );
		assertEquals( 12, smiley.getStopPosition() );
	}
}
