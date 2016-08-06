
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

package net.usikkert.kouchat.ui.swing;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.usikkert.kouchat.jmx.JMXAgent;
import net.usikkert.kouchat.message.CoreMessages;
import net.usikkert.kouchat.misc.ChatLogger;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.SoundBeeper;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileToSend;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.settings.PropertyFileSettingsSaver;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.ui.swing.settings.SettingsDialog;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This class is a mediator for the gui, and gets all the events from the gui layer
 * that needs access to other components, or classes in lower layers. It is also
 * the interface for classes in lower layers to update the gui.
 *
 * @author Christian Ihle
 */
public class SwingMediator implements Mediator, UserInterface {

    private final UITools uiTools = new UITools();

    private final SettingsDialog settingsDialog;
    private final KouChatFrame gui;
    private final MainPanel mainP;
    private final SysTray sysTray;
    private final MenuBar menuBar;
    private final ButtonPanel buttonP;

    private final Controller controller;
    private final User me;
    private final CommandParser cmdParser;
    private final SoundBeeper beeper;
    private final MessageController msgController;
    private final JMXAgent jmxAgent;

    private final ImageLoader imageLoader;
    private final Settings settings;
    private final SwingMessages swingMessages;
    private final ErrorHandler errorHandler;

    /**
     * Constructor. Initializes the lower layers.
     *
     * @param compHandler An object with references to all the gui components this mediator works with.
     * @param imageLoader The image loader.
     * @param settings The settings to use.
     * @param swingMessages The swing messages to use for the user interface.
     * @param coreMessages The core messages to use elsewhere.
     * @param errorHandler The error handler to use.
     */
    public SwingMediator(final ComponentHandler compHandler, final ImageLoader imageLoader, final Settings settings,
                         final SwingMessages swingMessages, final CoreMessages coreMessages,
                         final ErrorHandler errorHandler) {
        Validate.notNull(compHandler, "Component handler can not be null");
        Validate.notNull(imageLoader, "Image loader can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(swingMessages, "Swing messages can not be null");
        Validate.notNull(coreMessages, "Core messages can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        compHandler.validate();

        this.imageLoader = imageLoader;
        this.settings = settings;
        this.swingMessages = swingMessages;
        this.errorHandler = errorHandler;

        final SidePanel sideP = compHandler.getSidePanel();
        settingsDialog = compHandler.getSettingsDialog();
        gui = compHandler.getGui();
        mainP = compHandler.getMainPanel();
        sysTray = compHandler.getSysTray();
        menuBar = compHandler.getMenuBar();
        buttonP = compHandler.getButtonPanel();

        me = settings.getMe();

        msgController = new MessageController(mainP, this, settings, errorHandler);
        final PropertyFileSettingsSaver settingsSaver =
                new PropertyFileSettingsSaver(settings, coreMessages, errorHandler);
        controller = new Controller(this, settings, settingsSaver, coreMessages, errorHandler);
        cmdParser = new CommandParser(controller, this, settings, coreMessages);
        beeper = new SoundBeeper(settings, new ResourceLoader(), errorHandler);
        jmxAgent = new JMXAgent(controller.createJMXBeanLoader());

        sideP.setUserList(controller.getUserList());
        mainP.setAutoCompleter(controller.getAutoCompleter());
    }

    /**
     * Hides the main window in the system tray if a system tray is supported.
     * Or just minimizes the window to the taskbar.
     */
    @Override
    public void minimize() {
        if (sysTray.isSystemTraySupport()) {
            gui.setVisible(false);
        } else {
            uiTools.minimize(gui);
        }
    }

    /**
     * Clears all the text from the main chat area.
     */
    @Override
    public void clearChat() {
        mainP.clearChat();
        mainP.getMsgTF().requestFocusInWindow();
    }

    /**
     * If the user is not away, asks for an away reason,
     * and sets the user as away.
     *
     * If user is away, asks if the user wants to come back.
     */
    @Override
    public void setAway() {
        if (me.isAway()) {
            final int choice = uiTools.showOptionDialog(swingMessages.getMessage("swing.away.comeBackPopup.message", me.getAwayMsg()),
                                                        swingMessages.getMessage("swing.away.comeBackPopup.title"),
                                                        swingMessages.getMessage("swing.button.yes"),
                                                        swingMessages.getMessage("swing.button.cancel"));

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    controller.comeBack();
                }

                catch (final CommandException e) {
                    uiTools.showWarningMessage(e.getMessage(), swingMessages.getMessage("swing.away.warningPopup.generalError.title"));
                }
            }
        }

        else {
            final String reason = uiTools.showInputDialog(swingMessages.getMessage("swing.away.goAwayPopup.message"),
                                                          swingMessages.getMessage("swing.away.goAwayPopup.title"),
                                                          null);

            if (reason != null && reason.trim().length() > 0) {
                if (controller.isWrote()) {
                    controller.changeWriting(me.getCode(), false);
                    mainP.getMsgTF().setText("");
                }

                try {
                   controller.goAway(reason);
                }

                catch (final CommandException e) {
                    uiTools.showWarningMessage(e.getMessage(), swingMessages.getMessage("swing.away.warningPopup.generalError.title"));
                }
            }
        }

        mainP.getMsgTF().requestFocusInWindow();
    }

    /**
     * Asks for the new topic, and changes it.
     */
    @Override
    public void setTopic() {
        final Topic topic = controller.getTopic();
        final String newTopic = uiTools.showInputDialog(swingMessages.getMessage("swing.topic.changeTopicPopup.message"),
                                                        swingMessages.getMessage("swing.topic.changeTopicPopup.title"),
                                                        topic.getTopic());

        if (newTopic != null) {
            try {
                cmdParser.fixTopic(newTopic);
            }

            catch (final CommandException e) {
                uiTools.showWarningMessage(e.getMessage(), swingMessages.getMessage("swing.topic.warningPopup.generalError.title"));
            }
        }

        mainP.getMsgTF().requestFocusInWindow();
    }

    /**
     * Logs on to the network and activates jmx beans.
     */
    @Override
    public void start() {
        controller.start();
        controller.logOn();
        jmxAgent.activate();
        updateTitleAndTray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkNetwork() {
        controller.checkNetwork();
    }

    /**
     * Asks if the user wants to quit.
     */
    @Override
    public void quit() {
        final int choice = uiTools.showOptionDialog(swingMessages.getMessage("swing.quitPopup.message"),
                                                    swingMessages.getMessage("swing.quitPopup.title"),
                                                    swingMessages.getMessage("swing.button.yes"),
                                                    swingMessages.getMessage("swing.button.cancel"));

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Updates the titlebar and the system tray tooltip with
     * current information about the application and the user.
     */
    @Override
    public void updateTitleAndTray() {
        final String title;

        if (!controller.isConnected()) {
            if (controller.isLoggedOn()) {
                title = swingMessages.getMessage("swing.mainChat.title.connectionLost", me.getNick());
            }

            else {
                title = swingMessages.getMessage("swing.mainChat.title.notConnected", me.getNick());
            }
        }

        else {
            if (me.isAway() && controller.getTopic().hasTopic()) {
                title = swingMessages.getMessage("swing.mainChat.title.awayAndTopic", me.getNick(), controller.getTopic());
            }

            else if (me.isAway()) {
                title = swingMessages.getMessage("swing.mainChat.title.away", me.getNick());
            }

            else if (controller.getTopic().hasTopic()) {
                title = swingMessages.getMessage("swing.mainChat.title.topic", me.getNick(), controller.getTopic());
            }

            else {
                title = me.getNick();
            }
        }

        gui.setTitle(uiTools.createTitle(title));
        gui.updateWindowIcon();
        sysTray.setToolTip(uiTools.createTitle(title));
    }

    /**
     * Shows or hides the main window.
     * The window will always be brought to front when shown.
     */
    @Override
    public void showOrHideWindow() {
        if (gui.isVisible()) {
            minimize();
        } else {
            if (uiTools.isMinimized(gui)) {
                uiTools.restore(gui);
            }

            gui.showWindow();
        }
    }

    /**
     * If the main window is hidden it is set visible,
     * but only as minimized in the taskbar.
     */
    @Override
    public void minimizeWindowIfHidden() {
        if (!gui.isVisible()) {
            uiTools.minimize(gui);
            gui.setVisible(true);
        }
    }

    /**
     * Opens the settings dialog window.
     */
    @Override
    public void showSettings() {
        settingsDialog.showSettings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings() {
        controller.saveSettings();
    }

    /**
     * Opens a file chooser, where the user can select a file to send to
     * another user.
     *
     * @param user The user to send the file to.
     * @param selectedFile A file that already exists to open the file chooser with
     *                     that file already selected, or <code>null</code> if the
     *                     file chooser should start fresh.
     */
    @Override
    public void sendFile(final User user, final File selectedFile) {
        if (user == null) {
            return;
        } else if (user.isMe()) {
            uiTools.showWarningMessage(swingMessages.getMessage("swing.sendFile.warningPopup.userIsMe"),
                                       swingMessages.getMessage("swing.sendFile.warningPopup.title"));
        }

        else if (me.isAway()) {
            uiTools.showWarningMessage(swingMessages.getMessage("swing.sendFile.warningPopup.meIsAway"),
                                       swingMessages.getMessage("swing.sendFile.warningPopup.title"));
        }

        else if (user.isAway()) {
            uiTools.showWarningMessage(swingMessages.getMessage("swing.sendFile.warningPopup.userIsAway", user.getNick()),
                                       swingMessages.getMessage("swing.sendFile.warningPopup.title"));
        }

        else if (!user.isOnline()) {
            uiTools.showWarningMessage(swingMessages.getMessage("swing.sendFile.warningPopup.userIsOffline", user.getNick()),
                                       swingMessages.getMessage("swing.sendFile.warningPopup.title"));
        }

        else {
            final JFileChooser chooser = uiTools.createFileChooser(swingMessages.getMessage("swing.sendFile.chooseFileDialog.title"));

            if (selectedFile != null && selectedFile.exists()) {
                chooser.setSelectedFile(selectedFile);
            }

            final int returnVal = chooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File file = chooser.getSelectedFile().getAbsoluteFile();

                if (file.exists() && file.isFile()) {
                    try {
                        cmdParser.sendFile(user, new FileToSend(file));
                    }

                    catch (final CommandException e) {
                        uiTools.showWarningMessage(e.getMessage(), swingMessages.getMessage("swing.sendFile.warningPopup.generalError.title"));
                    }
                }
            }
        }
    }

    /**
     * Gets the text written in the input field and either sends it to
     * the command parser or sends it as a message.
     */
    @Override
    public void write() {
        final String line = mainP.getMsgTF().getText();

        if (line.trim().length() > 0) {
            if (line.startsWith("/")) {
                cmdParser.parse(line);
            }

            else {
                try {
                    controller.sendChatMessage(line);
                    msgController.showOwnMessage(line);
                }

                catch (final CommandException e) {
                    msgController.showSystemMessage(e.getMessage());
                }
            }
        }

        mainP.getMsgTF().setText("");
    }

    /**
     * Gets the text from the input field of the private chat, and
     * sends it as a message to the user.
     *
     * @param privchat The private chat.
     */
    @Override
    public void writePrivate(final PrivateChatWindow privchat) {
        final String line = privchat.getChatText();
        final User user = privchat.getUser();

        if (line.trim().length() > 0) {
            try {
                controller.sendPrivateMessage(line, user);
                msgController.showPrivateOwnMessage(user, line);
            }

            catch (final CommandException e) {
                msgController.showPrivateSystemMessage(user, e.getMessage());
            }
        }

        privchat.clearChatText();
    }

    /**
     * Shows a list of the supported commands and their syntax.
     */
    @Override
    public void showCommands() {
        cmdParser.showCommands();
    }

    /**
     * Checks if the user is currently writing, and updates the status.
     */
    @Override
    public void updateWriting() {
        controller.updateMeWriting(!mainP.getMsgTF().getText().isEmpty());
    }

    /**
     * Changes the nick name of the user, if the nick is valid.
     *
     * @param nick The new nick name to change to.
     * @return If the nick name was changed successfully.
     */
    @Override
    public boolean changeNick(final String nick) {
        final String trimNick = nick.trim();

        if (!trimNick.equals(me.getNick())) {
            if (controller.isNickInUse(trimNick)) {
                uiTools.showWarningMessage(swingMessages.getMessage("swing.changeNick.warningPopup.nickInUse"),
                                           swingMessages.getMessage("swing.changeNick.warningPopup.title"));
            }

            else if (!Tools.isValidNick(trimNick)) {
                uiTools.showWarningMessage(swingMessages.getMessage("swing.changeNick.warningPopup.invalidNick", trimNick),
                                           swingMessages.getMessage("swing.changeNick.warningPopup.title"));
            }

            else {
                try {
                    controller.changeMyNick(trimNick);
                    msgController.showSystemMessage(swingMessages.getMessage("swing.changeNick.systemMessage.nickChanged", me.getNick()));
                    updateTitleAndTray();
                    return true;
                }

                catch (final CommandException e) {
                    uiTools.showWarningMessage(e.getMessage(), swingMessages.getMessage("swing.changeNick.warningPopup.title"));
                }
            }
        }

        else {
            return true;
        }

        return false;
    }

    /**
     * Runs when the user presses the cancel/close button in the
     * transfer dialog. If the button's text is close, the dialog should
     * close. If the text is cancel, the file transfer should stop,
     * and the button should change text to close.
     *
     * @param transferDialog The transfer dialog.
     */
    @Override
    public void transferCancelled(final TransferDialog transferDialog) {
        if (transferDialog.isCloseable()) {
            transferDialog.dispose();
        } else {
            transferDialog.registerAsCloseable();
            final FileTransfer fileTransfer = transferDialog.getFileTransfer();
            cmdParser.cancelFileTransfer(fileTransfer);
        }
    }

    /**
     * Notifies the user of a new message in different ways,
     * depending on the state of the main chat window.
     *
     * <ul>
     *   <li><i>Main chat in focus</i> - do nothing</li>
     *   <li><i>Main chat out of focus</i> - beep, update main chat icon</li>
     *   <li><i>Main chat hidden</i> - beep, update systray, show balloon</li>
     * </ul>
     *
     * @param user The user that sent the message.
     * @param message The message sent by the user.
     */
    @Override
    public void notifyMessageArrived(final User user, final String message) {
        // Main chat hidden - beep, update systray, show balloon
        if (!gui.isVisible()) {
            if (me.isAway()) {
                sysTray.setAwayActivityState();
            } else {
                sysTray.setNormalActivityState();
                beeper.beep();
                sysTray.showBalloonMessage(uiTools.createTitle(me.getNick()),
                                           swingMessages.getMessage("swing.systemTray.balloon.newMessage", user.getNick()));
            }
        }

        // Main chat out of focus - beep, update main chat icon
        else if (!gui.isFocused()) {
            updateTitleAndTray();

            if (!me.isAway()) {
                beeper.beep();
            }
        }
    }

    /**
     * Notifies the user of new private message in different ways,
     * depending on the state of the main chat window and the private
     * chat window.
     *
     * <br /><br />
     *
     * A private message can never be sent while the sender
     * or receiver is away, so this method assumes that is the case.
     *
     * <ul>
     *   <li><b>Main chat in focus</b></li>
     *   <ul>
     *     <li><i>Private chat in focus</i> - not possible</li>
     *     <li><i>Private chat out of focus</i> - update privchat icon</li>
     *     <li><i>Private chat hidden</i> - do nothing</li>
     *   </ul>
     *
     *   <li><b>Main chat out of focus</b></li>
     *   <ul>
     *     <li><i>Private chat in focus</i> - do nothing</li>
     *     <li><i>Private chat out of focus</i> - beep, update privchat icon</li>
     *     <li><i>Private chat hidden</i> - beep, update main chat icon</li>
     *   </ul>
     *
     *   <li><b>Main chat hidden</b></li>
     *   <ul>
     *     <li><i>Private chat in focus</i> - do nothing</li>
     *     <li><i>Private chat out of focus</i> - beep, update privchat icon</li>
     *     <li><i>Private chat hidden</i> - beep, update systray, show balloon</li>
     *   </ul>
     * </ul>
     *
     * @param user The user that sent the private message.
     * @param message The private message sent by the user.
     */
    @Override
    public void notifyPrivateMessageArrived(final User user, final String message) {
        final PrivateChatWindow privchat = user.getPrivchat();

        // Main chat hidden
        if (!gui.isVisible()) {
            // Private chat hidden - beep, update systray, show balloon
            if (!privchat.isVisible()) {
                sysTray.setNormalActivityState();
                beeper.beep();
                sysTray.showBalloonMessage(uiTools.createTitle(me.getNick()),
                                           swingMessages.getMessage("swing.systemTray.balloon.newPrivateMessage", user.getNick()));
            }

            // Private chat out of focus - beep, update privchat icon
            else if (!privchat.isFocused()) {
                privchat.updateUserInformation();
                beeper.beep();
            }
        }

        // Main chat out of focus
        else if (!gui.isFocused()) {
            // Private chat hidden - beep, update main chat icon
            if (!privchat.isVisible()) {
                me.setNewMsg(true);
                updateTitleAndTray();
                beeper.beep();
            }

            // Private chat out of focus - beep, update privchat icon
            else if (!privchat.isFocused()) {
                privchat.updateUserInformation();
                beeper.beep();
            }
        }

        // Main chat in focus
        else if (gui.isFocused()) {
            // Private chat out of focus - update privchat icon
            if (privchat.isVisible() && !privchat.isFocused()) {
                privchat.updateUserInformation();
            }
        }
    }

    /**
     * Gives a notification beep, and opens a dialog box asking if the user
     * wants to accept a file transfer from another user.
     *
     * @param user The user that wants to send a file.
     * @param fileName The name of the file.
     * @param size The size of the file, in readable format.
     * @return If the file was accepted or not.
     */
    @Override
    public boolean askFileSave(final String user, final String fileName, final String size) {
        beeper.beep();

        final int choice = uiTools.showOptionDialog(
                swingMessages.getMessage("swing.receiveFile.askToReceivePopup.message", user, fileName, size),
                swingMessages.getMessage("swing.receiveFile.askToReceivePopup.title"),
                swingMessages.getMessage("swing.button.yes"),
                swingMessages.getMessage("swing.button.cancel"));

        return choice == JOptionPane.YES_OPTION;
    }

    /**
     * Opens a file chooser so the user can choose where to save a file
     * another user is trying to send. Warns if the file name chosen
     * already exists.
     *
     * @param fileReceiver Information about the file to save.
     */
    @Override
    public void showFileSave(final FileReceiver fileReceiver) {
        uiTools.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                showFileSaveInternal(fileReceiver);
            }
        });
    }

    private void showFileSaveInternal(final FileReceiver fileReceiver) {
        final JFileChooser chooser = uiTools.createFileChooser(swingMessages.getMessage("swing.receiveFile.saveFileDialog.title"));
        chooser.setSelectedFile(fileReceiver.getFile());
        boolean done = false;

        while (!done) {
            done = true;
            final int returnVal = chooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File file = chooser.getSelectedFile().getAbsoluteFile();

                if (file.exists()) {
                    final int overwrite = uiTools.showOptionDialog(
                            swingMessages.getMessage("swing.receiveFile.fileExistPopup.message", file.getName()),
                            swingMessages.getMessage("swing.receiveFile.fileExistPopup.title"),
                            swingMessages.getMessage("swing.button.yes"),
                            swingMessages.getMessage("swing.button.cancel"));

                    if (overwrite != JOptionPane.YES_OPTION) {
                        done = false;
                    }
                }

                if (done) {
                    fileReceiver.setFile(file);
                    fileReceiver.accept();
                }
            }

            else {
                fileReceiver.reject();
            }
        }
    }

    /**
     * Updates the titlebar and tray tooltip with current information.
     */
    @Override
    public void showTopic() {
        updateTitleAndTray();
    }

    /**
     * Creates a new {@link TransferDialog} for that {@link FileReceiver}.
     *
     * @param fileRes The file receiver to create a transfer dialog for.
     */
    @Override
    public void showTransfer(final FileReceiver fileRes) {
        uiTools.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                final TransferDialog transferDialog =
                        new TransferDialog(SwingMediator.this, fileRes, imageLoader, settings, swingMessages, errorHandler);

                transferDialog.open();
            }
        });
    }

    /**
     * Creates a new {@link TransferDialog} for that {@link FileSender}.
     *
     * @param fileSend The file sender to create a transfer dialog for.
     */
    @Override
    public void showTransfer(final FileSender fileSend) {
        final TransferDialog transferDialog =
                new TransferDialog(this, fileSend, imageLoader, settings, swingMessages, errorHandler);

        transferDialog.open();
    }

    /**
     * Updates the gui components depending on the away state.
     *
     * @param away If away or not.
     */
    @Override
    public void changeAway(final boolean away) {
        if (away) {
            sysTray.setAwayState();
            mainP.getMsgTF().setEnabled(false);
            menuBar.setAwayState(true);
            buttonP.setAwayState(true);
        }

        else {
            sysTray.setNormalState();
            mainP.getMsgTF().setEnabled(true);
            menuBar.setAwayState(false);
            buttonP.setAwayState(false);
        }

        updateAwayInPrivChats(away);
        updateTitleAndTray();
    }

    /**
     * Notifies the open private chat windows that the away state has changed.
     *
     * @param away If the user is away.
     */
    private void updateAwayInPrivChats(final boolean away) {
        final UserList list = controller.getUserList();

        for (int i = 0; i < list.size(); i++) {
            final User user = list.get(i);

            if (user.getPrivchat() != null) {
                user.getPrivchat().updateAwayState();

                if (away) {
                    msgController.showPrivateSystemMessage(user, swingMessages.getMessage(
                            "swing.privateChat.systemMessage.wentAway", me.getAwayMsg()));
                }

                else {
                    msgController.showPrivateSystemMessage(user, swingMessages.getMessage(
                            "swing.privateChat.systemMessage.cameBack"));
                }
            }
        }
    }

    /**
     * Creates a new private chat window with the user, as well as configuring the logger.
     *
     * @param user The user to create a new private chat for.
     */
    @Override
    public void createPrivChat(final User user) {
        if (user.getPrivchat() == null) {
            uiTools.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    user.setPrivchat(new PrivateChatFrame(SwingMediator.this, user, imageLoader,
                                                          settings, swingMessages, errorHandler));
                }
            });
        }

        if (user.getPrivateChatLogger() == null) {
            user.setPrivateChatLogger(new ChatLogger(user.getNick(), settings, errorHandler));
        }
    }

    /**
     * Shows the user's private chat window.
     *
     * @param user The user to show the private chat for.
     */
    @Override
    public void showPrivChat(final User user) {
        createPrivChat(user);
        user.getPrivchat().setVisible(true);
    }

    /**
     * Resets the new private message field of the user.
     *
     * @param user The user to reset the field for.
     */
    @Override
    public void activatedPrivChat(final User user) {
        if (user.isNewPrivMsg()) {
            user.setNewPrivMsg(false); // In case the user has logged off
            controller.changeNewMessage(user.getCode(), false);
        }
    }

    /**
     * Returns the message controller for swing.
     *
     * @return The message controller.
     */
    @Override
    public MessageController getMessageController() {
        return msgController;
    }

    /**
     * Returns if the main chat is in focus.
     *
     * @return If the main chat is in focus.
     */
    @Override
    public boolean isFocused() {
        return gui.isFocused();
    }

    /**
     * Returns if the main chat is visible.
     *
     * @return If the main chat is visible.
     */
    @Override
    public boolean isVisible() {
        return gui.isVisible();
    }
}
