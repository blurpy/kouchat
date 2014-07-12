
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
import static org.mockito.Mockito.*;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Test of {@link UITools}.
 *
 * @author Christian Ihle
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(UIManager.class)
public class UIToolsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UITools uiTools;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(UIManager.class);
        uiTools = new UITools();
    }

    @Test
    @Ignore("Run this manually. Should fail on other platforms than KDE.")
    public void isRunningOnKDEShouldBeTrueOnKDE() {
        assertTrue(uiTools.isRunningOnKDE());
    }

    @Test
    public void getCurrentLookAndFeelShouldThrowExceptionIfNoLookAndFeelIsSet() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("No look and feel set. That's unexpected.");

        when(UIManager.getLookAndFeel()).thenReturn(null);

        uiTools.getCurrentLookAndFeel();
    }

    @Test
    public void getCurrentLookAndFeelShouldThrowExceptionIfNoLookAndFeelInfoCanBeFoundForTheCurrentLookAndFeel() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("No look and feel info found for 'SomeLookAndFeel'. That's unexpected.");

        final LookAndFeel currentLookAndFeel = mock(LookAndFeel.class);
        when(currentLookAndFeel.getName()).thenReturn("SomeLookAndFeel");
        when(UIManager.getLookAndFeel()).thenReturn(currentLookAndFeel);

        when(UIManager.getInstalledLookAndFeels()).thenReturn(new UIManager.LookAndFeelInfo[0]);

        uiTools.getCurrentLookAndFeel();
    }

    @Test
    public void getCurrentLookAndFeelShouldReturnCorrectLookAndFeelInfoForTheCurrentLookAndFeel() {
        final SynthLookAndFeel synthLookAndFeel = mock(SynthLookAndFeel.class);
        final NimbusLookAndFeel nimbusLookAndFeel = mock(NimbusLookAndFeel.class);
        final BasicLookAndFeel basicLookAndFeel = mock(BasicLookAndFeel.class);

        final UIManager.LookAndFeelInfo synthLookAndFeelInfo =
                new UIManager.LookAndFeelInfo("SynthLookAndFeel", synthLookAndFeel.getClass().getName());
        final UIManager.LookAndFeelInfo nimbusLookAndFeelInfo =
                new UIManager.LookAndFeelInfo("NimbusLookAndFeel", nimbusLookAndFeel.getClass().getName());
        final UIManager.LookAndFeelInfo basicLookAndFeelInfo =
                new UIManager.LookAndFeelInfo("BasicLookAndFeel", basicLookAndFeel.getClass().getName());

        when(UIManager.getInstalledLookAndFeels()).thenReturn(new UIManager.LookAndFeelInfo[] {
                synthLookAndFeelInfo, nimbusLookAndFeelInfo, basicLookAndFeelInfo
        });

        when(UIManager.getLookAndFeel()).thenReturn(nimbusLookAndFeel);

        final UIManager.LookAndFeelInfo currentLookAndFeel = uiTools.getCurrentLookAndFeel();

        assertSame(nimbusLookAndFeelInfo, currentLookAndFeel);
    }
}
