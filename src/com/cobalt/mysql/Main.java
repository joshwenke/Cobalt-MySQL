package com.cobalt.mysql;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	public Logger log = Logger.getLogger("Minecraft");
	protected MySQL mysql = null;
	
	@Override
	public void onEnable()
	{
		final FileConfiguration config = this.getConfig();
		
		config.addDefault("settings.useFancyConsole", true);
		config.addDefault("settings.task-delay (min)", 5);
		config.addDefault("MySQL.host", "localhost");
		config.addDefault("MySQL.port", 3306);
		config.addDefault("MySQL.database", "cobalt");
		config.addDefault("MySQL.username", "root");
		config.addDefault("MySQL.password", "8728le");
		config.options().copyDefaults(true);
		saveConfig();
		
		mysql = new MySQL(this);
		mysql.start();
		TaskManager task = new TaskManager(this);
		task.start();
	}
	
	public void onDisable()
	{
		if(mysql != null)
			mysql.stop();
	}
	
	public void log(String message)
	{
		if(this.getConfig().getBoolean("settings.useFancyConsole"))
		{
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			ChatColor a = ChatColor.AQUA;
			ChatColor g = ChatColor.GRAY;
			String name = "["+a+this.getDescription().getName()+g+"] ";
			console.sendMessage(name+message);
		}else
		{
			String name = "["+this.getDescription().getName()+"] ";
			log.info(name+message);
		}
	}
}
