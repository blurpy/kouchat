
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
        assertOneArgument(new ArgumentParser(new String[]{"-h"}), "-h", Argument.HELP);
        assertOneArgument(new ArgumentParser(new String[]{"--help"}), "--help", Argument.HELP);
    }

    @Test
    public void shouldParseVersionArguments() {
        assertOneArgument(new ArgumentParser(new String[]{"-v"}), "-v", Argument.VERSION);
        assertOneArgument(new ArgumentParser(new String[]{"--version"}), "--version", Argument.VERSION);
    }

    @Test
    public void shouldParseDebugArguments() {
        assertOneArgument(new ArgumentParser(new String[]{"-d"}), "-d", Argument.DEBUG);
        assertOneArgument(new ArgumentParser(new String[]{"--debug"}), "--debug", Argument.DEBUG);
    }

    @Test
    public void shouldParseConsoleArguments() {
        assertOneArgument(new ArgumentParser(new String[]{"-c"}), "-c", Argument.CONSOLE);
        assertOneArgument(new ArgumentParser(new String[]{"--console"}), "--console", Argument.CONSOLE);
    }

    @Test
    public void shouldHandleUnknownArguments() {
        assertOneArgument(new ArgumentParser(new String[]{"-z"}), "-z", Argument.UNKNOWN);
        assertOneArgument(new ArgumentParser(new String[]{"--zzzz"}), "--zzzz", Argument.UNKNOWN);
    }

    @Test
    public void shouldParseSeveralShortArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[]{"-h", "-d", "-c", "-v"});

        assertEquals(4, parser.getNumberOfArguments());

        assertContainsArgument(parser, "-h", Argument.HELP);
        assertContainsArgument(parser, "-d", Argument.DEBUG);
        assertContainsArgument(parser, "-c", Argument.CONSOLE);
        assertContainsArgument(parser, "-v", Argument.VERSION);
    }

    @Test
    public void shouldParseSeveralFullArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[]{"--help", "--debug", "--console", "--version"});

        assertEquals(4, parser.getNumberOfArguments());

        assertContainsArgument(parser, "--help", Argument.HELP);
        assertContainsArgument(parser, "--debug", Argument.DEBUG);
        assertContainsArgument(parser, "--console", Argument.CONSOLE);
        assertContainsArgument(parser, "--version", Argument.VERSION);
    }

    @Test
    public void shouldParseMixOfShortAndFullArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[]{"--help", "-d", "--console", "-v"});

        assertEquals(4, parser.getNumberOfArguments());

        assertContainsArgument(parser, "--help", Argument.HELP);
        assertContainsArgument(parser, "-d", Argument.DEBUG);
        assertContainsArgument(parser, "--console", Argument.CONSOLE);
        assertContainsArgument(parser, "-v", Argument.VERSION);
    }

    @Test
    public void getUnknownArgumentsShouldReturnAllUnknownArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[]{"--help", "-x", "--yyyy", "-z"});

        assertEquals(4, parser.getNumberOfArguments());
        assertEquals(3, parser.getNumberOfUnknownArguments());

        assertContainsArgument(parser, "--help", Argument.HELP);

        final List<ParsedArgument> unknownArguments = parser.getUnknownArguments();
        assertNotNull(unknownArguments);
        assertEquals(3, unknownArguments.size());

        assertEquals("-x", unknownArguments.get(0).getOriginalArgument());
        assertEquals(Argument.UNKNOWN, unknownArguments.get(0).getArgument());

        assertEquals("--yyyy", unknownArguments.get(1).getOriginalArgument());
        assertEquals(Argument.UNKNOWN, unknownArguments.get(1).getArgument());

        assertEquals("-z", unknownArguments.get(2).getOriginalArgument());
        assertEquals(Argument.UNKNOWN, unknownArguments.get(2).getArgument());
    }

    @Test
    public void getUnknownArgumentsShouldEmptyListWhenNoUnknownArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[]{"--help"});

        assertEquals(1, parser.getNumberOfArguments());
        assertEquals(0, parser.getNumberOfUnknownArguments());

        assertContainsArgument(parser, "--help", Argument.HELP);

        final List<ParsedArgument> unknownArguments = parser.getUnknownArguments();
        assertNotNull(unknownArguments);
        assertEquals(0, unknownArguments.size());
    }

    private void assertContainsArgument(final ArgumentParser parser, final String stringArgument,
                                        final Argument enumArgument) {
        final ParsedArgument detectedArgument = parser.getArgument(enumArgument);

        if (detectedArgument == null) {
            fail("Did not find parsed argument for: " + enumArgument);
        }

        assertArgument(parser, stringArgument, enumArgument, detectedArgument);
    }

    private void assertOneArgument(final ArgumentParser parser, final String stringArgument,
                                   final Argument enumArgument) {
        assertEquals(1, parser.getNumberOfArguments());
        final List<ParsedArgument> arguments = parser.getArguments();
        assertEquals(1, arguments.size());

        final ParsedArgument argument = arguments.get(0);
        assertArgument(parser, stringArgument, enumArgument, argument);
    }

    private void assertArgument(final ArgumentParser parser, final String stringArgument, final Argument enumArgument,
                                final ParsedArgument argument) {
        assertNotNull(argument);
        assertEquals(stringArgument, argument.getOriginalArgument());
        assertEquals(enumArgument, argument.getArgument());
        assertTrue(parser.hasArgument(enumArgument));

        final ParsedArgument parsedArgument = parser.getArgument(enumArgument);
        assertNotNull(parsedArgument);
        assertEquals(stringArgument, parsedArgument.getOriginalArgument());
        assertEquals(enumArgument, parsedArgument.getArgument());
    }
}
