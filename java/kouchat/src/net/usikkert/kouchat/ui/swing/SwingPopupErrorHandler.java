
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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.ErrorListener;
import net.usikkert.kouchat.misc.ErrorHandler;

/**
 * This is the implementation of the error listener for use
 * in the swing gui. When an error occurs, a message box is shown.
 * 
 * @author Christian Ihle
 */
public class SwingPopupErrorHandler implements ErrorListener
{
	/**
	 * Default constructor. Registers the class as a listener
	 * in the error handler.
	 */
	public SwingPopupErrorHandler()
	{
		ErrorHandler.getErrorHandler().addErrorListener( this );
	}
	
	/**
	 * Shows the error message in a JOptionPane message box.
	 * The message box will not block.
	 */
	@Override
	public void errorReported( final String errorMsg )
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				JOptionPane.showMessageDialog( null, errorMsg, 
						Constants.APP_NAME + " - Error", JOptionPane.ERROR_MESSAGE );
			}
		} );
	}

	/**
	 * Shows the error message in a JOptionPane message box.
	 * The message box will block.
	 */
	@Override
	public void exitErrorReported( String errorMsg )
	{
		JOptionPane.showMessageDialog( null, errorMsg, 
				Constants.APP_NAME + " - Critical Error", JOptionPane.ERROR_MESSAGE );
	}
}
