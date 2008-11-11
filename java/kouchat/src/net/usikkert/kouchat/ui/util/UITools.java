
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

package net.usikkert.kouchat.ui.util;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.Loggers;

/**
 * This is a collection of practical and reusable methods
 * for ui use.
 *
 * @author Christian Ihle
 */
public final class UITools
{
	private static final Logger LOG = Loggers.UI_LOG;
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
	public static void browse( final String url )
	{
		String browser = SETTINGS.getBrowser();
		Desktop desktop = Desktop.getDesktop();

		// The default is to use the browser in the settings.
		if ( browser != null && browser.trim().length() > 0  )
		{
			try
			{
				Runtime.getRuntime().exec( browser + " " + url );
			}

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
				ERRORHANDLER.showError( "Could not open the browser '"
						+ browser + "'. Please check the settings." );
			}
		}

		// But if no browser is set there, try opening the system default browser
		else if ( Desktop.isDesktopSupported() && desktop.isSupported( Action.BROWSE ) )
		{
			try
			{
				desktop.browse( new URI( url ) );
			}

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
				ERRORHANDLER.showError( "Could not open '" + url + "' with the default browser."
						+ " Try setting a browser in the settings." );
			}

			catch ( final URISyntaxException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}
		}

		else
		{
			ERRORHANDLER.showError( "No browser detected."
					+ " A browser can be chosen in the settings." );
		}
	}

	/**
	 * Opens a file in the registered application for the file type.
	 *
	 * <p>If this fails, {@link #browse(String)} is used as a fallback.</p>
	 *
	 * @param file A file or directory to open.
	 */
	public static void open( final File file )
	{
		Desktop desktop = Desktop.getDesktop();
		boolean desktopOpenSuccess = false;

		if ( Desktop.isDesktopSupported() && desktop.isSupported( Action.OPEN ) )
		{
			try
			{
				desktop.open( file );
				desktopOpenSuccess = true;
			}

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}
		}

		if ( !desktopOpenSuccess )
		{
			browse( file.getAbsolutePath() );
		}
	}

	/**
	 * Changes to the system Look And Feel.
	 * Ignores any exceptions, as this is not critical.
	 */
	public static void setSystemLookAndFeel()
	{
		if ( isSystemLookAndFeelSupported() )
		{
			try
			{
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			}

			catch ( final ClassNotFoundException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}

			catch ( final InstantiationException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}

			catch ( final IllegalAccessException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}

			catch ( final UnsupportedLookAndFeelException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}
		}
	}

	/**
	 * Changes to the chosen look and feel. Ignores any exceptions.
	 *
	 * @param lnfName Name of the look and feel to change to.
	 */
	public static void setLookAndFeel( final String lnfName )
	{
		try
		{
			LookAndFeelInfo lookAndFeel = getLookAndFeel( lnfName );

			if ( lookAndFeel != null )
				UIManager.setLookAndFeel( lookAndFeel.getClassName() );

		}

		catch ( final ClassNotFoundException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		catch ( final InstantiationException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		catch ( final IllegalAccessException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		catch ( final UnsupportedLookAndFeelException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}
	}

	/**
	 * Checks if the system look and feel differs
	 * from the cross platform look and feel.
	 *
	 * @return True if the system look and feel is different
	 * from the cross platform look and feel.
	 */
	public static boolean isSystemLookAndFeelSupported()
	{
		return !UIManager.getSystemLookAndFeelClassName().equals( UIManager.getCrossPlatformLookAndFeelClassName() );
	}

	/**
	 * Gets an array of the available look and feels, in a wrapper.
	 *
	 * @return All the available look and feels.
	 */
	public static LookAndFeelWrapper[] getLookAndFeels()
	{
		LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		LookAndFeelWrapper[] lookAndFeelWrappers = new LookAndFeelWrapper[lookAndFeels.length];

		for ( int i = 0; i < lookAndFeels.length; i++ )
		{
			lookAndFeelWrappers[i] = new LookAndFeelWrapper( lookAndFeels[i] );
		}

		return lookAndFeelWrappers;
	}

	/**
	 * Gets the {@link LookAndFeelInfo} found with the specified name,
	 * or null if none was found.
	 *
	 * @param lnfName The name of the look and feel to look for.
	 * @return The LookAndFeelInfo for that name.
	 */
	public static LookAndFeelInfo getLookAndFeel( final String lnfName )
	{
		LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();

		for ( LookAndFeelInfo lookAndFeelInfo : lookAndFeels )
		{
			if ( lookAndFeelInfo.getName().equals( lnfName ) )
			{
				return lookAndFeelInfo;
			}
		}

		return null;
	}

	/**
	 * Gets the width of the text in pixels with the specified font.
	 *
	 * @param text The text to check the width of.
	 * @param graphics Needed to be able to check the width.
	 * @param font The font the text uses.
	 * @return The text width, in pixels.
	 */
	public static double getTextWidth( final String text, final Graphics graphics, final Font font )
	{
		FontMetrics fm = graphics.getFontMetrics( font );
		return fm.getStringBounds( text, graphics ).getWidth();
	}
}
