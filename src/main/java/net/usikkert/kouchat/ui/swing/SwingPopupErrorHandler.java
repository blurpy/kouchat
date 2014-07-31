
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

import net.usikkert.kouchat.event.ErrorListener;
import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the implementation of the error listener for use
 * in the swing gui. When an error occurs, a message box is shown.
 *
 * @author Christian Ihle
 */
public class SwingPopupErrorHandler implements ErrorListener {

    private final UITools uiTools = new UITools();

    private final Messages messages;

    public SwingPopupErrorHandler(final Messages messages) {
        Validate.notNull(messages, "Messages can not be null");

        this.messages = messages;
    }

    /**
     * Shows an error message in a non-blocking JOptionPane message box.
     *
     * @param errorMsg The message to show.
     */
    @Override
    public void errorReported(final String errorMsg) {
        uiTools.invokeLater(new Runnable() {
            @Override
            public void run() {
                uiTools.showErrorMessage(errorMsg, messages.getMessage("swing.popup.errorHandler.errorReported.title"));
            }
        });
    }

    /**
     * Shows a critical error message in a JOptionPane message box.
     *
     * @param criticalErrorMsg The message to show.
     */
    @Override
    public void criticalErrorReported(final String criticalErrorMsg) {
        uiTools.showErrorMessage(criticalErrorMsg, messages.getMessage("swing.popup.errorHandler.criticalErrorReported.title"));
    }
}
