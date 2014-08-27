
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

import static org.junit.Assert.*;

import javax.swing.JOptionPane;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration test of {@link UITools}.
 *
 * @author Christian Ihle
 */
@Ignore
@SuppressWarnings("HardCodedStringLiteral")
public class UIToolsIntegrationTest {

    private UITools uiTools;

    @Before
    public void setUp() {
        uiTools = new UITools();
    }

    @Test
    public void showOptionDialog() {
        final int responce = uiTools.showOptionDialog("The message", "The title", "Positive", "Negative");

        System.out.println(responce + " " + (responce == JOptionPane.YES_OPTION));
    }

    @Test
    public void isRunningOnKDEShouldBeTrueOnKDE() {
        // Should fail on other platforms than KDE
        assertTrue(uiTools.isRunningOnKDE());
    }
}
