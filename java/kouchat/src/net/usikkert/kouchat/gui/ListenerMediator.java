
package net.usikkert.kouchat.gui;

import java.util.Date;

import javax.swing.JOptionPane;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.DayListener;
import net.usikkert.kouchat.event.MessageListener;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.Nick;
import net.usikkert.kouchat.misc.NickList;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.util.DayTimer;
import net.usikkert.kouchat.util.Tools;

public class ListenerMediator implements MessageListener
{
	private KouChatGUI gui;
	private MainPanel mainP;
	private Settings settings;
	private SysTray sysTray;
	private MenuBar menuBar;
	private ButtonPanel buttonP;
	private Controller controller;
	//private SidePanel sideP;
	private SettingsFrame settingsFrame;
	private Nick me;
	private DayTimer dayTimer;
	
	public ListenerMediator( KouChatGUI gui )
	{
		this.gui = gui;
		controller = new Controller();
		settings = Settings.getSettings();
		me = settings.getNick();
		controller.addMessageListener( this );
		
		dayTimer = new DayTimer();
		dayTimer.addDayListener( new DayListener()
		{
			@Override
			public void dayChanged( Date date )
			{
				mainP.appendSystemMessage( "*** Day changed to " + Tools.dateToString( null, "EEEE, d MMMM yyyy" ) );
			}
		} );
	}
	
	public void setMainP( MainPanel mainP )
	{
		this.mainP = mainP;
	}

	public void setSysTray( SysTray sysTray )
	{
		this.sysTray = sysTray;
	}

	public void setMenuBar( MenuBar menuBar )
	{
		this.menuBar = menuBar;
	}

	public void setButtonP( ButtonPanel buttonP )
	{
		this.buttonP = buttonP;
	}

	public void setSideP( SidePanel sideP )
	{
		//this.sideP = sideP;
	}

	public void setSettingsFrame( SettingsFrame settingsFrame )
	{
		this.settingsFrame = settingsFrame;
	}

	public Controller getController()
	{
		return controller;
	}
	
	public void minimize()
	{
		gui.setVisible( false );
		mainP.getMsgTF().requestFocus();
	}
	
	public void clearChat()
	{
		mainP.clearChat();
		mainP.getMsgTF().requestFocus();
	}
	
	public void setAway()
	{
		if ( me.isAway() )
		{
			Object[] options = { "Yes", "Cancel" };
			int choice = JOptionPane.showOptionDialog( null, "Back from '" + me.getAwayMsg()
					+ "'?", Constants.APP_NAME + " - Away", JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0] );
			
			if ( choice == JOptionPane.YES_OPTION )
			{
				controller.changeAwayStatus( me.getCode(), false, "" );
				sysTray.setNormalState();
				updateTitleAndTray();
				mainP.getMsgTF().setEnabled( true );
				menuBar.setAwayState( false );
				buttonP.setAwayState( false );
				mainP.appendSystemMessage( "*** You came back" );
				controller.sendBackMessage();
			}
		}
		
		else
		{
			String reason = JOptionPane.showInputDialog( null, "Reason for away?",
					Constants.APP_NAME + " - Away", JOptionPane.QUESTION_MESSAGE );
			
			if ( reason != null && reason.trim().length() > 0 )
			{
				controller.changeAwayStatus( me.getCode(), true, reason );
				sysTray.setAwayState();
				updateTitleAndTray();
				mainP.getMsgTF().setEnabled( false );
				menuBar.setAwayState( true );
				buttonP.setAwayState( true );
				mainP.appendSystemMessage( "*** You went away: " + me.getAwayMsg() );
				controller.sendAwayMessage();
			}
		}
		
		mainP.getMsgTF().requestFocus();
	}
	
	public void setTopic()
	{
		Topic topic = controller.getTopic();
		
		Object objecttopic = JOptionPane.showInputDialog( null, "Change topic?", Constants.APP_NAME
				+ " - Topic", JOptionPane.QUESTION_MESSAGE, null, null, topic.getTopic() );
		
		if ( objecttopic != null )
		{
			String newTopic = objecttopic.toString();
			
			if ( !newTopic.trim().equals( topic.getTopic().trim() ) )
			{
				long time = System.currentTimeMillis();
				
				if ( newTopic.trim().length() > 0 )
				{
					mainP.appendSystemMessage( "*** You changed the topic to: " + newTopic );
					topic.changeTopic( newTopic, me.getNick(), time );
				}
				
				else
				{
					mainP.appendSystemMessage( "*** You removed the topic..." );
					topic.changeTopic( "", "", time );
				}
				
				controller.sendTopicMessage( topic );
				updateTitleAndTray();
			}
		}
		
		mainP.getMsgTF().requestFocus();
	}
	
	public void start()
	{
		controller.logOn();
		updateTitleAndTray();
	}
	
	public void quit()
	{
		Object[] options = { "Yes", "Cancel" };
		int choice = JOptionPane.showOptionDialog( null, "Are you sure you want to quit?",
				Constants.APP_NAME + " - Quit?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0] );
		
		if ( choice == JOptionPane.YES_OPTION )
		{
			controller.logOff();
			System.exit( 0 );
		}
	}
	
	public void updateTitleAndTray()
	{
		if ( me != null )
		{
			String title = Constants.APP_NAME + " v" + Constants.APP_VERSION + " - Nick: " + me.getNick();
			String tooltip = Constants.APP_NAME + " v" + Constants.APP_VERSION + " - " + me.getNick();
			
			if ( me.isAway() )
			{
				title += " (Away)";
				tooltip += " (Away)";
			}
			
			title += " - Topic: " + controller.getTopic();
			gui.setTitle( title );
			sysTray.setToolTip( tooltip );
		}
	}
	
	public void showWindow()
	{
		if ( gui.isVisible() )
		{
			gui.setVisible( false );
		}
		
		else
		{
			gui.setVisible( true );
			gui.repaint();
		}
	}
	
	public void showSettings()
	{
		settingsFrame.showSettings();
	}
	
	public void sendFile()
	{
//		if ( controller.getMe() != sideP.getSelectedNick() )
//		{
//			JFileChooser chooser = new JFileChooser();
//			int returnVal = chooser.showOpenDialog( null );
//			
//			if ( returnVal == JFileChooser.APPROVE_OPTION )
//			{
//				File file = chooser.getSelectedFile().getAbsoluteFile();
//				
//				if ( file.exists() && file.isFile() )
//				{
//					Nick tempme = controller.getMe();
//					Nick tempnick = sideP.getSelectedNick();
//					
////					fileList.add( file );
//					
//					DecimalFormat strform = new DecimalFormat( "0.00" );
//					
//					long byteSize = file.length();
//					double kbSize = byteSize / 1024.0;
//					String size = "";
//					
//					if ( kbSize > 1024 )
//					{
//						kbSize /= 1024;
//						size = strform.format( kbSize ) + "MB";
//					}
//					
//					else
//					{
//						size = strform.format( kbSize ) + "KB";
//					}
//						
////					sender.send( tempme.getCode() + "!SENDFILE#" + tempme.getNick() + ":" + "("
////							+ tempnick.getCode() + ")" + "[" + file.length() + "]" + "{"
////							+ file.hashCode() + "}" + file.getName() );
////					appendToChat( "*** " + "Trying to send the file " + file.getName()
////							+ " [" + size + "] to " + tempnick.getNick(), settings.getMsgColor() );
//				}
//			}
//		}
//		
//		else
//		{
//			JOptionPane.showMessageDialog( null, "No point in doing that!", Constants.APP_NAME
//					+ " - Warning", JOptionPane.WARNING_MESSAGE );
//		}
	}
	
	public void write()
	{
		String line = mainP.getMsgTF().getText();
		
		if ( line.trim().length() > 0 )
		{
			if ( line.startsWith( "/" ) )
			{
				mainP.appendSystemMessage( "*** No commands yet..." );
			}
			
			else
			{
				mainP.appendOwnMessage( "<" + me.getNick() + ">: " + line );
				controller.sendChatMessage( line );
			}
		}
		
		mainP.getMsgTF().setText( "" );
	}
	
	public void updateWriting()
	{
		if ( mainP.getMsgTF().getText().length() > 0 )
		{
			if ( !controller.isWrote() )
			{
				controller.changeWriting( me.getCode(), true );
			}
		}
		
		else
		{
			if ( controller.isWrote() )
			{
				controller.changeWriting( me.getCode(), false );
			}
		}
	}
	
	public void changeNick( String nick )
	{
		if ( !nick.equals( me.getNick() ) )
		{
			if ( controller.checkIfValidNick( nick, false ) )
			{
				controller.changeNick( me.getCode(), nick );
				mainP.appendSystemMessage( "*** You changed nick to " + me.getNick() );
				updateTitleAndTray();
			}
		}
	}

	@Override
	public void messageArrived(  String msg, int color  )
	{
		mainP.appendUserMessage( msg, color );
		
		if ( !gui.isVisible() && me.isAway() )
		{
			sysTray.setAwayActivityState();
		}
		
		else if ( !gui.isVisible() )
		{
			sysTray.setNormalActivityState();
		}
	}

	@Override
	public void userLogOff( int userCode )
	{
		Nick user = controller.getNick( userCode );
		controller.getNickList().remove( user );
		mainP.appendSystemMessage( "*** " + user.getNick() + " logged off..." );
	}

	@Override
	public void userLogOn( Nick newUser )
	{
		if ( me.getNick().equals( newUser.getNick() ) )
		{
			controller.sendNickCrashMessage( newUser.getNick() );
			newUser.setNick( "" + newUser.getCode() );
		}
		
		else if ( !controller.checkIfValidNick( newUser.getNick(), true ) )
		{
			newUser.setNick( "" + newUser.getCode() );
		}
		
		controller.getNickList().add( newUser );
		mainP.appendSystemMessage( "*** " + newUser.getNick() + " logged on from " + newUser.getIpAddress() + "..." );
	}

	@Override
	public void topicChanged( String newTopic, String nick, long time )
	{
		Topic topic = controller.getTopic();
		
		if ( newTopic != null )
		{
			if ( !newTopic.equals( topic.getTopic() ) )
			{
				mainP.appendSystemMessage( "*** " + nick + " changed topic to: " + newTopic );
				topic.changeTopic( newTopic, nick, time );
				updateTitleAndTray();
			}
		}
		
		else
		{
			if ( !topic.getTopic().equals( newTopic ) )
			{
				mainP.appendSystemMessage( "*** " + nick + " removed the topic..." );
				topic.changeTopic( "", "", time );
				updateTitleAndTray();
			}
		}
	}

	@Override
	public void userExposing( Nick user )
	{
		if ( controller.checkIfNewUser( user.getCode() ) )
		{
			controller.getNickList().add( user );
		}
	}

	@Override
	public void meLogOn( String ipAddress )
	{
		me.setIpAddress( ipAddress );
		mainP.appendSystemMessage( "*** Today is " + Tools.dateToString( null, "EEEE, d MMMM yyyy" ) );
		mainP.appendSystemMessage( "*** You logged on as " + me.getNick() + " from " + ipAddress );
	}

	@Override
	public void writingChanged( int userCode, boolean writing )
	{
		controller.changeWriting( userCode, writing );
	}

	@Override
	public void awayChanged( int userCode, boolean away, String awayMsg )
	{
		Nick user = controller.getNick( userCode );
		controller.changeAwayStatus( userCode, away, awayMsg );
		
		if ( away )
		{
			mainP.appendSystemMessage( "*** " + user.getNick() + " went away: " + awayMsg );
		}

		else
		{
			mainP.appendSystemMessage( "*** " + user.getNick() + " came back..." );
		}
	}

	@Override
	public void meIdle()
	{
		NickList nickList = controller.getNickList();
		
		for ( int i = 0; i < nickList.size(); i++ )
		{
			Nick temp = nickList.get( i );
			
			if ( temp.getCode() != me.getCode() && temp.getLastIdle() < System.currentTimeMillis() - 120000 )
			{
				nickList.remove( temp );
				mainP.appendSystemMessage( "*** " + temp.getNick() + " timed out..." );
				i--;
			}
		}
	}

	@Override
	public void userIdle( int userCode )
	{
		if ( controller.checkIfNewUser( userCode ) )
		{
			controller.sendExposeMessage();
			controller.sendGetTopicMessage();
		}
		
		else
		{
			controller.updateLastIdle( userCode, System.currentTimeMillis() );
		}
	}

	@Override
	public void topicRequested()
	{
		controller.sendTopicMessage( controller.getTopic() );
	}

	@Override
	public void nickCrash()
	{
		me.setNick( "" + me.getCode() );
		mainP.appendSystemMessage( "*** " + "Nick crash, resetting nick to " + settings.getNick() );
	}

	@Override
	public void exposeRequested()
	{
		controller.sendExposingMessage();
	}

	@Override
	public void nickChanged( int userCode, String newNick )
	{
		Nick user = controller.getNick( userCode );
		String oldNick = user.getNick();
		controller.changeNick( userCode, newNick );
		mainP.appendSystemMessage( "*** " + oldNick + " changed nick to " + newNick );
	}
}
