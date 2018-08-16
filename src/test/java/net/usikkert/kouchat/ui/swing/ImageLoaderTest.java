
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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.logging.Logger;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.ui.swing.messages.SwingMessages;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

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

    private ImageLoader imageLoader;

    private SwingMessages messages;

    @Before
    public void setUp() {
        messages = new SwingMessages();

        imageLoader = new ImageLoader(mock(ErrorHandler.class), messages, new ResourceValidator(), new ResourceLoader());

        // Silence the static logger
        TestUtils.setFieldValueWithMock(imageLoader, "LOG", Logger.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new ImageLoader(null, messages, new ResourceValidator(), new ResourceLoader());
    }

    @Test
    public void constructorShouldThrowExceptionIfMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Swing messages can not be null");

        new ImageLoader(mock(ErrorHandler.class), null, new ResourceValidator(), new ResourceLoader());
    }

    @Test
    public void constructorShouldThrowExceptionIfResourceValidatorIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Resource validator can not be null");

        new ImageLoader(mock(ErrorHandler.class), messages, null, new ResourceLoader());
    }

    @Test
    public void constructorShouldThrowExceptionIfResourceLoaderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Resource loader can not be null");

        new ImageLoader(mock(ErrorHandler.class), messages, new ResourceValidator(), null);
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAway16IsMissing() {
        checkMissingImage("/icons/16x16/kou_away_16x16.png");
    }

    @Test
    public void constructorShouldThrowExceptionIfKouAway20IsMissing() {
        checkMissingImage("/icons/20x20/kou_away_20x20.png");
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
    public void constructorShouldThrowExceptionIfKouAwayActivity20IsMissing() {
        checkMissingImage("/icons/20x20/kou_away_activity_20x20.png");
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
    public void constructorShouldThrowExceptionIfKouNormal20IsMissing() {
        checkMissingImage("/icons/20x20/kou_normal_20x20.png");
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
    public void constructorShouldThrowExceptionIfKouNormalActivity20IsMissing() {
        checkMissingImage("/icons/20x20/kou_normal_activity_20x20.png");
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

    @Test
    public void constructorShouldThrowExceptionWithAllMissingImagesInMessage() {
        final ResourceLoader resourceLoader = spy(new ResourceLoader());
        when(resourceLoader.getResource("/smileys/embarrassed.png")).thenReturn(null);
        when(resourceLoader.getResource("/icons/24x24/kou_normal_activity_24x24.png")).thenReturn(null);
        when(resourceLoader.getResource("/icons/dot.png")).thenReturn(null);

        final ErrorHandler errorHandler = mock(ErrorHandler.class);

        expectedSystemExit.expectSystemExitWithStatus(1);
        expectedSystemExit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                verify(errorHandler).showCriticalError("These images were expected, but not found:\n\n" +
                        "/smileys/embarrassed.png\n" +
                        "/icons/24x24/kou_normal_activity_24x24.png\n" +
                        "/icons/dot.png\n\n" +
                        "KouChat will now shutdown.");
            }
        });

        new ImageLoader(errorHandler, messages, new ResourceValidator(), resourceLoader);
    }

    @Test
    public void constructorShouldLoadCorrectSmileys() {
        assertThat(imageLoader.getSmileIcon().getDescription(), containsString("smile.png"));
        assertThat(imageLoader.getSadIcon().getDescription(), containsString("sad.png"));
        assertThat(imageLoader.getTongueIcon().getDescription(), containsString("tongue.png"));
        assertThat(imageLoader.getTeethIcon().getDescription(), containsString("teeth.png"));
        assertThat(imageLoader.getWinkIcon().getDescription(), containsString("wink.png"));
        assertThat(imageLoader.getOmgIcon().getDescription(), containsString("omg.png"));
        assertThat(imageLoader.getAngryIcon().getDescription(), containsString("angry.png"));
        assertThat(imageLoader.getConfusedIcon().getDescription(), containsString("confused.png"));
        assertThat(imageLoader.getCryIcon().getDescription(), containsString("cry.png"));
        assertThat(imageLoader.getEmbarrassedIcon().getDescription(), containsString("embarrassed.png"));
        assertThat(imageLoader.getShadeIcon().getDescription(), containsString("shade.png"));
    }

    @Test
    public void constructorShouldLoadCorrectDotAndEnvelope() {
        assertThat(imageLoader.getDotIcon().getDescription(), containsString("dot.png"));
        assertThat(imageLoader.getEnvelopeIcon().getDescription(), containsString("envelope.png"));
    }

    @Test
    public void constructorShouldLoadCorrectKouNormalIcons() {
        assertThat(imageLoader.getKouNormal16Icon().getDescription(), containsString("kou_normal_16x16.png"));
        assertThat(imageLoader.getKouNormal20Icon().getDescription(), containsString("kou_normal_20x20.png"));
        assertThat(imageLoader.getKouNormal24Icon().getDescription(), containsString("kou_normal_24x24.png"));
        assertThat(imageLoader.getKouNormal32Icon().getDescription(), containsString("kou_normal_32x32.png"));
    }

    @Test
    public void constructorShouldLoadCorrectKouNormalActivityIcons() {
        assertThat(imageLoader.getKouNormalActivity16Icon().getDescription(), containsString("kou_normal_activity_16x16.png"));
        assertThat(imageLoader.getKouNormalActivity20Icon().getDescription(), containsString("kou_normal_activity_20x20.png"));
        assertThat(imageLoader.getKouNormalActivity24Icon().getDescription(), containsString("kou_normal_activity_24x24.png"));
        assertThat(imageLoader.getKouNormalActivity32Icon().getDescription(), containsString("kou_normal_activity_32x32.png"));
    }

    @Test
    public void constructorShouldLoadCorrectKouAwayIcons() {
        assertThat(imageLoader.getKouAway16Icon().getDescription(), containsString("kou_away_16x16.png"));
        assertThat(imageLoader.getKouAway20Icon().getDescription(), containsString("kou_away_20x20.png"));
        assertThat(imageLoader.getKouAway24Icon().getDescription(), containsString("kou_away_24x24.png"));
        assertThat(imageLoader.getKouAway32Icon().getDescription(), containsString("kou_away_32x32.png"));
    }

    @Test
    public void constructorShouldLoadCorrectKouAwayActivityIcons() {
        assertThat(imageLoader.getKouAwayActivity16Icon().getDescription(), containsString("kou_away_activity_16x16.png"));
        assertThat(imageLoader.getKouAwayActivity20Icon().getDescription(), containsString("kou_away_activity_20x20.png"));
        assertThat(imageLoader.getKouAwayActivity24Icon().getDescription(), containsString("kou_away_activity_24x24.png"));
        assertThat(imageLoader.getKouAwayActivity32Icon().getDescription(), containsString("kou_away_activity_32x32.png"));
    }

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

        new ImageLoader(errorHandler, messages, new ResourceValidator(), resourceLoader);
    }

    private String expectedMissingImage(final String expectedMissingImages) {
        return "These images were expected, but not found:\n\n" +
                expectedMissingImages + "\n\n" +
                "KouChat will now shutdown.";
    }
}
