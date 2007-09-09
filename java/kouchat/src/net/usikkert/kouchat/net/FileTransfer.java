
package net.usikkert.kouchat.net;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.Nick;

public interface FileTransfer
{
	public enum Direction { SEND, RECEIVE };
	public Direction getDirection();
	public String getFileName();
	public Nick getNick();
	public int getPercent();
	public long getTransferred();
	public long getFileSize();
	public void cancel();
	public boolean isCanceled();
	public boolean isTransferred();
	public boolean transfer();
	public void registerListener( FileTransferListener listener );
}
