
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

package net.usikkert.kouchat.util;

import static org.mockito.Mockito.*;

import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link Logger}.
 *
 * @author Christian Ihle
 */
public class LoggerTest {

    private Logger logger;

    private java.util.logging.Logger julLogger;

    @Before
    public void setUp() {
        logger = Logger.getLogger(LoggerTest.class);

        julLogger = TestUtils.setFieldValueWithMock(logger, "logger", java.util.logging.Logger.class);
    }

    @Test
    public void severeShouldLogMessageWithLevelSevere() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);

        logger.severe("message");

        verify(julLogger).log(Level.SEVERE, "message");
    }

    @Test
    public void severeShouldReplaceParametersInMessage() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);

        logger.severe("message with %s cookies and %s", 2, "milk");

        verify(julLogger).log(Level.SEVERE, "message with 2 cookies and milk");
    }

    @Test
    public void severeShouldNotLogIfLogLevelDisabled() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(false);

        logger.severe("message");

        verify(julLogger, never()).log(any(Level.class), anyString());
    }

    @Test
    public void severeWithExceptionShouldLogMessageAndExceptionWithLevelSevere() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);
        final RuntimeException exception = new RuntimeException();

        logger.severe(exception, "message");

        verify(julLogger).log(Level.SEVERE, "message", exception);
    }

    @Test
    public void severeWithExceptionShouldReplaceParametersInMessage() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);
        final RuntimeException exception = new RuntimeException();

        logger.severe(exception, "message with %s cookies and %s", 2, "milk");

        verify(julLogger).log(Level.SEVERE, "message with 2 cookies and milk", exception);
    }

    @Test
    public void severeWithExceptionShouldNotLogIfLogLevelDisabled() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(false);

        logger.severe(new RuntimeException(), "message");

        verify(julLogger, never()).log(any(Level.class), anyString(), any(Throwable.class));
    }
}
