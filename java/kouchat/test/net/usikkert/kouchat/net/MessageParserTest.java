
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

package net.usikkert.kouchat.net;

import net.usikkert.kouchat.misc.NickDTO;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MessageParserTest implements MessageResponder
{
	private MessageParser msgParser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{

	}

	@Before
	public void setUp() throws Exception
	{
		msgParser = new MessageParser( this );
	}

	@After
	public void tearDown() throws Exception
	{

	}

	@Test
	public void testMessageParser()
	{
		// Already done that
	}

	@Test
	public void testStop()
	{
		//msgParser.stop();
	}

	@Test
	public void testRestart()
	{
		//msgParser.restart();
	}

	@Test
	public void testMessageArrived()
	{
		msgParser.messageArrived( "15626995!MSG#Ola:", "nilseteip" );
	}

	@Override
	public void awayChanged( final int userCode, final boolean away, final String awayMsg )
	{

	}

	@Override
	public void exposeRequested()
	{

	}

	@Override
	public void fileSend( final int userCode, final long byteSize, final String fileName,
			final String user, final int fileHash, final int fileCode )
	{

	}

	@Override
	public void fileSendAborted( final int userCode, final String fileName, final int fileHash )
	{

	}

	@Override
	public void fileSendAccepted( final int userCode, final String fileName, final int fileHash, final int port )
	{

	}

	@Override
	public void meIdle( final String ipAddress )
	{

	}

	@Override
	public void meLogOn( final String ipAddress )
	{

	}

	@Override
	public void messageArrived( final int userCode, final String msg, final int color )
	{

	}

	@Override
	public void nickChanged( final int userCode, final String newNick )
	{

	}

	@Override
	public void nickCrash()
	{

	}

	@Override
	public void topicChanged( final int userCode, final String newTopic, final String nick, final long time )
	{

	}

	@Override
	public void topicRequested()
	{

	}

	@Override
	public void userExposing( final NickDTO user )
	{

	}

	@Override
	public void userIdle( final int userCode, final String ipAddress )
	{

	}

	@Override
	public void userLogOff( final int userCode )
	{

	}

	@Override
	public void userLogOn( final NickDTO newUser )
	{

	}

	@Override
	public void writingChanged( final int userCode, final boolean writing )
	{

	}

	@Override
	public void clientInfo( final int userCode, final String client, final long logonTime,
			final String operatingSystem, final int privateChatPort )
	{

	}
}
