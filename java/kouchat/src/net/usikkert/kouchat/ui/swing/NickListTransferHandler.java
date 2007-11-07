
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.TransferHandler;

/**
 * This takes care of drag and drop of files in the nick list.
 * When a file is dropped the mediator opens the file.
 * 
 * @author Christian Ihle
 */
public class NickListTransferHandler extends TransferHandler
{
	private static final Logger log = Logger.getLogger( NickListTransferHandler.class.getName() );
	private static final long serialVersionUID = 1L;
	
	private Mediator mediator;
	
	/**
	 * Sets the mediator to use for opening the dropped file.
	 * 
	 * @param mediator The mediator to use.
	 */
	public void setMediator( Mediator mediator )
	{
		this.mediator = mediator;
	}

	/**
	 * Checks to see if the dropped data is a URI.
	 * Returns false if the data is of any other type.
	 */
	@Override
	public boolean canImport( TransferSupport support )
	{
		DataFlavor[] flavors = support.getDataFlavors();
		
		for ( int i = 0; i < flavors.length; i++ )
		{
			if ( flavors[i].getSubType().equals( "uri-list" ) )
				return true;
		}
		
		return false;
	}

	/**
	 * Double checks to see if the data is of the correct type,
	 * and then tries to create a file object to send to the mediator.
	 */
	@Override
	public boolean importData( TransferSupport support )
	{
		if ( canImport( support ) )
		{
			try
			{
				Object data = support.getTransferable().getTransferData( DataFlavor.stringFlavor );

				if ( data != null )
				{
					URL url = new URL( data.toString() );
					
					if ( url != null )
					{
						File file = new File( url.getFile() );
						mediator.sendFile( file );
						
						return true;
					}
				}
			}
			
			catch ( UnsupportedFlavorException e )
			{
				log.log( Level.WARNING, e.getMessage() );
			}
			
			catch ( IOException e )
			{
				log.log( Level.WARNING, e.getMessage() );
			}
		}
		
		return false;
	}
}
