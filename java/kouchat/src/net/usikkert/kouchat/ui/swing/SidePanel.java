
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.NickList;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.Tools;

public class SidePanel extends JPanel implements ActionListener, MouseListener, FileDropSource
{
	private static final long serialVersionUID = 1L;

	private final JPopupMenu nickMenu;
	private final JMenuItem infoMI, sendfileMI, privchatMI;
	private final JScrollPane nickSP;
	private final JList nickL;
	private final NickDTO me;
	private final FileTransferHandler fileTransferHandler;

	private NickListModel nickDLM;
	private Mediator mediator;

	public SidePanel( final ButtonPanel buttonP )
	{
		setLayout( new BorderLayout( 2, 2 ) );

		fileTransferHandler = new FileTransferHandler( this );
		nickL = new JList();
		nickL.setCellRenderer( new NickListCellRenderer() );
		nickL.addMouseListener( this );
		nickL.setTransferHandler( fileTransferHandler );
		nickL.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		nickSP = new JScrollPane( nickL );

		add( nickSP, BorderLayout.CENTER );
		add( buttonP, BorderLayout.SOUTH );

		nickMenu = new JPopupMenu();
		infoMI = new JMenuItem( "Information" );
		infoMI.setMnemonic( 'I' );
		infoMI.addActionListener( this );
		sendfileMI = new JMenuItem( "Send file" );
		sendfileMI.setMnemonic( 'S' );
		sendfileMI.addActionListener( this );
		privchatMI = new JMenuItem( "Private chat" );
		privchatMI.setMnemonic( 'P' );
		privchatMI.addActionListener( this );
		nickMenu.add( infoMI );
		nickMenu.add( sendfileMI );
		nickMenu.add( privchatMI );

		setPreferredSize( new Dimension( 114, 0 ) );
		me = Settings.getSettings().getMe();
	}

	public void setMediator( final Mediator mediator )
	{
		this.mediator = mediator;
		fileTransferHandler.setMediator( mediator );
	}

	public void setNickList( final NickList nickList )
	{
		nickDLM = new NickListModel( nickList );
		nickL.setModel( nickDLM );
	}

	@Override
	public NickDTO getUser()
	{
		return (NickDTO) nickL.getSelectedValue();
	}

	public JList getNickList()
	{
		return nickL;
	}

	@Override
	public void actionPerformed( final ActionEvent e )
	{
		if ( e.getSource() == infoMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					NickDTO user = (NickDTO) nickDLM.getElementAt( nickL.getSelectedIndex() );
					String info = "Information about " + user.getNick();

					if ( user.isAway() )
						info += " (Away)";

					info += ".\n\nIP address: " + user.getIpAddress()
							+ "\nClient: " + user.getClient()
							+ "\nOperating System: " + user.getOperatingSystem()
							+ "\n\nOnline: " + Tools.howLongFromNow( user.getLogonTime() );

					if ( user.isAway() )
						info += "\nAway message: " + user.getAwayMsg();

					JOptionPane.showMessageDialog( null, info, Constants.APP_NAME + " - Info", JOptionPane.INFORMATION_MESSAGE );
				}
			} );
		}

		else if ( e.getSource() == sendfileMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.sendFile( getUser(), null );
				}
			} );
		}

		else if ( e.getSource() == privchatMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					NickDTO user = (NickDTO) nickDLM.getElementAt( nickL.getSelectedIndex() );
					mediator.showPrivChat( user );
				}
			} );
		}
	}

	@Override
	public void mouseClicked( final MouseEvent e )
	{

	}

	@Override
	public void mouseEntered( final MouseEvent e )
	{

	}

	@Override
	public void mouseExited( final MouseEvent e )
	{

	}

	@Override
	public void mousePressed( final MouseEvent e )
	{
		if ( e.getSource() == nickL )
		{
			Point p = e.getPoint();
			int index = nickL.locationToIndex( p );

			if ( index != -1 )
			{
				Rectangle r = nickL.getCellBounds( index, index );

				if ( r.x <= p.x && p.x <= r.x + r.width && r.y <= p.y && p.y <= r.y + r.height )
				{
					nickL.setSelectedIndex( index );
				}

				else
				{
					nickL.clearSelection();
				}
			}
		}
	}

	@Override
	public void mouseReleased( final MouseEvent e )
	{
		if ( e.getSource() == nickL )
		{
			if ( nickMenu.isPopupTrigger( e ) && nickL.getSelectedIndex() != -1 )
			{
				NickDTO temp = (NickDTO) nickDLM.getElementAt( nickL.getSelectedIndex() );

				if ( temp.isMe() )
				{
					sendfileMI.setVisible( false );
					privchatMI.setVisible( false );
				}

				else if ( temp.isAway() || me.isAway() )
				{
					sendfileMI.setVisible( true );
					sendfileMI.setEnabled( false );
					privchatMI.setVisible( true );

					if ( temp.getPrivateChatPort() == 0 )
						privchatMI.setEnabled( false );
					else
						privchatMI.setEnabled( true );
				}

				else
				{
					sendfileMI.setVisible( true );
					sendfileMI.setEnabled( true );
					privchatMI.setVisible( true );

					if ( temp.getPrivateChatPort() == 0 )
						privchatMI.setEnabled( false );
					else
						privchatMI.setEnabled( true );
				}

				nickMenu.show( nickL, e.getX(), e.getY() );
			}

			else if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && nickL.getSelectedIndex() != -1 )
			{
				NickDTO user = (NickDTO) nickDLM.getElementAt( nickL.getSelectedIndex() );

				if ( user != me && user.getPrivateChatPort() != 0 )
					mediator.showPrivChat( user );
			}
		}
	}
}
