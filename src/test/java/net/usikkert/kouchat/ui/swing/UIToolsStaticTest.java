
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

package net.usikkert.kouchat.ui.swing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Desktop;
import java.awt.SystemTray;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;

import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test of {@link UITools} using {@link PowerMockito} to mock statics.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UIManager.class, Runtime.class, Desktop.class, SystemTray.class, UITools.class })
public class UIToolsStaticTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UITools uiTools;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(UIManager.class);
        PowerMockito.mockStatic(Runtime.class);
        PowerMockito.mockStatic(Desktop.class);
        PowerMockito.mockStatic(SystemTray.class);

        uiTools = new UITools();
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
        final KouLookAndFeel kouLookAndFeel = mock(KouLookAndFeel.class);
        final BasicLookAndFeel basicLookAndFeel = mock(BasicLookAndFeel.class);

        final UIManager.LookAndFeelInfo synthLookAndFeelInfo =
                new UIManager.LookAndFeelInfo("SynthLookAndFeel", synthLookAndFeel.getClass().getName());
        final UIManager.LookAndFeelInfo kouLookAndFeelInfo =
                new UIManager.LookAndFeelInfo("KouLookAndFeel", kouLookAndFeel.getClass().getName());
        final UIManager.LookAndFeelInfo basicLookAndFeelInfo =
                new UIManager.LookAndFeelInfo("BasicLookAndFeel", basicLookAndFeel.getClass().getName());

        when(UIManager.getInstalledLookAndFeels()).thenReturn(new UIManager.LookAndFeelInfo[] {
                synthLookAndFeelInfo, kouLookAndFeelInfo, basicLookAndFeelInfo
        });

        when(UIManager.getLookAndFeel()).thenReturn(kouLookAndFeel);

        final UIManager.LookAndFeelInfo currentLookAndFeel = uiTools.getCurrentLookAndFeel();

        assertSame(kouLookAndFeelInfo, currentLookAndFeel);
    }

    @Test
    public void runCommandShouldUseRuntimeExec() throws IOException {
        final Runtime runtime = mock(Runtime.class);
        when(Runtime.getRuntime()).thenReturn(runtime);

        uiTools.runCommand("ls -l");

        verify(runtime).exec("ls -l");
    }

    @Test
    public void browseWithUrlShouldUseDesktopBrowse() throws IOException, URISyntaxException {
        final Desktop desktop = mock(Desktop.class);
        when(Desktop.getDesktop()).thenReturn(desktop);

        uiTools.browse("www.ape.no");

        verify(desktop).browse(new URI("www.ape.no"));
    }

    @Test
    public void isSystemTraySupportedShouldReturnValueFromSystemTray() {
        when(SystemTray.isSupported()).thenReturn(true);
        assertTrue(uiTools.isSystemTraySupported());

        when(SystemTray.isSupported()).thenReturn(false);
        assertFalse(uiTools.isSystemTraySupported());
    }

    @Test
    public void getSystemTrayShouldReturnSystemTrayInstanceFromSystemTray() {
        final SystemTray mockSystemTray = mock(SystemTray.class);
        when(SystemTray.getSystemTray()).thenReturn(mockSystemTray);

        final SystemTray systemTrayFromUiTools = uiTools.getSystemTray();

        assertSame(mockSystemTray, systemTrayFromUiTools);
    }
}
