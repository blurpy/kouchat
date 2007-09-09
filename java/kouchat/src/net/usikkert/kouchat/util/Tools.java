
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

package net.usikkert.kouchat.util;

import java.text.DecimalFormat;
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
	
	public static String decimalFormat( String format, double number )
	{
		DecimalFormat formatter = new DecimalFormat( format );
		return formatter.format( number );
	}
	
	public static boolean isValidNick( String nick )
	{
		if ( nick == null || nick.trim().equals( "" ) || nick.length() > 10 )
			return false;
		else
			return true;
	}
	
	public static String byteToString( long bytes )
	{
		String size = "";
		double kbSize = bytes / 1024.0;
		
		if ( kbSize > 1024 )
		{
			kbSize /= 1024;
			size = decimalFormat( "0.00", kbSize ) + "MB";
		}
		
		else
		{
			size = decimalFormat( "0.00", kbSize ) + "KB";
		}
		
		return size;
	}
}
