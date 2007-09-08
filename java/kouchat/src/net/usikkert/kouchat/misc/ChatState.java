
package net.usikkert.kouchat.misc;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

public class ChatState
{
	private Topic topic;
	private boolean wrote;
	private List<File> fileList;
	
	public ChatState()
	{
		fileList = new ArrayList<File>();
		topic = new Topic();
		wrote = false;
	}

	public boolean isWrote()
	{
		return wrote;
	}

	public void setWrote( boolean wrote )
	{
		this.wrote = wrote;
	}

	public Topic getTopic()
	{
		return topic;
	}

	public List<File> getFileList()
	{
		return fileList;
	}
}
