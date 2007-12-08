
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

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.usikkert.kouchat.Constants;

/**
 * A collection of static utility methods.
 *
 * @author Christian Ihle
 */
public final class Tools
{
	/**
	 * Private constructor. Only static methods here.
	 */
	private Tools()
	{

	}

	private static final Logger LOG = Logger.getLogger( Tools.class.getName() );

	/**
	 * Creates a timestamp in the format [HH:MM:SS].
	 *
	 * @return The current time.
	 */
	public static String getTime()
	{
		int h = Calendar.getInstance().get( Calendar.HOUR_OF_DAY );
		int m = Calendar.getInstance().get( Calendar.MINUTE );
		int s = Calendar.getInstance().get( Calendar.SECOND );

		return "[" + getDoubleDigit( h ) + ":" + getDoubleDigit( m ) + ":" + getDoubleDigit( s ) + "]";
	}

	/**
	 * Checks if a number is lower than 10, and creates a string with
	 * a 0 added at the start if that is the case. Useful for clocks.
	 *
	 * @param number The number to check.
	 * @return A string representation of the number.
	 */
	public static String getDoubleDigit( int number )
	{
		if ( number < 10 )
			return "0" + number;
		else
			return "" + number;
	}

	/**
	 * Converts a date to a string, in the format specified.
	 *
	 * @param d The date to convert to a string.
	 * @param format The format to get the date in.
	 * @return A converted date.
	 * @see SimpleDateFormat
	 */
	public static String dateToString( Date d, String format )
	{
		String date = "";
		SimpleDateFormat formatter = new SimpleDateFormat( format );

		if ( d == null )
			d = new Date();

		date = formatter.format( d );

		return date;
	}

	/**
	 * Converts a string into a date, from the format specified.
	 *
	 * @param s The string to convert into a date.
	 * @param format The format of the date.
	 * @return The string as a date.
	 * @see SimpleDateFormat
	 */
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
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		return date;
	}

	/**
	 * Get a decimal number as a string, in the specified format.
	 *
	 * @param format The format to get the number.
	 * @param number The number to add formatting to.
	 * @return The formatted number.
	 * @see DecimalFormat
	 */
	public static String decimalFormat( String format, double number )
	{
		DecimalFormat formatter = new DecimalFormat( format );
		return formatter.format( number );
	}

	/**
	 * Nick is valid if it consists of between 1 and 10 characters
	 * of type [a-Z], [0-9], '-' and '_'.
	 */
	public static boolean isValidNick( String nick )
	{
		if ( nick == null )
			return false;

		Pattern p = Pattern.compile( "[\\p{Alnum}[-_]]{1,10}" );
		Matcher m = p.matcher( nick );

		return m.matches();
	}

	/**
	 * Converts a number of bytes into megabytes or kilobytes,
	 * depending on the size.
	 *
	 * @param bytes The number of bytes to convert.
	 * @return A string representation of the bytes.
	 */
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

	/**
	 * Returns a string showing how long has passed from 'then' to now.
	 *
	 * @param then An earlier time.
	 * @return How long it's been since 'then'.
	 */
	public static String howLongFromNow( long then )
	{
		if ( then != 0 )
		{
			long diff = System.currentTimeMillis() - then;
			long totSec = diff / 1000;

			int oneday = 86400;
			int onehour = 3600;
			int onemin = 60;

			int days = Math.round( totSec / oneday );
			int hours = Math.round( totSec - days * oneday ) / onehour;
			int minutes = Math.round( totSec - days * oneday - hours * onehour ) / onemin;
			int seconds = Math.round( totSec - days * oneday - hours * onehour - minutes * onemin );

			return days + " days, " + getDoubleDigit( hours ) + ":" + getDoubleDigit( minutes )
					+ ":" + getDoubleDigit( seconds );
		}

		else
		{
			return "0 days, 00:00:00";
		}
	}

	/**
	 * Returns the number of bytes a String consists of.
	 *
	 * @param text The text to count the bytes in.
	 * @return Number of bytes found in the text.
	 */
	public static int getBytes( String text )
	{
		try
		{
			return text.getBytes( Constants.MESSAGE_CHARSET ).length;
		}

		catch ( UnsupportedEncodingException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
			return 0;
		}
	}
}
