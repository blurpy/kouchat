
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

public class ComponentHandler
{
	private SidePanel sidePanel;
	private SettingsDialog settingsDialog;
	private SysTray sysTray;
	private MenuBar menuBar;
	private ButtonPanel buttonPanel;
	private KouChatFrame gui;
	private MainPanel mainPanel;

	public SidePanel getSidePanel()
	{
		return sidePanel;
	}

	public void setSidePanel( final SidePanel sidePanel )
	{
		this.sidePanel = sidePanel;
	}

	public SettingsDialog getSettingsDialog()
	{
		return settingsDialog;
	}

	public void setSettingsDialog( final SettingsDialog settingsDialog )
	{
		this.settingsDialog = settingsDialog;
	}

	public SysTray getSysTray()
	{
		return sysTray;
	}

	public void setSysTray( final SysTray sysTray )
	{
		this.sysTray = sysTray;
	}

	public MenuBar getMenuBar()
	{
		return menuBar;
	}

	public void setMenuBar( final MenuBar menuBar )
	{
		this.menuBar = menuBar;
	}

	public ButtonPanel getButtonPanel()
	{
		return buttonPanel;
	}

	public void setButtonPanel( final ButtonPanel buttonPanel )
	{
		this.buttonPanel = buttonPanel;
	}

	public KouChatFrame getGui()
	{
		return gui;
	}

	public void setGui( final KouChatFrame gui )
	{
		this.gui = gui;
	}

	public MainPanel getMainPanel()
	{
		return mainPanel;
	}

	public void setMainPanel( final MainPanel mainPanel )
	{
		this.mainPanel = mainPanel;
	}
}
