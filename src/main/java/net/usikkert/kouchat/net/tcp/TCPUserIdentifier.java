
/***************************************************************************
 *   Copyright 2006-2018 by Christian Ihle                                 *
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

package net.usikkert.kouchat.net.tcp;

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Sleeper;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Message listener for a client that will identify the user on the other side
 * if that user sends its user code as the first message.
 *
 * @author Christian Ihle
 */
public class TCPUserIdentifier implements TCPClientListener {

    private static final Logger LOG = Logger.getLogger(TCPUserIdentifier.class);

    private final Controller controller;
    private final TCPClient client;
    private final Sleeper sleeper;

    @Nullable
    private String message;

    public TCPUserIdentifier(final Controller controller, final TCPClient client) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(client, "Client can not be null");

        this.controller = controller;
        this.client = client;
        this.sleeper = new Sleeper();

        client.registerClientListener(this);
    }

    @Override
    public void messageArrived(final String theMessage, final TCPClient theClient) {
        client.registerClientListener(null);
        LOG.fine("Received message: %s", theMessage);

        this.message = theMessage;
    }

    @Override
    public void disconnected(final TCPClient theClient) {

    }

    @Nullable
    public User waitForUser() {
        waitForMessage();

        final User user = userFromMessage();

        if (user != null && !user.getIpAddress().equals(client.getIPAddress())) {
            LOG.warning("Unexpected client ip connected. user=%s, userIP=%s, clientIP=%s",
                        user.getNick(), user.getIpAddress(), client.getIPAddress());
            return null;
        }

        return user;
    }

    private void waitForMessage() {
        int tries = 0;

        while (message == null && tries < 50) {
            sleeper.sleep(50);
            tries++;
        }
    }

    @Nullable
    private User userFromMessage() {
        if (message == null) {
            return null;
        }

        try {
            final int userCode = Integer.valueOf(message);
            return controller.getUser(userCode);
        }

        catch (final NumberFormatException e) {
            LOG.severe("Failed to identify user from message. %s", e.toString());
        }

        return null;
    }
}
