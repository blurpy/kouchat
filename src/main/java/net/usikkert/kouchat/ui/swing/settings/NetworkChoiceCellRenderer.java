
/***************************************************************************
 *   Copyright 2006-2018 by Christian Ihle                                 *
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

package net.usikkert.kouchat.ui.swing.settings;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * A cell renderer for the combobox with network choices that enables tooltips on the elements,
 * showing the display name of the network device.
 *
 * @author Christian Ihle
 */
public class NetworkChoiceCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(final JList list, final Object value,
                                                  final int index, final boolean isSelected,
                                                  final boolean cellHasFocus) {
        final NetworkChoice networkChoice = (NetworkChoice) value;
        list.setToolTipText(networkChoice.getDisplayName());

        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
