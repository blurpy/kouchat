
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

package net.usikkert.kouchat.net;

import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.tcp.TCPReceiverListener;
import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Proxy that listens for messages from both multicast and tcp and forwards from only one source at
 * a time (per user) to avoid duplicates.
 *
 * @author Christian Ihle
 */
public class MessageDeduplicator implements ReceiverListener, TCPReceiverListener {

    private static final Logger LOG = Logger.getLogger(MessageDeduplicator.class);

    private final Controller controller;

    @Nullable
    private ReceiverListener listener;

    public MessageDeduplicator(final Controller controller) {
        Validate.notNull(controller, "Controller can not be null");
        this.controller = controller;
    }

    public void registerReceiverListener(final ReceiverListener theListener) {
        this.listener = theListener;
    }

    @Override
    public void messageArrived(final String message, final String ipAddress) {
        if (listener == null) {
            return;
        }

        final User user = parseUserFromMessage(message);

        if (user == null || !user.isTcpEnabled()) {
            if (user == null || !user.isMe()) {
                LOG.fine("Multicast message: " + message);
            }

            listener.messageArrived(message, ipAddress);
        }
    }

    @Override
    public void messageArrived(final String message, final String ipAddress, final User user) {
        if (listener == null) {
            return;
        }

        if (user.isTcpEnabled()) {
            LOG.fine("TCP message: " + message);
            listener.messageArrived(message, ipAddress);
        }
    }

    @Nullable
    private User parseUserFromMessage(final String message) {
        try {
            final int msgCode = Integer.parseInt(message.substring(0, message.indexOf("!")));
            return controller.getUser(msgCode);
        }

        catch (final NumberFormatException | StringIndexOutOfBoundsException e) {
            LOG.warning("Failed to parse user from message: %s", e.getMessage());
            return null;
        }
    }
}
