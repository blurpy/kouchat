
/***************************************************************************
 *   Copyright 2006-2016 by Christian Ihle                                 *
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.net.NetworkInterfaceInfo;
import net.usikkert.kouchat.net.NetworkUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link NetworkChoice}.
 *
 * @author Christian Ihle
 */
public class NetworkChoiceTest {

    private NetworkChoice networkChoiceWithStrings;
    private NetworkChoice networkChoiceWithNetworkInterface;

    @Before
    public void setUp() {
        networkChoiceWithStrings = new NetworkChoice("eth4", "Realtek RTL8111");

        final NetworkInterfaceInfo networkInterfaceInfo = mock(NetworkInterfaceInfo.class);
        when(networkInterfaceInfo.getName()).thenReturn("wlan0");
        when(networkInterfaceInfo.getDisplayName()).thenReturn("Intel Pro Wireless 2100");

        final NetworkUtils networkUtils = mock(NetworkUtils.class);
        when(networkUtils.getIPv4Addresses(networkInterfaceInfo)).thenReturn("192.168.1.1");

        networkChoiceWithNetworkInterface = new NetworkChoice(networkInterfaceInfo, networkUtils);
    }

    @Test
    public void constructorWithStringsShouldSetFields() {
        assertEquals("eth4", networkChoiceWithStrings.getDeviceName());
        assertEquals("Realtek RTL8111", networkChoiceWithStrings.getDisplayName());
    }

    @Test
    public void constructorWithNetworkInterfaceShouldSetFields() {
        assertEquals("wlan0", networkChoiceWithNetworkInterface.getDeviceName());
        assertEquals("Intel Pro Wireless 2100", networkChoiceWithNetworkInterface.getDisplayName());
    }

    @Test
    public void toStringWithStringsShouldReturnDeviceName() {
        assertEquals("eth4", networkChoiceWithStrings.toString());
    }

    @Test
    public void toStringWithNetworkInterfaceShouldReturnDeviceNameAndIpAddress() {
        assertEquals("wlan0 - 192.168.1.1", networkChoiceWithNetworkInterface.toString());
    }

    @Test
    public void matchShouldBeTrueWhenSameDeviceName() {
        assertTrue(networkChoiceWithStrings.match("eth4"));
        assertTrue(networkChoiceWithNetworkInterface.match("wlan0"));
    }

    @Test
    public void matchShouldNotCareAboutCasing() {
        assertTrue(networkChoiceWithStrings.match("ETH4"));
        assertTrue(networkChoiceWithNetworkInterface.match("WLAN0"));
    }

    @Test
    public void matchShouldBeFalseWhenWrongDeviceName() {
        assertFalse(networkChoiceWithStrings.match("eth5"));
        assertFalse(networkChoiceWithNetworkInterface.match("wlan1"));
    }
}
