
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

package net.usikkert.kouchat.ui.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.ui.UserInterface;

/**
 * Contains the main input loop for the console mode.
 *
 * @author Christian Ihle
 */
public class ConsoleInput extends Thread {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ConsoleInput.class.getName());

    /** For reading keyboard input from the command line. */
    private final BufferedReader stdin;

    /** The controller, for access to lower layer functionality. */
    private final Controller controller;

    /** For parsing commands. */
    private final CommandParser cmdParser;

    /** For showing messages in the ui. */
    private final MessageController msgController;

    /**
     * Constructor. Initializes input from System.in.
     *
     * @param controller The controller to use.
     * @param ui The user interface to send messages to.
     */
    public ConsoleInput(final Controller controller, final UserInterface ui) {
        this.controller = controller;

        setName("ConsoleInputThread");
        msgController = ui.getMessageController();
        stdin = new BufferedReader(new InputStreamReader(System.in));
        cmdParser = new CommandParser(controller, ui);

        Runtime.getRuntime().addShutdownHook(new Thread("ConsoleInputShutdownHook") {
            @Override
            public void run() {
                System.out.println("Quitting - good bye!");
            }
        });
    }

    /**
     * Starts a loop waiting for input.
     * To stop the loop and exit the application, write /quit.
     */
    @Override
    public void run() {
        String input = "";

        while (input != null) {
            try {
                input = stdin.readLine();

                if (input != null && input.trim().length() > 0) {
                    if (input.startsWith("/"))
                    {
                        cmdParser.parse(input);
                    }

                    else
                    {
                        try
                        {
                            controller.sendChatMessage(input);
                            msgController.showOwnMessage(input);
                        }

                        catch (final CommandException e)
                        {
                            msgController.showSystemMessage(e.getMessage());
                        }
                    }
                }
            }

            catch (final IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
                input = null;
            }
        }

        System.exit(1);
    }
}
