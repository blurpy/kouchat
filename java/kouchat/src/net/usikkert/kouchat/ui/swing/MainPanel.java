
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ChatWindow;
import net.usikkert.kouchat.misc.CommandHistory;

public class MainPanel extends JPanel implements ActionListener, CaretListener, ChatWindow, KeyListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger( MainPanel.class.getName() );

	private final JScrollPane chatSP;
	private final JTextPane chatTP;
	private final MutableAttributeSet chatAttr;
	private final StyledDocument chatDoc;
	private final JTextField msgTF;
	private final CommandHistory cmdHistory;
	private Mediator mediator;

	public MainPanel( final SidePanel sideP )
	{
		setLayout( new BorderLayout( 2, 2 ) );

		chatTP = new JTextPane();
		chatTP.setEditable( false );
		chatTP.setBorder( BorderFactory.createEmptyBorder( 4, 6, 4, 6 ) );

		chatSP = new JScrollPane( chatTP );
		chatSP.setMinimumSize( new Dimension( 290, 200 ) );
		chatAttr = new SimpleAttributeSet();
		chatDoc = chatTP.getStyledDocument();

		URLMouseListener urlML = new URLMouseListener( chatTP );
		chatTP.addMouseListener( urlML );
		chatTP.addMouseMotionListener( urlML );

		AbstractDocument doc = (AbstractDocument) chatDoc;
		doc.setDocumentFilter( new URLDocumentFilter() );

		msgTF = new JTextField();
		msgTF.addActionListener( this );
		msgTF.addCaretListener( this );
		msgTF.addKeyListener( this );

		AbstractDocument msgDoc = (AbstractDocument) msgTF.getDocument();
		msgDoc.setDocumentFilter( new SizeDocumentFilter( Constants.MESSAGE_MAX_BYTES ) );

		add( chatSP, BorderLayout.CENTER );
		add( sideP, BorderLayout.EAST );
		add( msgTF, BorderLayout.SOUTH );

		new MsgPopup( msgTF );
		new ChatPopup( chatTP );

		setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
		cmdHistory = new CommandHistory();
	}

	public void setMediator( final Mediator mediator )
	{
		this.mediator = mediator;
	}

	public void appendToChat( final String message, final int color )
	{
		try
		{
			StyleConstants.setForeground( chatAttr, new Color( color ) );
			chatDoc.insertString( chatDoc.getLength(), message + "\n", chatAttr );
			chatTP.setCaretPosition( chatDoc.getLength() );
		}

		catch ( final BadLocationException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}
	}

	public JTextPane getChatTP()
	{
		return chatTP;
	}

	public JScrollPane getChatSP()
	{
		return chatSP;
	}

	public void clearChat()
	{
		chatTP.setText( "" );
	}

	public JTextField getMsgTF()
	{
		return msgTF;
	}

	public void caretUpdate( final CaretEvent e )
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				mediator.updateWriting();
			}
		} );
	}

	@Override
	public void actionPerformed( final ActionEvent e )
	{
		if ( e.getSource() == msgTF )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					cmdHistory.add( msgTF.getText() );
					mediator.write();
				}
			} );
		}
	}

	@Override
	public void keyPressed( final KeyEvent e )
	{

	}

	@Override
	public void keyTyped( final KeyEvent e )
	{

	}

	@Override
	public void keyReleased( final KeyEvent ke )
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				if ( ke.getKeyCode() == KeyEvent.VK_UP )
				{
					String up = cmdHistory.goUp();

					if ( !msgTF.getText().equals( up ) )
						msgTF.setText( up );
				}

				else if ( ke.getKeyCode() == KeyEvent.VK_DOWN )
				{
					String down = cmdHistory.goDown();

					if ( !msgTF.getText().equals( down ) )
						msgTF.setText( down );
				}
			}
		} );
	}
}
