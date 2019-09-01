
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
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

package net.usikkert.kouchat.functional;

import java.util.Calendar;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;
import net.usikkert.kouchat.util.Tools;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test that simulates a chat between characters in The Hitchhiker's Guide to the Galaxy.
 *
 * Does not really assert anything. Mostly used for making new screenshots.
 *
 * @author Christian Ihle
 */
@Ignore
@SuppressWarnings("HardCodedStringLiteral")
public class HitchhikerTest {

    private static TestClient arthur;
    private static TestClient ford;
    private static TestClient trillian;

    @Before
    public void setUp() {
        // Making sure the test clients only logs on once during all the tests
        if (arthur == null) {
            arthur = new TestClient("Arthur", 0, -6750208);
            arthur.logon();

            ford = new TestClient("Ford", 0, -13534789);
            ford.setInitialTopic("DON'T PANIC", getDateInPast());
            ford.logon();

            trillian = new TestClient("Trillian", 0);
            trillian.logon();
        }
    }

    /**
     * In the main chat:
     *
     * Topic: DON'T PANIC
     *
     * <Christian>: hey :)
     * <Arthur>: What are you doing?
     * <Ford>: Preparing for hyperspace. It's rather unpleasantly like being drunk.
     * <Arthur>: What's so wrong about being drunk?
     * <Ford>: Ask a glass of water.
     * *** Trillian went away: It won't affect me, I'm already a woman.
     * <Christian>: interesting!
     */
    @Test
    public void test02DoMainChat() throws CommandException {
        // If Console, start KouChat first, to avoid warnings about port numbers in use.
        sleep(7000);

        // (Re)Start KouChat, write: hey :)
        final User christian = arthur.getUser("Christian");

        sleep(10000);
        arthur.sendChatMessage("What are you doing?");

        sleep(16000);
        ford.sendChatMessage("Preparing for hyperspace. It's rather unpleasantly like being drunk.");

        sleep(9000);
        arthur.sendChatMessage("What's so wrong about being drunk?");

        sleep(7000);
        ford.sendChatMessage("Ask a glass of water.");

        sleep(8000);
        trillian.goAway("It won't affect me, I'm already a woman.");

        sleep(6000);
        // Write: interesting!

        sleep(1000);
        arthur.sendPrivateChatMessage("OK. Leave this to me. I'm British. I know how to queue.", christian);

        // Take screenshot of the main chat
        sleep(20000);
    }

    private long getDateInPast() {
        final Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR_OF_DAY, -2);
        calendar.add(Calendar.MINUTE, -15);
        calendar.add(Calendar.DATE, -12);

        return calendar.getTimeInMillis();
    }

    private void sleep(final int ms) {
        Tools.sleep(ms); // Screenshot mode
//        Tools.sleep(1500); // Test mode
    }
}
