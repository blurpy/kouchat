
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

import net.usikkert.kouchat.Constants;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class KouChatFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private MainPanel mainP;
	private SidePanel sideP;
	private ButtonPanel buttonP;
	private Mediator mediator;
	private SysTray sysTray;
	private SettingsDialog settingsDialog;
	private MenuBar menuBar;

	public KouChatFrame()
	{
		System.setProperty( Constants.PROPERTY_CLIENT_UI, "Swing" );
		new SwingPopupErrorHandler();
		
		buttonP = new ButtonPanel();
		sideP = new SidePanel( buttonP );
		mainP = new MainPanel( sideP );
		sysTray = new SysTray();
		settingsDialog = new SettingsDialog();
		menuBar = new MenuBar();
		
		ComponentHandler compHandler = new ComponentHandler();
		compHandler.setGui( this );
		compHandler.setButtonPanel( buttonP );
		compHandler.setSidePanel( sideP );
		compHandler.setMainPanel( mainP );
		compHandler.setSysTray( sysTray );
		compHandler.setSettingsDialog( settingsDialog );
		compHandler.setMenuBar( menuBar );
		
		mediator = new SwingMediator( compHandler );
		buttonP.setMediator( mediator );
		sideP.setMediator( mediator );
		mainP.setMediator( mediator );
		sysTray.setMediator( mediator );
		settingsDialog.setMediator( mediator );
		menuBar.setMediator( mediator );
		
		setJMenuBar( menuBar );
		getContentPane().add( mainP, BorderLayout.CENTER );
		setTitle( Constants.APP_NAME + " v" + Constants.APP_VERSION + " - (Not connected)" );
		setIconImage( new ImageIcon( getClass().getResource( "/icons/kou_normal.png" ) ).getImage() );
		setSize( 650, 480 );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setVisible( true );

		/* If this window is focused, the textfield will get keyboard events
		 * no matter which component in the window was focused when typing was started. */
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher()
		{
			@Override
			public boolean dispatchKeyEvent( KeyEvent e )
			{
				if ( e.getID() == KeyEvent.KEY_TYPED && isFocused() && ( e.getSource() == mainP.getChatTP() || e.getSource() == sideP.getNicList() ) ) 
				{
					KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent( mainP.getMsgTF(), e );
					mainP.getMsgTF().requestFocus();

					return true;
				}

				else
					return false;
			}
		} );

		// Ugly hack to make sure the menubar gets focus when navigating
		// with the keyboard. This is because of the focus hack above.
		getRootPane().addFocusListener( new FocusListener()
		{
			@Override
			public void focusGained( FocusEvent e ) {}

			@Override
			public void focusLost( FocusEvent e )
			{
				if ( menuBar.isPopupMenuVisible() )
					getRootPane().requestFocus();
			}
		} );

		// Minimize with Escape key
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );

		Action escapeAction = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( sysTray.isSystemTraySupport() )
					setVisible( false );
			}
		};
		
		mainP.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( escapeKeyStroke, "ESCAPE" );
		mainP.getActionMap().put( "ESCAPE", escapeAction );

		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( WindowEvent arg0 )
			{
				// Shut down the right way
				SwingUtilities.invokeLater( new Runnable()
				{
					@Override
					public void run()
					{
						mediator.quit();
					}
				} );
			}
			
			@Override
			public void windowActivated( WindowEvent e )
			{
				// Focus the textfield when the window is shown.
				mainP.getMsgTF().requestFocus();
			}
		} );

		// Try to stop the gui from lagging during startup
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				mediator.start();
				mainP.getMsgTF().requestFocus();
			}
		} );
	}
}
