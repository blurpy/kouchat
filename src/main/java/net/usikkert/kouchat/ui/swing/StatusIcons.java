
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

import java.awt.Image;

import javax.swing.ImageIcon;

import net.usikkert.kouchat.util.Validate;

/**
 * Loads the different icons to use when the application is
 * in different states.
 *
 * @author Christian Ihle
 */
public class StatusIcons {

    /** User is not away, and has no new messages. */
    private final ImageIcon normalIcon;

    /** User is not away, but has new messages. */
    private final ImageIcon normalActivityIcon;

    /** User is away, but has no new messages. */
    private final ImageIcon awayIcon;

    /** User is away, and has new messages. */
    private final ImageIcon awayActivityIcon;

    /**
     * Constructor. Loads the icons.
     *
     * @param imageLoader The image loader.
     */
    public StatusIcons(final ImageLoader imageLoader) {
        this(imageLoader, StatusIconSize.SIZE_32x32);
    }

    /**
     * Constructor. Loads the icons.
     *
     * @param imageLoader The image loader.
     * @param size Size of the status icons to use.
     */
    public StatusIcons(final ImageLoader imageLoader, final StatusIconSize size) {
        Validate.notNull(imageLoader, "Image loader can not be null");

        normalIcon = chooseNormalIcon(imageLoader, size);
        normalActivityIcon = chooseNormalActivityIcon(imageLoader, size);
        awayIcon = chooseAwayIcon(imageLoader, size);
        awayActivityIcon = chooseAwayActivityIcon(imageLoader, size);
    }

    /**
     * Gets the normal icon.
     *
     * @return The normal icon.
     */
    public Image getNormalIcon() {
        return normalIcon.getImage();
    }

    /**
     * Gets the normal icon as image icon.
     *
     * @return The normal icon.
     */
    public ImageIcon getNormalIconImage() {
        return normalIcon;
    }

    /**
     * Gets the normal activity icon.
     *
     * @return The normal activity icon.
     */
    public Image getNormalActivityIcon() {
        return normalActivityIcon.getImage();
    }

    /**
     * Gets the away icon.
     *
     * @return The away icon.
     */
    public Image getAwayIcon() {
        return awayIcon.getImage();
    }

    /**
     * Gets the away activity icon.
     *
     * @return The away activity icon.
     */
    public Image getAwayActivityIcon() {
        return awayActivityIcon.getImage();
    }

    private ImageIcon chooseNormalIcon(final ImageLoader imageLoader, final StatusIconSize size) {
        switch (size) {
            case SIZE_16x16: return imageLoader.getKouNormal16Icon();
            case SIZE_20x20: return imageLoader.getKouNormal20Icon();
            case SIZE_22x22: return imageLoader.getKouNormal22Icon();
            case SIZE_24x24: return imageLoader.getKouNormal24Icon();
            case SIZE_32x32: return imageLoader.getKouNormal32Icon();
            default: throw new IllegalArgumentException("Unsupported status icon size: " + size);
        }
    }

    private ImageIcon chooseNormalActivityIcon(final ImageLoader imageLoader, final StatusIconSize size) {
        switch (size) {
            case SIZE_16x16: return imageLoader.getKouNormalActivity16Icon();
            case SIZE_20x20: return imageLoader.getKouNormalActivity20Icon();
            case SIZE_22x22: return imageLoader.getKouNormalActivity22Icon();
            case SIZE_24x24: return imageLoader.getKouNormalActivity24Icon();
            case SIZE_32x32: return imageLoader.getKouNormalActivity32Icon();
            default: throw new IllegalArgumentException("Unsupported status icon size: " + size);
        }
    }

    private ImageIcon chooseAwayIcon(final ImageLoader imageLoader, final StatusIconSize size) {
        switch (size) {
            case SIZE_16x16: return imageLoader.getKouAway16Icon();
            case SIZE_20x20: return imageLoader.getKouAway20Icon();
            case SIZE_22x22: return imageLoader.getKouAway22Icon();
            case SIZE_24x24: return imageLoader.getKouAway24Icon();
            case SIZE_32x32: return imageLoader.getKouAway32Icon();
            default: throw new IllegalArgumentException("Unsupported status icon size: " + size);
        }
    }

    private ImageIcon chooseAwayActivityIcon(final ImageLoader imageLoader, final StatusIconSize size) {
        switch (size) {
            case SIZE_16x16: return imageLoader.getKouAwayActivity16Icon();
            case SIZE_20x20: return imageLoader.getKouAwayActivity20Icon();
            case SIZE_22x22: return imageLoader.getKouAwayActivity22Icon();
            case SIZE_24x24: return imageLoader.getKouAwayActivity24Icon();
            case SIZE_32x32: return imageLoader.getKouAwayActivity32Icon();
            default: throw new IllegalArgumentException("Unsupported status icon size: " + size);
        }
    }
}
