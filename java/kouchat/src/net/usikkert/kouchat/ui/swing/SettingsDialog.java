
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;

public class SettingsDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JButton saveB, cancelB, chooseOwnColorB, chooseSysColorB, browserB;
	private JTextField nickTF, browserTF;
	private JLabel nickL, ownColorL, sysColorL, browserL;
	private JCheckBox soundCB, loggingCB;
	private Settings settings;
	private Mediator mediator;
	private ErrorHandler errorHandler;

	public SettingsDialog()
	{
		Container container = getContentPane();
		JPanel panel = new JPanel( new BorderLayout() );

		JPanel nickP = new JPanel();
		nickL = new JLabel( "Nick:" );
		nickTF = new JTextField( 10 );

		nickP.add( nickL );
		nickP.add( nickTF );
		nickP.setBorder( BorderFactory.createTitledBorder( "Choose nick" ) );

		JPanel colorLabelP = new JPanel();
		ownColorL = new JLabel( "   Own text color" );
		sysColorL = new JLabel( "   System text color" );
		colorLabelP.add( ownColorL );
		colorLabelP.add( sysColorL );

		JPanel colorButtonP = new JPanel();
		chooseOwnColorB = new JButton( "Change color" );
		chooseOwnColorB.addActionListener( this );
		chooseSysColorB = new JButton( "Change color" );
		chooseSysColorB.addActionListener( this );
		colorButtonP.add( chooseOwnColorB );
		colorButtonP.add( chooseSysColorB );

		JPanel colorP = new JPanel( new GridLayout( 2, 1 ) );
		colorP.add( colorLabelP );
		colorP.add( colorButtonP );
		colorP.setBorder( BorderFactory.createTitledBorder( "Choose color" ) );
		
		JPanel miscP = new JPanel();
		soundCB = new JCheckBox( "Enable sound" );
		loggingCB = new JCheckBox( "Enable logging" );
		miscP.add( soundCB );
		miscP.add( loggingCB );
		miscP.setBorder( BorderFactory.createTitledBorder( "Misc" ) );

		browserL = new JLabel( "Browser: " );
		browserTF = new JTextField( 12 );
		browserB = new JButton( "Test" );
		browserB.addActionListener( this );

		JPanel browserP = new JPanel();
		browserP.add( browserL );
		browserP.add( browserTF );
		browserP.add( browserB );
		browserP.setBorder( BorderFactory.createTitledBorder( "Choose browser" ) );

		JPanel centerP = new JPanel( new BorderLayout() );
		centerP.add( colorP, BorderLayout.CENTER );
		centerP.add( miscP, BorderLayout.SOUTH );
		centerP.add( browserP, BorderLayout.NORTH );

		JPanel buttonP = new JPanel();
		saveB = new JButton( "OK" );
		saveB.addActionListener( this );
		buttonP.add( saveB );
		cancelB = new JButton( "Cancel" );
		cancelB.addActionListener( this );
		saveB.setPreferredSize( cancelB.getPreferredSize() );
		buttonP.add( cancelB );

		panel.add( nickP, BorderLayout.NORTH );
		panel.add( centerP, BorderLayout.CENTER );
		panel.add( buttonP, BorderLayout.SOUTH );
		panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );

		container.add( panel );

		pack();
		setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
		setIconImage( new ImageIcon( getClass().getResource( Constants.APP_ICON ) ).getImage() );
		setTitle( Constants.APP_NAME + " - Settings" );
		setResizable( false );
		setModal( true );

		// Hide with Escape key
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );

		Action escapeAction = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			public void actionPerformed( ActionEvent e )
			{
				setVisible( false );
			}
		};

		getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( escapeKeyStroke, "ESCAPE" );
		getRootPane().getActionMap().put( "ESCAPE", escapeAction );
		
		// So the save button activates using Enter
		getRootPane().setDefaultButton( saveB );
		
		settings = Settings.getSettings();
		errorHandler = ErrorHandler.getErrorHandler();
	}
	
	public void setMediator( Mediator mediator )
	{
		this.mediator = mediator;
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == saveB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					if ( mediator.changeNick( nickTF.getText() ) )
					{
						settings.setSysColor( sysColorL.getForeground().getRGB() );
						settings.setOwnColor( ownColorL.getForeground().getRGB() );
						settings.setSound( soundCB.isSelected() );
						settings.setLogging( loggingCB.isSelected() );
						settings.setBrowser( browserTF.getText() );
						settings.saveSettings();
						setVisible( false );
					}
				}
			} );
		}

		else if ( e.getSource() == cancelB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					setVisible( false );
				}
			} );
		}

		else if ( e.getSource() == chooseOwnColorB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					Color newColor = JColorChooser.showDialog( null, Constants.APP_NAME + " - Choose color for own messages", new Color( settings.getOwnColor() ) );

					if ( newColor != null )
					{
						ownColorL.setForeground( newColor );
					}
				}
			} );
		}

		else if ( e.getSource() == chooseSysColorB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					Color newColor = JColorChooser.showDialog( null, Constants.APP_NAME	+ " - Choose color for system messages", new Color( settings.getSysColor() ) );

					if ( newColor != null )
					{
						sysColorL.setForeground( newColor );
					}
				}
			} );
		}
		
		else if ( e.getSource() == browserB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					String browser = browserTF.getText();
					
					try
					{
						Runtime.getRuntime().exec( browser + " " + Constants.AUTHOR_WEB );
					}
					
					catch ( IOException e )
					{
						errorHandler.showError( "Could not open the browser '" + browser + "'. Try using the full path." );
					}
				}
			} );
		}
	}

	public void showSettings()
	{
		nickTF.setText( settings.getMe().getNick() );
		sysColorL.setForeground( new Color( settings.getSysColor() ) );
		ownColorL.setForeground( new Color( settings.getOwnColor() ) );
		soundCB.setSelected( settings.isSound() );
		loggingCB.setSelected( settings.isLogging() );
		browserTF.setText( settings.getBrowser() );
		setVisible( true );
	}
}
