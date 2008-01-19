
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.util.Tools;

public final class Settings
{
	private static final Logger LOG = Logger.getLogger( Settings.class.getName() );
	private static final String FILENAME = Constants.APP_FOLDER + "kouchat.ini";
	private static final Settings SETTINGS = new Settings();

	private final NickDTO me;
	private final List<SettingsListener> listeners;
	private final ErrorHandler errorHandler;

	// The settings
	private int ownColor, sysColor;
	private boolean sound, logging, debug, nativeLnF;
	private String browser;

	private Settings()
	{
		int code = 10000000 + (int) ( Math.random() * 9999999 );

		me = new NickDTO( "" + code, code );
		me.setMe( true );
		me.setLastIdle( System.currentTimeMillis() );
		me.setLogonTime( System.currentTimeMillis() );
		me.setOperatingSystem( System.getProperty( "os.name" ) );
		me.setClient( Constants.APP_NAME + " v" + Constants.APP_VERSION
				+ " " + System.getProperty( Constants.PROPERTY_CLIENT_UI ) );

		listeners = new ArrayList<SettingsListener>();
		errorHandler = ErrorHandler.getErrorHandler();
		browser = "";
		ownColor = -15987646;
		sysColor = -16759040;
		sound = true;
		nativeLnF = true;

		loadSettings();
	}

	public static Settings getSettings()
	{
		return SETTINGS;
	}

	public void saveSettings()
	{
		FileWriter fileWriter = null;
		BufferedWriter buffWriter = null;

		File appFolder = new File( Constants.APP_FOLDER );

		if ( !appFolder.exists() )
			appFolder.mkdir();

		try
		{
			fileWriter = new FileWriter( FILENAME );
			buffWriter = new BufferedWriter( fileWriter );

			buffWriter.write( "nick=" + me.getNick() );
			buffWriter.newLine();
			buffWriter.write( "owncolor=" + ownColor );
			buffWriter.newLine();
			buffWriter.write( "syscolor=" + sysColor );
			buffWriter.newLine();
			buffWriter.write( "logging=" + logging );
			buffWriter.newLine();
			buffWriter.write( "sound=" + sound );
			buffWriter.newLine();
			buffWriter.write( "debug=" + debug );
			buffWriter.newLine();
			// Properties does not support loading back slash, so replace with forward slash
			buffWriter.write( "browser=" + browser.replaceAll( "\\\\", "/" ) );
			buffWriter.newLine();
			buffWriter.write( "nativelnf=" + nativeLnF );
			buffWriter.newLine();
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString() );
			errorHandler.showError( "Settings could not be saved:\n " + e );
		}

		finally
		{
			try
			{
				if ( buffWriter != null )
					buffWriter.flush();
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}

			try
			{
				if ( fileWriter != null )
					fileWriter.flush();
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}

			try
			{
				if ( buffWriter != null )
					buffWriter.close();
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}

			try
			{
				if ( fileWriter != null )
					fileWriter.close();
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}
		}
	}

	private void loadSettings()
	{
		FileInputStream fileStream = null;

		try
		{
			Properties fileContents = new Properties();
			fileStream = new FileInputStream( FILENAME );
			fileContents.load( fileStream );

			String tmpNick = fileContents.getProperty( "nick" );

			if ( tmpNick != null && Tools.isValidNick( tmpNick ) )
			{
				me.setNick( tmpNick.trim() );
			}

			try
			{
				ownColor = Integer.parseInt( fileContents.getProperty( "owncolor" ) );
			}

			catch ( final NumberFormatException e )
			{
				LOG.log( Level.WARNING, "Could not read setting for owncolor.." );
			}

			try
			{
				sysColor = Integer.parseInt( fileContents.getProperty( "syscolor" ) );
			}

			catch ( final NumberFormatException e )
			{
				LOG.log( Level.WARNING, "Could not read setting for syscolor.." );
			}

			logging = Boolean.valueOf( fileContents.getProperty( "logging" ) );
			debug = Boolean.valueOf( fileContents.getProperty( "debug" ) );
			browser = fileContents.getProperty( "browser" );

			if ( fileContents.getProperty( "sound" ) != null )
				sound = Boolean.valueOf( fileContents.getProperty( "sound" ) );

			if ( fileContents.getProperty( "nativelnf" ) != null )
				nativeLnF = Boolean.valueOf( fileContents.getProperty( "nativelnf" ) );
		}

		catch ( final FileNotFoundException e )
		{
			LOG.log( Level.WARNING, "Could not find " + FILENAME + ", using default settings." );
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		finally
		{
			try
			{
				if ( fileStream != null )
					fileStream.close();
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}
		}
	}

	public NickDTO getMe()
	{
		return me;
	}

	public int getOwnColor()
	{
		return ownColor;
	}

	public void setOwnColor( final int ownColor )
	{
		if ( this.ownColor != ownColor )
		{
			this.ownColor = ownColor;
			fireSettingChanged( "ownColor" );
		}
	}

	public int getSysColor()
	{
		return sysColor;
	}

	public void setSysColor( final int sysColor )
	{
		if ( this.sysColor != sysColor )
		{
			this.sysColor = sysColor;
			fireSettingChanged( "sysColor" );
		}
	}

	public boolean isSound()
	{
		return sound;
	}

	public void setSound( final boolean sound )
	{
		if ( this.sound != sound )
		{
			this.sound = sound;
			fireSettingChanged( "sound" );
		}
	}

	public boolean isLogging()
	{
		return logging;
	}

	public void setLogging( final boolean logging )
	{
		if ( this.logging != logging )
		{
			this.logging = logging;
			fireSettingChanged( "logging" );
		}
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug( final boolean debug )
	{
		this.debug = debug;
	}

	public String getBrowser()
	{
		return browser;
	}

	public void setBrowser( final String browser )
	{
		this.browser = browser;
	}

	public boolean isNativeLnF()
	{
		return nativeLnF;
	}

	public void setNativeLnF( final boolean nativeLnF )
	{
		this.nativeLnF = nativeLnF;
	}

	private void fireSettingChanged( final String setting )
	{
		for ( SettingsListener listener : listeners )
		{
			listener.settingChanged( setting );
		}
	}

	public void addSettingsListener( final SettingsListener listener )
	{
		listeners.add( listener );
	}

	public void removeSettingsListener( final SettingsListener listener )
	{
		listeners.remove( listener );
	}
}
