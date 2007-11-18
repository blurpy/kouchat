
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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import net.usikkert.kouchat.Constants;

public class MenuBar extends JMenuBar implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JMenu fileMenu, toolsMenu, helpMenu;
	private JMenuItem minimizeMI, quitMI, clearMI, awayMI, topicMI, settingsMI, aboutMI, commandsMI;
	private Mediator mediator;

	public MenuBar()
	{
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

		toolsMenu = new JMenu( "Tools" );
		toolsMenu.setMnemonic( 'T' );
		clearMI = new JMenuItem( "Clear chat" );
		clearMI.setMnemonic( 'C' );
		clearMI.addActionListener( this );
		awayMI = new JMenuItem( "Set away" );
		awayMI.setMnemonic( 'A' );
		awayMI.addActionListener( this );
		topicMI = new JMenuItem( "Change topic" );
		topicMI.setMnemonic( 'O' );
		topicMI.addActionListener( this );
		settingsMI = new JMenuItem( "Settings" );
		settingsMI.setMnemonic( 'S' );
		settingsMI.addActionListener( this );

		toolsMenu.add( clearMI );
		toolsMenu.add( awayMI );
		toolsMenu.add( topicMI );
		toolsMenu.addSeparator();
		toolsMenu.add( settingsMI );

		helpMenu = new JMenu( "Help" );
		helpMenu.setMnemonic( 'H' );
		commandsMI = new JMenuItem( "Commands" );
		commandsMI.setMnemonic( 'C' );
		commandsMI.addActionListener( this );
		aboutMI = new JMenuItem( "About" );
		aboutMI.setMnemonic( 'A' );
		aboutMI.addActionListener( this );

		helpMenu.add( commandsMI );
		helpMenu.addSeparator();
		helpMenu.add( aboutMI );

		add( fileMenu );
		add( toolsMenu );
		add( helpMenu );
	}
	
	public void setMediator( Mediator mediator )
	{
		this.mediator = mediator;
	}

	public void setAwayState( boolean away )
	{
		settingsMI.setEnabled( !away );
		topicMI.setEnabled( !away );
	}
	
	public void disableMinimize()
	{
		minimizeMI.setEnabled( false );
	}
	
	public boolean isPopupMenuVisible()
	{
		return fileMenu.isPopupMenuVisible() || toolsMenu.isPopupMenuVisible() || helpMenu.isPopupMenuVisible();
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == quitMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.quit();
				}
			} );
		}

		else if ( e.getSource() == settingsMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.showSettings();
				}
			} );
		}

		else if ( e.getSource() == minimizeMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.minimize();
				}
			} );
		}

		else if ( e.getSource() == awayMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.setAway();
				}
			} );
		}

		else if ( e.getSource() == topicMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.setTopic();
				}
			} );
		}

		else if ( e.getSource() == clearMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.clearChat();
				}
			} );
		}

		else if ( e.getSource() == commandsMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.showCommands();
				}
			} );
		}

		else if ( e.getSource() == aboutMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					MessageDialog aboutD = new MessageDialog( null, true );
					
					aboutD.setTitle( Constants.APP_NAME + " - About" );
					aboutD.setTopText( Constants.APP_NAME + " v" + Constants.APP_VERSION );
					aboutD.setContent( "Copyright 2006-2007 by " + Constants.AUTHOR_NAME + "\n" + Constants.AUTHOR_MAIL
							+ "\n" + Constants.APP_WEB + "\n\nSource available under the " + Constants.APP_LICENSE
							+ ".\nSee " + Constants.APP_LICENSE_FILE + " for details." );
					
					aboutD.setVisible( true );
				}
			} );
		}
	}
}
