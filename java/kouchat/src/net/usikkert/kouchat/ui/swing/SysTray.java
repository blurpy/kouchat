
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

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;

public class SysTray implements ActionListener, MouseListener
{
	private static final Logger log = Logger.getLogger( SysTray.class.getName() );
	private static final String IMG_KOU_NORMAL = "/icons/kou_normal.png";
	private static final String IMG_KOU_NORMAL_ACT = "/icons/kou_normal_activity.png";
	private static final String IMG_KOU_AWAY = "/icons/kou_away.png";
	private static final String IMG_KOU_AWAY_ACT = "/icons/kou_away_activity.png";

	private SystemTray sysTray;
	private TrayIcon trayIcon;
	private Image kou_icon_normal, kou_icon_normal_activity, kou_icon_away, kou_icon_away_activity;
	private PopupMenu menu;
	private MenuItem quitMI;
	private Mediator mediator;
	private ErrorHandler errorHandler;
	private boolean systemTraySupported;

	public SysTray()
	{
		errorHandler = ErrorHandler.getErrorHandler();

		if ( SystemTray.isSupported() )
		{
			URL kou_norm = getClass().getResource( IMG_KOU_NORMAL );
			URL kou_norm_act = getClass().getResource( IMG_KOU_NORMAL_ACT );
			URL kou_away = getClass().getResource( IMG_KOU_AWAY );
			URL kou_away_act = getClass().getResource( IMG_KOU_AWAY_ACT );

			if ( kou_norm == null || kou_norm_act == null || kou_away == null || kou_away_act == null )
			{
				List<String> missingList = new ArrayList<String>();
				
				if ( kou_norm == null )
					missingList.add( IMG_KOU_NORMAL );
				if ( kou_norm_act == null )
					missingList.add( IMG_KOU_NORMAL_ACT );
				if ( kou_away == null )
					missingList.add( IMG_KOU_AWAY );
				if ( kou_away_act == null )
					missingList.add( IMG_KOU_AWAY_ACT );
				
				String missing = "";
				
				for ( int i = 0; i < missingList.size(); i++ )
				{
					missing += missingList.get( i );
					
					if ( i < missingList.size() -1 )
						missing += "\n";
				}
				
				String error = "These images were expected, but not found:\n\n" + missing + "\n\n"
						+ Constants.APP_NAME + " will now shutdown and quit...";
				
				log.log( Level.SEVERE, error );
				errorHandler.showCriticalError( error );
				System.exit( 1 );
			}

			kou_icon_normal = new ImageIcon( kou_norm ).getImage();
			kou_icon_normal_activity = new ImageIcon( kou_norm_act ).getImage();
			kou_icon_away = new ImageIcon( kou_away ).getImage();
			kou_icon_away_activity = new ImageIcon( kou_away_act ).getImage();

			menu = new PopupMenu();
			quitMI = new MenuItem( "Quit" );
			quitMI.addActionListener( this );
			menu.add( quitMI );

			sysTray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon( kou_icon_normal, "", menu );
			trayIcon.setImageAutoSize( true );
			trayIcon.addMouseListener( this );
			trayIcon.setToolTip( Constants.APP_NAME + " v" + Constants.APP_VERSION + " - (Not connected)" );

			try
			{
				sysTray.add( trayIcon );
				systemTraySupported = true;
			}

			catch ( AWTException e )
			{
				// This happens if the System Tray is hidden on a system
				// that actually supports a System Tray.
				log.log( Level.SEVERE, e.toString() );
				errorHandler.showError( "System Tray is not visible. Deactivating System Tray support..." );
			}
		}

		else
		{
			String error = "System Tray is not supported. Deactivating System Tray support...";
			log.log( Level.SEVERE, error );
			errorHandler.showError( error );
		}
	}

	public boolean isSystemTraySupport()
	{
		return systemTraySupported;
	}

	public void setMediator( Mediator mediator )
	{
		this.mediator = mediator;
	}

	public void setAwayState()
	{
		if ( trayIcon != null )
		{
			if ( trayIcon.getImage() != kou_icon_away )
				trayIcon.setImage( kou_icon_away );
		}
	}

	public void setAwayActivityState()
	{
		if ( trayIcon != null )
		{
			if ( trayIcon.getImage() != kou_icon_away_activity )
				trayIcon.setImage( kou_icon_away_activity );
		}
	}

	public void setNormalState()
	{
		if ( trayIcon != null )
		{
			if ( trayIcon.getImage() != kou_icon_normal )
				trayIcon.setImage( kou_icon_normal );
		}
	}

	public void setNormalActivityState()
	{
		if ( trayIcon != null )
		{
			if ( trayIcon.getImage() != kou_icon_normal_activity )
				trayIcon.setImage( kou_icon_normal_activity );
		}
	}

	public void setToolTip( String toolTip )
	{
		if ( trayIcon != null )
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
			if ( trayIcon.getImage() == kou_icon_normal_activity )
				trayIcon.setImage( kou_icon_normal );

			else if ( trayIcon.getImage() == kou_icon_away_activity )
				trayIcon.setImage( kou_icon_away );

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
