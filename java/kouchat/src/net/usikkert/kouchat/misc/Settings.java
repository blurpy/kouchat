
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Properties;

import net.usikkert.kouchat.util.Tools;

public class Settings
{
	private static final String FILENAME = System.getProperty( "user.home" ) + System.getProperty( "file.separator" ) + ".kouchat.ini";
	private static final Settings settings = new Settings();
	
	private Nick nick;
	private int ownColor, sysColor;
	
	private Settings()
	{
		int code = 10000000 + (int) ( Math.random() * 9999999 );
		nick = new Nick( "" + code, code );
		nick.setMe( true );
		
		loadSettings();
	}
	
	public static Settings getSettings()
	{
		return settings;
	}
	
	public void saveSettings()
	{
		FileWriter fileWriter = null;
		BufferedWriter buffWriter = null;
		
		try
		{
			fileWriter = new FileWriter( FILENAME );
			buffWriter = new BufferedWriter( fileWriter );
			
			buffWriter.write( "nick=" + nick.getNick() );
			buffWriter.newLine();
			buffWriter.write( "owncolor=" + ownColor );
			buffWriter.newLine();
			buffWriter.write( "syscolor=" + sysColor );
		}
		
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if ( buffWriter != null )
					buffWriter.flush();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( fileWriter != null )
					fileWriter.flush();
			}
			
			catch ( IOException e1 ) {}
			
			try
			{
				if ( buffWriter != null )
					buffWriter.close();
			}
			
			catch ( IOException e ) {}
			
			try
			{
				if ( fileWriter != null )
					fileWriter.close();
			}
			
			catch ( IOException e ) {}
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
				nick.setNick( tmpNick.trim() );
			}
			
			try
			{
				ownColor = Integer.parseInt( fileContents.getProperty( "owncolor" ) );
			}
			
			catch ( NumberFormatException e ) {}
			
			try
			{
				sysColor = Integer.parseInt( fileContents.getProperty( "syscolor" ) );
			}
			
			catch ( NumberFormatException e ) {}
		}
		
		catch ( FileNotFoundException e )
		{
			System.err.println( "Could not find " + FILENAME + ", using default settings..." );
		}
		
		catch ( IOException e )
		{
			System.err.println( e );
		}
		
		finally
		{
			try
			{
				if ( fileStream != null )
					fileStream.close();
			}
			
			catch ( IOException e )
			{
				System.err.println( e );
			}
		}
	}

	public Nick getNick()
	{
		return nick;
	}

	public int getOwnColor()
	{
		return ownColor;
	}

	public void setOwnColor( int ownColor )
	{
		this.ownColor = ownColor;
	}

	public int getSysColor()
	{
		return sysColor;
	}

	public void setSysColor( int sysColor )
	{
		this.sysColor = sysColor;
	}
}
