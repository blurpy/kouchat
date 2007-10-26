
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

package net.usikkert.kouchat.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.util.Tools;

/**
 * This is a simple logger. Creates a new unique log file for each time open()
 * is called.
 * 
 * @author Christian Ihle
 */
public class ChatLogger implements Observer
{
	/**
	 * The folder where log files are saved.
	 */
	private static final String LOG_FOLDER = "log";

	/**
	 * The name of the log file. Uses date, time, and milliseconds to make sure
	 * it is unique.
	 */
	private static final String LOG_FILE = "kouchat-" + Tools.dateToString( null, "yyyy.MM.dd-HH.mm.ss-SSS" ) + ".log";

	private static Logger log = Logger.getLogger( ChatLogger.class.getName() );
	
	private Settings settings;
	private BufferedWriter writer;
	private boolean open;

	/**
	 * Default constructor. Adds a shutdown hook to make sure the log file
	 * is closed on shutdown.
	 */
	public ChatLogger()
	{
		settings = Settings.getSettings();
		settings.addObserver( this );
		
		if ( settings.isLogging() )
		{
			open();
		}
		
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			public void run()
			{
				close();
			}
		} );
	}

	/**
	 * Opens a new log file for writing.
	 */
	public void open()
	{
		close();

		try
		{
			File logdir = new File( LOG_FOLDER );

			if ( !logdir.exists() )
				logdir.mkdir();

			writer = new BufferedWriter( new FileWriter( LOG_FOLDER + File.separator + LOG_FILE ) );
			open = true;
		}

		catch ( IOException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	/**
	 * Flushed and closes the current open log file.
	 */
	public void close()
	{
		if ( open )
		{
			try
			{
				writer.flush();
				writer.close();
			}

			catch ( IOException e )
			{
				log.log( Level.SEVERE, e.getMessage(), e );
			}

			finally
			{
				open = false;
			}
		}
	}

	/**
	 * Adds a new line of text to the current open log file, if any.
	 * 
	 * @param line The line of text to add to the log.
	 */
	public void append( String line )
	{
		if ( open )
		{
			try
			{
				writer.append( line );
				writer.newLine();
				writer.flush();
			}

			catch ( IOException e )
			{
				log.log( Level.SEVERE, e.getMessage(), e );
				close();
			}
		}
	}

	/**
	 * Returns if a log file is opened for writing or not.
	 * 
	 * @return True if a log file is open.
	 */
	public boolean isOpen()
	{
		return open;
	}

	/**
	 * Opens or closes the log file when the logging setting is changed.
	 */
	@Override
	public void update( Observable obs, Object arg )
	{
		if ( arg.equals( "logging" ) )
		{
			if ( settings.isLogging() )
			{
				if ( !isOpen() )
				{
					open();
				}
			}
			
			else
			{
				close();
			}
		}
	}
}
