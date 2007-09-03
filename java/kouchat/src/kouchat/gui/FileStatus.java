
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

package kouchat.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FileStatus extends JFrame
{
	private JProgressBar filePB;
	private JLabel infoL, sendingL;
	private JButton cancelB;
	private boolean cancel;
	
	public FileStatus( String sendText, String transferText )
	{
		Container container = getContentPane();
		
		infoL = new JLabel( sendText );
		filePB = new JProgressBar( 0, 100 );
		filePB.setStringPainted( true );
		sendingL = new JLabel( transferText );
		cancelB = new JButton( "Cancel" );
		
		cancelB.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent arg0 )
			{
				cancel = true;
				dispose();
			}
		} );
		
		JPanel northP = new JPanel();
		JPanel southP = new JPanel();
		northP.add( infoL );
		southP.add( sendingL );
		southP.add( cancelB );
		
		container.add( northP, BorderLayout.NORTH );
		container.add( filePB, BorderLayout.CENTER );
		container.add( southP, BorderLayout.SOUTH );
		
		pack();
		setSize( getWidth() + 50, getHeight() );
		setTitle( "File transfer" );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setVisible( true );
	}
	
	public void setStatus( int status )
	{
		filePB.setValue( status );
	}
	
	public void setSendingLabelText( String str )
	{
		sendingL.setText( str );
	}
	
	public void close()
	{
		dispose();
	}

	public boolean isCancel()
	{
		return cancel;
	}
}
