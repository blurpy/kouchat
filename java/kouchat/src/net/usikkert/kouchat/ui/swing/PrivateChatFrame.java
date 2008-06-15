
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.util.Loggers;
import net.usikkert.kouchat.util.Validate;

/**
 * The window used for private chat sessions.
 *
 * @author Christian Ihle
 */
public class PrivateChatFrame extends JFrame implements ActionListener, KeyListener,
		PrivateChatWindow, FileDropSource, WindowListener, FocusListener
{
	private static final Logger LOG = Loggers.UI_LOG;
	private static final long serialVersionUID = 1L;

	private final JTextPane chatTP;
	private final MutableAttributeSet chatAttr;
	private final StyledDocument chatDoc;
	private final JMenu fileMenu, toolsMenu;
	private final JMenuItem clearMI, closeMI;
	private final JTextField msgTF;
	private final CommandHistory cmdHistory;
	private final Mediator mediator;
	private final NickDTO me, user;
	private final FileTransferHandler fileTransferHandler;

	/**
	 * Creates a new private chat frame. To open the window, use setVisible().
	 *
	 * @param mediator The mediator to command.
	 * @param user The user in the private chat.
	 */
	public PrivateChatFrame( final Mediator mediator, final NickDTO user )
	{
		Validate.notNull( mediator, "Mediator can not be null" );
		Validate.notNull( user, "User can not be null" );

		this.mediator = mediator;
		this.user = user;

		me = Settings.getSettings().getMe();
		user.setPrivchat( this );

		setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		setSize( 460, 340 );
		setMinimumSize( new Dimension( 300, 250 ) );
		setIconImage( new ImageIcon( getClass().getResource( Constants.APP_ICON ) ).getImage() );
		updateNick();

		fileTransferHandler = new FileTransferHandler( this );
		fileTransferHandler.setMediator( mediator );

		chatAttr = new SimpleAttributeSet();
		chatTP = new JTextPane();
		chatTP.setEditable( false );
		chatTP.setBorder( BorderFactory.createEmptyBorder( 4, 6, 4, 6 ) );
		chatTP.setEditorKit( new MiddleAlignedIconViewEditorKit() );
		chatTP.setTransferHandler( fileTransferHandler );
		chatDoc = chatTP.getStyledDocument();
		JScrollPane chatScroll = new JScrollPane( chatTP );

		URLMouseListener urlML = new URLMouseListener( chatTP );
		chatTP.addMouseListener( urlML );
		chatTP.addMouseMotionListener( urlML );

		ChatAreaDocumentFilter chatAreaDocumentFilter = new ChatAreaDocumentFilter();
		chatAreaDocumentFilter.addDocumentFilter( new URLDocumentFilter( false ) );
		chatAreaDocumentFilter.addDocumentFilter( new SmileyDocumentFilter( false ) );
		AbstractDocument doc = (AbstractDocument) chatDoc;
		doc.setDocumentFilter( chatAreaDocumentFilter );

		msgTF = new JTextField();
		msgTF.addActionListener( this );
		msgTF.addKeyListener( this );

		AbstractDocument msgDoc = (AbstractDocument) msgTF.getDocument();
		msgDoc.setDocumentFilter( new SizeDocumentFilter( Constants.MESSAGE_MAX_BYTES ) );

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

		fileMenu = new JMenu();
		fileMenu.setMnemonic( 'F' );
		fileMenu.setText( "File" );
		fileMenu.add( closeMI );

		clearMI = new JMenuItem();
		clearMI.setMnemonic( 'C' );
		clearMI.setText( "Clear chat" );
		clearMI.addActionListener( this );

		toolsMenu = new JMenu();
		toolsMenu.setMnemonic( 'T' );
		toolsMenu.setText( "Tools" );
		toolsMenu.add( clearMI );

		JMenuBar menuBar = new JMenuBar();
		menuBar.add( fileMenu );
		menuBar.add( toolsMenu );
		setJMenuBar( menuBar );

		new MsgPopup( msgTF );
		new ChatPopup( chatTP );

		getRootPane().addFocusListener( this );
		addWindowListener( this );
		fixTextFieldFocus();
		hideWithEscape( backP );

		cmdHistory = new CommandHistory();
	}

	/**
	 * If this window is focused, the text field will get the keyboard events
	 * if the chat area was focused when typing was started.
	 */
	private void fixTextFieldFocus()
	{
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher()
		{
			public boolean dispatchKeyEvent( final KeyEvent e )
			{
				if ( e.getID() == KeyEvent.KEY_TYPED && isFocused() && e.getSource() == chatTP )
				{
					KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent( msgTF, e );
					msgTF.requestFocusInWindow();

					return true;
				}

				else
					return false;
			}
		} );
	}

	/**
	 * Adds a shortcut to hide the window when escape is pressed.
	 *
	 * @param panel The panel to add the shortcut to.
	 */
	private void hideWithEscape( final JPanel panel )
	{
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );

		Action escapeAction = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				close();
			}
		};

		panel.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( escapeKeyStroke, "ESCAPE" );
		panel.getActionMap().put( "ESCAPE", escapeAction );
	}

	/**
	 * Adds a new line to the chat.
	 *
	 * @param message The line of text to add.
	 * @param color The color that the text should have.
	 */
	@Override
	public void appendToPrivateChat( final String message, final int color )
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
	 * Hides or shows the private chat window.
	 */
	@Override
	public void setVisible( final boolean visible )
	{
		if ( visible )
		{
			// Stop the window from jumping around the screen if it's already visible
			if ( !isVisible() )
				setLocationRelativeTo( getParent() );

			if ( !user.isOnline() || user.isAway() || me.isAway() )
				msgTF.setEnabled( false );
		}

		super.setVisible( visible );
	}

	@Override
	public void actionPerformed( final ActionEvent e )
	{
		// Sends a message when the user presses the enter key.
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
			close();
		}

		else if ( e.getSource() == clearMI )
		{
			chatTP.setText( "" );
		}
	}

	/**
	 * Closes or disposes the window depending on if the user is logged off or not.
	 */
	private void close()
	{
		if ( !user.isOnline() )
			dispose();
		else
			setVisible( false );
	}

	@Override
	public void keyPressed( final KeyEvent e )
	{

	}

	@Override
	public void keyTyped( final KeyEvent e )
	{

	}

	/**
	 * Browse through the history when the user
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

	/**
	 * Clears the text in the write area.
	 */
	@Override
	public void clearChatText()
	{
		msgTF.setText( "" );
	}

	/**
	 * Returns the contents of the write area.
	 */
	@Override
	public String getChatText()
	{
		return msgTF.getText();
	}

	/**
	 * Disables the write field if away.
	 */
	@Override
	public void setAway( final boolean away )
	{
		msgTF.setEnabled( !away );
		updateNick();
	}

	/**
	 * Disables the write field, and opens the window if
	 * there are unread messages so they don't get lost.
	 * If not, the window is disposed.
	 */
	@Override
	public void setLoggedOff()
	{
		msgTF.setEnabled( false );

		if ( !isVisible() && user.isNewPrivMsg() )
		{
			setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
			setExtendedState( ICONIFIED );
			setVisible( true );
		}

		else if ( !isVisible() )
		{
			dispose();
		}
	}

	/**
	 * Updates the titlebar with information about the private chat.
	 * Activity from the other user will result in <code>[!!]</code> being
	 * added to the start of the title.
	 */
	@Override
	public void updateNick()
	{
		String title = user.getNick();

		if ( user.isAway() )
			title += " (Away)";

		title += " - " + Constants.APP_NAME;

		if ( user.isNewPrivMsg() && !isFocused() && isVisible() )
			setTitle( "[!!] " + title );
		else
			setTitle( title );
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
		if ( fileMenu.isPopupMenuVisible() || toolsMenu.isPopupMenuVisible() )
			getRootPane().requestFocusInWindow();
	}

	/**
	 * Focus the text field when the window is shown.
	 */
	@Override
	public void windowActivated( final WindowEvent e )
	{
		chatTP.repaint();
		mediator.activatedPrivChat( user );
		updateNick();

		if ( msgTF.isEnabled() )
			msgTF.requestFocusInWindow();
	}

	@Override
	public void windowClosed( final WindowEvent e )
	{

	}

	@Override
	public void windowClosing( final WindowEvent e )
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
