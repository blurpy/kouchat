
package net.usikkert.kouchat.misc;

import java.util.*;

public class SortedNickList implements NickList
{
	private List<Nick> nickList;
	private List<NickListListener> listeners;
	
	public SortedNickList()
	{
		nickList = new ArrayList<Nick>();
		listeners = new ArrayList<NickListListener>();
	}

	public boolean add( Nick nick )
	{
		boolean success = nickList.add( nick );
		
		if ( success )
		{
			Collections.sort( nickList );
			fireNickAdded( nickList.size() -1 );
		}
		
		return success;
	}

	public Nick get( int pos )
	{
		return nickList.get( pos );
	}

	public int indexOf( Nick nick )
	{
		return nickList.indexOf( nick );
	}

	public Nick remove( int pos )
	{
		Nick nick = nickList.remove( pos );
		fireNickRemoved( pos );
		
		return nick;
	}
	
	public boolean remove( Nick nick )
	{
		int pos = nickList.indexOf( nick );
		boolean success = nickList.remove( nick );
		fireNickRemoved( pos );
		
		return success;
	}

	public Nick set( int pos, Nick nick )
	{
		Nick oldNick = nickList.set( pos, nick );
		Collections.sort( nickList );
		fireNickChanged( pos );
		
		return oldNick;
	}

	public int size()
	{
		return nickList.size();
	}
	
	public void addNickListListener( NickListListener listener )
	{
		listeners.add( listener );
	}
	
	public void removeNickListListener( NickListListener listener )
	{
		listeners.remove( listener );
	}
	
	private void fireNickAdded( int pos )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			NickListListener listener = listeners.get( i );
			listener.nickAdded( pos );
		}
	}
	
	private void fireNickChanged( int pos )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			NickListListener listener = listeners.get( i );
			listener.nickChanged( pos );
		}
	}
	
	private void fireNickRemoved( int pos )
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			NickListListener listener = listeners.get( i );
			listener.nickRemoved( pos );
		}
	}
}
