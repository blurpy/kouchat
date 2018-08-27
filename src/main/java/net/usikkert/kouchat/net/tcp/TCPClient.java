
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

    private boolean connected;

    public TCPClient(final Socket socket) {
        Validate.notNull(socket, "Socket can not be null");

        try {
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        }

        catch (final IOException e) {
            throw new RuntimeException(e);
        }

        LOG.fine("Connected to %s:%s", getIPAddress(), socket.getPort());
    }

    @Override
    public void run() {
        try {
            while (connected) {
                final String message = inputStream.readUTF();
                LOG.fine("Received message: %s", message);
            }
        }

        catch (final IOException e) {
            LOG.severe(e.toString());
            connected = false;
        }
    }

    public void send(final String message) {
        if (!connected) {
            return;
        }

        try {
            LOG.fine("Sending message: %s", message);
            outputStream.writeUTF(message);
        }

        catch (final IOException e) {
            LOG.severe(e.toString());
            connected = false;
        }
    }

    public void startListener() {
        LOG.fine("Listening on %s:%s", getIPAddress(), socket.getPort());

        connected = true;
        new Thread(this, getClass().getSimpleName()).start();
    }

    public void disconnect() {
        try {
            connected = false;
            LOG.fine("Disconnected from %s:%s", getIPAddress(), socket.getPort());
            socket.close();
        }

        catch (final IOException e) {
            LOG.warning(e.toString());
        }
    }

    private String getIPAddress() {
        return socket.getInetAddress().getHostAddress();
    }
}
