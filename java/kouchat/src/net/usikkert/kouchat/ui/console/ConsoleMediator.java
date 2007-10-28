
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

package net.usikkert.kouchat.ui.console;

import java.io.File;

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.NickDTO;
import net.usikkert.kouchat.misc.UIMessages;
import net.usikkert.kouchat.misc.UserInterface;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;

public class ConsoleMediator implements UserInterface
{
	private UIMessages uiMsg;
	private MessageController msgController;
	private ConsoleChatWindow chat;
	private Controller controller;
	private ConsoleInput ci;
	
	public ConsoleMediator()
	{
		chat = new ConsoleChatWindow();
		msgController = new MessageController( chat, this );
		uiMsg = new UIMessages( msgController );
		uiMsg.showWelcomeMsg();
		
		controller = new Controller( this );
		ci = new ConsoleInput( controller, this );
	}
	
	public void start()
	{
		controller.logOn();
		ci.input();
	}
	
	@Override
	public boolean askFileSave( String user, String fileName, String size )
	{
		uiMsg.showSaveWith();
		return true;
	}

	@Override
	public void changeAway( boolean away )
	{
		
	}

	@Override
	public void clearChat()
	{
		uiMsg.showNotSupported();
	}

	@Override
	public UIMessages getUIMessages()
	{
		return uiMsg;
	}

	@Override
	public File showFileSave( String fileName )
	{
		return null;
	}

	@Override
	public void showTopic()
	{

	}

	@Override
	public void showTransfer( FileReceiver fileRes )
	{
		new TransferHandler( fileRes );
	}
	
	@Override
	public void showTransfer( FileSender fileSend )
	{
		new TransferHandler( fileSend );
	}

	@Override
	public void notifyMessageArrived()
	{

	}

	@Override
	public void createPrivChat( NickDTO user )
	{

	}

	@Override
	public void notifyPrivateMessageArrived()
	{

	}

	@Override
	public void notifyAwayChanged( boolean away )
	{

	}
}
