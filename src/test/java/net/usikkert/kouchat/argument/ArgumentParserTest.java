
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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
        assertOneArgument(new ArgumentParser(new String[] {"-h"}), "-h", Argument.HELP);
        assertOneArgument(new ArgumentParser(new String[] {"--help"}), "--help", Argument.HELP);
    }

    @Test
    public void shouldParseVersionArguments() {
        assertOneArgument(new ArgumentParser(new String[] {"-v"}), "-v", Argument.VERSION);
        assertOneArgument(new ArgumentParser(new String[] {"--version"}), "--version", Argument.VERSION);
    }

    @Test
    public void shouldParseDebugArguments() {
        assertOneArgument(new ArgumentParser(new String[] {"-d"}), "-d", Argument.DEBUG);
        assertOneArgument(new ArgumentParser(new String[] {"--debug"}), "--debug", Argument.DEBUG);
    }

    @Test
    public void shouldParseConsoleArguments() {
        assertOneArgument(new ArgumentParser(new String[] {"-c"}), "-c", Argument.CONSOLE);
        assertOneArgument(new ArgumentParser(new String[] {"--console"}), "--console", Argument.CONSOLE);
    }

    @Test
    public void shouldParseNoPrivateChatArgument() {
        assertOneArgument(new ArgumentParser(new String[] {"--no-private-chat"}), "--no-private-chat", Argument.NO_PRIVATE_CHAT);
    }

    @Test
    public void shouldParseAlwaysLogArgument() {
        assertOneArgument(new ArgumentParser(new String[] {"--always-log"}), "--always-log", Argument.ALWAYS_LOG);
    }

    @Test
    public void shouldParseLogLocationArgumentWithoutValue() {
        assertOneArgument(new ArgumentParser(new String[] {"--log-location"}), "--log-location", Argument.LOG_LOCATION);
    }

    @Test
    public void shouldParseLogLocationArgumentWithEmptyValue() {
        assertOneArgument(new ArgumentParser(new String[] {"--log-location="}), "--log-location=", Argument.LOG_LOCATION);
    }

    @Test
    public void shouldParseLogLocationArgumentWithValue() {
        final String argument = "--log-location=/var/log/";
        assertOneArgument(new ArgumentParser(new String[] {argument}), argument, Argument.LOG_LOCATION, "/var/log/");
    }

    @Test
    public void shouldParseLogLocationArgumentWithValueHavingSpace() {
        final String argument = "--log-location=C:\\Documents and settings\\log\\";
        assertOneArgument(new ArgumentParser(new String[] {argument}), argument, Argument.LOG_LOCATION, "C:\\Documents and settings\\log\\");
    }

    @Test
    public void shouldHandleUnknownArguments() {
        assertOneArgument(new ArgumentParser(new String[] {"-z"}), "-z", Argument.UNKNOWN);
        assertOneArgument(new ArgumentParser(new String[] {"--zzzz"}), "--zzzz", Argument.UNKNOWN);
        assertOneArgument(new ArgumentParser(new String[] {"-c=value"}), "-c=value", Argument.UNKNOWN, "value");
        assertOneArgument(new ArgumentParser(new String[] {"--debug=value"}), "--debug=value", Argument.UNKNOWN, "value");
    }

    @Test
    public void shouldParseSeveralShortArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[] {"-h", "-d", "-c", "-v"});

        assertEquals(4, parser.getNumberOfArguments());

        assertContainsArgument(parser, "-h", Argument.HELP);
        assertContainsArgument(parser, "-d", Argument.DEBUG);
        assertContainsArgument(parser, "-c", Argument.CONSOLE);
        assertContainsArgument(parser, "-v", Argument.VERSION);
    }

    @Test
    public void shouldParseSeveralFullArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[] {"--help", "--debug", "--console", "--version"});

        assertEquals(4, parser.getNumberOfArguments());

        assertContainsArgument(parser, "--help", Argument.HELP);
        assertContainsArgument(parser, "--debug", Argument.DEBUG);
        assertContainsArgument(parser, "--console", Argument.CONSOLE);
        assertContainsArgument(parser, "--version", Argument.VERSION);
    }

    @Test
    public void shouldParseMixOfShortAndFullArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[] {"--help", "-d", "--console", "-v"});

        assertEquals(4, parser.getNumberOfArguments());

        assertContainsArgument(parser, "--help", Argument.HELP);
        assertContainsArgument(parser, "-d", Argument.DEBUG);
        assertContainsArgument(parser, "--console", Argument.CONSOLE);
        assertContainsArgument(parser, "-v", Argument.VERSION);
    }

    @Test
    public void shouldParseMixOfArgumentsWithAndWithoutValue() {
        final ArgumentParser parser = new ArgumentParser(new String[] {"--help", "--log-location=a b c", "--version"});

        assertEquals(3, parser.getNumberOfArguments());

        assertContainsArgument(parser, "--help", Argument.HELP);
        assertContainsArgument(parser, "--log-location=a b c", Argument.LOG_LOCATION, "a b c");
        assertContainsArgument(parser, "--version", Argument.VERSION);
    }

    @Test
    public void getUnknownArgumentsShouldReturnAllUnknownArguments() {
        final ArgumentParser parser = new ArgumentParser(new String[] {"--help", "-x", "--yyyy", "-z"});

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
        final ArgumentParser parser = new ArgumentParser(new String[] {"--help"});

        assertEquals(1, parser.getNumberOfArguments());
        assertEquals(0, parser.getNumberOfUnknownArguments());

        assertContainsArgument(parser, "--help", Argument.HELP);

        final List<ParsedArgument> unknownArguments = parser.getUnknownArguments();
        assertNotNull(unknownArguments);
        assertEquals(0, unknownArguments.size());
    }

    private void assertContainsArgument(final ArgumentParser parser, final String stringArgument,
                                        final Argument enumArgument) {
        assertContainsArgument(parser, stringArgument, enumArgument, null);
    }

    private void assertContainsArgument(final ArgumentParser parser, final String stringArgument,
                                        final Argument enumArgument, final String value) {
        final ParsedArgument detectedArgument = parser.getArgument(enumArgument);

        if (detectedArgument == null) {
            fail("Did not find parsed argument for: " + enumArgument);
        }

        assertArgument(parser, stringArgument, enumArgument, detectedArgument, value);
    }

    private void assertOneArgument(final ArgumentParser parser, final String stringArgument,
                                   final Argument enumArgument) {
        assertOneArgument(parser, stringArgument, enumArgument, null);
    }

    private void assertOneArgument(final ArgumentParser parser, final String stringArgument,
                                   final Argument enumArgument, final String value) {
        assertEquals(1, parser.getNumberOfArguments());
        final List<ParsedArgument> arguments = parser.getArguments();
        assertEquals(1, arguments.size());

        final ParsedArgument argument = arguments.get(0);
        assertArgument(parser, stringArgument, enumArgument, argument, value);
    }

    private void assertArgument(final ArgumentParser parser, final String stringArgument, final Argument enumArgument,
                                final ParsedArgument argument, final String value) {
        assertNotNull(argument);
        assertEquals(stringArgument, argument.getOriginalArgument());
        assertEquals(enumArgument, argument.getArgument());
        assertTrue(parser.hasArgument(enumArgument));

        final ParsedArgument parsedArgument = parser.getArgument(enumArgument);
        assertNotNull(parsedArgument);
        assertEquals(stringArgument, parsedArgument.getOriginalArgument());
        assertEquals(enumArgument, parsedArgument.getArgument());
        assertEquals(value, parsedArgument.getValue());
    }
}
