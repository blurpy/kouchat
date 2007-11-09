
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

/**
 * Shows a popup menu with copy, cut, paste and clear menu items.
 * 
 * @author Christian Ihle
 */
public class MsgPopup extends JPopupMenu implements MouseListener, ActionListener
{
	private static final long serialVersionUID = 1L;

	private JMenuItem copyMI, pasteMI, cutMI, clearMI;
	private JTextField msgTF;

	/**
	 * Constructor. Creates the menu.
	 * 
	 * @param msgTF The text field to use the popup on.
	 */
	public MsgPopup( JTextField msgTF )
	{
		this.msgTF = msgTF;

		copyMI = new JMenuItem( new DefaultEditorKit.CopyAction() );
		copyMI.setText( "Copy" );
		copyMI.setMnemonic( 'C' );
		copyMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.CTRL_MASK ) );

		cutMI = new JMenuItem( new DefaultEditorKit.CutAction() );
		cutMI.setText( "Cut" );
		cutMI.setMnemonic( 'U' );
		cutMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, KeyEvent.CTRL_MASK ) );

		pasteMI = new JMenuItem( new DefaultEditorKit.PasteAction() );
		pasteMI.setText( "Paste" );
		pasteMI.setMnemonic( 'P' );
		pasteMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, KeyEvent.CTRL_MASK ) );

		clearMI = new JMenuItem( "Clear" );
		clearMI.setMnemonic( 'L' );

		add( cutMI );
		add( copyMI );
		add( pasteMI );
		addSeparator();
		add( clearMI );

		msgTF.addMouseListener( this );
		clearMI.addActionListener( this );
	}

	@Override
	public void mouseClicked( MouseEvent e ) {}

	@Override
	public void mouseEntered( MouseEvent e ) {}

	@Override
	public void mouseExited( MouseEvent e ) {}

	@Override
	public void mousePressed( MouseEvent e ) {}

	/**
	 * Shows the popup menu if right mouse button was used.
	 */
	@Override
	public void mouseReleased( MouseEvent e )
	{
		if ( isPopupTrigger( e ) && msgTF.isEnabled() )
		{
			msgTF.requestFocusInWindow();

			if ( msgTF.getSelectedText() == null )
			{
				copyMI.setEnabled( false );
				cutMI.setEnabled( false );
			}

			else
			{
				copyMI.setEnabled( true );
				cutMI.setEnabled( true );
			}

			if ( msgTF.getText().length() > 0 )
				clearMI.setEnabled( true );
			else
				clearMI.setEnabled( false );

			show( msgTF, e.getX(), e.getY() );
		}
	}

	/**
	 * Clears the text in the text field.
	 */
	@Override
	public void actionPerformed( ActionEvent e )
	{
		msgTF.setText( "" );
	}
}
