
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

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import net.usikkert.kouchat.Constants;

public class SysTray implements ActionListener, MouseListener
{
	private static Logger log = Logger.getLogger( SysTray.class.getName() );
	
	private SystemTray sysTray;
	private TrayIcon trayIcon;
	private Image cow_icon_normal, cow_icon_normal_activity, cow_icon_away, cow_icon_away_activity;
	private PopupMenu menu;
	private MenuItem quitMI;
	private Mediator mediator;
	
	public SysTray( Mediator mediator )
	{
		this.mediator = mediator;
		
		cow_icon_normal = new ImageIcon( getClass().getResource( "/icons/kou_normal.png" ) ).getImage();
		cow_icon_normal_activity = new ImageIcon( getClass().getResource( "/icons/kou_normal_activity.png" ) ).getImage();
		cow_icon_away = new ImageIcon( getClass().getResource( "/icons/kou_away.png" ) ).getImage();
		cow_icon_away_activity = new ImageIcon( getClass().getResource( "/icons/kou_away_activity.png" ) ).getImage();
		
		menu = new PopupMenu();
		quitMI = new MenuItem( "Quit" );
		quitMI.addActionListener( this );
		menu.add( quitMI );
		sysTray = SystemTray.getSystemTray();
		trayIcon = new TrayIcon( cow_icon_normal, "", menu );
		trayIcon.setImageAutoSize( true );
		trayIcon.addMouseListener( this );
		trayIcon.setToolTip( Constants.APP_NAME + " v" + Constants.APP_VERSION + " - (Not connected)" );
		
		try
		{
			sysTray.add( trayIcon );
		}
		
		catch ( AWTException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
		
		mediator.setSysTray( this );
	}
	
	public void setAwayState()
	{
		trayIcon.setImage( cow_icon_away );
	}
	
	public void setAwayActivityState()
	{
		trayIcon.setImage( cow_icon_away_activity );
	}
	
	public void setNormalState()
	{
		trayIcon.setImage( cow_icon_normal );
	}
	
	public void setNormalActivityState()
	{
		trayIcon.setImage( cow_icon_normal_activity );
	}
	
	public void setToolTip( String toolTip )
	{
		trayIcon.setToolTip( toolTip );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == quitMI )
		{
			mediator.quit();
		}
	}

	@Override
	public void mouseClicked( MouseEvent e )
	{
		if ( e.getSource() == trayIcon && e.getButton() == MouseEvent.BUTTON1 )
		{
			if ( trayIcon.getImage() == cow_icon_normal_activity )
				trayIcon.setImage( cow_icon_normal );
			
			else if ( trayIcon.getImage() == cow_icon_away_activity )
				trayIcon.setImage( cow_icon_away );
			
			mediator.showWindow();
		}
	}

	@Override
	public void mouseEntered( MouseEvent arg0 ) {}

	@Override
	public void mouseExited( MouseEvent arg0 ) {}

	@Override
	public void mousePressed( MouseEvent arg0 ) {}

	@Override
	public void mouseReleased( MouseEvent arg0 ) {}
}
