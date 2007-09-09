
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.Nick;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.Tools;

public class TransferFrame extends JFrame implements FileTransferListener
{
	private JButton cancelB;
	private JLabel file1L, file2L, dest1L, dest2L, trans1L, trans2L, source1L, source2L, status1L, status2L;
	private JProgressBar filePB;
	private FileTransfer fileTransfer;
	private String fileSize;

	public TransferFrame( FileTransfer fileTransfer )
	{
		this.fileTransfer = fileTransfer;
		initComponents();
		setVisible( true );
	}

	private void initComponents()
	{
		cancelB = new JButton( "Cancel" );
		filePB = new JProgressBar( 0, 100 );
		filePB.setStringPainted( true );
		trans1L = new JLabel( "Transferred:" );
		trans2L = new JLabel( "0B of 0B" );
		file1L = new JLabel( "Filename:" );
		file2L = new JLabel( "(No file)" );
		status1L = new JLabel( "Status:" );
		status2L = new JLabel( "Waiting..." );
		source1L = new JLabel( "Source:" );
		source2L = new JLabel( "Source (No IP)" );
		dest1L = new JLabel( "Destination:" );
		dest2L = new JLabel( "Destination (No IP)" );

		cancelB.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent arg0 )
			{
				if ( cancelB.getText().equals( "Close" ) )
					dispose();
				else
					fileTransfer.cancel();
			}
		} );

		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		setTitle( "File transfer" );
		setResizable( false );

		GroupLayout layout = new GroupLayout( getContentPane() );
		getContentPane().setLayout( layout );
		
		// Layout generated in NetBeans 6
		layout.setHorizontalGroup(
				layout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addGroup( GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING )
								.addComponent( filePB, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE )
								.addGroup( layout.createSequentialGroup()
										.addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING )
												.addGroup( layout.createSequentialGroup()
														.addComponent( trans1L )
														.addPreferredGap( ComponentPlacement.RELATED )
														.addComponent( trans2L ) )
														.addComponent( file1L ) )
														.addGap( 0, 0, 0 ) )
														.addComponent( cancelB, GroupLayout.Alignment.TRAILING )
														.addGroup( layout.createSequentialGroup()
																.addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING )
																		.addComponent( dest1L )
																		.addComponent( source1L )
																		.addComponent( status1L ) )
																		.addPreferredGap( ComponentPlacement.RELATED )
																		.addGroup( layout.createParallelGroup( GroupLayout.Alignment.LEADING )
																				.addComponent( status2L )
																				.addComponent( source2L )
																				.addComponent( file2L )
																				.addComponent( dest2L ) ) ) )
																				.addContainerGap() )
		);
		
		layout.setVerticalGroup(
				layout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addGroup( layout.createSequentialGroup()
						.addContainerGap()
						.addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
								.addComponent( status1L )
								.addComponent( status2L ) )
								.addPreferredGap( ComponentPlacement.RELATED )
								.addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
										.addComponent( source1L )
										.addComponent( source2L ) )
										.addPreferredGap( ComponentPlacement.RELATED )
										.addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
												.addComponent( dest1L )
												.addComponent( dest2L ) )
												.addPreferredGap( ComponentPlacement.RELATED )
												.addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
														.addComponent( file1L )
														.addComponent( file2L ) )
														.addPreferredGap( ComponentPlacement.RELATED )
														.addComponent( filePB, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE )
														.addPreferredGap( ComponentPlacement.RELATED )
														.addGroup( layout.createParallelGroup( GroupLayout.Alignment.BASELINE )
																.addComponent( trans1L )
																.addComponent( trans2L ) )
																.addPreferredGap( ComponentPlacement.RELATED )
																.addComponent( cancelB )
																.addContainerGap() )
		);
		
		pack();
	}              

	@Override
	public void statusCompleted()
	{
		status2L.setForeground( new Color( 0, 176, 0 ) );
		
		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
			status2L.setText( "Receiving complete..." );
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			status2L.setText( "Sending complete..." );
		
		cancelB.setText( "Close" );
	}

	@Override
	public void statusConnecting()
	{
		status2L.setText( "Connecting..." );
	}

	@Override
	public void statusFailed()
	{
		status2L.setForeground( Color.RED );
		
		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
			status2L.setText( "Receiving failed..." );
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			status2L.setText( "Sending failed..." );
		
		cancelB.setText( "Close" );
	}

	@Override
	public void statusTransfering()
	{
		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
			status2L.setText( "Receiving..." );
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			status2L.setText( "Sending..." );
	}

	@Override
	public void statusWaiting()
	{
		Nick me = Settings.getSettings().getNick();
		Nick other = fileTransfer.getNick();
		fileSize = Tools.byteToString( fileTransfer.getFileSize() );
		
		status2L.setText( "Waiting..." );
		
		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
		{
			source2L.setText( other.getNick() + " (" + other.getIpAddress() + ")" );
			dest2L.setText( me.getNick() + " (" + me.getIpAddress() + ")" );
		}
		
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
		{
			dest2L.setText( other.getNick() + " (" + other.getIpAddress() + ")" );
			source2L.setText( me.getNick() + " (" + me.getIpAddress() + ")" );
		}
		
		String fileName = fileTransfer.getFileName();
		
		if ( fileName.length() >=42 )
		{
			String shortName = fileName.substring( 0, 40 ) + "...";
			file2L.setText( shortName );
			file2L.setToolTipText( fileName );
		}
		
		else
		{
			file2L.setText( fileName );
			file2L.setToolTipText( null );
		}
		
		file2L.setToolTipText( fileTransfer.getFileName() );
		trans2L.setText( "0B of " + fileSize );
		filePB.setValue( 0 );
	}

	@Override
	public void transferUpdate()
	{
		trans2L.setText( Tools.byteToString( fileTransfer.getTransferred() ) + " of " + fileSize );
		filePB.setValue( fileTransfer.getPercent() );
	}
}
