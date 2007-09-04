
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

package net.usikkert.kouchat.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.Nick;

public class SidePanel extends JPanel implements ActionListener, MouseListener
{
	private JPopupMenu nickMenu;
	private JMenuItem infoMI;
	private JMenuItem sendfileMI;
	private JScrollPane nickSP;
	private JList nickL;
	private NickListModel nickDLM;
	private ButtonPanel buttonP;
	private ListenerMediator listener;
	
	public SidePanel( ListenerMediator listener )
	{
		this.listener = listener;
		listener.setSideP( this );
		
		nickDLM = new NickListModel( listener.getController().getNickList() );
		
		setLayout( new BorderLayout( 2, 2 ) );
		
		nickL = new JList( nickDLM );
		nickL.setCellRenderer( new CellRenderer() );
		nickL.setFixedCellWidth( 110 );
		nickL.addMouseListener( this );
		nickSP = new JScrollPane( nickL );
		
		buttonP = new ButtonPanel( listener );
		
		add( nickSP, BorderLayout.CENTER );
		add( buttonP, BorderLayout.SOUTH );
		
		nickMenu = new JPopupMenu ();
		infoMI = new JMenuItem( "Information" );
		infoMI.addActionListener( this );
		sendfileMI = new JMenuItem( "Send file" );
		sendfileMI.addActionListener( this );
		nickMenu.add( infoMI );
		nickMenu.add( sendfileMI );
	}
	
	public Nick getSelectedNick()
	{
		return (Nick) nickL.getSelectedValue();
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == infoMI )
		{
			Nick temp = (Nick) nickDLM.getElementAt( nickL.getSelectedIndex() );
			String info = "Nick: " + temp.getNick() + "\nIP address: " + temp.getIpAddress();
			
			if ( temp.isAway() )
				info += "\nAway message: " + temp.getAwayMsg();
			
			JOptionPane.showMessageDialog( null, info, Constants.APP_NAME + " - Info", JOptionPane.INFORMATION_MESSAGE );
		}
		
		else if ( e.getSource() == sendfileMI )
		{
			listener.sendFile();
		}
	}

	@Override
	public void mouseClicked( MouseEvent e ) {}

	@Override
	public void mouseEntered( MouseEvent e ) {}

	@Override
	public void mouseExited( MouseEvent e ) {}

	@Override
	public void mousePressed( MouseEvent e )
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
	public void mouseReleased( MouseEvent e )
	{
		if ( e.getSource() == nickL )
		{
			if ( nickMenu.isPopupTrigger( e ) && nickL.getSelectedIndex() != -1 )
			{
				nickMenu.show( nickL, e.getX(), e.getY() );
			}
		}
	}
}
