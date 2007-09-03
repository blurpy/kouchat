
package kouchat.misc;

import java.io.*;
import java.util.Properties;

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
			
			if ( tmpNick != null )
				nick.setNick( tmpNick );
			
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
