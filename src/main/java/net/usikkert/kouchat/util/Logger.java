
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

import java.util.logging.Level;

import org.jetbrains.annotations.NonNls;

/**
 * A wrapper around {@link java.util.logging.Logger} to provide more convenient methods for logging,
 * including message parameters.
 *
 * @author Christian Ihle
 */
public final class Logger {

    private final java.util.logging.Logger logger;

    public static Logger getLogger(final Class<?> clazz) {
        return new Logger(java.util.logging.Logger.getLogger(clazz.getName()));
    }

    private Logger(final java.util.logging.Logger logger) {
        this.logger = logger;
    }

    public void severe(@NonNls final String message,
                       @NonNls final Object... messageParameters) {
        log(Level.SEVERE, message, messageParameters);
    }

    public void severe(final Throwable throwable,
                       @NonNls final String message,
                       @NonNls final Object... messageParameters) {
        log(Level.SEVERE, message, messageParameters, throwable);
    }

    private void log(final Level level, final String message, final Object[] messageParameters,
                     final Throwable throwable) {
        if (logger.isLoggable(level)) {
            final String formattedMessage = String.format(message, messageParameters);
            logger.log(level, formattedMessage, throwable);
        }
    }

    private void log(final Level level, final String message, final Object[] messageParameters) {
        if (logger.isLoggable(level)) {
            final String formattedMessage = String.format(message, messageParameters);
            logger.log(level, formattedMessage);
        }
    }
}
