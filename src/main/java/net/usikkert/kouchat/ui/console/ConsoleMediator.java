
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

package net.usikkert.kouchat.ui.console;

import net.usikkert.kouchat.jmx.JMXAgent;
import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.misc.ChatLogger;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.settings.PropertyFileSettingsSaver;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Sleeper;
import net.usikkert.kouchat.util.Validate;

/**
 * This class is the binding between the controller and the console ui.
 *
 * @author Christian Ihle
 */
public class ConsoleMediator implements UserInterface {

    private final Settings settings;
    private final Messages messages;
    private final MessageController msgController;
    private final Controller controller;
    private final JMXAgent jmxAgent;
    private final ConsoleInput consoleInput;
    private final Sleeper sleeper;
    private final ErrorHandler errorHandler;

    /**
     * Constructor.
     *
     * <p>Initializes the lower layers.</p>
     *
     * @param settings The settings to use.
     * @param messages The messages to use.
     */
    public ConsoleMediator(final Settings settings, final Messages messages) {
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(messages, "Messages can not be null");

        this.settings = settings;
        this.messages = messages;

        errorHandler = ErrorHandler.getErrorHandler();
        final ConsoleChatWindow chat = new ConsoleChatWindow();
        msgController = new MessageController(chat, this, settings, errorHandler);
        final PropertyFileSettingsSaver settingsSaver = new PropertyFileSettingsSaver(settings, errorHandler);
        controller = new Controller(this, settings, settingsSaver, errorHandler);
        jmxAgent = new JMXAgent(controller.createJMXBeanLoader());
        consoleInput = new ConsoleInput(controller, this, settings, messages);
        sleeper = new Sleeper();
    }

    /**
     * Logs on to the network, starts the input loop thread, and activates jmx beans.
     */
    public void start() {
        controller.start();
        controller.logOn();
        consoleInput.start();
        jmxAgent.activate();
    }

    /**
     * Shows information about how to save the file, then returns true.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean askFileSave(final String user, final String fileName, final String size) {
        msgController.showSystemMessage("/receive or /reject the file");
        return true;
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void changeAway(final boolean away) {

    }

    /**
     * Shows a message that says this is not supported.
     *
     * {@inheritDoc}
     */
    @Override
    public void clearChat() {
        msgController.showSystemMessage("Clear chat is not supported in console mode");
    }

    /**
     * Waits until the user has accepted or rejected the file transfer, and then returns.
     *
     * {@inheritDoc}
     */
    @Override
    public void showFileSave(final FileReceiver fileReceiver) {
        while (!fileReceiver.isAccepted() && !fileReceiver.isRejected() && !fileReceiver.isCanceled()) {
            sleeper.sleep(500);
        }
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void showTopic() {

    }

    /**
     * Creates a new {@link TransferHandler}.
     *
     * {@inheritDoc}
     */
    @Override
    public void showTransfer(final FileReceiver fileRes) {
        new TransferHandler(fileRes, msgController);
    }

    /**
     * Creates a new {@link TransferHandler}.
     *
     * {@inheritDoc}
     */
    @Override
    public void showTransfer(final FileSender fileSend) {
        new TransferHandler(fileSend, msgController);
    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void notifyMessageArrived(final User user) {

    }

    /**
     * Not implemented.
     *
     * {@inheritDoc}
     */
    @Override
    public void notifyPrivateMessageArrived(final User user) {

    }

    /**
     * Creates a new private chat window with the user, as well as configuring the logger.
     *
     * @param user The user to create a new private chat for.
     */
    @Override
    public void createPrivChat(final User user) {
        if (user.getPrivchat() == null) {
            user.setPrivchat(new PrivateChatConsole(user));
        }

        if (user.getPrivateChatLogger() == null) {
            user.setPrivateChatLogger(new ChatLogger(user.getNick(), settings, errorHandler));
        }
    }

    /**
     * Returns the message controller for console mode.
     *
     * {@inheritDoc}
     */
    @Override
    public MessageController getMessageController() {
        return msgController;
    }

    /**
     * Will always return true.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isFocused() {
        return true;
    }

    /**
     * Will always return true.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * Quits the application.
     */
    @Override
    public void quit() {
        System.exit(0);
    }
}
