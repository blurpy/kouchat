
package net.usikkert.kouchat.net;

import java.util.*;
import net.usikkert.kouchat.misc.*;

public class MessageParser implements ReceiverListener
{
	private Receiver receiver;
	private List<MessageListener> listeners;
	private Settings settings;
	private boolean loggedOn;
	
	public MessageParser()
	{
		settings = Settings.getSettings();
		listeners = new ArrayList<MessageListener>();
		receiver = new Receiver();
		receiver.addReceiverListener( this );
		receiver.start();
	}
	
	public void addMessageListener( MessageListener listener )
	{
		listeners.add( listener );
	}
	
	public void removeMessageListener( MessageListener listener )
	{
		listeners.remove( listener );
	}
	
	private void fireMessageArrived( String msg, int color )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.messageArrived( msg, color );
		}
	}
	
	private void fireMeLogOn( String ipAddress )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.meLogOn( ipAddress );
		}
	}
	
	private void fireUserLogOn( Nick newUser )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.userLogOn( newUser );
		}
	}
	
	private void fireUserLogOff( int userCode )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.userLogOff( userCode );
		}
	}
	
	private void fireUserExposing( Nick user )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.userExposing( user );
		}
	}
	
	private void fireTopicChanged( String newTopic, String nick, long time )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.topicChanged( newTopic, nick, time );
		}
	}
	
	private void fireTopicRequested()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.topicRequested();
		}
	}

	private void fireAwayChanged( int userCode, boolean away, String awayMsg )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.awayChanged( userCode, away, awayMsg );
		}
	}
	
	private void fireWritingChanged( int msgCode, boolean writing )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.writingChanged( msgCode, writing );
		}
	}
	
	private void fireMeIdle()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.meIdle();
		}
	}
	
	private void fireUserIdle( int userCode )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.userIdle( userCode );
		}
	}
	
	private void fireNickCrash()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.nickCrash();
		}
	}
	
	private void fireExposeRequested()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.exposeRequested();
		}
	}
	
	private void fireNickChanged( int userCode, String newNick )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			MessageListener ml = listeners.get( i );
			ml.nickChanged( userCode, newNick );
		}
	}
	
	public void stop()
	{
		receiver.stopReceiver();
	}
	
//	private void logOn()
//	{
//				if ( !sideP.checkIfValidNick( tempme.getNick(), true ) )
//				{
//					String orgNick = tempme.getNick();
//					sideP.changeNick( tempme.getCode(), "" + tempme.getCode() );
//					
//					if ( orgNick.trim().length() > 0 )
//					{
//						mainP.appendToChat( getTime() + "*** " + "Nick crash, resetting nick to " + tempme.getCode()
//								+ "\n", settings.getMsgColor() );
//					}
//				}
//				idle.start();
//	}
	
	public void messageArrived( ReceiverEvent re )
	{
		String message = re.getMessage();
		String ipAddress = re.getIp();
		
		System.out.println( message );
		
		try
		{
			int exclamation = message.indexOf( "!" );
			int hash = message.indexOf( "#" );
			int colon = message.indexOf( ":" );
			
			int msgCode = Integer.parseInt( message.substring( 0, exclamation ) );
			String type = message.substring( exclamation +1, hash );
			String msgNick = message.substring( hash +1, colon );
			String msg = message.substring( colon +1, message.length() );
			
			Nick tempme = settings.getNick();
			
			if ( msgCode != tempme.getCode() && loggedOn )
			{
				if ( type.equals( "MSG" ) )
				{
					int leftBracket = msg.indexOf( "[" );
					int rightBracket = msg.indexOf( "]" );
					int rgb = Integer.parseInt( msg.substring( leftBracket +1, rightBracket ) );
					
					fireMessageArrived( "<" + msgNick + ">: " + msg.substring( rightBracket +1, msg.length() ), rgb);
				}
				
				else if ( type.equals( "LOGON" ) )
				{
					Nick newUser = new Nick( msgNick, msgCode );
					newUser.setIpAddress( ipAddress );
					newUser.setLastIdle( System.currentTimeMillis() );
					
					fireUserLogOn( newUser );
				}
				
				else if ( type.equals( "EXPOSING" ) )
				{
					Nick user = new Nick( msgNick, msgCode );
					user.setIpAddress( ipAddress );
					user.setAwayMsg( msg );
					
					if ( msg.length() > 0 )
						user.setAway( true );
					
					user.setLastIdle( System.currentTimeMillis() );
					fireUserExposing( user );
				}
				
				else if ( type.equals( "LOGOFF" ) )
				{
					fireUserLogOff( msgCode );
				}
				
				else if ( type.equals( "AWAY" ) )
				{
					fireAwayChanged( msgCode, true, msg );
				}
				
				else if ( type.equals( "BACK" ) )
				{
					fireAwayChanged( msgCode, false, "" );
				}
				
				else if ( type.equals( "EXPOSE" ) )
				{
					fireExposeRequested();
				}
				
				else if ( type.equals( "NICKCRASH" ) )
				{
					if ( tempme.getNick().equals( msg ) )
					{
						fireNickCrash();
					}
				}
				
				else if ( type.equals( "WRITING" ) )
				{
					fireWritingChanged( msgCode, true );
				}
				
				else if ( type.equals( "STOPPEDWRITING" ) )
				{
					fireWritingChanged( msgCode, false );
				}
				
				else if ( type.equals( "GETTOPIC" ) )
				{
					fireTopicRequested();
				}
				
				else if ( type.equals( "TOPIC" ) )
				{
					try
					{
						int leftBracket = msg.indexOf( "[" );
						int rightBracket = msg.indexOf( "]" );
						int leftPara = msg.indexOf( "(" );
						int rightPara = msg.indexOf( ")" );
						
						if ( rightBracket != -1 && leftBracket != -1 )
						{
							String theNick = msg.substring( leftPara +1, rightPara );
							long theTime = Long.parseLong( msg.substring( leftBracket +1, rightBracket ) );
							String theTopic = null;
							
							if ( msg.length() > rightBracket + 1 )
							{
								theTopic = msg.substring( rightBracket +1, msg.length() );
							}
							
							fireTopicChanged( theTopic, theNick, theTime );
						}
					}
					
					catch ( StringIndexOutOfBoundsException e )
					{
						e.printStackTrace();
					}
				}
				
				else if ( type.equals( "NICK" ) )
				{
					fireNickChanged( msgCode, msgNick );
				}
				
				else if ( type.equals( "IDLE" ) )
				{
					fireUserIdle( msgCode );
				}
				
				else if ( type.equals( "SENDFILEACCEPT" ) )
				{
//					int leftBracket = msg.indexOf( "[" );
//					int rightBracket = msg.indexOf( "]" );
//					int leftPara = msg.indexOf( "(" );
//					int rightPara = msg.indexOf( ")" );
//					final int leftCurly = msg.indexOf( "{" );
//					final int rightCurly = msg.indexOf( "}" );
//					int fileCode = Integer.parseInt( msg.substring( leftPara +1, rightPara ) );
//					final int portnr = Integer.parseInt( msg.substring( leftBracket +1, rightBracket ) );
//					final int fileHash = Integer.parseInt( msg.substring( leftCurly +1, rightCurly ) );
//					
//					final String fMsg = msg;
//					final String fMsgNick = msgNick;
//					final int fMsgCode = msgCode;
//					
//					if ( fileCode == tempme.getCode() )
//					{
//						new Thread()
//						{
//							public void run()
//							{
//								String fileName = fMsg.substring( rightCurly +1, fMsg.length() );
//								mainP.appendToChat( getTime() + "*** " + fMsgNick + " accepted sending of "
//										+ fileName + "\n", settings.getMsgColor() );
//								File file = null;
//								
//								for ( int i = 0; i < fileList.size(); i++ )
//								{
//									if ( fileList.get( i ).hashCode() == fileHash )
//									{
//										file = (File) fileList.get( i );
//										fileList.remove( i );
//										break;
//									}
//								}
//								
//								if ( file != null )
//								{
//									Nick tempnick = sideP.getNick( fMsgCode );
//									FileTransferer fileTrans = new FileTransferer();
//									
//									// Give the server some time to set up the connection first
//									try
//									{
//										Thread.sleep( 200 );
//									}
//									
//									catch ( InterruptedException e ) {}
//									
//									if ( fileTrans.send( tempnick, portnr, file ) )
//									{
//										mainP.appendToChat( getTime() + "*** " + fileName + " successfully sent to "
//												+ fMsgNick + "\n", settings.getMsgColor() );
//									}
//									
//									else
//									{
//										mainP.appendToChat( getTime() + "*** Failed to send " + fileName + " to "
//												+ fMsgNick + "\n", settings.getMsgColor() );
//									}
//								}
//							}
//						}.start();
					//}
				}
				
				else if ( type.equals( "SENDFILEABORT" ) )
				{
//					int leftPara = msg.indexOf( "(" );
//					int rightPara = msg.indexOf( ")" );
//					int leftCurly = msg.indexOf( "{" );
//					int rightCurly = msg.indexOf( "}" );
//					int fileHash = Integer.parseInt( msg.substring( leftCurly +1, rightCurly ) );
//					int fileCode = Integer.parseInt( msg.substring( leftPara +1, rightPara ) );
//					
//					if ( fileCode == tempme.getCode() )
//					{
//						String fileName = msg.substring( rightCurly +1, msg.length() );
//						mainP.appendToChat( getTime() + "*** " + msgNick + " aborted sending of " + fileName
//								+ "\n", settings.getMsgColor() );
//						
//						for ( int i = 0; i < fileList.size(); i++ )
//						{
//							if ( fileList.get( i ).hashCode() == fileHash )
//							{
//								fileList.remove( i );
//							}
//						}
//					}
				}
				
				else if ( type.equals( "SENDFILE" ) )
				{
//					int leftBracket = msg.indexOf( "[" );
//					int rightBracket = msg.indexOf( "]" );
//					int leftPara = msg.indexOf( "(" );
//					int rightPara = msg.indexOf( ")" );
//					int leftCurly = msg.indexOf( "{" );
//					int rightCurly = msg.indexOf( "}" );
//					int fileHash = Integer.parseInt( msg.substring( leftCurly +1, rightCurly ) );
//					int fileCode = Integer.parseInt( msg.substring( leftPara +1, rightPara ) );
//					
//					if ( fileCode == tempme.getCode() )
//					{
//						DecimalFormat strform = new DecimalFormat( "0.00" );
//						
//						long byteSize = Long.parseLong( msg.substring( leftBracket +1, rightBracket ) );
//						double kbSize = byteSize / 1024.0;
//						String size = "";
//						
//						if ( kbSize > 1024 )
//						{
//							kbSize /= 1024;
//							size = strform.format( kbSize ) + "MB";
//						}
//						
//						else
//						{
//							size = strform.format( kbSize ) + "KB";
//						}
						
						//String fileName = msg.substring( rightCurly +1, msg.length() );
						
//						mainP.appendToChat( getTime() + "*** " + msgNick + " is trying to send the file "
//								+ fileName + " [" + size + "]\n", settings.getMsgColor() );
						
//						final String fMsgNick = msgNick;
//						final String fFileName = fileName;
//						final String fSize = size;
//						final int fMsgCode = msgCode;
//						final Nick fTempMe = tempme;
//						final int fFileHash = fileHash;
//						final long fByteSize = byteSize;
						
//						new Thread()
//						{
//							public void run()
//							{
//								Object[] options = { "Yes", "Cancel" };
//								int choice = JOptionPane.showOptionDialog( null, fMsgNick + " wants to send you the file "
//										+ fFileName + " (" + fSize + ")\nAccept?", Constants.APP_NAME + " - File send",
//										JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] );
//								
//								if ( choice == JOptionPane.YES_OPTION )
//								{
//									JFileChooser chooser = new JFileChooser();
//									chooser.setSelectedFile( new File( fFileName ) );
//									boolean done = false;
//									
//									while ( !done )
//									{
//										done = true;
//										int returnVal = chooser.showSaveDialog( null );
//										
//										if ( returnVal == JFileChooser.APPROVE_OPTION )
//										{
//											Nick tempnick = sideP.getNick( fMsgCode );
//											File file = chooser.getSelectedFile().getAbsoluteFile();
//	
//											if ( file.exists() )
//											{
//												int overwrite = JOptionPane.showOptionDialog( null, file.getName()
//														+ " already exists.\nOverwrite?", Constants.APP_NAME + " - File exists",
//															JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
//															options, options[0] );
//													
//												if ( overwrite != JOptionPane.YES_OPTION )
//												{
//													done = false;
//												}
//											}
//										
//											if ( done )
//											{
//												FileTransferer fileTrans = new FileTransferer();
//												sender.send( fTempMe.getCode() + "!SENDFILEACCEPT#" + fTempMe.getNick()
//														+ ":" + "(" + fMsgCode + ")" + "[" + 50123 + "]" + "{" + fFileHash + "}"
//														+ fFileName );
//												
//												if ( fileTrans.receive( tempnick, 50123, file, fByteSize ) )
//												{
//													mainP.appendToChat( getTime() + "*** Successfully received " + fFileName
//															+ " from " + fMsgNick + ", and saved as " + file.getName()
//															+ "\n", settings.getMsgColor() );
//												}
//												
//												else
//												{
//													mainP.appendToChat( getTime() + "*** Failed to receive " + fFileName
//															+ " from " + fMsgNick + "\n", settings.getMsgColor() );
//												}
//											}
//										}
//										
//										else
//										{
//											mainP.appendToChat( getTime() + "*** You declined to receive " + fFileName
//													+ " from " + fMsgNick + "\n", settings.getMsgColor() );
//											
//											sender.send( fTempMe.getCode() + "!SENDFILEABORT#" + fTempMe.getNick()
//													+ ":" + "(" + fMsgCode + ")" + "{" + fFileHash + "}" + fFileName );
//										}
//									}
//								}
//								
//								else
//								{
//									mainP.appendToChat( getTime() + "*** You declined to receive " + fFileName
//											+ " from " + fMsgNick + "\n", settings.getMsgColor() );
//									
//									sender.send( fTempMe.getCode() + "!SENDFILEABORT#" + fTempMe.getNick()
//											+ ":" + "(" + fMsgCode + ")" + "{" + fFileHash + "}" + fFileName );
//								}
//							}
//						}.start();
					//}
				}
			}
			
			else if ( type.equals( "LOGON" ) )
			{
				fireMeLogOn( ipAddress );
				loggedOn = true;
			}
			
			else if ( type.equals( "IDLE" ) )
			{
				fireMeIdle();
			}
		}
		
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
