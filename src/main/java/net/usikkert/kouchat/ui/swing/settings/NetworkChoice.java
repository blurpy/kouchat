
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

package net.usikkert.kouchat.ui.swing.settings;

import java.net.NetworkInterface;

import net.usikkert.kouchat.net.NetworkUtils;

/**
 * Class for representing a network interface to be chosen in the dropdown box of the settings.
 *
 * @author Christian Ihle
 */
public class NetworkChoice {

    private final String displayName;
    private final String deviceName;
    private final String ipAddresses;

    public NetworkChoice(final NetworkInterface networkInterface) {
        this.displayName = networkInterface.getDisplayName();
        this.deviceName = networkInterface.getName();
        this.ipAddresses = NetworkUtils.getIPv4Addresses(networkInterface);
    }

    public NetworkChoice(final String deviceName, final String displayName) {
        this.displayName = displayName;
        this.deviceName = deviceName;
        this.ipAddresses = null;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean match(final String savedNetworkInterface) {
        return deviceName.equalsIgnoreCase(savedNetworkInterface);
    }

    @Override
    public String toString() {
        if (ipAddresses != null) {
            return deviceName + " - " + ipAddresses;
        } else {
            return deviceName;
        }
    }
}
