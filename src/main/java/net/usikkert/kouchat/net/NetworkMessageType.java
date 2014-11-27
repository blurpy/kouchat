
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

/**
 * All the supported types of network messages.
 *
 * @author Christian Ihle
 */
public class NetworkMessageType {

    public static final String MSG = "MSG";
    public static final String LOGON = "LOGON";
    public static final String EXPOSING = "EXPOSING";
    public static final String LOGOFF = "LOGOFF";
    public static final String AWAY = "AWAY";
    public static final String BACK = "BACK";
    public static final String EXPOSE = "EXPOSE";
    public static final String NICKCRASH = "NICKCRASH";
    public static final String WRITING = "WRITING";
    public static final String STOPPEDWRITING = "STOPPEDWRITING";
    public static final String GETTOPIC = "GETTOPIC";
    public static final String TOPIC = "TOPIC";
    public static final String NICK = "NICK";
    public static final String IDLE = "IDLE";
    public static final String SENDFILEACCEPT = "SENDFILEACCEPT";
    public static final String SENDFILEABORT = "SENDFILEABORT";
    public static final String SENDFILE = "SENDFILE";
    public static final String CLIENT = "CLIENT";
    public static final String PRIVMSG = "PRIVMSG";
}
