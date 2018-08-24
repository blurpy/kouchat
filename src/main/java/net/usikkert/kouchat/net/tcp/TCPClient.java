
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
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.util.Validate;

/**
 * Client for communicating over a tcp socket.
 *
 * @author Christian Ihle
 */
public class TCPClient implements Runnable {

    private static final Logger LOG = Logger.getLogger(TCPClient.class.getName());

    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    private boolean connected;

    public TCPClient(final Socket socket) {
        Validate.notNull(socket, "Socket can not be null");

        LOG.log(Level.FINE, "Connected to " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        }

        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (connected) {
                final String message = inputStream.readUTF();
                LOG.log(Level.FINE, "Received message: " + message);
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString());
            connected = false;
        }
    }

    public void send(final String message) {
        if (!connected) {
            return;
        }

        try {
            LOG.log(Level.FINE, "Sending message: " + message);
            outputStream.writeUTF(message);
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString());
            connected = false;
        }
    }

    public void startListener() {
        LOG.log(Level.FINE, "Listening on " + socket.getInetAddress() + ":" + socket.getPort());

        connected = true;
        new Thread(this, getClass().getSimpleName()).start();
    }

    public void disconnect() {
        try {
            connected = false;
            LOG.log(Level.FINE, "Disconnected from " + socket.getInetAddress() + ":" + socket.getPort());
            socket.close();
        }

        catch (final IOException e) {
            LOG.log(Level.WARNING, e.toString());
        }
    }
}
