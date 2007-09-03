
package kouchat.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tools
{
	public static String getTime()
	{
		int h = Calendar.getInstance().get( Calendar.HOUR_OF_DAY );
		int m = Calendar.getInstance().get( Calendar.MINUTE );
		int s = Calendar.getInstance().get( Calendar.SECOND );
		
		String hour = "";
		String min = "";
		String sec = "";
		
		if ( h < 10 )
			hour = "0" + h;
		else
			hour = "" + h;
		
		if ( m < 10 )
			min = "0" + m;
		else
			min = "" + m;
		
		if ( s < 10 )
			sec = "0" + s;
		else
			sec = "" + s;
		
		return "[" + hour + ":" + min + ":" + sec + "] ";
	}
	
	public static String dateToString( Date d, String format )
	{
		String date = "";
		SimpleDateFormat formatter = new SimpleDateFormat( format );
		
		if ( d == null )
			d = new Date();
		
		date = formatter.format( d );
		
		return date;
	}
	
	public static Date stringToDate( String s, String format )
	{
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat( format );
		
		try
		{
			date = formatter.parse( s );
		}
		
		catch ( ParseException e )
		{
			e.printStackTrace();
		}
		
		return date;
	}
}
