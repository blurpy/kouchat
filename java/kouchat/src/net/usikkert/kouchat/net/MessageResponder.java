
/***************************************************************************
 *   Copyright 2006-2008 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouchat.net;

import net.usikkert.kouchat.misc.NickDTO;

/**
 * This is the interface for responders to multicast messages.
 *
 * @author Christian Ihle
 */
public interface MessageResponder
{
	void messageArrived( int userCode, String msg, int color );
	void topicChanged( int userCode, String newTopic, String nick, long time );
	void topicRequested();
	void awayChanged( int userCode, boolean away, String awayMsg );
	void nickChanged( int userCode, String newNick );
	void nickCrash();
	void meLogOn( String ipAddress );
	void userLogOn( NickDTO newUser );
	void userLogOff( int userCode );
	void userExposing( NickDTO user );
	void exposeRequested();
	void writingChanged( int userCode, boolean writing );
	void meIdle( String ipAddress );
	void userIdle( int userCode, String ipAddress );
	void fileSend( int userCode, long byteSize, String fileName, String user, int fileHash, int fileCode );
	void fileSendAborted( int userCode, String fileName, int fileHash );
	void fileSendAccepted( int userCode, String fileName, int fileHash, int port );
	void clientInfo( int userCode, String client, long timeSinceLogon, String operatingSystem, int privateChatPort );
}
