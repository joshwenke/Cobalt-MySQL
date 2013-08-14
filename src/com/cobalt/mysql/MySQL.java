/** 
 * Copyright 2013 Cobalt Network, LLC
 */
 
package com.cobalt.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MySQL
{
	private Main plugin;
	public MySQL(Main i) { plugin = i; }
	static private Connection connect = null;
	static private Statement statement = null;
	static private ResultSet resultSet = null;
	private static PreparedStatement preparedStatement = null;
	
	public void start()
	{
		if(this.startDriver())
		{
			if(this.openConnection())
			{
				plugin.log("Connected with MySQL database");
				this.createTable();
			}else
			{
				plugin.log.warning("[Cobalt-MySQL] Failed starting MySQL, shutting down...");
				Bukkit.getServer().getPluginManager().disablePlugin(plugin);
			}
		}else
		{
			plugin.log.warning("[Cobalt-MySQL] Failed starting MySQL, shutting down...");
			Bukkit.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
	
	public void stop()
	{
		if(this.stopConnection())
		{
			plugin.log("Stopped connection with MySQL");
		}else
		{
			plugin.log.warning("[Cobalt-MySQL] Failed stopping MySQL, shutting down...");
			Bukkit.getServer().getPluginManager().disablePlugin(plugin);
		}
			
	}
	
	public void updatePlayers()
	{
		try
		{
			statement.executeUpdate("TRUNCATE TABLE players;");
			for(Player player : Bukkit.getServer().getOnlinePlayers())
			{
				String pname = player.getName();
				preparedStatement = connect.prepareStatement("INSERT INTO players(username) VALUES(?);");
				preparedStatement.setString(1, pname);
				preparedStatement.executeUpdate();
				plugin.log("Added "+pname+" to database");
			}
		} catch(SQLException e)
		{
			plugin.log.warning("[Cobalt-MySQL] Failed saving players connection:");
			plugin.log.warning(e.getMessage());
		}
	}
	
	public boolean createTable()
	{
		try {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS players (username VARCHAR(250) NOT NULL UNIQUE);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS plugins (plugin VARCHAR(250) NOT NULL UNIQUE);");
		} catch (SQLException e) {
			plugin.log.warning("[Cobalt-MySQL] Failed creating table:");
			plugin.log.warning(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean openConnection()
	{
		try
		{
			String host = plugin.getConfig().getString("MySQL.host", "localhost");
			String port = String.valueOf(plugin.getConfig().getInt("MySQL.port", 3306));
			String databse = plugin.getConfig().getString("MySQL.database", "cobalt");
			String username = plugin.getConfig().getString("MySQL.username", "root");
			String pass = plugin.getConfig().getString("MySQL.password", "8728le");
			connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port +"/" + databse + "?" +
					"user=" + username + "&password=" + pass);
			statement = connect.createStatement();
		} catch(SQLException e)
		{
			plugin.log.warning("[Cobalt-MySQL] Failed loading connection:");
			plugin.log.warning(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean startDriver()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			plugin.log("Loaded MySQL driver");
		} catch(ClassNotFoundException e)
		{
			plugin.log.warning("[Cobalt-MySQL] Failed starting driver:");
			plugin.log.warning(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean stopConnection()
	{
		try
		{
			if(statement != null)
				statement.close();
			if(resultSet != null)
				resultSet.close();
			if(connect != null)
				connect.close();
		} catch(SQLException e)
		{
			plugin.log.warning("[Cobalt-MySQL] stopping driver:");
			plugin.log.warning(e.getMessage());
			return false;
		}
		return true;
	}
}
