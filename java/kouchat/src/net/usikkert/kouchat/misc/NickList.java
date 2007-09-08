
package net.usikkert.kouchat.misc;

import net.usikkert.kouchat.event.NickListListener;

public interface NickList
{
	public boolean add( Nick nick );
	public Nick get( int pos );
	public int indexOf( Nick nick );
	public Nick remove( int pos );
	public boolean remove( Nick nick );
	public Nick set( int pos, Nick nick );
	public int size();
	public void addNickListListener( NickListListener listener );
	public void removeNickListListener( NickListListener listener );
}
