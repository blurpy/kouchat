
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.usikkert.kouchat.Constants;

public class MenuBar extends JMenuBar implements ActionListener
{
	private JMenu fileMenu, toolsMI, helpMenu;
	private JMenuItem minimizeMI, quitMI, clearMI, awayMI, topicMI, settingsMI, aboutMI;
	private ListenerMediator listener;
	
	public MenuBar( ListenerMediator listener )
	{
		this.listener = listener;
		listener.setMenuBar( this );
		
		fileMenu = new JMenu( "File" );
		fileMenu.setMnemonic( 'F' );
		minimizeMI = new JMenuItem( "Minimize" );
		minimizeMI.setMnemonic( 'M' );
		minimizeMI.addActionListener( this );
		quitMI = new JMenuItem( "Quit" );
		quitMI.setMnemonic( 'Q' );
		quitMI.addActionListener( this );
		
		fileMenu.add( minimizeMI );
		fileMenu.addSeparator();
		fileMenu.add( quitMI );
		
		toolsMI = new JMenu( "Tools" );
		toolsMI.setMnemonic( 'T' );
		clearMI = new JMenuItem( "Clear chat" );
		clearMI.setMnemonic( 'C' );
		clearMI.addActionListener( this );
		awayMI = new JMenuItem( "Set away" );
		awayMI.setMnemonic( 'S' );
		awayMI.addActionListener( this );
		topicMI = new JMenuItem( "Change topic" );
		topicMI.setMnemonic( 'E' );
		topicMI.addActionListener( this );
		settingsMI = new JMenuItem( "Settings" );
		settingsMI.setMnemonic( 'S' );
		settingsMI.addActionListener( this );
		
		toolsMI.add( clearMI );
		toolsMI.add( awayMI );
		toolsMI.add( topicMI );
		toolsMI.addSeparator();
		toolsMI.add( settingsMI );
		
		helpMenu = new JMenu( "Help" );
		helpMenu.setMnemonic( 'H' );
		aboutMI = new JMenuItem( "About" );
		aboutMI.setMnemonic( 'A' );
		aboutMI.addActionListener( this );
		
		helpMenu.add( aboutMI );
		
		add( fileMenu );
		add( toolsMI );
		add( helpMenu );
	}
	
	public void setAwayState( boolean away )
	{
		settingsMI.setEnabled( !away );
		topicMI.setEnabled( !away );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == quitMI )
		{
			listener.quit();
		}
		
		else if ( e.getSource() == settingsMI )
		{
			listener.showSettings();
		}
		
		else if ( e.getSource() == minimizeMI )
		{
			listener.minimize();
		}
		
		else if ( e.getSource() == awayMI )
		{
			listener.setAway();
		}
		
		else if ( e.getSource() == topicMI )
		{
			listener.setTopic();
		}
		
		else if ( e.getSource() == clearMI )
		{
			listener.clearChat();
		}
		
		else if ( e.getSource() == aboutMI )
		{
			JOptionPane.showMessageDialog( null, Constants.APP_NAME + " v" + Constants.APP_VERSION
					+ "\n\nCopyright 2006-2007 " + Constants.AUTHOR_NAME + "\n" + Constants.AUTHOR_MAIL
					+ "\n" + Constants.AUTHOR_WEB + "\n\nSource available under the " + Constants.APP_LICENSE
					+ ".\nSee " + Constants.APP_LICENSE_FILE + " for details.", Constants.APP_NAME
					+ " - About", JOptionPane.INFORMATION_MESSAGE );
		}
	}
}
