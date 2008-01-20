
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

import java.awt.Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;

/**
 * Opens a text file in a 80x24 character dialog window.
 *
 * @author Christian Ihle
 */
public class TextViewerDialog extends JDialog
{
	private static final Logger LOG = Logger.getLogger( TextViewerDialog.class.getName() );
	private static final long serialVersionUID = 1L;

	private final ErrorHandler errorHandler;
	private final JTextArea viewerTA;

	/**
	 * Constructor.
	 *
	 * If the file is read properly, the dialog is shown. If not,
	 * the dialog is disposed and an error message is shown instead.
	 *
	 * @param textFile The text file to open and view.
	 * @param title The title to use for the dialog window.
	 */
	public TextViewerDialog( final String textFile, final String title )
	{
		errorHandler = ErrorHandler.getErrorHandler();

		viewerTA = new JTextArea();
		viewerTA.setFont( new Font( "Monospaced", Font.PLAIN, 12 ) );
		viewerTA.setColumns( 80 );
		viewerTA.setRows( 24 );
		viewerTA.setEditable( false );

		JScrollPane viewerScroll = new JScrollPane( viewerTA );

		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );
		panel.add( viewerScroll );
		add( panel );

		setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		setResizable( false );
		setTitle( Constants.APP_NAME + " - " + title );

		if ( openFile( textFile ) )
		{
			pack();
			setVisible( true );
		}

		else
		{
			dispose();
		}
	}

	/**
	 * Opens the text file, and adds the contents to the text area.
	 *
	 * @param textFile The text file to open.
	 * @return True if the text file was read without any problems.
	 */
	private boolean openFile( final String textFile )
	{
		boolean open = false;
		URL fileURL = getClass().getResource( "/" + textFile );

		if ( fileURL != null )
		{
			BufferedReader reader = null;

			try
			{
				reader = new BufferedReader( new InputStreamReader( fileURL.openStream() ) );

				while ( reader.ready() )
				{
					viewerTA.append( reader.readLine() + "\n" );
				}

				viewerTA.setCaretPosition( 0 );
				open = true;
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString() );
				errorHandler.showError( "Could not open " + textFile );
			}

			finally
			{
				if ( reader != null )
				{
					try
					{
						reader.close();
					}

					catch ( final IOException e )
					{
						LOG.log( Level.WARNING, "Problems closing: " + textFile );
					}
				}
			}
		}

		else
		{
			LOG.log( Level.SEVERE, "Text file not found: " + textFile );
			errorHandler.showError( "Text file not found: " + textFile );
		}

		return open;
	}
}
