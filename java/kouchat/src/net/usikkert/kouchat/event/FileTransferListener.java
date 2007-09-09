
package net.usikkert.kouchat.event;

public interface FileTransferListener
{
	public void statusWaiting();
	public void statusConnecting();
	public void statusTransfering();
	public void statusCompleted();
	public void statusFailed();
	public void transferUpdate();
}
