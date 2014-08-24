
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

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.message.Messages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.ResourceLoader;
import net.usikkert.kouchat.util.ResourceValidator;
import net.usikkert.kouchat.util.Validate;

/**
 * Loads, validates and gives access to all the images used in the application.
 *
 * <p>Note: if any of the images fails to load the application will exit.</p>
 *
 * @author Christian Ihle
 */
public class ImageLoader {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ImageLoader.class.getName());

    private final ErrorHandler errorHandler;
    private final Messages messages;
    private final ResourceValidator resourceValidator;
    private final ResourceLoader resourceLoader;

    /** The smile image icon. */
    private final ImageIcon smileIcon;

    /** The sad image icon. */
    private final ImageIcon sadIcon;

    /** The tongue image icon. */
    private final ImageIcon tongueIcon;

    /** The teeth image icon. */
    private final ImageIcon teethIcon;

    /** The wink image icon. */
    private final ImageIcon winkIcon;

    /** The omg image icon. */
    private final ImageIcon omgIcon;

    /** The angry image icon. */
    private final ImageIcon angryIcon;

    /** The confused image icon. */
    private final ImageIcon confusedIcon;

    /** The cry image icon. */
    private final ImageIcon cryIcon;

    /** The embarrassed image icon. */
    private final ImageIcon embarrassedIcon;

    /** The shade image icon. */
    private final ImageIcon shadeIcon;

    /** The normal kou image icon in 16x16px. */
    private final ImageIcon kouNormal16Icon;

    /** The normal kou image icon in 22x22px. */
    private final ImageIcon kouNormal22Icon;

    /** The normal kou image icon in 24x24px. */
    private final ImageIcon kouNormal24Icon;

    /** The normal kou image icon in 32x32px. */
    private final ImageIcon kouNormal32Icon;

    /** The normal activity kou image icon in 16x16px. */
    private final ImageIcon kouNormalActivity16Icon;

    /** The normal activity kou image icon in 22x22px. */
    private final ImageIcon kouNormalActivity22Icon;

    /** The normal activity kou image icon in 24x24px. */
    private final ImageIcon kouNormalActivity24Icon;

    /** The normal activity kou image icon in 32x32px. */
    private final ImageIcon kouNormalActivity32Icon;

    /** The away kou image icon in 16x16px. */
    private final ImageIcon kouAway16Icon;

    /** The away kou image icon in 22x22px. */
    private final ImageIcon kouAway22Icon;

    /** The away kou image icon in 24x24px. */
    private final ImageIcon kouAway24Icon;

    /** The away kou image icon in 32x32px. */
    private final ImageIcon kouAway32Icon;

    /** The away activity kou image icon in 16x16px. */
    private final ImageIcon kouAwayActivity16Icon;

    /** The away activity kou image icon in 22x22px. */
    private final ImageIcon kouAwayActivity22Icon;

    /** The away activity kou image icon in 24x24px. */
    private final ImageIcon kouAwayActivity24Icon;

    /** The away activity kou image icon in 32x32px. */
    private final ImageIcon kouAwayActivity32Icon;

    /** The envelope image icon. */
    private final ImageIcon envelopeIcon;

    /** The dot image icon. */
    private final ImageIcon dotIcon;

    /**
     * Constructor. Loads and validates the images.
     *
     * @param errorHandler The error handler to use to show messages if image loading fails.
     * @param messages The messages to use in errors.
     * @param resourceValidator Validator that verifies that all the images are found.
     * @param resourceLoader Resource loader for the images.
     */
    public ImageLoader(final ErrorHandler errorHandler, final Messages messages,
                       final ResourceValidator resourceValidator, final ResourceLoader resourceLoader) {
        Validate.notNull(errorHandler, "Error handler can not be null");
        Validate.notNull(messages, "Messages can not be null");
        Validate.notNull(resourceValidator, "Resource validator can not be null");
        Validate.notNull(resourceLoader, "Resource loader can not be null");

        this.errorHandler = errorHandler;
        this.messages = messages;
        this.resourceValidator = resourceValidator;
        this.resourceLoader = resourceLoader;

        // Load resources from jar or local file system
        final URL smileURL = loadImage(Images.SMILEY_SMILE);
        final URL sadURL = loadImage(Images.SMILEY_SAD);
        final URL tongueURL = loadImage(Images.SMILEY_TONGUE);
        final URL teethURL = loadImage(Images.SMILEY_TEETH);
        final URL winkURL = loadImage(Images.SMILEY_WINK);
        final URL omgURL = loadImage(Images.SMILEY_OMG);
        final URL angryURL = loadImage(Images.SMILEY_ANGRY);
        final URL confusedURL = loadImage(Images.SMILEY_CONFUSED);
        final URL cryURL = loadImage(Images.SMILEY_CRY);
        final URL embarrassedURL = loadImage(Images.SMILEY_EMBARRASSED);
        final URL shadeURL = loadImage(Images.SMILEY_SHADE);

        final URL kouNorm16URL = loadImage(Images.ICON_KOU_NORMAL_16);
        final URL kouNorm22URL = loadImage(Images.ICON_KOU_NORMAL_22);
        final URL kouNorm24URL = loadImage(Images.ICON_KOU_NORMAL_24);
        final URL kouNorm32URL = loadImage(Images.ICON_KOU_NORMAL_32);

        final URL kouNormAct16URL = loadImage(Images.ICON_KOU_NORMAL_ACT_16);
        final URL kouNormAct22URL = loadImage(Images.ICON_KOU_NORMAL_ACT_22);
        final URL kouNormAct24URL = loadImage(Images.ICON_KOU_NORMAL_ACT_24);
        final URL kouNormAct32URL = loadImage(Images.ICON_KOU_NORMAL_ACT_32);

        final URL kouAway16URL = loadImage(Images.ICON_KOU_AWAY_16);
        final URL kouAway22URL = loadImage(Images.ICON_KOU_AWAY_22);
        final URL kouAway24URL = loadImage(Images.ICON_KOU_AWAY_24);
        final URL kouAway32URL = loadImage(Images.ICON_KOU_AWAY_32);

        final URL kouAwayAct16URL = loadImage(Images.ICON_KOU_AWAY_ACT_16);
        final URL kouAwayAct22URL = loadImage(Images.ICON_KOU_AWAY_ACT_22);
        final URL kouAwayAct24URL = loadImage(Images.ICON_KOU_AWAY_ACT_24);
        final URL kouAwayAct32URL = loadImage(Images.ICON_KOU_AWAY_ACT_32);

        final URL envelopeURL = loadImage(Images.ICON_ENVELOPE);
        final URL dotURL = loadImage(Images.ICON_DOT);

        validate();

        // Create icons from the resources
        smileIcon = new ImageIcon(smileURL);
        sadIcon = new ImageIcon(sadURL);
        tongueIcon = new ImageIcon(tongueURL);
        teethIcon = new ImageIcon(teethURL);
        winkIcon = new ImageIcon(winkURL);
        omgIcon = new ImageIcon(omgURL);
        angryIcon = new ImageIcon(angryURL);
        confusedIcon = new ImageIcon(confusedURL);
        cryIcon = new ImageIcon(cryURL);
        embarrassedIcon = new ImageIcon(embarrassedURL);
        shadeIcon = new ImageIcon(shadeURL);

        kouNormal16Icon = new ImageIcon(kouNorm16URL);
        kouNormal22Icon = new ImageIcon(kouNorm22URL);
        kouNormal24Icon = new ImageIcon(kouNorm24URL);
        kouNormal32Icon = new ImageIcon(kouNorm32URL);

        kouNormalActivity16Icon = new ImageIcon(kouNormAct16URL);
        kouNormalActivity22Icon = new ImageIcon(kouNormAct22URL);
        kouNormalActivity24Icon = new ImageIcon(kouNormAct24URL);
        kouNormalActivity32Icon = new ImageIcon(kouNormAct32URL);

        kouAway16Icon = new ImageIcon(kouAway16URL);
        kouAway22Icon = new ImageIcon(kouAway22URL);
        kouAway24Icon = new ImageIcon(kouAway24URL);
        kouAway32Icon = new ImageIcon(kouAway32URL);

        kouAwayActivity16Icon = new ImageIcon(kouAwayAct16URL);
        kouAwayActivity22Icon = new ImageIcon(kouAwayAct22URL);
        kouAwayActivity24Icon = new ImageIcon(kouAwayAct24URL);
        kouAwayActivity32Icon = new ImageIcon(kouAwayAct32URL);

        envelopeIcon = new ImageIcon(envelopeURL);
        dotIcon = new ImageIcon(dotURL);
    }

    /**
     * Loads the image to a URL, and updates the validator with the result.
     * Either the image was loaded, or it was not.
     *
     * @param image The image to load, with path.
     * @return The URL to the image, or <code>null</code> if the image wasn't loaded.
     */
    private URL loadImage(final String image) {
        final URL url = resourceLoader.getResource(image);
        resourceValidator.addResource(url, image);

        return url;
    }

    /**
     * Goes through all the images, and checks if they were loaded successfully.
     * If any of the images did not load successfully then a message is shown
     * to the user, and the application exits.
     */
    private void validate() {
        final String missing = resourceValidator.validate();

        if (missing.length() > 0) {
            final String error = messages.getMessage("swing.imageLoader.criticalErrorPopup.imagesMissing", missing, Constants.APP_NAME);

            LOG.log(Level.SEVERE, error);
            errorHandler.showCriticalError(error);

            System.exit(1);
        }
    }

    /**
     * Gets the smileIcon.
     *
     * @return The smileIcon.
     */
    public ImageIcon getSmileIcon() {
        return smileIcon;
    }

    /**
     * Gets the sadIcon.
     *
     * @return The sadIcon.
     */
    public ImageIcon getSadIcon() {
        return sadIcon;
    }

    /**
     * Gets the tongueIcon.
     *
     * @return The tongueIcon.
     */
    public ImageIcon getTongueIcon() {
        return tongueIcon;
    }

    /**
     * Gets the teethIcon.
     *
     * @return The teethIcon.
     */
    public ImageIcon getTeethIcon() {
        return teethIcon;
    }

    /**
     * Gets the winkIcon.
     *
     * @return The winkIcon.
     */
    public ImageIcon getWinkIcon() {
        return winkIcon;
    }

    /**
     * Gets the omgIcon.
     *
     * @return The omgIcon.
     */
    public ImageIcon getOmgIcon() {
        return omgIcon;
    }

    /**
     * Gets the angryIcon.
     *
     * @return The angryIcon.
     */
    public ImageIcon getAngryIcon() {
        return angryIcon;
    }

    /**
     * Gets the confusedIcon.
     *
     * @return The confusedIcon.
     */
    public ImageIcon getConfusedIcon() {
        return confusedIcon;
    }

    /**
     * Gets the cryIcon.
     *
     * @return The cryIcon.
     */
    public ImageIcon getCryIcon() {
        return cryIcon;
    }

    /**
     * Gets the embarrassedIcon.
     *
     * @return The embarrassedIcon.
     */
    public ImageIcon getEmbarrassedIcon() {
        return embarrassedIcon;
    }

    /**
     * Gets the shadeIcon.
     *
     * @return The shadeIcon.
     */
    public ImageIcon getShadeIcon() {
        return shadeIcon;
    }

    /**
     * Gets the kouNormal16Icon.
     *
     * @return The kouNormal16Icon.
     */
    public ImageIcon getKouNormal16Icon() {
        return kouNormal16Icon;
    }

    /**
     * Gets the kouNormal22Icon.
     *
     * @return The kouNormal22Icon.
     */
    public ImageIcon getKouNormal22Icon() {
        return kouNormal22Icon;
    }

    /**
     * Gets the kouNormal24Icon.
     *
     * @return The kouNormal24Icon.
     */
    public ImageIcon getKouNormal24Icon() {
        return kouNormal24Icon;
    }

    /**
     * Gets the kouNormal32Icon.
     *
     * @return The kouNormal32Icon.
     */
    public ImageIcon getKouNormal32Icon() {
        return kouNormal32Icon;
    }

    /**
     * Gets the kouNormalActivity16Icon.
     *
     * @return The kouNormalActivity16Icon.
     */
    public ImageIcon getKouNormalActivity16Icon() {
        return kouNormalActivity16Icon;
    }

    /**
     * Gets the kouNormalActivity22Icon.
     *
     * @return The kouNormalActivity22Icon.
     */
    public ImageIcon getKouNormalActivity22Icon() {
        return kouNormalActivity22Icon;
    }

    /**
     * Gets the kouNormalActivity24Icon.
     *
     * @return The kouNormalActivity24Icon.
     */
    public ImageIcon getKouNormalActivity24Icon() {
        return kouNormalActivity24Icon;
    }

    /**
     * Gets the kouNormalActivity32Icon.
     *
     * @return The kouNormalActivity32Icon.
     */
    public ImageIcon getKouNormalActivity32Icon() {
        return kouNormalActivity32Icon;
    }

    /**
     * Gets the kouAway16Icon.
     *
     * @return The kouAway16Icon.
     */
    public ImageIcon getKouAway16Icon() {
        return kouAway16Icon;
    }

    /**
     * Gets the kouAway22Icon.
     *
     * @return The kouAway22Icon.
     */
    public ImageIcon getKouAway22Icon() {
        return kouAway22Icon;
    }

    /**
     * Gets the kouAway24Icon.
     *
     * @return The kouAway24Icon.
     */
    public ImageIcon getKouAway24Icon() {
        return kouAway24Icon;
    }

    /**
     * Gets the kouAway32Icon.
     *
     * @return The kouAway32Icon.
     */
    public ImageIcon getKouAway32Icon() {
        return kouAway32Icon;
    }

    /**
     * Gets the kouAwayActivity16Icon.
     *
     * @return The kouAwayActivity16Icon.
     */
    public ImageIcon getKouAwayActivity16Icon() {
        return kouAwayActivity16Icon;
    }

    /**
     * Gets the kouAwayActivity22Icon.
     *
     * @return The kouAwayActivity22Icon.
     */
    public ImageIcon getKouAwayActivity22Icon() {
        return kouAwayActivity22Icon;
    }

    /**
     * Gets the kouAwayActivity24Icon.
     *
     * @return The kouAwayActivity24Icon.
     */
    public ImageIcon getKouAwayActivity24Icon() {
        return kouAwayActivity24Icon;
    }

    /**
     * Gets the kouAwayActivity32Icon.
     *
     * @return The kouAwayActivity32Icon.
     */
    public ImageIcon getKouAwayActivity32Icon() {
        return kouAwayActivity32Icon;
    }

    /**
     * Gets the envelopeIcon.
     *
     * @return The envelopeIcon.
     */
    public ImageIcon getEnvelopeIcon() {
        return envelopeIcon;
    }

    /**
     * Gets the dotIcon.
     *
     * @return The dotIcon.
     */
    public ImageIcon getDotIcon() {
        return dotIcon;
    }
}
