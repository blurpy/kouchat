
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.ui.swing;

import javax.swing.SwingUtilities;

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
	 * Shows an error message in a non-blocking JOptionPane message box.
	 *
	 * @param errorMsg The message to show.
	 */
	@Override
	public void errorReported( final String errorMsg )
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				UITools.showErrorMessage( errorMsg, "Error" );
			}
		} );
	}

	/**
	 * Shows a critical error message in a JOptionPane message box.
	 *
	 * @param criticalErrorMsg The message to show.
	 */
	@Override
	public void criticalErrorReported( final String criticalErrorMsg )
	{
		UITools.showErrorMessage( criticalErrorMsg, "Critical Error" );
	}
}
