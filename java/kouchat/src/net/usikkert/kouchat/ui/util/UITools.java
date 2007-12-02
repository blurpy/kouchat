
/***************************************************************************
 *   Copyright 2006-2007 by Christian Ihle                                 *
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

package net.usikkert.kouchat.ui.util;

import java.awt.Desktop;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;

/**
 * This is a collection of practical and reusable methods
 * for ui use.
 *
 * @author Christian Ihle
 */
public final class UITools
{
	private static final Logger LOG = Logger.getLogger( UITools.class.getName() );
	private static final ErrorHandler ERRORHANDLER = ErrorHandler.getErrorHandler();
	private static final Settings SETTINGS = Settings.getSettings();

	/**
	 * Private constructor. Only static methods here.
	 */
	private UITools()
	{

	}

	/**
	 * Opens a url in a browser. The first choice is taken from the settings,
	 * but if no browser i configured there, the systems default browser
	 * is tried.
	 *
	 * @param url The url to open in the browser.
	 */
	public static void browse( String url )
	{
		String browser = SETTINGS.getBrowser();

		// The default is to use the browser in the settings.
		if ( browser != null && browser.trim().length() > 0  )
		{
			try
			{
				Runtime.getRuntime().exec( browser + " " + url );
			}

			catch ( IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
				ERRORHANDLER.showError( "Could not open the browser '" +
						browser + "'. Please check the settings." );
			}
		}

		// But if no browser is set there, try opening the system default browser
		else if ( Desktop.isDesktopSupported() )
		{
			try
			{
				Desktop.getDesktop().browse( new URI( url ) );
			}

			catch ( IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
				ERRORHANDLER.showError( "Could not open '" + url + "' with the default browser." +
						" Try setting a browser in the settings." );
			}

			catch ( URISyntaxException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}
		}

		else
		{
			ERRORHANDLER.showError( "No browser detected." +
			" A browser can be chosen in the settings." );
		}
	}
}
