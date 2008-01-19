
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
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.ui.util.UITools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

/**
 * This is the main chat window.
 *
 * @author Christian Ihle
 */
public class KouChatFrame extends JFrame implements WindowListener, FocusListener
{
	private static final long serialVersionUID = 1L;

	private final MainPanel mainP;
	private final SidePanel sideP;
	private final ButtonPanel buttonP;
	private final Mediator mediator;
	private final SysTray sysTray;
	private final SettingsDialog settingsDialog;
	private final MenuBar menuBar;
	private final Settings settings;

	public KouChatFrame()
	{
		System.setProperty( Constants.PROPERTY_CLIENT_UI, "Swing" );
		settings = Settings.getSettings();

		if ( settings.isNativeLnF() )
			UITools.setSystemLookAndFeel();

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

		// Show tooltips for 10 seconds. Default is very short.
		ToolTipManager.sharedInstance().setDismissDelay( 10000 );

		setJMenuBar( menuBar );
		getContentPane().add( mainP, BorderLayout.CENTER );
		setTitle( Constants.APP_NAME + " v" + Constants.APP_VERSION + " - (Not connected)" );
		setIconImage( new ImageIcon( getClass().getResource( Constants.APP_ICON ) ).getImage() );
		setSize( 650, 480 );
		setMinimumSize( new Dimension( 450, 300 ) );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setVisible( true );

		getRootPane().addFocusListener( this );
		addWindowListener( this );
		fixTextFieldFocus();
		hideWithEscape();

		// Try to stop the gui from lagging during startup
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				mediator.start();
				mainP.getMsgTF().requestFocusInWindow();
			}
		} );
	}

	/**
	 * Adds a shortcut to hide the window when escape is pressed.
	 */
	private void hideWithEscape()
	{
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );

		Action escapeAction = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				if ( sysTray.isSystemTraySupport() )
					setVisible( false );
			}
		};

		mainP.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( escapeKeyStroke, "ESCAPE" );
		mainP.getActionMap().put( "ESCAPE", escapeAction );
	}

	/**
	 * If this window is focused, the text field will get the keyboard events
	 * if the chat area or the nick list was focused when typing was started.
	 */
	private void fixTextFieldFocus()
	{
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher()
		{
			@Override
			public boolean dispatchKeyEvent( final KeyEvent e )
			{
				if ( e.getID() == KeyEvent.KEY_TYPED && isFocused() && ( e.getSource() == mainP.getChatTP() || e.getSource() == sideP.getNickList() ) )
				{
					KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent( mainP.getMsgTF(), e );
					mainP.getMsgTF().requestFocusInWindow();

					return true;
				}

				else
					return false;
			}
		} );
	}

	@Override
	public void focusGained( final FocusEvent e )
	{

	}

	/**
	 * Make sure the menubar gets focus when navigating with the keyboard.
	 */
	@Override
	public void focusLost( final FocusEvent e )
	{
		if ( menuBar.isPopupMenuVisible() )
			getRootPane().requestFocusInWindow();
	}

	/**
	 * Shut down the right way.
	 */
	@Override
	public void windowClosing( final WindowEvent e )
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

	/**
	 * Fix focus and repaint issues when the window gets focused.
	 */
	@Override
	public void windowActivated( final WindowEvent e )
	{
		mainP.getChatSP().repaint();
		sideP.getNickList().repaint();
		mainP.getMsgTF().requestFocusInWindow();
	}

	@Override
	public void windowClosed( final WindowEvent e )
	{

	}

	@Override
	public void windowDeactivated( final WindowEvent e )
	{

	}

	@Override
	public void windowDeiconified( final WindowEvent e )
	{

	}

	@Override
	public void windowIconified( final WindowEvent e )
	{

	}

	@Override
	public void windowOpened( final WindowEvent e )
	{

	}
}
