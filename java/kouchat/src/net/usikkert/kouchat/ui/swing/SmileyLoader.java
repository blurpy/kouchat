
/***************************************************************************
 *   Copyright 2006-2008 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouchat.ui.swing;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.Loggers;
import net.usikkert.kouchat.util.ResourceValidator;

/**
 * This class has a list of all the supported smileys.
 *
 * @author Christian Ihle
 */
public class SmileyLoader
{
	/** The logger. */
	private static final Logger LOG = Loggers.UI_LOG;

	/** The image for the <code>:)</code> smiley. */
	private static final String SMILEY_SMILE = "/smileys/smile.png";

	/** The image for the <code>:(</code> smiley. */
	private static final String SMILEY_SAD = "/smileys/sad.png";

	/** The image for the <code>:p</code> smiley. */
	private static final String SMILEY_TONGUE = "/smileys/tongue.png";

	/** The image for the <code>:D</code> smiley. */
	private static final String SMILEY_TEETH = "/smileys/teeth.png";

	/** The image for the <code>;)</code> smiley. */
	private static final String SMILEY_WINK = "/smileys/wink.png";

	/** The image for the <code>:O</code> smiley. */
	private static final String SMILEY_OMG = "/smileys/omg.png";

	/** The image for the <code>:@</code> smiley. */
	private static final String SMILEY_ANGRY = "/smileys/angry.png";

	/** The image for the <code>:S</code> smiley. */
	private static final String SMILEY_CONFUSED = "/smileys/confused.png";

	/** The image for the <code>;(</code> smiley. */
	private static final String SMILEY_CRY = "/smileys/cry.png";

	/** The image for the <code>:$</code> smiley. */
	private static final String SMILEY_EMBARASSED = "/smileys/embarassed.png";

	/** The image for the <code>8)</code> smiley. */
	private static final String SMILEY_SHADE = "/smileys/shade.png";

	/** The map linking the smiley code with the smiley image. */
	private final Map<String, ImageIcon> smileyMap;

	/**
	 * Constructor.
	 *
	 * Initializes all the smileys. If any of the smileys can't be
	 * loaded the application with exit with an error.
	 */
	public SmileyLoader()
	{
		// Load resources from jar or local file system
		URL smileURL = getClass().getResource( SMILEY_SMILE );
		URL sadURL = getClass().getResource( SMILEY_SAD );
		URL tongueURL = getClass().getResource( SMILEY_TONGUE );
		URL teethURL = getClass().getResource( SMILEY_TEETH );
		URL winkURL = getClass().getResource( SMILEY_WINK );
		URL omgURL = getClass().getResource( SMILEY_OMG );
		URL angryURL = getClass().getResource( SMILEY_ANGRY );
		URL confusedURL = getClass().getResource( SMILEY_CONFUSED );
		URL cryURL = getClass().getResource( SMILEY_CRY );
		URL embarassedURL = getClass().getResource( SMILEY_EMBARASSED );
		URL shadeURL = getClass().getResource( SMILEY_SHADE );

		// Check if all the images were found
		ResourceValidator resourceValidator = new ResourceValidator();
		resourceValidator.addResource( smileURL, SMILEY_SMILE );
		resourceValidator.addResource( sadURL, SMILEY_SAD );
		resourceValidator.addResource( tongueURL, SMILEY_TONGUE );
		resourceValidator.addResource( teethURL, SMILEY_TEETH );
		resourceValidator.addResource( winkURL, SMILEY_WINK );
		resourceValidator.addResource( omgURL, SMILEY_OMG );
		resourceValidator.addResource( angryURL, SMILEY_ANGRY );
		resourceValidator.addResource( confusedURL, SMILEY_CONFUSED );
		resourceValidator.addResource( cryURL, SMILEY_CRY );
		resourceValidator.addResource( embarassedURL, SMILEY_EMBARASSED );
		resourceValidator.addResource( shadeURL, SMILEY_SHADE );
		String missing = resourceValidator.validate();

		// If any resources are missing, then exit
		if ( missing.length() > 0 )
		{
			String error = "These smileys were expected, but not found:\n\n" + missing + "\n\n"
					+ Constants.APP_NAME + " will now shutdown.";

			LOG.log( Level.SEVERE, error );
			ErrorHandler.getErrorHandler().showCriticalError( error );
			System.exit( 1 );
		}

		// Create icons from the resources
		ImageIcon smileIcon = new ImageIcon( smileURL );
		ImageIcon sadIcon = new ImageIcon( sadURL );
		ImageIcon tongueIcon = new ImageIcon( tongueURL );
		ImageIcon teethIcon = new ImageIcon( teethURL );
		ImageIcon winkIcon = new ImageIcon( winkURL );
		ImageIcon omgIcon = new ImageIcon( omgURL );
		ImageIcon angryIcon = new ImageIcon( angryURL );
		ImageIcon confusedIcon = new ImageIcon( confusedURL );
		ImageIcon cryIcon = new ImageIcon( cryURL );
		ImageIcon embarassedIcon = new ImageIcon( embarassedURL );
		ImageIcon shadeIcon = new ImageIcon( shadeURL );

		// Map smiley codes to icons
		smileyMap = new HashMap<String, ImageIcon>();
		smileyMap.put( ":)", smileIcon );
		smileyMap.put( ":(", sadIcon );
		smileyMap.put( ":p", tongueIcon );
		smileyMap.put( ":D", teethIcon );
		smileyMap.put( ";)", winkIcon );
		smileyMap.put( ":O", omgIcon );
		smileyMap.put( ":@", angryIcon );
		smileyMap.put( ":S", confusedIcon );
		smileyMap.put( ";(", cryIcon );
		smileyMap.put( ":$", embarassedIcon );
		smileyMap.put( "8)", shadeIcon );
	}

	/**
	 * Gets the smiley with the spesified key.
	 *
	 * @param key The key for the smiley to get.
	 * @return The smiley with the spesified key.
	 */
	public ImageIcon getSmiley( final String key )
	{
		return smileyMap.get( key );
	}

	/**
	 * Gets a set of all the smiley codes.
	 *
	 * @return A set of all the smiley codes.
	 */
	public Set<String> getTextSmileys()
	{
		return smileyMap.keySet();
	}
}
