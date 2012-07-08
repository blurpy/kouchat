
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.ResourceValidator;

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

    /** The normal kou image icon. */
    private final ImageIcon kouNormal32Icon;

    /** The normal activity kou image icon. */
    private final ImageIcon kouNormalActivity32Icon;

    /** The away kou image icon. */
    private final ImageIcon kouAway32Icon;

    /** The away activity kou image icon. */
    private final ImageIcon kouAwayActivity32Icon;

    /** The envelope image icon. */
    private final ImageIcon envelopeIcon;

    /** The dot image icon. */
    private final ImageIcon dotIcon;

    /**
     * Constructor. Loads and validates the images.
     */
    public ImageLoader() {
        final ResourceValidator resourceValidator = new ResourceValidator();

        // Load resources from jar or local file system
        final URL smileURL = loadImage(resourceValidator, Images.SMILEY_SMILE);
        final URL sadURL = loadImage(resourceValidator, Images.SMILEY_SAD);
        final URL tongueURL = loadImage(resourceValidator, Images.SMILEY_TONGUE);
        final URL teethURL = loadImage(resourceValidator, Images.SMILEY_TEETH);
        final URL winkURL = loadImage(resourceValidator, Images.SMILEY_WINK);
        final URL omgURL = loadImage(resourceValidator, Images.SMILEY_OMG);
        final URL angryURL = loadImage(resourceValidator, Images.SMILEY_ANGRY);
        final URL confusedURL = loadImage(resourceValidator, Images.SMILEY_CONFUSED);
        final URL cryURL = loadImage(resourceValidator, Images.SMILEY_CRY);
        final URL embarrassedURL = loadImage(resourceValidator, Images.SMILEY_EMBARRASSED);
        final URL shadeURL = loadImage(resourceValidator, Images.SMILEY_SHADE);
        final URL kouNormURL = loadImage(resourceValidator, Images.ICON_KOU_NORMAL_32);
        final URL kouNormActURL = loadImage(resourceValidator, Images.ICON_KOU_NORMAL_ACT_32);
        final URL kouAwayURL = loadImage(resourceValidator, Images.ICON_KOU_AWAY_32);
        final URL kouAwayActURL = loadImage(resourceValidator, Images.ICON_KOU_AWAY_ACT_32);
        final URL envelopeURL = loadImage(resourceValidator, Images.ICON_ENVELOPE);
        final URL dotURL = loadImage(resourceValidator, Images.ICON_DOT);

        validate(resourceValidator);

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
        kouNormal32Icon = new ImageIcon(kouNormURL);
        kouNormalActivity32Icon = new ImageIcon(kouNormActURL);
        kouAway32Icon = new ImageIcon(kouAwayURL);
        kouAwayActivity32Icon = new ImageIcon(kouAwayActURL);
        envelopeIcon = new ImageIcon(envelopeURL);
        dotIcon = new ImageIcon(dotURL);
    }

    /**
     * Loads the image to a URL, and updates the validator with the result.
     * Either the image was loaded, or it was not.
     *
     * @param resourceValidator The validator.
     * @param image The image to load, with path.
     * @return The URL to the image, or <code>null</code> if the image wasn't loaded.
     */
    private URL loadImage(final ResourceValidator resourceValidator, final String image) {
        final URL url = getClass().getResource(image);
        resourceValidator.addResource(url, image);
        return url;
    }

    /**
     * Goes through all the images, and checks if they were loaded successfully.
     * If any of the images did not load successfully then a message is shown
     * to the user, and the application exits.
     *
     * @param resourceValidator The validator.
     */
    private void validate(final ResourceValidator resourceValidator) {
        final String missing = resourceValidator.validate();

        if (missing.length() > 0) {
            final String error = "These images were expected, but not found:\n\n" + missing + "\n\n" +
                    Constants.APP_NAME + " will now shutdown.";

            LOG.log(Level.SEVERE, error);
            ErrorHandler.getErrorHandler().showCriticalError(error);
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
     * Gets the kouNormalIcon.
     *
     * @return The kouNormalIcon.
     */
    public ImageIcon getKouNormal32Icon() {
        return kouNormal32Icon;
    }

    /**
     * Gets the kouNormalActivityIcon.
     *
     * @return The kouNormalActivityIcon.
     */
    public ImageIcon getKouNormalActivity32Icon() {
        return kouNormalActivity32Icon;
    }

    /**
     * Gets the kouAwayIcon.
     *
     * @return The kouAwayIcon.
     */
    public ImageIcon getKouAway32Icon() {
        return kouAway32Icon;
    }

    /**
     * Gets the kouAwayActivityIcon.
     *
     * @return The kouAwayActivityIcon.
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
