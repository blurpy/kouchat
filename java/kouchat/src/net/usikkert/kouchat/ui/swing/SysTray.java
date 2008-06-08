
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
import net.usikkert.kouchat.util.Loggers;

public class SysTray implements ActionListener, MouseListener
{
	private static final Logger LOG = Loggers.UI_LOG;
	private static final String IMG_KOU_NORMAL = "/icons/kou_normal.png";
	private static final String IMG_KOU_NORMAL_ACT = "/icons/kou_normal_activity.png";
	private static final String IMG_KOU_AWAY = "/icons/kou_away.png";
	private static final String IMG_KOU_AWAY_ACT = "/icons/kou_away_activity.png";

	private SystemTray sysTray;
	private TrayIcon trayIcon;
	private Image kouIconNormal, kouIconNormalActivity, kouIconAway, kouIconAwayActivity;
	private PopupMenu menu;
	private MenuItem quitMI;
	private Mediator mediator;
	private boolean systemTraySupported;
	private final ErrorHandler errorHandler;

	public SysTray()
	{
		errorHandler = ErrorHandler.getErrorHandler();

		if ( SystemTray.isSupported() )
		{
			URL kouNorm = getClass().getResource( IMG_KOU_NORMAL );
			URL kouNormAct = getClass().getResource( IMG_KOU_NORMAL_ACT );
			URL kouAway = getClass().getResource( IMG_KOU_AWAY );
			URL kouAwayAct = getClass().getResource( IMG_KOU_AWAY_ACT );

			if ( kouNorm == null || kouNormAct == null || kouAway == null || kouAwayAct == null )
			{
				List<String> missingList = new ArrayList<String>();

				if ( kouNorm == null )
					missingList.add( IMG_KOU_NORMAL );
				if ( kouNormAct == null )
					missingList.add( IMG_KOU_NORMAL_ACT );
				if ( kouAway == null )
					missingList.add( IMG_KOU_AWAY );
				if ( kouAwayAct == null )
					missingList.add( IMG_KOU_AWAY_ACT );

				String missing = "";

				for ( int i = 0; i < missingList.size(); i++ )
				{
					missing += missingList.get( i );

					if ( i < missingList.size() - 1 )
						missing += "\n";
				}

				String error = "These images were expected, but not found:\n\n" + missing + "\n\n"
						+ Constants.APP_NAME + " will now shutdown.";

				LOG.log( Level.SEVERE, error );
				errorHandler.showCriticalError( error );
				System.exit( 1 );
			}

			kouIconNormal = new ImageIcon( kouNorm ).getImage();
			kouIconNormalActivity = new ImageIcon( kouNormAct ).getImage();
			kouIconAway = new ImageIcon( kouAway ).getImage();
			kouIconAwayActivity = new ImageIcon( kouAwayAct ).getImage();

			menu = new PopupMenu();
			quitMI = new MenuItem( "Quit" );
			quitMI.addActionListener( this );
			menu.add( quitMI );

			sysTray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon( kouIconNormal, "", menu );
			trayIcon.setImageAutoSize( true );
			trayIcon.addMouseListener( this );
			trayIcon.setToolTip( Constants.APP_NAME + " v" + Constants.APP_VERSION + " - (Not started)" );

			try
			{
				sysTray.add( trayIcon );
				systemTraySupported = true;
			}

			catch ( final AWTException e )
			{
				// This happens if the System Tray is hidden on a system
				// that actually supports a System Tray.
				LOG.log( Level.SEVERE, e.toString() );
				errorHandler.showError( "System Tray is not visible. Deactivating System Tray support." );
			}
		}

		else
		{
			String error = "System Tray is not supported. Deactivating System Tray support.";
			LOG.log( Level.SEVERE, error );
			errorHandler.showError( error );
		}
	}

	public boolean isSystemTraySupport()
	{
		return systemTraySupported;
	}

	public void setMediator( final Mediator mediator )
	{
		this.mediator = mediator;
	}

	public void setAwayState()
	{
		if ( trayIcon != null )
		{
			if ( trayIcon.getImage() != kouIconAway )
				trayIcon.setImage( kouIconAway );
		}
	}

	public void setAwayActivityState()
	{
		if ( trayIcon != null )
		{
			if ( trayIcon.getImage() != kouIconAwayActivity )
				trayIcon.setImage( kouIconAwayActivity );
		}
	}

	public void setNormalState()
	{
		if ( trayIcon != null )
		{
			if ( trayIcon.getImage() != kouIconNormal )
				trayIcon.setImage( kouIconNormal );
		}
	}

	public void setNormalActivityState()
	{
		if ( trayIcon != null )
		{
			if ( trayIcon.getImage() != kouIconNormalActivity )
				trayIcon.setImage( kouIconNormalActivity );
		}
	}

	public void setToolTip( final String toolTip )
	{
		if ( trayIcon != null )
			trayIcon.setToolTip( toolTip );
	}

	@Override
	public void actionPerformed( final ActionEvent e )
	{
		if ( e.getSource() == quitMI )
		{
			mediator.quit();
		}
	}

	@Override
	public void mouseClicked( final MouseEvent e )
	{
		if ( e.getSource() == trayIcon && e.getButton() == MouseEvent.BUTTON1 )
		{
			if ( trayIcon.getImage() == kouIconNormalActivity )
				trayIcon.setImage( kouIconNormal );

			else if ( trayIcon.getImage() == kouIconAwayActivity )
				trayIcon.setImage( kouIconAway );

			mediator.showWindow();
		}
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

	}

	@Override
	public void mouseReleased( final MouseEvent e )
	{

	}
}
