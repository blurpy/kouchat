
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

import static org.mockito.Mockito.*;

import java.util.logging.Logger;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link ImageLoader}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ImageLoaderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final ExpectedSystemExit expectedSystemExit = ExpectedSystemExit.none();

    @Before
    public void setUp() {
        // Silence the static logger
        final ImageLoader imageLoader = new ImageLoader(mock(ErrorHandler.class), new ResourceValidator(), new ResourceLoader());
        TestUtils.setFieldValueWithMock(imageLoader, "LOG", Logger.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new ImageLoader(null, new ResourceValidator(), new ResourceLoader());
    }

    @Test
    public void constructorShouldThrowExceptionIfResourceValidatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Resource validator can not be null");

        new ImageLoader(mock(ErrorHandler.class), null, new ResourceLoader());
    }

    @Test
    public void constructorShouldThrowExceptionIfResourceLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Resource loader can not be null");

        new ImageLoader(mock(ErrorHandler.class), new ResourceValidator(), null);
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAway16IsMissing() {
        checkMissingImage("/icons/16x16/kou_away_16x16.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAway22IsMissing() {
        checkMissingImage("/icons/22x22/kou_away_22x22.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAway24IsMissing() {
        checkMissingImage("/icons/24x24/kou_away_24x24.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAway32IsMissing() {
        checkMissingImage("/icons/32x32/kou_away_32x32.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAwayActivity16IsMissing() {
        checkMissingImage("/icons/16x16/kou_away_activity_16x16.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAwayActivity22IsMissing() {
        checkMissingImage("/icons/22x22/kou_away_activity_22x22.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAwayActivity24IsMissing() {
        checkMissingImage("/icons/24x24/kou_away_activity_24x24.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAwayActivity32IsMissing() {
        checkMissingImage("/icons/32x32/kou_away_activity_32x32.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouNormal16IsMissing() {
        checkMissingImage("/icons/16x16/kou_normal_16x16.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouNormal22IsMissing() {
        checkMissingImage("/icons/22x22/kou_normal_22x22.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouNormal24IsMissing() {
        checkMissingImage("/icons/24x24/kou_normal_24x24.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouNormal32IsMissing() {
        checkMissingImage("/icons/32x32/kou_normal_32x32.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouNormalActivity16IsMissing() {
        checkMissingImage("/icons/16x16/kou_normal_activity_16x16.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouNormalActivity22IsMissing() {
        checkMissingImage("/icons/22x22/kou_normal_activity_22x22.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouNormalActivity24IsMissing() {
        checkMissingImage("/icons/24x24/kou_normal_activity_24x24.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouNormalActivity32IsMissing() {
        checkMissingImage("/icons/32x32/kou_normal_activity_32x32.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfDotIsMissing() {
        checkMissingImage("/icons/dot.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfEnvelopeIsMissing() {
        checkMissingImage("/icons/envelope.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileySmileIsMissing() {
        checkMissingImage("/smileys/smile.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileySadIsMissing() {
        checkMissingImage("/smileys/sad.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyTongueIsMissing() {
        checkMissingImage("/smileys/tongue.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyTeethIsMissing() {
        checkMissingImage("/smileys/teeth.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyWinkIsMissing() {
        checkMissingImage("/smileys/wink.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyOmgIsMissing() {
        checkMissingImage("/smileys/omg.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyAngryIsMissing() {
        checkMissingImage("/smileys/angry.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyConfusedIsMissing() {
        checkMissingImage("/smileys/confused.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyCryIsMissing() {
        checkMissingImage("/smileys/cry.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyEmbarrassedIsMissing() {
        checkMissingImage("/smileys/embarrassed.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyShadeIsMissing() {
        checkMissingImage("/smileys/shade.png");
    }

    // TODO multiple

    private void checkMissingImage(final String missingImage) {
        final ResourceLoader resourceLoader = spy(new ResourceLoader());
        when(resourceLoader.getResource(missingImage)).thenReturn(null);

        final ErrorHandler errorHandler = mock(ErrorHandler.class);

        expectedSystemExit.expectSystemExitWithStatus(1);
        expectedSystemExit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                verify(errorHandler).showCriticalError(expectedMissingImage(missingImage));
            }
        });

        new ImageLoader(errorHandler, new ResourceValidator(), resourceLoader);
    }

    private String expectedMissingImage(final String expectedMissingImages) {
        return "These images were expected, but not found:\n\n" +
                expectedMissingImages + "\n\n" +
                "KouChat will now shutdown.";
    }
}
