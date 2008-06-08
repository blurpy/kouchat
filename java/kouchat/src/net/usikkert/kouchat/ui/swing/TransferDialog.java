
/***************************************************************************
 *   Copyright 2006-2008 by Christian Ihle                                 *
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the dialog window for file transfers in the swing user interface.
 *
 * @author Christian Ihle
 */
public class TransferDialog extends JDialog implements FileTransferListener, ActionListener
{
	private static final long serialVersionUID = 1L;

	private final JButton cancelB;
	private final JLabel file1L, file2L, dest1L, dest2L, trans1L, trans2L, source1L, source2L, status1L, status2L;
	private final JProgressBar filePB;
	private final FileTransfer fileTransfer;
	private final Mediator mediator;
	private String fileSize;

	/**
	 * Constructor. Initializes components and registers this dialog
	 * as a listener on the file transfer object.
	 *
	 * @param mediator The mediator.
	 * @param fileTransfer The file transfer object this dialog is showing the state of.
	 */
	public TransferDialog( final Mediator mediator, final FileTransfer fileTransfer )
	{
		Validate.notNull( mediator, "Mediator can not be null" );
		Validate.notNull( fileTransfer, "File transfer can not be null" );
		this.mediator = mediator;
		this.fileTransfer = fileTransfer;

		cancelB = new JButton( "Cancel" );
		cancelB.addActionListener( this );
		filePB = new JProgressBar( 0, 100 );
		filePB.setStringPainted( true );
		trans1L = new JLabel( "Transferred:" );
		trans2L = new JLabel( "0KB of 0KB at 0KB/s" );
		file1L = new JLabel( "Filename:" );
		file2L = new JLabel( "(No file)" );
		status1L = new JLabel( "Status:" );
		status2L = new JLabel( "Waiting..." );
		source1L = new JLabel( "Source:" );
		source2L = new JLabel( "Source (No IP)" );
		dest1L = new JLabel( "Destination:" );
		dest2L = new JLabel( "Destination (No IP)" );

		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		setTitle( Constants.APP_NAME + " - File transfer" );
		setResizable( false );
		setIconImage( new ImageIcon( getClass().getResource( Constants.APP_ICON ) ).getImage() );
		getRootPane().setDefaultButton( cancelB );

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
		setVisible( true );
		fileTransfer.registerListener( this );
	}

	/**
	 * Changes the button text on the cancel button.
	 *
	 * @param text The new text on the button.
	 */
	public void setCancelButtonText( final String text )
	{
		cancelB.setText( text );
	}

	/**
	 * Gets the button text on the cancel button.
	 *
	 * @return The button text.
	 */
	public String getCancelButtonText()
	{
		return cancelB.getText();
	}

	/**
	 * Gets the file transfer object this dialog is listening to.
	 *
	 * @return The file transfer object.
	 */
	public FileTransfer getFileTransfer()
	{
		return fileTransfer;
	}

	/**
	 * Listener for the cancel/close button.
	 *
	 * <p>Cancels the file transfer, or closes the dialog window if
	 * it's done transferring.</p>
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed( final ActionEvent e )
	{
		mediator.transferCancelled( this );
	}

	/**
	 * This method is called from the file transfer object when
	 * the file transfer was completed successfully.
	 */
	@Override
	public void statusCompleted()
	{
		status2L.setForeground( new Color( 0, 176, 0 ) );

		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
			status2L.setText( "File successfully received" );
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			status2L.setText( "File successfully sent" );

		cancelB.setText( "Close" );
	}

	/**
	 * This method is called from the file transfer object when
	 * it is ready to connect.
	 */
	@Override
	public void statusConnecting()
	{
		status2L.setText( "Connecting..." );
	}

	/**
	 * This method is called from the file transfer object when
	 * a file transfer was canceled or failed somehow.
	 */
	@Override
	public void statusFailed()
	{
		status2L.setForeground( Color.RED );

		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
			status2L.setText( "Failed to receive file" );
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			status2L.setText( "Failed to send file" );

		cancelB.setText( "Close" );
	}

	/**
	 * This method is called from the file transfer object when
	 * the connection was successful and the transfer is in progress.
	 */
	@Override
	public void statusTransferring()
	{
		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
			status2L.setText( "Receiving..." );
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			status2L.setText( "Sending..." );
	}

	/**
	 * This method is called from the file transfer object when
	 * this dialog registers as a listener. Nothing is happening
	 * with the file transfer, but the necessary information to
	 * initialize the dialog fields are ready.
	 */
	@Override
	public void statusWaiting()
	{
		NickDTO me = Settings.getSettings().getMe();
		NickDTO other = fileTransfer.getNick();
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

		if ( fileName.length() >= 42 )
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

		trans2L.setText( "0KB of " + fileSize + " at 0KB/s" );
		filePB.setValue( 0 );
	}

	/**
	 * This method is called from the file transfer object when
	 * it's time to update the status of the file transfer.
	 * This happens several times while the file transfer is
	 * in progress.
	 */
	@Override
	public void transferUpdate()
	{
		trans2L.setText( Tools.byteToString( fileTransfer.getTransferred() ) + " of " + fileSize + " at "
				+ Tools.byteToString( fileTransfer.getSpeed() ) + "/s" );
		filePB.setValue( fileTransfer.getPercent() );
	}
}
