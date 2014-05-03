
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

import net.usikkert.kouchat.misc.Settings;

/**
 * Loads settings based on arguments.
 *
 * @author Christian Ihle
 */
public class ArgumentSettingsLoader {

    /**
     * Loads settings from the parsed arguments.
     *
     * <p>Supports the following arguments:</p>
     * <ul>
     *   <li>--always-log ({@link Settings#isAlwaysLog()}</li>
     *   <li>--no-private-chat ({@link Settings#isNoPrivateChat()}</li>
     *   <li>--log-location ({@link Settings#getLogLocation()}</li>
     * </ul>
     *
     * @param argumentParser The parsed arguments.
     * @param settings The settings to put the parsed arguments.
     */
    public void loadSettingsFromArguments(final ArgumentParser argumentParser, final Settings settings) {
        settings.setAlwaysLog(argumentParser.hasArgument(Argument.ALWAYS_LOG));
        settings.setNoPrivateChat(argumentParser.hasArgument(Argument.NO_PRIVATE_CHAT));

        if (argumentParser.hasArgument(Argument.LOG_LOCATION)) {
            settings.setLogLocation(argumentParser.getArgument(Argument.LOG_LOCATION).getValue());
        }
    }
}
