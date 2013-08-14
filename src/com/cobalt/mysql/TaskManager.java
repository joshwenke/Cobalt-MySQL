/** 
 * Copyright 2013 Cobalt Network, LLC
 */

package com.cobalt.mysql;

public class TaskManager
{
	private Main plugin;
	private MySQL mysql;
	public TaskManager(Main i)
	{
		plugin = i;
		mysql = plugin.mysql;
	}
	
	public void start()
	{
		if(mysql != null)
		{
			this.mainTask();
		}
	}
	
	public void mainTask()
	{
		int delay = plugin.getConfig().getInt("settings.task-delay (min)");
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				MySQL mysql = TaskManager.this.mysql;
				mysql.openConnection();
				mysql.updatePlayers();
				mysql.stopConnection();
			}
		}
		, delay * 1200, delay * 1200);
	}
}
