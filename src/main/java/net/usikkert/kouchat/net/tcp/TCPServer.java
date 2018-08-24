
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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Server listening for tcp connections from users.
 *
 * @author Christian Ihle
 */
public class TCPServer implements Runnable {

    private static final Logger LOG = Logger.getLogger(TCPServer.class.getName());

    private static final int MAX_PORT_ATTEMPTS = 50;

    private final ErrorHandler errorHandler;
    private final User me;

    private boolean connected;

    @Nullable
    private ServerSocket serverSocket;

    public TCPServer(final Settings settings, final ErrorHandler errorHandler) {
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.me = settings.getMe();
        this.errorHandler = errorHandler;
    }

    @Override
    public void run() {
        while (connected && serverSocket != null) {
            try {
                // TODO use socket
                final Socket socket = serverSocket.accept();
                socket.close();
            }

            // Happens when server socket is closed, or network is down
            catch (final IOException e) {
                connected = false;
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    public void startServer() {
        LOG.log(Level.FINE, "Connecting...");

        if (connected) {
            LOG.log(Level.FINE, "Already connected.");
            return;
        }

        int port = Constants.NETWORK_TCP_CHAT_PORT;
        int portAttempt = 0;

        while (portAttempt < MAX_PORT_ATTEMPTS && !connected) {
            try {
                serverSocket = new ServerSocket(port);
                connected = true;

                // The background thread watching for connections from the network.
                final Thread worker = new Thread(this, getClass().getSimpleName());
                worker.start();

                me.setTcpChatPort(port);
                LOG.log(Level.FINE, "Connected to port " + port);
            }

            catch (final IOException e) {
                LOG.log(Level.SEVERE, e.toString() + " " + port);

                portAttempt++;
                port++;
                me.setTcpChatPort(0);
            }
        }

        if (!connected) {
            final String error = "Failed to initialize tcp network:" +
                    "\nNo available listening port between " + Constants.NETWORK_TCP_CHAT_PORT +
                    " and " + (port - 1) + "." +
                    "\n\nYou will not be able to receive tcp messages!";

            LOG.log(Level.SEVERE, error);
            errorHandler.showError(error);
        }
    }

    public void stopServer() {
        LOG.log(Level.FINE, "Disconnecting...");

        if (!connected) {
            LOG.log(Level.FINE, "Not connected.");
            return;
        }

        connected = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (final IOException e) {
                LOG.log(Level.SEVERE, e.toString());
            }
        }

        LOG.log(Level.FINE, "Disconnected.");
    }
}
