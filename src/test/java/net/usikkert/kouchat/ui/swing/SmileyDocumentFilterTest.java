
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.ui.swing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.swing.ImageIcon;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;

import org.junit.Test;

/**
 * Test of {@link SmileyDocumentFilter}.
 *
 * @author Christian Ihle
 */
public class SmileyDocumentFilterTest {

    /** The smiley filter being tested. */
    private final SmileyDocumentFilter filter;

    /**
     * Constructor.
     */
    public SmileyDocumentFilterTest() {
        final ImageLoader imageLoader = new ImageLoader(mock(ErrorHandler.class));
        filter = new SmileyDocumentFilter(true, imageLoader, mock(Settings.class));
    }

    /**
     * Tests that a smiley is detected when the smiley has no text before or after.
     */
    @Test
    public void testSmileyHasWhitespace1() {
        final Smiley smiley = new Smiley(0, new ImageIcon(""), ":)");
        assertTrue(filter.smileyHasWhitespace(smiley, ":)"));
    }

    /**
     * Tests that a smiley is detected when the smiley has no text before.
     */
    @Test
    public void testSmileyHasWhitespace2() {
        final Smiley smiley = new Smiley(1, new ImageIcon(""), ":)");
        assertTrue(filter.smileyHasWhitespace(smiley, " :)"));
    }

    /**
     * Tests that a smiley is detected when the smiley has no text after.
     */
    @Test
    public void testSmileyHasWhitespace3() {
        final Smiley smiley = new Smiley(0, new ImageIcon(""), ":)");
        assertTrue(filter.smileyHasWhitespace(smiley, ":) "));
    }

    /**
     * Tests that a smiley is detected when the smiley has whitespace before and after.
     */
    @Test
    public void testSmileyHasWhitespace4() {
        final Smiley smiley = new Smiley(1, new ImageIcon(""), ":)");
        assertTrue(filter.smileyHasWhitespace(smiley, " :) "));
    }

    /**
     * Tests that a smiley is not detected when the smiley has non-whitespace text around.
     */
    @Test
    public void testSmileyHasNoWhitespace() {
        final Smiley smiley = new Smiley(0, new ImageIcon(""), ":)");
        assertFalse(filter.smileyHasWhitespace(smiley, ":):)"));
    }

    /**
     * Tests that the correct smiley is found when there are several,
     * but only one with whitespace.
     */
    @Test
    public void testFindSmiley() {
        final Smiley smiley = filter.findSmiley("Test :):) :) :):) Test", 0);

        assertNotNull(smiley);
        assertEquals(10, smiley.getStartPosition());
        assertEquals(12, smiley.getStopPosition());
        assertEquals(":)", smiley.getCode());
        assertNotNull(smiley.getIcon());
    }

    /**
     * Tests that all the correct smileys are found when there are several valid.
     */
    @Test
    public void testFindAllSmileys() {
        final String text = ":$ Test :p :S :) 8) :) ;);) ;) Test";

        final Smiley smiley1 = filter.findSmiley(text, 0);
        assertNotNull(smiley1);
        assertEquals(0, smiley1.getStartPosition());
        assertEquals(2, smiley1.getStopPosition());
        assertEquals(":$", smiley1.getCode());

        final Smiley smiley2 = filter.findSmiley(text, smiley1.getStopPosition());
        assertNotNull(smiley2);
        assertEquals(8, smiley2.getStartPosition());
        assertEquals(10, smiley2.getStopPosition());
        assertEquals(":p", smiley2.getCode());

        final Smiley smiley3 = filter.findSmiley(text, smiley2.getStopPosition());
        assertNotNull(smiley3);
        assertEquals(11, smiley3.getStartPosition());
        assertEquals(13, smiley3.getStopPosition());
        assertEquals(":S", smiley3.getCode());

        final Smiley smiley4 = filter.findSmiley(text, smiley3.getStopPosition());
        assertNotNull(smiley4);
        assertEquals(14, smiley4.getStartPosition());
        assertEquals(16, smiley4.getStopPosition());
        assertEquals(":)", smiley4.getCode());

        final Smiley smiley5 = filter.findSmiley(text, smiley4.getStopPosition());
        assertNotNull(smiley5);
        assertEquals(17, smiley5.getStartPosition());
        assertEquals(19, smiley5.getStopPosition());
        assertEquals("8)", smiley5.getCode());

        final Smiley smiley6 = filter.findSmiley(text, smiley5.getStopPosition());
        assertNotNull(smiley6);
        assertEquals(20, smiley6.getStartPosition());
        assertEquals(22, smiley6.getStopPosition());
        assertEquals(":)", smiley6.getCode());

        final Smiley smiley7 = filter.findSmiley(text, smiley6.getStopPosition());
        assertNotNull(smiley7);
        assertEquals(28, smiley7.getStartPosition());
        assertEquals(30, smiley7.getStopPosition());
        assertEquals(";)", smiley7.getCode());

        assertNull(filter.findSmiley(text, smiley7.getStopPosition()));
    }

    /**
     * Tests that all the different smileys are found.
     */
    @Test
    public void testAllSmileys() {
        final Smiley smile = filter.findSmiley(":)", 0);
        assertNotNull(smile);
        assertEquals(":)", smile.getCode());
        assertNotNull(smile.getIcon());

        final Smiley sad = filter.findSmiley(":(", 0);
        assertNotNull(sad);
        assertEquals(":(", sad.getCode());
        assertNotNull(sad.getIcon());

        final Smiley tongue = filter.findSmiley(":p", 0);
        assertNotNull(tongue);
        assertEquals(":p", tongue.getCode());
        assertNotNull(tongue.getIcon());

        final Smiley teeth = filter.findSmiley(":D", 0);
        assertNotNull(teeth);
        assertEquals(":D", teeth.getCode());
        assertNotNull(teeth.getIcon());

        final Smiley wink = filter.findSmiley(";)", 0);
        assertNotNull(wink);
        assertEquals(";)", wink.getCode());
        assertNotNull(wink.getIcon());

        final Smiley omg = filter.findSmiley(":O", 0);
        assertNotNull(omg);
        assertEquals(":O", omg.getCode());
        assertNotNull(omg.getIcon());

        final Smiley angry = filter.findSmiley(":@", 0);
        assertNotNull(angry);
        assertEquals(":@", angry.getCode());
        assertNotNull(angry.getIcon());

        final Smiley confused = filter.findSmiley(":S", 0);
        assertNotNull(confused);
        assertEquals(":S", confused.getCode());
        assertNotNull(confused.getIcon());

        final Smiley cry = filter.findSmiley(";(", 0);
        assertNotNull(cry);
        assertEquals(";(", cry.getCode());
        assertNotNull(cry.getIcon());

        final Smiley embarrassed = filter.findSmiley(":$", 0);
        assertNotNull(embarrassed);
        assertEquals(":$", embarrassed.getCode());
        assertNotNull(embarrassed.getIcon());

        final Smiley shade = filter.findSmiley("8)", 0);
        assertNotNull(shade);
        assertEquals("8)", shade.getCode());
        assertNotNull(shade.getIcon());
    }

    /**
     * Test that nothing is returned for unregistered smileys.
     */
    @Test
    public void testUnknownSmileys() {
        assertNull(filter.findSmiley(":/", 0));
        assertNull(filter.findSmiley("#)", 0));
        assertNull(filter.findSmiley(":", 0));
        assertNull(filter.findSmiley(")", 0));
    }
}
