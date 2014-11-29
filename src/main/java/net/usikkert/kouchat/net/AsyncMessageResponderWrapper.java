
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

package net.usikkert.kouchat.net;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Validate;

/**
 * Wrapper around a real {@link MessageResponder} that handles operations that need to be async and
 * operations from unknown users.
 *
 * @author Christian Ihle
 */
public class AsyncMessageResponderWrapper implements MessageResponder {

    private final MessageResponder messageResponder;

    public AsyncMessageResponderWrapper(final MessageResponder messageResponder) {
        Validate.notNull(messageResponder, "MessageResponder can not be null");

        this.messageResponder = messageResponder;
    }

    @Override
    public void messageArrived(final int userCode, final String msg, final int color) {
        messageResponder.messageArrived(userCode, msg, color);
    }

    @Override
    public void topicChanged(final int userCode, final String newTopic, final String nick, final long time) {
        messageResponder.topicChanged(userCode, newTopic, nick, time);
    }

    @Override
    public void topicRequested() {
        messageResponder.topicRequested();
    }

    @Override
    public void awayChanged(final int userCode, final boolean away, final String awayMsg) {
        messageResponder.awayChanged(userCode, away, awayMsg);
    }

    @Override
    public void nickChanged(final int userCode, final String newNick) {
        messageResponder.nickChanged(userCode, newNick);
    }

    @Override
    public void nickCrash() {
        messageResponder.nickCrash();
    }

    @Override
    public void meLogOn(final String ipAddress) {
        messageResponder.meLogOn(ipAddress);
    }

    @Override
    public void userLogOn(final User newUser) {
        messageResponder.userLogOn(newUser);
    }

    @Override
    public void userLogOff(final int userCode) {
        messageResponder.userLogOff(userCode);
    }

    @Override
    public void userExposing(final User user) {
        messageResponder.userExposing(user);
    }

    @Override
    public void exposeRequested() {
        messageResponder.exposeRequested();
    }

    @Override
    public void writingChanged(final int userCode, final boolean writing) {
        messageResponder.writingChanged(userCode, writing);
    }

    @Override
    public void meIdle(final String ipAddress) {
        messageResponder.meIdle(ipAddress);
    }

    @Override
    public void userIdle(final int userCode, final String ipAddress) {
        messageResponder.userIdle(userCode, ipAddress);
    }

    @Override
    public void fileSend(final int userCode, final long byteSize, final String fileName,
                         final String user, final int fileHash) {
        messageResponder.fileSend(userCode, byteSize, fileName, user, fileHash);
    }

    @Override
    public void fileSendAborted(final int userCode, final String fileName, final int fileHash) {
        messageResponder.fileSendAborted(userCode, fileName, fileHash);
    }

    @Override
    public void fileSendAccepted(final int userCode, final String fileName, final int fileHash, final int port) {
        messageResponder.fileSendAccepted(userCode, fileName, fileHash, port);
    }

    @Override
    public void clientInfo(final int userCode, final String client, final long timeSinceLogon,
                           final String operatingSystem, final int privateChatPort) {
        messageResponder.clientInfo(userCode, client, timeSinceLogon, operatingSystem, privateChatPort);
    }
}
