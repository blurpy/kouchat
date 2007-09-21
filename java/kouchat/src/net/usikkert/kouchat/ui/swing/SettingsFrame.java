
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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.Settings;

public class SettingsFrame extends JFrame implements ActionListener
{
	private JButton useNickB, saveB, cancelB, chooseOwnColorB, chooseSysColorB;
	private JTextField nickTF;
	private JLabel nickL, ownColorL, sysColorL;
	private Settings settings;
	private Mediator mediator;

	public SettingsFrame( Mediator mediator )
	{
		this.mediator = mediator;
		mediator.setSettingsFrame( this );
		settings = Settings.getSettings();

		Container container = getContentPane();
		JPanel panel = new JPanel( new BorderLayout() );

		JPanel nickP = new JPanel();
		nickL = new JLabel( "Nick:" );
		nickTF = new JTextField( 10 );
		nickTF.setText( settings.getMe().getNick() );

		JPanel buttonP = new JPanel();
		useNickB = new JButton( "Use" );
		useNickB.addActionListener( this );
		nickP.add( nickL );
		nickP.add( nickTF );
		nickP.add( useNickB );
		nickP.setBorder( BorderFactory.createTitledBorder( "Choose nick" ) );

		JPanel colorLabelP = new JPanel();
		ownColorL = new JLabel( "Your own text color" );
		ownColorL.setForeground( new Color( settings.getOwnColor() ) );
		sysColorL = new JLabel( "Message text color" );
		sysColorL.setForeground( new Color( settings.getSysColor() ) );
		colorLabelP.add( ownColorL );
		colorLabelP.add( sysColorL );

		JPanel colorButtonP = new JPanel();
		chooseOwnColorB = new JButton( "Choose color" );
		chooseOwnColorB.addActionListener( this );
		chooseSysColorB = new JButton( "Choose color" );
		chooseSysColorB.addActionListener( this );
		colorButtonP.add( chooseOwnColorB );
		colorButtonP.add( chooseSysColorB );

		JPanel colorP = new JPanel( new GridLayout( 2, 1 ) );
		colorP.add( colorLabelP );
		colorP.add( colorButtonP );
		colorP.setBorder( BorderFactory.createTitledBorder( "Choose color" ) );

		saveB = new JButton( "Save" );
		saveB.addActionListener( this );
		buttonP.add( saveB );
		cancelB = new JButton( "Cancel" );
		cancelB.addActionListener( this );
		buttonP.add( cancelB );

		panel.add( nickP, BorderLayout.NORTH );
		panel.add( colorP, BorderLayout.CENTER );
		panel.add( buttonP, BorderLayout.SOUTH );
		panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );

		container.add( panel );

		pack();
		setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		setIconImage( new ImageIcon( getClass().getResource( "/icons/kou_normal.png" ) ).getImage() );
		setTitle( Constants.APP_NAME + " - Settings" );
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == useNickB )
		{
			mediator.changeNick( nickTF.getText() );
		}

		else if ( e.getSource() == saveB )
		{
			settings.saveSettings();
			setVisible( false );
		}

		else if ( e.getSource() == cancelB )
		{
			setVisible( false );
		}

		else if ( e.getSource() == chooseOwnColorB )
		{
			Color newColor = JColorChooser.showDialog( null, Constants.APP_NAME + " - Choose color for your own text", new Color( settings.getOwnColor() ) );

			if ( newColor != null )
			{
				settings.setOwnColor( newColor.getRGB() );
				ownColorL.setForeground( newColor );
			}
		}

		else if ( e.getSource() == chooseSysColorB )
		{
			Color newColor = JColorChooser.showDialog( null, Constants.APP_NAME	+ " - Choose color for messages", new Color( settings.getSysColor() ) );

			if ( newColor != null )
			{
				settings.setSysColor( newColor.getRGB() );
				sysColorL.setForeground( newColor );
			}
		}
	}

	public void showSettings()
	{
		nickTF.setText( settings.getMe().getNick() );
		setVisible( true );
	}
}
