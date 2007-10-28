
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
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.CommandHistory;

/**
 * Used for private chat sessions.
 * 
 * @author Christian Ihle
 */
public class PrivateChatDialog extends JDialog implements ActionListener, KeyListener
{
	private static Logger log = Logger.getLogger( PrivateChatDialog.class.getName() );
	private static final long serialVersionUID = 1L;

	private JTextPane chatTP;
	private MutableAttributeSet chatAttr;
	private StyledDocument chatDoc;
	private JMenuItem clearMI, closeMI;
	private JTextField msgTF;
	private CommandHistory cmdHistory;
	//private Mediator mediator;

	/**
	 * Creates a new privchat dialog. To open the dialog, use setVisible().
	 * 
	 * @param parent The parent frame.
	 * @param modal If the dialog should be blocking or not.
	 * @param mediator The mediator to command.
	 */
	public PrivateChatDialog( Frame parent, boolean modal, Mediator mediator )
	{
		super( parent, modal );
		//this.mediator = mediator;
		initComponents();
	}

	/**
	 * Initializes the components.
	 */
	private void initComponents()
	{
		setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		setSize( 460, 340 );
		setChatUser( "<user>" );

		chatTP = new JTextPane();
		chatTP.setEditable( false );
		chatAttr = new SimpleAttributeSet();
		chatDoc = chatTP.getStyledDocument();
		JScrollPane chatScroll = new JScrollPane( chatTP );
		
		msgTF = new JTextField();
		msgTF.addActionListener( this );
		msgTF.addKeyListener( this );

		JPanel backP = new JPanel();
		backP.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		backP.setLayout( new BorderLayout( 2, 2 ) );
		backP.add( chatScroll, BorderLayout.CENTER );
		backP.add( msgTF, BorderLayout.PAGE_END );

		getContentPane().add( backP, BorderLayout.CENTER );

		closeMI = new JMenuItem();
		closeMI.setMnemonic( 'c' );
		closeMI.setText( "Close" );

		JMenu fileMenu = new JMenu();
		fileMenu.setMnemonic( 'f' );
		fileMenu.setText( "File" );
		fileMenu.add( closeMI );

		clearMI = new JMenuItem();
		clearMI.setMnemonic( 'l' );
		clearMI.setText( "Clear chat" );

		JMenu toolsMenu = new JMenu();
		toolsMenu.setMnemonic( 't' );
		toolsMenu.setText( "Tools" );
		toolsMenu.add( clearMI );

		JMenuBar menuBar = new JMenuBar();
		menuBar.add( fileMenu );
		menuBar.add( toolsMenu );
		setJMenuBar( menuBar );
		
		/* If this window is focused, the textfield will get keyboard events
		 * no matter which component in the window was focused when typing was started. */
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher()
		{
			public boolean dispatchKeyEvent( KeyEvent e )
			{
				if( e.getID() == KeyEvent.KEY_TYPED && isFocused() )
				{
					KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent( msgTF, e );
					msgTF.requestFocus();

					return true;
				}

				else
					return false;
			}
		} );
		
		// Hide with Escape key
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );

		Action escapeAction = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				setVisible( false );
			}
		};

		getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( escapeKeyStroke, "ESCAPE" );
		getRootPane().getActionMap().put( "ESCAPE", escapeAction );
		
		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowActivated( WindowEvent e )
			{
				// Focus the textfield when the window is shown.
				msgTF.requestFocus();
			}
		} );
		
		cmdHistory = new CommandHistory();
	}
	
	/**
	 * Updates the titlebar with the nick name of the
	 * user being talked to.
	 * 
	 * @param user The user.
	 */
	public void setChatUser( String user )
	{
		setTitle( Constants.APP_NAME + " - Private chat with " + user );
	}
	
	/**
	 * Adds a new line to the chat.
	 * 
	 * @param text The line of text to add.
	 * @param color The color that the text should have.
	 */
	public void appendToPrivateChat( String text, int color )
	{
		try
		{
			StyleConstants.setForeground( chatAttr, new Color( color ) );
			chatDoc.insertString( chatDoc.getLength(), text + "\n", chatAttr );
			chatTP.setCaretPosition( chatDoc.getLength() );
		}

		catch ( BadLocationException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	/**
	 * Shows the privchat dialog.
	 */
	@Override
	public void setVisible( boolean visible )
	{
		setLocationRelativeTo( getParent() );
		super.setVisible( visible );
	}
	
	/**
	 * Sends a message when the user presses the enter key.
	 */
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == msgTF )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					cmdHistory.add( msgTF.getText() );
					//mediator.write(); TODO
				}
			} );
		}
	}

	/**
	 * Not in use.
	 */
	@Override
	public void keyPressed( KeyEvent arg0 ) {}

	/**
	 * Not in use.
	 */
	@Override
	public void keyTyped( KeyEvent arg0 ) {}

	/**
	 * Browser through the history when the user
	 * presses up or down.
	 */
	@Override
	public void keyReleased( final KeyEvent ke )
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				if ( ke.getKeyCode() == KeyEvent.VK_UP )
					msgTF.setText( cmdHistory.goUp() );
				else if ( ke.getKeyCode() == KeyEvent.VK_DOWN )
					msgTF.setText( cmdHistory.goDown() );
			}
		} );
	}
}
