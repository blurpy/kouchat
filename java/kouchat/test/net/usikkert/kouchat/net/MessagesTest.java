
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;

import org.junit.Test;

/**
 * Test of {@link Messages}.
 *
 * @author Christian Ihle
 */
public class MessagesTest
{
	/** The settings. */
	private final Settings settings;

	/** The application user. */
	private final User me;

	/** The message class tested here. */
	private final Messages messages;

	/** Mocked network service used by messages. */
	private final NetworkService service;

	/**
	 * Constructor.
	 */
	public MessagesTest()
	{
		settings = Settings.getSettings();
		me = settings.getMe();
		service = mock( NetworkService.class );
		messages = new Messages( service );
	}

	/**
	 * Tests sendAwayMessage().
	 *
	 * Expects: 11515687!AWAY#Christian:I am away
	 */
	@Test
	public void testSendAwayMessage()
	{
		String awayMsg = "I am away";
		me.setAwayMsg( awayMsg );
		messages.sendAwayMessage();
		me.setAwayMsg( "" );
		verify( service ).sendMulticastMsg( createMessage( "AWAY" ) + awayMsg );
	}

	/**
	 * Tests sendBackMessage().
	 *
	 * Expects: 12485102!BACK#Christian:
	 */
	@Test
	public void testSendBackMessage()
	{
		messages.sendBackMessage();
		verify( service ).sendMulticastMsg( createMessage( "BACK" ) );
	}

	/**
	 * Tests sendChatMessage().
	 *
	 * Expects: 16899115!MSG#Christian:[-15987646]Some chat message
	 */
	@Test
	public void testSendChatMessage()
	{
		String msg = "Some chat message";
		messages.sendChatMessage( msg );
		verify( service ).sendMulticastMsg( createMessage( "MSG" ) + "[" + settings.getOwnColor() + "]" + msg );
	}

	/**
	 * Tests sendClient().
	 *
	 * Expects: 13132531!CLIENT#Christian:(KouChat v0.9.9-dev null)[134]{Linux}<0>
	 *
	 * FIXME: this test sometimes fails because of a millisecond issue.
	 */
	@Test
	public void testSendClientMessage()
	{
		String client = "(" + me.getClient() + ")"
			+ "[" + ( System.currentTimeMillis() - me.getLogonTime() ) + "]"
			+ "{" + me.getOperatingSystem() + "}"
			+ "<" + me.getPrivateChatPort() + ">";

		messages.sendClient();
		verify( service ).sendMulticastMsg( createMessage( "CLIENT" ) + client );
	}

	/**
	 * Tests sendExposeMessage().
	 *
	 * Expects: 16424378!EXPOSE#Christian:
	 */
	@Test
	public void testSendExposeMessage()
	{
		messages.sendExposeMessage();
		verify( service ).sendMulticastMsg( createMessage( "EXPOSE" ) );
	}

	/**
	 * Tests sendExposingMessage().
	 *
	 * Expects: 17871777!EXPOSING#Christian:
	 */
	@Test
	public void testSendExposingMessage()
	{
		messages.sendExposingMessage();
		verify( service ).sendMulticastMsg( createMessage( "EXPOSING" ) );
	}

	/**
	 * Tests sendFile().
	 *
	 * Expects: 14394329!SENDFILE#Christian:(1234)[80800]{37563645}a_file.txt
	 */
	@Test
	public void testSendFileMessage()
	{
		int userCode = 1234;
		long fileLength = 80800L;
		int fileHash = 37563645;
		String fileName = "a_file.txt";

		String info = "(" + 1234 + ")"
			+ "[" + fileLength + "]"
			+ "{" + fileHash + "}"
			+ fileName;

		messages.sendFile( userCode, fileLength, fileHash, fileName );
		verify( service ).sendMulticastMsg( createMessage( "SENDFILE" ) + info );
	}

	/**
	 * Tests sendFileAbort().
	 *
	 * Expects: 15234876!SENDFILEABORT#Christian:(4321){8578765}another_file.txt
	 */
	@Test
	public void testSendFileAbortMessage()
	{
		int userCode = 4321;
		int fileHash = 8578765;
		String fileName = "another_file.txt";

		String info = "(" + userCode + ")"
			+ "{" + fileHash + "}"
			+ fileName;

		messages.sendFileAbort( userCode, fileHash, fileName );
		verify( service ).sendMulticastMsg( createMessage( "SENDFILEABORT" ) + info );
	}

	/**
	 * Tests sendFileAccept().
	 *
	 * Expects: 17247198!SENDFILEACCEPT#Christian:(4321)[20103]{8578765}some_file.txt
	 */
	@Test
	public void testSendFileAcceptMessage()
	{
		int userCode = 4321;
		int port = 20103;
		int fileHash = 8578765;
		String fileName = "some_file.txt";

		String info = "(" + userCode + ")"
			+ "[" + port + "]"
			+ "{" + fileHash + "}"
			+ fileName;

		messages.sendFileAccept( userCode, port, fileHash, fileName );
		verify( service ).sendMulticastMsg( createMessage( "SENDFILEACCEPT" ) + info );
	}

	/**
	 * Tests sendGetTopicMessage().
	 *
	 * Expects: 19909338!GETTOPIC#Christian:
	 */
	@Test
	public void testSendGetTopicMessage()
	{
		messages.sendGetTopicMessage();
		verify( service ).sendMulticastMsg( createMessage( "GETTOPIC" ) );
	}

	/**
	 * Tests sendIdleMessage().
	 *
	 * Expects: 10223997!IDLE#Christian:
	 */
	@Test
	public void testSendIdleMessage()
	{
		messages.sendIdleMessage();
		verify( service ).sendMulticastMsg( createMessage( "IDLE" ) );
	}

	/**
	 * Tests sendLogoffMessage().
	 *
	 * Expects: 18265486!LOGOFF#Christian:
	 */
	@Test
	public void testSendLogoffMessage()
	{
		messages.sendLogoffMessage();
		verify( service ).sendMulticastMsg( createMessage( "LOGOFF" ) );
	}

	/**
	 * Tests sendLogonMessage().
	 *
	 * Expects: 10794786!LOGON#Christian:
	 */
	@Test
	public void testSendLogonMessage()
	{
		messages.sendLogonMessage();
		verify( service ).sendMulticastMsg( createMessage( "LOGON" ) );
	}

	/**
	 * Tests sendNickCrashMessage().
	 *
	 * Expects: 16321536!NICKCRASH#Christian:niles
	 */
	@Test
	public void testSendNickCrashMessage()
	{
		String nick = "niles";
		messages.sendNickCrashMessage( nick );
		verify( service ).sendMulticastMsg( createMessage( "NICKCRASH" ) + nick );
	}

	/**
	 * Tests sendNickMessage().
	 *
	 * Expects: 14795611!NICK#Christian:
	 */
	@Test
	public void testSendNickMessage()
	{
		messages.sendNickMessage();
		verify( service ).sendMulticastMsg( createMessage( "NICK" ) );
	}

	/**
	 * Tests sendPrivateMessage().
	 *
	 * Expects: 10897608!PRIVMSG#Christian:(435435)[-15987646]this is a private message
	 */
	@Test
	public void testSendPrivateMessage()
	{
		String privmsg = "this is a private message";
		String userIP = "192.168.5.155";
		int userPort = 12345;
		int userCode = 435435;

		String message = "(" + userCode + ")"
			+ "[" + settings.getOwnColor() + "]"
			+ privmsg;

		messages.sendPrivateMessage( privmsg, userIP, userPort, userCode );
		verify( service ).sendUDPMsg( createMessage( "PRIVMSG" ) + message, userIP, userPort );
	}

	/**
	 * Tests sendStoppedWritingMessage().
	 *
	 * Expects: 15140738!STOPPEDWRITING#Christian:
	 */
	@Test
	public void testSendStoppedWritingMessage()
	{
		messages.sendStoppedWritingMessage();
		verify( service ).sendMulticastMsg( createMessage( "STOPPEDWRITING" ) );
	}

	/**
	 * Tests sendTopicMessage().
	 *
	 * Expects: 18102542!TOPIC#Christian:(Snoopy)[2132321323]Interesting topic
	 */
	@Test
	public void testSendTopicMessage()
	{
		Topic topic = new Topic( "Interesting topic", "Snoopy", 2132321323L );
		String message = "(" + topic.getNick() + ")"
			+ "[" + topic.getTime() + "]"
			+ topic.getTopic();

		messages.sendTopicMessage( topic );
		verify( service ).sendMulticastMsg( createMessage( "TOPIC" ) + message );
	}

	/**
	 * Tests sendWritingMessage().
	 *
	 * Expects: 19610068!WRITING#Christian:
	 */
	@Test
	public void testSendWritingMessage()
	{
		messages.sendWritingMessage();
		verify( service ).sendMulticastMsg( createMessage( "WRITING" ) );
	}

	/**
	 * Creates the standard part for most of the message types.
	 *
	 * @param type The message type.
	 * @return A message.
	 */
	private String createMessage( final String type )
	{
		return me.getCode() + "!" + type + "#" + me.getNick() + ":";
	}
}
