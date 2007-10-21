
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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ButtonPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JButton minimizeB, clearB, awayB, topicB;
	private Mediator mediator;

	public ButtonPanel()
	{
		setLayout( new GridLayout( 4, 1 ) );

		clearB = new JButton( "Clear" );
		clearB.addActionListener( this );
		add( clearB );

		awayB = new JButton( "Away" );
		awayB.addActionListener( this );
		add( awayB );

		topicB = new JButton( "Topic" );
		topicB.addActionListener( this );
		add( topicB );

		minimizeB = new JButton( "Minimize" );
		minimizeB.addActionListener( this );
		add( minimizeB );

		setBorder( BorderFactory.createEmptyBorder( 1, 1, 2, 1 ) );
	}
	
	public void setMediator( Mediator mediator )
	{
		this.mediator = mediator;
	}

	public void setAwayState( boolean away )
	{
		topicB.setEnabled( !away );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == minimizeB )
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

		else if ( e.getSource() == clearB )
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

		else if ( e.getSource() == awayB )
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

		else if ( e.getSource() == topicB )
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
	}
}
