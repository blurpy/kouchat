
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

package net.usikkert.kouchat.argument;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PrintStream;

import net.usikkert.kouchat.Constants;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link ArgumentResponder}.
 *
 * @author Christian Ihle
 */
public class ArgumentResponderTest {

    private PrintStream printStream;

    @Before
    public void setUp() {
        printStream = mock(PrintStream.class);
    }

    @Test
    public void respondWithNoArgumentsShouldReturnTrueAndPrintDetailsAboutHelp() {
        final ArgumentResponder handler = createHandlerWithArguments();

        assertTrue(handler.respond());

        verify(printStream).println("KouChat v" + Constants.APP_VERSION);
        verify(printStream).println("By Christian Ihle - contact@kouchat.net - http://www.kouchat.net/");
        verify(printStream).println("Use --help for more information");
        verifyNoMoreInteractions(printStream);
    }

    @Test
    public void respondWithUninterestingArgumentsShouldReturnTrueAndDoNothingElse() {
        final ArgumentResponder handler = createHandlerWithArguments(
                "--debug", "--console", "--no-private-chat", "--always-log", "--log-location=/");

        assertTrue(handler.respond());

        verify(printStream).println("KouChat v" + Constants.APP_VERSION);
        verify(printStream).println("By Christian Ihle - contact@kouchat.net - http://www.kouchat.net/");
        verifyNoMoreInteractions(printStream);
    }

    @Test
    public void respondWithVersionArgumentShouldReturnFalseAndDoNothingElse() {
        final ArgumentResponder handler = createHandlerWithArguments("--version");

        assertFalse(handler.respond());

        verify(printStream).println("KouChat v" + Constants.APP_VERSION);
        verify(printStream).println("By Christian Ihle - contact@kouchat.net - http://www.kouchat.net/");
        verifyNoMoreInteractions(printStream);
    }

    @Test
    public void respondWithHelpArgumentShouldReturnFalseAndPrintDetailsAboutArguments() {
        final ArgumentResponder handler = createHandlerWithArguments("--help");

        assertFalse(handler.respond());

        verify(printStream).println("KouChat v" + Constants.APP_VERSION);
        verify(printStream).println("By Christian Ihle - contact@kouchat.net - http://www.kouchat.net/");
        verify(printStream).println("\nArguments:");
        verify(printStream).println(Argument.getArgumentsAsString());
        verifyNoMoreInteractions(printStream);
    }

    @Test
    public void respondWithBothHelpAndVersionShouldReturnFalseAndPrintDetailsAboutArguments() {
        final ArgumentResponder handler = createHandlerWithArguments("--help", "--version");

        assertFalse(handler.respond());

        verify(printStream).println("KouChat v" + Constants.APP_VERSION);
        verify(printStream).println("By Christian Ihle - contact@kouchat.net - http://www.kouchat.net/");
        verify(printStream).println("\nArguments:");
        verify(printStream).println(Argument.getArgumentsAsString());
        verifyNoMoreInteractions(printStream);
    }

    @Test
    public void respondWithUnknownArgumentsShouldReturnFalseAndPrintDetailsAboutUnknownArguments() {
        final ArgumentResponder handler = createHandlerWithArguments("--something", "-w");

        assertFalse(handler.respond());

        verify(printStream).println("KouChat v" + Constants.APP_VERSION);
        verify(printStream).println("By Christian Ihle - contact@kouchat.net - http://www.kouchat.net/");
        verify(printStream).println("\nUnknown arguments: [--something, -w]. Use --help for more information");
        verifyNoMoreInteractions(printStream);
    }

    @Test
    public void respondWithUnknownArgumentAndValidArgumentShouldReturnFalseAndPrintDetailsAboutUnknownArguments() {
        final ArgumentResponder handler = createHandlerWithArguments("--help", "--wrong", "--always-log");

        assertFalse(handler.respond());

        verify(printStream).println("KouChat v" + Constants.APP_VERSION);
        verify(printStream).println("By Christian Ihle - contact@kouchat.net - http://www.kouchat.net/");
        verify(printStream).println("\nUnknown arguments: [--wrong]. Use --help for more information");
        verifyNoMoreInteractions(printStream);
    }

    private ArgumentResponder createHandlerWithArguments(final String... arguments) {
        return new ArgumentResponder(new ArgumentParser(arguments), printStream);
    }
}
