
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Client for communicating over a tcp socket.
 *
 * @author Christian Ihle
 */
public class TCPClient implements Runnable {

    private static final Logger LOG = Logger.getLogger(TCPClient.class);

    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    @Nullable
    private TCPClientListener clientListener;

    private boolean connected;

    public TCPClient(final Socket socket) {
        Validate.notNull(socket, "Socket can not be null");

        try {
            this.socket = socket;
            // TODO how is this outside of Java?
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        }

        catch (final IOException e) {
            throw new RuntimeException(e); // TODO how to handle?
        }

        LOG.fine("Connected to %s:%s", getIPAddress(), socket.getPort());
    }

    @Override
    public void run() {
        try {
            while (connected) {
                final String message = inputStream.readUTF();
                LOG.fine("Message arrived from %s: %s", getIPAddress(), message);

                if (clientListener != null) {
                    clientListener.messageArrived(message, this);
                }
            }
        }

        catch (final IOException e) {
            LOG.severe(e.toString());
            connected = false;

            if (clientListener != null) {
                clientListener.disconnected(this);
            }
        }
    }

    public void send(final String message) {
        if (!connected) {
            return;
        }

        try {
            outputStream.writeUTF(message);
            LOG.fine("Sent message: %s", message);
        }

        catch (final IOException e) {
            LOG.severe(e.toString());
            connected = false;

            if (clientListener != null) {
                clientListener.disconnected(this);
            }
        }
    }

    public void startListener() {
        LOG.fine("Listening on %s:%s", getIPAddress(), socket.getPort());

        connected = true;
        new Thread(this, getClass().getSimpleName()).start();
    }

    public void disconnect() {
        try {
            LOG.fine("Disconnected from %s:%s", getIPAddress(), socket.getPort());
            connected = false;

            if (clientListener != null) {
                clientListener.disconnected(this);
            }

            socket.close();
        }

        catch (final IOException e) {
            LOG.warning(e.toString());
        }
    }

    public String getIPAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public void registerClientListener(@Nullable final TCPClientListener theClientListener) {
        this.clientListener = theClientListener;
    }
}
