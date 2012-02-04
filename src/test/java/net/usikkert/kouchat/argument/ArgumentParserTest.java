
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

package net.usikkert.kouchat.argument;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * Test of {@link ArgumentParser}.
 *
 * @author Christian Ihle
 */
public class ArgumentParserTest {

    @Test
    public void shouldHandleNullArgument() {
        final ArgumentParser parser = new ArgumentParser(null);

        assertEquals(0, parser.getNumberOfArguments());
        assertTrue(parser.getArguments().isEmpty());
    }

    @Test
    public void shouldHandleNoArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[] {});

        assertEquals(0, parser.getNumberOfArguments());
        assertTrue(parser.getArguments().isEmpty());

        for (final Argument argument : Argument.values()) {
            assertFalse(parser.hasArgument(argument));
        }
    }

    @Test
    public void shouldParseHelpArguments() {
        assertCorrectArgument(new ArgumentParser(new String[] {"-h"}), "-h", Argument.HELP);
        assertCorrectArgument(new ArgumentParser(new String[] {"--help"}), "--help", Argument.HELP);
    }

    @Test
    public void shouldParseVersionArguments() {
        assertCorrectArgument(new ArgumentParser(new String[] {"-v"}), "-v", Argument.VERSION);
        assertCorrectArgument(new ArgumentParser(new String[] {"--version"}), "--version", Argument.VERSION);
    }

    @Test
    public void shouldParseDebugArguments() {
        assertCorrectArgument(new ArgumentParser(new String[] {"-d"}), "-d", Argument.DEBUG);
        assertCorrectArgument(new ArgumentParser(new String[] {"--debug"}), "--debug", Argument.DEBUG);
    }

    @Test
    public void shouldParseConsoleArguments() {
        assertCorrectArgument(new ArgumentParser(new String[] {"-c"}), "-c", Argument.CONSOLE);
        assertCorrectArgument(new ArgumentParser(new String[] {"--console"}), "--console", Argument.CONSOLE);
    }

    @Test
    public void shouldHandleUnknownArguments() {
        assertCorrectArgument(new ArgumentParser(new String[] {"-z"}), "-z", Argument.UNKNOWN);
        assertCorrectArgument(new ArgumentParser(new String[] {"--zzzz"}), "--zzzz", Argument.UNKNOWN);
    }

    private void assertCorrectArgument(final ArgumentParser parser, final String stringArgument, final Argument enumArgument) {
        assertEquals(1, parser.getNumberOfArguments());
        final List<ParsedArgument> arguments = parser.getArguments();
        assertEquals(1, arguments.size());

        final ParsedArgument argument = arguments.get(0);
        assertNotNull(argument);
        assertEquals(stringArgument, argument.getOriginalArgument());
        assertEquals(enumArgument, argument.getArgument());
        assertTrue(parser.hasArgument(enumArgument));
    }
}
