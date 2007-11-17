
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

package net.usikkert.kouchat.ui.swing;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;

/**
 * This listener adds support for opening a url in a browser
 * by clicking on a link. The mouse cursor will also change when
 * hovering over a link.
 * 
 * @author Christian Ihle
 */
public class URLMouseListener implements MouseListener, MouseMotionListener
{
	private static final Logger log = Logger.getLogger( URLMouseListener.class.getName() );

	private Cursor handCursor;
	private Settings settings;
	private ErrorHandler errorHandler;
	private JTextPane textPane;
	private StyledDocument doc;

	/**
	 * Constructor.
	 * 
	 * @param textPane The text pane this listener is registered to.
	 */
	public URLMouseListener( JTextPane textPane )
	{
		this.textPane = textPane;

		doc = textPane.getStyledDocument();
		handCursor = new Cursor( Cursor.HAND_CURSOR );
		settings = Settings.getSettings();
		errorHandler = ErrorHandler.getErrorHandler();
	}

	@Override
	public void mouseDragged( MouseEvent e ) {}

	/**
	 * Updates the mouse cursor when hovering over a link.
	 */
	@Override
	public void mouseMoved( MouseEvent e )
	{
		int mousePos = textPane.viewToModel( e.getPoint() );

		AttributeSet attr = doc.getCharacterElement( mousePos ).getAttributes();

		if ( StyleConstants.isUnderline( attr ) )
		{
			if ( textPane.getCursor() != handCursor )
				textPane.setCursor( handCursor );
		}

		else
		{
			if ( textPane.getCursor() == handCursor )
				textPane.setCursor( null );
		}
	}

	@Override
	public void mouseClicked( MouseEvent e ) {}

	@Override
	public void mouseEntered( MouseEvent e ) {}

	@Override
	public void mouseExited( MouseEvent e ) {}

	@Override
	public void mousePressed( MouseEvent e ) {}

	/**
	 * Opens a link in the browser. If no browser is
	 * configured in the settings, the systems default browser is tried.
	 */
	@Override
	public void mouseReleased( MouseEvent e )
	{
		if ( e.getButton() == MouseEvent.BUTTON1 )
		{
			int clickPos = textPane.viewToModel( e.getPoint() );

			AttributeSet attr = doc.getCharacterElement( clickPos ).getAttributes();

			if ( StyleConstants.isUnderline( attr ) )
			{
				Object obj = attr.getAttribute( URLDocumentFilter.URL_ATTRIBUTE );

				if ( obj != null )
				{
					final String url = obj.toString();

					SwingUtilities.invokeLater( new Runnable()
					{
						@Override
						public void run()
						{
							String browser = settings.getBrowser();

							// The default is to use the browser in the settings.
							if ( browser != null && browser.trim().length() > 0  )
							{
								try
								{
									Runtime.getRuntime().exec( browser + " " + url );
								}

								catch ( IOException e )
								{
									log.log( Level.WARNING, e.toString() );
									errorHandler.showError( "Could not open the browser '" + 
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
									log.log( Level.WARNING, e.toString() );
									errorHandler.showError( "Could not open the default browser." +
											" Please set a browser in the settings." );
								}

								catch ( URISyntaxException e )
								{
									log.log( Level.WARNING, e.toString() );
								}
							}

							else
							{
								errorHandler.showError( "No browser detected." +
										" A browser can be chosen in the settings." );
							}
						}
					} );
				}
			}
		}
	}
}
