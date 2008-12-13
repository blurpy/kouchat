
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the complete right side panel of the application.
 * It consists of the user list, and the button panel.
 *
 * @author Christian Ihle
 */
public class SidePanel extends JPanel implements ActionListener, MouseListener, FileDropSource
{
	/** The standard version UID. */
	private static final long serialVersionUID = 1L;

	private final JPopupMenu userMenu;
	private final JMenuItem infoMI, sendfileMI, privchatMI;
	private final JScrollPane userSP;
	private final JList userL;
	private final User me;
	private final FileTransferHandler fileTransferHandler;

	private UserListModel userListModel;
	private Mediator mediator;

	/**
	 * Constructor. Creates the panel.
	 *
	 * @param buttonP The button panel.
	 * @param imageLoader The image loader.
	 */
	public SidePanel( final ButtonPanel buttonP, final ImageLoader imageLoader )
	{
		Validate.notNull( buttonP, "Button panel can not be null" );
		Validate.notNull( imageLoader, "Image loader can not be null" );

		setLayout( new BorderLayout( 2, 2 ) );

		fileTransferHandler = new FileTransferHandler( this );
		userL = new JList();
		userL.setCellRenderer( new UserListCellRenderer( imageLoader ) );
		userL.addMouseListener( this );
		userL.setTransferHandler( fileTransferHandler );
		userL.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		userSP = new JScrollPane( userL );

		add( userSP, BorderLayout.CENTER );
		add( buttonP, BorderLayout.SOUTH );

		userMenu = new JPopupMenu();
		infoMI = new JMenuItem( "Information" );
		infoMI.setMnemonic( 'I' );
		infoMI.addActionListener( this );
		sendfileMI = new JMenuItem( "Send file" );
		sendfileMI.setMnemonic( 'S' );
		sendfileMI.addActionListener( this );
		privchatMI = new JMenuItem( "Private chat" );
		privchatMI.setMnemonic( 'P' );
		privchatMI.addActionListener( this );
		userMenu.add( infoMI );
		userMenu.add( sendfileMI );
		userMenu.add( privchatMI );

		setPreferredSize( new Dimension( 114, 0 ) );
		me = Settings.getSettings().getMe();
	}

	public void setMediator( final Mediator mediator )
	{
		this.mediator = mediator;
		fileTransferHandler.setMediator( mediator );
	}

	public void setUserList( final UserList userList )
	{
		userListModel = new UserListModel( userList );
		userL.setModel( userListModel );
	}

	@Override
	public User getUser()
	{
		return (User) userL.getSelectedValue();
	}

	public JList getUserList()
	{
		return userL;
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
					User user = userListModel.getElementAt( userL.getSelectedIndex() );
					String info = "Information about " + user.getNick();

					if ( user.isAway() )
						info += " (Away)";

					info += ".\n\nIP address: " + user.getIpAddress()
							+ "\nClient: " + user.getClient()
							+ "\nOperating System: " + user.getOperatingSystem()
							+ "\n\nOnline: " + Tools.howLongFromNow( user.getLogonTime() );

					if ( user.isAway() )
						info += "\nAway message: " + user.getAwayMsg();

					UITools.showInfoMessage( info, "Info" );
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
					User user = userListModel.getElementAt( userL.getSelectedIndex() );
					mediator.showPrivChat( user );
				}
			} );
		}
	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked( final MouseEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseEntered( final MouseEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseExited( final MouseEvent e )
	{

	}

	@Override
	public void mousePressed( final MouseEvent e )
	{
		if ( e.getSource() == userL )
		{
			Point p = e.getPoint();
			int index = userL.locationToIndex( p );

			if ( index != -1 )
			{
				Rectangle r = userL.getCellBounds( index, index );

				if ( r.x <= p.x && p.x <= r.x + r.width && r.y <= p.y && p.y <= r.y + r.height )
				{
					userL.setSelectedIndex( index );
				}

				else
				{
					userL.clearSelection();
				}
			}
		}
	}

	@Override
	public void mouseReleased( final MouseEvent e )
	{
		if ( e.getSource() == userL )
		{
			if ( userMenu.isPopupTrigger( e ) && userL.getSelectedIndex() != -1 )
			{
				User temp = userListModel.getElementAt( userL.getSelectedIndex() );

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

				userMenu.show( userL, e.getX(), e.getY() );
			}

			else if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && userL.getSelectedIndex() != -1 )
			{
				User user = userListModel.getElementAt( userL.getSelectedIndex() );

				if ( user != me && user.getPrivateChatPort() != 0 )
					mediator.showPrivChat( user );
			}
		}
	}
}
