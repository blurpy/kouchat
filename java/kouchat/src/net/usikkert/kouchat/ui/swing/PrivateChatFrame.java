
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
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
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
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.CommandHistory;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.PrivateChatWindow;
import net.usikkert.kouchat.misc.Settings;

/**
 * Used for private chat sessions.
 * 
 * @author Christian Ihle
 */
public class PrivateChatFrame extends JFrame implements ActionListener, KeyListener, PrivateChatWindow, FileDropSource
{
	private static final Logger log = Logger.getLogger( PrivateChatFrame.class.getName() );
	private static final long serialVersionUID = 1L;

	private JTextPane chatTP;
	private MutableAttributeSet chatAttr;
	private StyledDocument chatDoc;
	private JMenuItem clearMI, closeMI;
	private JTextField msgTF;
	private CommandHistory cmdHistory;
	private Mediator mediator;
	private NickDTO user, me;
	private FileTransferHandler fileTransferHandler;
	private boolean newMsg;

	/**
	 * Creates a new privchat frame. To open the window, use setVisible().
	 * 
	 * @param mediator The mediator to command.
	 * @param user The user in the private chat.
	 */
	public PrivateChatFrame( Mediator mediator, NickDTO user )
	{
		this.mediator = mediator;
		this.user = user;

		me = Settings.getSettings().getMe();
		user.setPrivchat( this );
		initComponents();
	}

	/**
	 * Initializes the components.
	 */
	private void initComponents()
	{
		setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		setSize( 460, 340 );
		setMinimumSize( new Dimension( 300, 250 ) );
		updateNick();
		setIconImage( new ImageIcon( getClass().getResource( Constants.APP_ICON ) ).getImage() );

		fileTransferHandler = new FileTransferHandler( this );
		fileTransferHandler.setMediator( mediator );

		chatTP = new JTextPane();
		chatTP.setEditable( false );
		chatTP.setBorder( BorderFactory.createEmptyBorder( 4, 6, 4, 6 ) );
		chatTP.setTransferHandler( fileTransferHandler );
		chatAttr = new SimpleAttributeSet();
		chatDoc = chatTP.getStyledDocument();
		JScrollPane chatScroll = new JScrollPane( chatTP );

		URLMouseListener urlML = new URLMouseListener( chatTP );
		chatTP.addMouseListener( urlML );
		chatTP.addMouseMotionListener( urlML );

		AbstractDocument doc = (AbstractDocument) chatDoc;
		doc.setDocumentFilter( new URLDocumentFilter() );

		msgTF = new JTextField();
		msgTF.addActionListener( this );
		msgTF.addKeyListener( this );

		AbstractDocument msgDoc = (AbstractDocument) msgTF.getDocument();
		msgDoc.setDocumentFilter( new SizeDocumentFilter( Constants.MESSAGE_MAX_CHARACTERS ) );

		JPanel backP = new JPanel();
		backP.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		backP.setLayout( new BorderLayout( 2, 2 ) );
		backP.add( chatScroll, BorderLayout.CENTER );
		backP.add( msgTF, BorderLayout.PAGE_END );

		getContentPane().add( backP, BorderLayout.CENTER );

		closeMI = new JMenuItem();
		closeMI.setMnemonic( 'C' );
		closeMI.setText( "Close" );
		closeMI.addActionListener( this );

		final JMenu fileMenu = new JMenu();
		fileMenu.setMnemonic( 'F' );
		fileMenu.setText( "File" );
		fileMenu.add( closeMI );

		clearMI = new JMenuItem();
		clearMI.setMnemonic( 'C' );
		clearMI.setText( "Clear chat" );
		clearMI.addActionListener( this );

		final JMenu toolsMenu = new JMenu();
		toolsMenu.setMnemonic( 'T' );
		toolsMenu.setText( "Tools" );
		toolsMenu.add( clearMI );

		JMenuBar menuBar = new JMenuBar();
		menuBar.add( fileMenu );
		menuBar.add( toolsMenu );
		setJMenuBar( menuBar );

		new MsgPopup( msgTF );
		new ChatPopup( chatTP );

		/* If this window is focused, the textfield will get keyboard events
		 * no matter which component in the window was focused when typing was started. */
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher()
		{
			public boolean dispatchKeyEvent( KeyEvent e )
			{
				if( e.getID() == KeyEvent.KEY_TYPED && isFocused() && e.getSource() == chatTP )
				{
					KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent( msgTF, e );
					msgTF.requestFocusInWindow();

					return true;
				}

				else
					return false;
			}
		} );

		// Make sure the menubar gets focus when navigating with the keyboard.
		getRootPane().addFocusListener( new FocusListener()
		{
			@Override
			public void focusGained( FocusEvent e ) {}

			@Override
			public void focusLost( FocusEvent e )
			{
				if ( fileMenu.isPopupMenuVisible() || toolsMenu.isPopupMenuVisible() )
					getRootPane().requestFocusInWindow();
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

		backP.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( escapeKeyStroke, "ESCAPE" );
		backP.getActionMap().put( "ESCAPE", escapeAction );

		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowActivated( WindowEvent e )
			{
				chatTP.repaint();

				// Focus the textfield when the window is shown.
				if ( msgTF.isEnabled() )
					msgTF.requestFocusInWindow();
			}
		} );

		cmdHistory = new CommandHistory();
	}

	/**
	 * Adds a new line to the chat.
	 * 
	 * @param text The line of text to add.
	 * @param color The color that the text should have.
	 */
	@Override
	public void appendToPrivateChat( final String text, final int color )
	{
		try
		{
			StyleConstants.setForeground( chatAttr, new Color( color ) );
			chatDoc.insertString( chatDoc.getLength(), text + "\n", chatAttr );
			chatTP.setCaretPosition( chatDoc.getLength() );
			
			if ( !isVisible() )
				newMsg = true;
		}

		catch ( BadLocationException e )
		{
			log.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	/**
	 * Returns the user from this private chat.
	 * 
	 * @return Private chat user.
	 */
	@Override
	public NickDTO getUser()
	{
		return user;
	}

	/**
	 * Shows the privchat dialog.
	 */
	@Override
	public void setVisible( boolean visible )
	{
		if ( visible )
		{
			setLocationRelativeTo( getParent() );
			newMsg = false;

			if ( user == null || user.isAway() || me.isAway() )
				msgTF.setEnabled( false );
		}
		
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
					mediator.writePrivate( user.getPrivchat() );
				}
			} );
		}

		else if ( e.getSource() == closeMI )
		{
			setVisible( false );
		}

		else if ( e.getSource() == clearMI )
		{
			chatTP.setText( "" );
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

	@Override
	public void clearChatText()
	{
		msgTF.setText( "" );
	}

	@Override
	public String getChatText()
	{
		return msgTF.getText();
	}

	@Override
	public void setAway( boolean away )
	{
		msgTF.setEnabled( !away );
	}

	@Override
	public void setLoggedOff()
	{
		msgTF.setEnabled( false );

		if ( !isVisible() && newMsg )
		{
			// Show the window so privmsgs don't get lost when a user logs off.
			setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
			setExtendedState( ICONIFIED );
			setVisible( true );
		}
		
		// To stop the open dialog from showing if a file is dropped
		user = null;
	}

	@Override
	public void updateNick()
	{
		setTitle( Constants.APP_NAME + " - Private chat with " + user.getNick() );
	}
}
