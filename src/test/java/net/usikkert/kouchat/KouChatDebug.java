
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

package net.usikkert.kouchat;

import net.usikkert.kouchat.ui.swing.debug.CheckThreadViolationRepaintManager;

/**
 * Starts {@link KouChat} with the {@link CheckThreadViolationRepaintManager}, for debugging.
 *
 * @author Christian Ihle
 */
public final class KouChatDebug {

    private KouChatDebug() {

    }

    /**
     * Initializes the repaint manager before running KouChat.
     *
     * @param args Arguments to be passed along to the real main method.
     */
    public static void main(final String[] args) {
        new CheckThreadViolationRepaintManager();
        KouChat.main(args);
    }
}
