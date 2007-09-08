
/***************************************************************************
 *   Copyright 2006-2007 by Christian Ihle                                 *
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

package net.usikkert.kouchat.event;

import net.usikkert.kouchat.misc.Nick;

public interface MessageListener
{
	public void messageArrived( String msg, int color );
	public void topicChanged( String newTopic, String nick, long time );
	public void topicRequested();
	public void awayChanged( int userCode, boolean away, String awayMsg );
	public void nickChanged( int userCode, String newNick );
	public void nickCrash();
	public void meLogOn( String ipAddress );
	public void userLogOn( Nick newUser );
	public void userLogOff( int userCode );
	public void userExposing( Nick user );
	public void exposeRequested();
	public void writingChanged( int userCode, boolean writing );
	public void meIdle();
	public void userIdle( int userCode );
	public void fileSend( long byteSize, String fileName, String user, int fileHash, int fileCode, int userCode );
	public void fileSendAborted( String user, String fileName, int fileHash );
	public void fileSendAccepted( String user, int userCode, String fileName, int fileHash, int port );
}
