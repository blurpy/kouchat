
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

package net.usikkert.kouchat.settings;

import static net.usikkert.kouchat.settings.PropertyFileSettings.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.PropertyTools;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * Loads settings stored in <code>~/.kouchat/kouchat.ini</code>.
 *
 * @author Christian Ihle
 */
public class PropertyFileSettingsLoader {

    private static final Logger LOG = Logger.getLogger(Settings.class.getName());

    private static final String FILENAME = Constants.APP_FOLDER + "kouchat.ini";

    private final PropertyTools propertyTools = new PropertyTools();

    /**
     * Loads the settings from file.
     * If some values are not found in the settings, the default is used instead.
     *
     * @param settings The settings to load into.
     */
    public void loadSettings(final Settings settings) {
        Validate.notNull(settings, "Settings can not be null");

        try {
            final Properties fileContents = propertyTools.loadProperties(FILENAME);

            final String tmpNick = fileContents.getProperty(NICK_NAME.getKey());

            if (tmpNick != null && Tools.isValidNick(tmpNick)) {
                final User me = settings.getMe();
                me.setNick(tmpNick.trim());
            }

            try {
                settings.setOwnColor(Integer.parseInt(fileContents.getProperty(OWN_COLOR.getKey())));
            }

            catch (final NumberFormatException e) {
                LOG.log(Level.WARNING, "Could not read setting for owncolor..");
            }

            try {
                settings.setSysColor(Integer.parseInt(fileContents.getProperty(SYS_COLOR.getKey())));
            }

            catch (final NumberFormatException e) {
                LOG.log(Level.WARNING, "Could not read setting for syscolor..");
            }

            settings.setLogging(Boolean.valueOf(fileContents.getProperty(LOGGING.getKey())));
            settings.setBalloons(Boolean.valueOf(fileContents.getProperty(BALLOONS.getKey())));
            settings.setBrowser(Tools.emptyIfNull(fileContents.getProperty(BROWSER.getKey())));
            settings.setLookAndFeel(Tools.emptyIfNull(fileContents.getProperty(LOOK_AND_FEEL.getKey())));
            settings.setNetworkInterface(fileContents.getProperty(NETWORK_INTERFACE.getKey()));

            // Defaults to true
            if (fileContents.getProperty(SOUND.getKey()) != null) {
                settings.setSound(Boolean.valueOf(fileContents.getProperty(SOUND.getKey())));
            }

            // Defaults to true
            if (fileContents.getProperty(SMILEYS.getKey()) != null) {
                settings.setSmileys(Boolean.valueOf(fileContents.getProperty(SMILEYS.getKey())));
            }
        }

        catch (final FileNotFoundException e) {
            LOG.log(Level.WARNING, "Could not find " + FILENAME + ", using default settings.");
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }
    }
}
