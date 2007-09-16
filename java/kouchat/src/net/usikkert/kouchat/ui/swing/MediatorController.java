
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

import net.usikkert.kouchat.misc.Controller;

/**
 * This is the mediator responsible for updating the other more
 * specialized mediators with components.
 * 
 * @author Christian Ihle
 */
public class MediatorController implements Mediator
{
	private Controller controller;
	private GUIMediator guiMediator;
	private NetworkMediator netMediator;
	
	public MediatorController()
	{
		controller = new Controller();
		
		guiMediator = new GUIMediator( controller );
		netMediator = new NetworkMediator( controller, guiMediator );
	}
	
	@Override
	public void setKouChatFrame( KouChatFrame gui )
	{
		guiMediator.setKouChatFrame( gui );
		netMediator.setKouChatFrame( gui );
	}

	@Override
	public void setMainP( MainPanel mainP )
	{
		guiMediator.setMainP( mainP );
		netMediator.setMainP( mainP );
	}

	@Override
	public void setSysTray( SysTray sysTray )
	{
		guiMediator.setSysTray( sysTray );
		netMediator.setSysTray( sysTray );
	}

	@Override
	public void setMenuBar( MenuBar menuBar )
	{
		guiMediator.setMenuBar( menuBar );
	}

	@Override
	public void setButtonP( ButtonPanel buttonP )
	{
		guiMediator.setButtonP( buttonP );
	}

	@Override
	public void setSideP( SidePanel sideP )
	{
		guiMediator.setSideP( sideP );
	}

	@Override
	public void setSettingsFrame( SettingsFrame settingsFrame )
	{
		guiMediator.setSettingsFrame( settingsFrame );
	}

	@Override
	public GUIListener getGUIListener()
	{
		return guiMediator;
	}
}
