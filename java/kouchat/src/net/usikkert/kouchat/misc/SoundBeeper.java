
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URL;
import java.net.URLDecoder;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.usikkert.kouchat.Constants;

/**
 * Can load an audio file, and play it.
 * 
 * @author Christian Ihle
 */
public class SoundBeeper
{
	private static Logger log = Logger.getLogger( SoundBeeper.class.getName() );

	/**
	 * The file to play when beep() is run.
	 */
	private static final String BEEP_FILE = "/error.wav";
	
	/**
	 * The number of milliseconds to wait after
	 * beeping before closing.
	 */
	private static final int WAIT_PERIOD = 5000;

	private Clip audioClip;
	private File audioFile;
	private Settings settings;
	private ErrorHandler errorHandler;
	private Thread closeTimer;
	private long closeTime;

	/**
	 * Default constructor.
	 */
	public SoundBeeper()
	{
		settings = Settings.getSettings();
		errorHandler = ErrorHandler.getErrorHandler();
	}

	/**
	 * Plays the loaded audio file if sound is enabled, and
	 * it's not already playing. If nothing has been played for
	 * 5 seconds the sound resource is released.
	 */
	public synchronized void beep()
	{
		if ( settings.isSound() )
		{
			if ( audioClip == null || !audioClip.isActive() )
			{
				if ( audioClip == null )
					open();
				else
					audioClip.setFramePosition( 0 );
				
				if ( audioClip != null )
				{
					audioClip.start();
					closeTime = System.currentTimeMillis() + WAIT_PERIOD;

					if ( closeTimer == null )
					{
						closeTimer = new Thread( new CloseTimer() );
						closeTimer.start();
					}
				}
				
				else
					log.log( Level.SEVERE, "Audio clip missing..." );
			}
		}
	}
	
	/**
	 * Loads an audio file.
	 */
	public void loadAudioFile()
	{
		URL url = getClass().getResource( BEEP_FILE );
		
		if ( url != null )
		{
			try
			{
				audioFile = new File( URLDecoder.decode( url.getFile(), Constants.NETWORK_CHARSET ) );
			}
			
			catch ( UnsupportedEncodingException e )
			{
				log.log( Level.SEVERE, "UnsupportedEncodingException: " + e.getMessage() );
			}
		}
		
		if ( audioFile == null || !audioFile.exists() )
		{
			log.log( Level.SEVERE, "Audio file not found: " + BEEP_FILE );
			settings.setSound( false );
			errorHandler.showError( "Could not initialize the sound..." +
					"\nAudio file not found: " + BEEP_FILE );
		}
	}

	/**
	 * Opens an audio file, and reserves the resources needed for playback.
	 */
	public void open()
	{
		AudioInputStream stream = null;

		if ( audioFile == null )
			loadAudioFile();
		
		if ( audioFile != null )
		{
			try
			{
				stream = AudioSystem.getAudioInputStream( new FileInputStream( audioFile ) );
				AudioFormat format = stream.getFormat();
				DataLine.Info info = new DataLine.Info( Clip.class, format );

				if ( AudioSystem.isLineSupported( info ) )
				{
					audioClip = (Clip) AudioSystem.getLine( info );
					audioClip.open( stream );
				}
			}

			catch ( UnsupportedAudioFileException e )
			{
				log.log( Level.SEVERE, "UnsupportedAudioFileException: " + e.getMessage() );
				settings.setSound( false );
				errorHandler.showError( "Could not initialize the sound..." +
						"\nUnsupported file format: " + audioFile.getName() );
			}

			catch ( IOException e )
			{
				log.log( Level.SEVERE, "IOException: " + e.getMessage() );
				settings.setSound( false );
				errorHandler.showError( "Could not initialize the sound..." +
						"\nAudio file could not be opened: " + audioFile.getName() );
			}

			catch ( LineUnavailableException e )
			{
				log.log( Level.WARNING, "LineUnavailableException: " + e.getMessage() );
			}

			finally
			{
				if ( stream != null )
				{
					try
					{
						stream.close();
					}

					catch ( IOException e )
					{
						log.log( Level.WARNING, e.getMessage() );
					}
				}
			}
		}
	}
	
	/**
	 * Closes the audio file and frees the resources used.
	 */
	public void close()
	{
		if ( audioClip != null )
		{
			audioClip.flush();
			audioClip.close();
			audioClip = null;
		}
	}
	
	/**
	 * A simple thread used for freeing sound resources when finished.
	 * 
	 * @author Christian Ihle
	 */
	private class CloseTimer implements Runnable
	{
		@Override
		public void run()
		{
			while ( System.currentTimeMillis() < closeTime )
			{
				try
				{
					Thread.sleep( 1000 );
				}
				
				catch ( InterruptedException e )
				{
					log.log( Level.WARNING, e.getMessage() );
				}
			}
			
			close();
			closeTimer = null;
		}
	}
}
