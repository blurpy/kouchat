
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

package net.usikkert.kouchat.net;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.util.Loggers;

/**
 * This thread is responsible for keeping the application connected
 * to the network.
 *
 * Every now and then, the thread will check if there are better
 * networks available, and reconnect to that network instead.
 *
 * @author Christian Ihle
 */
public class ConnectionWorker implements Runnable
{
	/** The logger. */
	private static final Logger LOG = Loggers.NETWORK_LOG;

	/** Period of time to sleep if network is up. 60 sec. */
	private static final int SLEEP_UP = 1000 * 60;

	/** Period of time to sleep if network is down. 15 sec. */
	private static final int SLEEP_DOWN = 1000 * 15;

	/** Indicates whether the thread should run or not. */
	private boolean run;

	/** Whether the network is up or not. */
	private boolean networkUp;

	/** The current network interface. */
	private NetworkInterface networkInterface;

	/** The working thread. */
	private Thread worker;

	/** A list of connection listeners. */
	private final List<NetworkConnectionListener> listeners;

	/** For locating the operating system's choice of network interface. */
	private final OperatingSystemNetworkInfo osNetworkInfo;

	/**
	 * Constructor.
	 */
	public ConnectionWorker()
	{
		listeners = new ArrayList<NetworkConnectionListener>();
		osNetworkInfo = new OperatingSystemNetworkInfo();
	}

	/**
	 * The thread. Responsible for keeping the best possible
	 * network connection up, and notifies listeners of any changes.
	 */
	@Override
	public void run()
	{
		LOG.log( Level.FINE, "Network is starting" );

		while ( run )
		{
			try
			{
				NetworkInterface netif = selectNetworkInterface();

				// No network interface to connect with
				if ( !NetworkUtils.isUsable( netif ) )
				{
					LOG.log( Level.FINE, "Network is down, sleeping" );
					// To avoid notifying about this every 15 seconds
					if ( networkUp )
						notifyNetworkDown();
					Thread.sleep( SLEEP_DOWN );
					continue;
				}

				// Switching network interface, like going from cable to wireless
				else if ( isNewNetworkInterface( netif ) )
				{
					String origNetwork = networkInterface == null ? "[null]" : networkInterface.getName();
					LOG.log( Level.FINE, "Changing network from " + origNetwork + " to " + netif.getName() );
					networkInterface = netif;
					if ( networkUp )
						notifyNetworkDown();
					notifyNetworkUp();
				}

				// If the connection was lost, like unplugging cable, and plugging back in
				else if ( !networkUp )
				{
					LOG.log( Level.FINE, "Network " + netif.getName() + " is up again" );
					networkInterface = netif;
					notifyNetworkUp();
				}

				// else - everything is normal

				Thread.sleep( SLEEP_UP );
			}

			// Sleep interrupted - probably from stop()
			catch ( final InterruptedException e )
			{
				LOG.log( Level.FINE, e.toString() );
			}

			// Hopefully not an issue (don't know), just try again next round.
			catch ( final SocketException e )
			{
				LOG.log( Level.WARNING, e.toString(), e );
			}
		}

		LOG.log( Level.FINE, "Network is stopping" );
		if ( networkUp )
			notifyNetworkDown();
		networkInterface = null;
	}

	/**
	 * Compares <code>netif</code> with the current network interface.
	 *
	 * @param netif The new network interface to compare against the original.
	 * @return True if netif is new.
	 */
	private boolean isNewNetworkInterface( final NetworkInterface netif )
	{
		return !NetworkUtils.sameNetworkInterface( netif, networkInterface );
	}

	/**
	 * Notifies all the listeners that the network is up.
	 */
	private synchronized void notifyNetworkUp()
	{
		networkUp = true;

		for ( NetworkConnectionListener listener : listeners )
		{
			listener.networkCameUp();
		}
	}

	/**
	 * Notifies all the listeners that the network is down.
	 */
	private synchronized void notifyNetworkDown()
	{
		networkUp = false;

		for ( NetworkConnectionListener listener : listeners )
		{
			listener.networkWentDown();
		}
	}

	/**
	 * Registers the listener as a connection listener.
	 *
	 * @param listener The listener to register.
	 */
	public void registerNetworkConnectionListener( final NetworkConnectionListener listener )
	{
		listeners.add( listener );
	}

	/**
	 * Starts a new thread if no thread is already running.
	 */
	public synchronized void start()
	{
		if ( !run && !isAlive() )
		{
			run = true;
			worker = new Thread( this, "ConnectionWorker" );
			worker.start();
		}
	}

	/**
	 * Stops the thread.
	 */
	public void stop()
	{
		run = false;

		if ( worker != null )
			worker.interrupt();
	}

	/**
	 * Locates the best network interface to use.
	 *
	 * <p>The operating system's choice of network interface is prioritized,
	 * but if the interface is not seen as usable, then the first usable
	 * interface in the list of available choices is used instead.</p>
	 *
	 * <p>If no usable network interfaces are found, then <code>null</code>
	 * is returned.</p>
	 *
	 * @return The network interface found, or <code>null</code>.
	 * @throws SocketException In case of network issues.
	 * @see NetworkUtils#isUsable(NetworkInterface)
	 */
	private NetworkInterface selectNetworkInterface() throws SocketException
	{
		NetworkInterface firstUsableNetIf = findFirstUsableNetworkInterface();

		if ( firstUsableNetIf == null )
		{
			LOG.log( Level.FINER, "No usable network interface detected." );
			return null;
		}

		NetworkInterface osNetIf = osNetworkInfo.getOperatingSystemNetworkInterface();

		if ( NetworkUtils.isUsable( osNetIf ) )
		{
			LOG.log( Level.FINER, "Using operating system's choice of network interface." );
			return osNetIf;
		}

		LOG.log( Level.FINER, "Overriding operating system's choice of network interface." );
		return firstUsableNetIf;
	}

	/**
	 * Iterates through a list of available network interfaces, and returns
	 * the first which is usable.
	 *
	 * @return The first usable network interface.
	 * @throws SocketException In case of network issues.
	 * @see NetworkUtils#isUsable(NetworkInterface)
	 */
	private NetworkInterface findFirstUsableNetworkInterface() throws SocketException
	{
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		// Because null is returned if no network interfaces are found
		if ( networkInterfaces == null )
			return null;

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();

			if ( NetworkUtils.isUsable( netif ) )
				return netif;
		}

		return null;
	}

	/**
	 * Finds the current network interface.
	 *
	 * @return The current network interface.
	 */
	public NetworkInterface getCurrentNetworkInterface()
	{
		NetworkInterface updatedNetworkInterface = null;

		try
		{
			updatedNetworkInterface = NetworkUtils.getUpdatedNetworkInterface( networkInterface );
		}

		catch ( final SocketException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		if ( updatedNetworkInterface != null )
			return updatedNetworkInterface;

		return networkInterface;
	}

	/**
	 * Checks if the network is up.
	 *
	 * @return If the network is up.
	 */
	public boolean isNetworkUp()
	{
		return networkUp;
	}

	/**
	 * Checks if the thread is alive.
	 *
	 * @return If the thread is alive.
	 */
	public boolean isAlive()
	{
		if ( worker == null )
			return false;
		else
			return worker.isAlive();
	}
}
