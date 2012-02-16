
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
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

import org.junit.Test;

/**
 * Test of {@link Argument}.
 *
 * @author Christian Ihle
 */
public class ArgumentTest {

    @Test
    public void getValidArgumentsShouldReturnAllArgumentsExceptUnknown() {
        final Argument[] validArguments = Argument.getValidArguments();

        assertEquals(4, validArguments.length);

        assertEquals(Argument.CONSOLE, validArguments[0]);
        assertEquals(Argument.DEBUG, validArguments[1]);
        assertEquals(Argument.HELP, validArguments[2]);
        assertEquals(Argument.VERSION, validArguments[3]);
    }

    @Test
    public void getArgumentsAsStringShouldIncludeDetailsFromAllValidArguments() {
        final String argumentsAsString = Argument.getArgumentsAsString();

        final String expected =
                " -c, --console \t starts KouChat in console mode\n" +
                " -d, --debug \t starts KouChat with verbose debug output enabled\n" +
                " -h, --help \t shows this help message\n" +
                " -v, --version \t shows version information";

        assertEquals(expected, argumentsAsString);
    }

    @Test
    public void isEqualToShouldHandleBothShortAndFullArgumentName() {
        assertTrue(Argument.CONSOLE.isEqualTo("-c"));
        assertTrue(Argument.CONSOLE.isEqualTo("--console"));

        assertFalse(Argument.CONSOLE.isEqualTo("-h"));
        assertFalse(Argument.CONSOLE.isEqualTo("--help"));
    }
}
