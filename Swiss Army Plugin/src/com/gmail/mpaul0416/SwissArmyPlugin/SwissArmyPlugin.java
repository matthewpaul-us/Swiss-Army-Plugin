package com.gmail.mpaul0416.SwissArmyPlugin;
import lumberjack.LumberJackListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import util.ConfigurationHandler;
import util.MetadataHandler;
import farmer.FarmerListener;

/**
 * ---------------------------------------------------------------------------
 * File name: SwissArmyPlugin.java<br/>
 * Project name: com.gmail.mpaul0416.SwissArmyPlugin<br/>
 * ---------------------------------------------------------------------------
 * Creator's name and email: Matthew Paul, mpaul0416@gmail.com<br/>
 * Creation Date: Jan 1, 2013<br/>
 * Date of Last Modification: Jan 1, 2013
 * ---------------------------------------------------------------------------
 */

/**
 * Provides a number of useful utilities.<br>
 *
 * <hr>
 * Date created: Jan 1, 2013<br>
 * Date last modified: Jan 1, 2013<br>
 * <hr>
 * @author Matthew Paul
 */
public class SwissArmyPlugin extends JavaPlugin {

	private LumberJackListener lumberJackListener;
	private FarmerListener farmerListener;
	
	private MetadataHandler metadataHandler;
	private ConfigurationHandler configHandler;


	/**
	 * Executed when the plugin is enabled. It iterates through the online players and
	 * checks whether or not they have a lumberjack metadata. If they don't, then it
	 * gives them one. <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		// Create a default config if needed
		this.saveDefaultConfig();
		
		// Create the metadata handler
		metadataHandler = new MetadataHandler(this);
		// Create the configuration handler
		configHandler = new ConfigurationHandler(this.getConfig(), this.getLogger(), this);
		
		// Create the lumberjack listener
		lumberJackListener = new LumberJackListener(metadataHandler, configHandler);
		// Create the farmer listener
		farmerListener = new FarmerListener(metadataHandler, configHandler);
		

		// Register the lumberjack and farmer listeners
		getServer().getPluginManager().registerEvents(lumberJackListener, this);
		getServer().getPluginManager().registerEvents(farmerListener, this);

		// check the lumberjack and farmer metadata for all online players
		Player[] playerList = Bukkit.getOnlinePlayers();
		for ( Player player : playerList)
		{
			// if they don't have the lumberjack metadata, give it to them
			if (!(lumberJackListener.hasLumberjackKey(player)))
			{
				lumberJackListener.setLumberjack(player, false);
			}
			
			// if they don't have the farmer metadata, give it to them
			if (!(farmerListener.hasFarmerKey(player)))
			{
				farmerListener.setFarmer(player, false);
			}
		}
	}



	/**
	 * Executed when the plugin is disabled. Currently, it only unregisters
	 * all the listeners used by this plugin. <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		this.saveConfig();
	}

	/**
	 * Handles command execution for the swiss army plugin. <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @param sender - the sender of the command
	 * @param cmd - the command that was sent
	 * @param label
	 * @param args - the arguments for the command
	 * @return - True if the command successfully executed. False otherwise.
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	public boolean onCommand(CommandSender sender, 
			Command cmd, String label, String[] args) {
		// Tree for the lumberjack plugin
		if (cmd.getName().equalsIgnoreCase("lumberjack")) {
			// If a player sent it and they only have one argument...
			if (args.length == 1 && sender instanceof Player) {
				// Get the player and the argument
				Player player = (Player) sender;
				String request = args[0].toLowerCase();

				// If they want to turn on lumberjack status...
				if (request.equals("on")) {
					// Turn on lumberjack for the player
					lumberJackListener.setLumberjack(player, true);
					// Notify the player of their status
					lumberJackListener.notifyLumberjack(player);
				}
				else if (request.equals("off")) {
					// Turn on lumberjack for the player
					lumberJackListener.setLumberjack(player, false);
					// Notify the player of their status
					lumberJackListener.notifyLumberjack(player);
				}
				else if (request.equals("status")) {
					// Notify the player of their status
					lumberJackListener.notifyLumberjack(player);
					}
				else
					return false;

				return true;
			}
			else {
				return false;
			}

		}
		// Tree for the farmer plugin
		else if (cmd.getName().equalsIgnoreCase("farmer")) {
			// If a player sent it and they only have one argument...
			if (args.length == 1 && sender instanceof Player) {
				Player player = (Player) sender;
				String request = args[0].toLowerCase();

				if (request.equals("on")) {
					farmerListener.setFarmer(player, true);
					farmerListener.notifyFarmer(player);
				}
				else if (request.equals("off")) {
					farmerListener.setFarmer(player, false);
					farmerListener.notifyFarmer(player);
				}
				else if (request.equals("status")) {
					farmerListener.notifyFarmer(player);
				}
				else
					return false;

				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

	
	protected static void debugMessage(Player player, String message)
	{
		player.sendMessage(ChatColor.BLUE + message);
	}
}
