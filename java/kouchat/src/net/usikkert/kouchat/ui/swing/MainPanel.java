
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.Tools;

public class MainPanel extends JPanel implements ActionListener, CaretListener
{
	private static Logger log = Logger.getLogger( MainPanel.class.getName() );
	
	private JScrollPane chatSP;
	private JTextPane chatTP;
	private MutableAttributeSet chatAttr;
	private StyledDocument chatDoc;
	private JTextField msgTF;
	private SidePanel sideP;
	private Settings settings;
	private Mediator mediator;
	
	public MainPanel( Mediator mediator )
	{
		this.mediator = mediator;
		
		setLayout( new BorderLayout( 2, 2 ) );
		
		chatTP = new JTextPane();
		chatTP.setEditable( false );
		chatSP = new JScrollPane( chatTP );
		chatAttr = new SimpleAttributeSet();
		chatDoc = chatTP.getStyledDocument();
		sideP = new SidePanel( mediator );
		msgTF = new JTextField();
		msgTF.addActionListener( this );
		msgTF.addCaretListener( this );
		
		add( chatSP, BorderLayout.CENTER );
		add( sideP, BorderLayout.EAST );
		add( msgTF, BorderLayout.SOUTH );
		
		setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		
		mediator.setMainP( this );
		settings = Settings.getSettings();
	}
	
	private void appendToChat( String text, Color color )
	{
		try
		{
			StyleConstants.setForeground( chatAttr, color );
			chatDoc.insertString( chatDoc.getLength(), Tools.getTime() + text + "\n", chatAttr );
			chatTP.setCaretPosition( chatDoc.getLength() );
		}
		
		catch ( BadLocationException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}
	
	public void appendSystemMessage( String message )
	{
		appendToChat( message, new Color( settings.getSysColor() ) );
	}
	
	public void appendOwnMessage( String message )
	{
		appendToChat( message, new Color( settings.getOwnColor() ) );
	}
	
	public void appendUserMessage( String message, int color )
	{
		appendToChat( message, new Color( color ) );
	}
	
	public void clearChat()
	{
		chatTP.setText( "" );
	}
	
	public JTextField getMsgTF()
	{
		return msgTF;
	}
	
	public void caretUpdate( CaretEvent e )
	{
		mediator.updateWriting();
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == msgTF )
		{
			mediator.write();
		}
	}
}
