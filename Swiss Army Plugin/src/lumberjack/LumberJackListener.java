/**
 * ---------------------------------------------------------------------------
 * File name: LumberjackListener.java<br/>
 * Project name: com.gmail.mpaul0416.SwissArmyPlugin<br/>
 * ---------------------------------------------------------------------------
 * Creator's name and email: Matthew Paul, mpaul0416@gmail.com<br/>
 * Creation Date: Jan 2, 2013<br/>
 * Date of Last Modification: Jan 3, 2013
 * ---------------------------------------------------------------------------
 */

package lumberjack;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import util.ConfigurationHandler;
import util.MetadataHandler;
import util.Utility;


/**
 * Listener for the lumberjack functionality. Listens for a login event, to check
 * for the appropriate metadata, and a player interact event, to check if we need
 * to handle the tree or not. <br>
 *
 * <hr>
 * Date created: Jan 1, 2013<br>
 * Date last modified: Jan 3, 2013<br>
 * <hr>
 * @author Matthew Paul
 */
public class LumberJackListener implements Listener
{
	
	/**
	 * The cost on durability of one log or one unit of leaves
	 */
	private int autoLoggerItemDurabilityCost;
	/**
	 * The metadata key used to store the lumberjack status
	 */
	private String lumberjackStatusMkey;

	/**
	 * A number containing the number of leaves broken multiplied by the cost.
	 * When it is over one, it will subtract the whole part and simulate that many uses on the tool.
	 */
	private double leafCount;
	
	/**
	 * The durability cost of one block of leaves
	 */
	private double leafCost;

	// Config file keys
	private final String MKEY_STRING = "lumberjack.metadataKey",
			LOG_COST_STRING = "lumberjack.logDurabilityCost",
			LEAF_COST_STRING = "lumberjack.leafDurabilityCost";

	// Default Values
	private static final double DEFAULT_LEAF_COST = 0.2;
	private static final int DEFAULT_LOG_COST = 1;
	private static final String DEFAULT_LUMBERJACK_STATUS = "lumberjackStatus";
	
	/**
	 * Convenience metadata handler
	 */
	private MetadataHandler metaHandler;
	private ConfigurationHandler confighandler;


	/**
	 * Constructor <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param handler - metadata handler to use
	 * @param configHandler - Configuration handler to use
	 */
	public LumberJackListener(MetadataHandler handler, ConfigurationHandler configHandler)
	{
		this.metaHandler = handler;
		this.confighandler = configHandler;

		loadSettingsFromConfigFile();
	}

	/**
	 * Load the settings from the lumberjack portion of the plugin.yml <br>        
	 *
	 * <hr>
	 * Date created: Jan 3, 2013 <br>
	 * Date last modified: Jan 4, 2013 <br>
	 *
	 * <hr>
	 */
	private void loadSettingsFromConfigFile() {
		// Load the metadata key from the config
		lumberjackStatusMkey = confighandler.safeRetrieve(MKEY_STRING, DEFAULT_LUMBERJACK_STATUS);

		// Load the durability cost from the config
		autoLoggerItemDurabilityCost = confighandler.safeRetrieve(LOG_COST_STRING, DEFAULT_LOG_COST);
		
		// Load the durability cost for leaves from the config
		leafCost = confighandler.safeRetrieve(LEAF_COST_STRING, DEFAULT_LEAF_COST);
	}
	

	/**
	 * Checks for the appropriate metadata when the player joins. If they don't
	 * have it, then it gives it to them. <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @param event - event that fired this handler
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if (!((boolean) metaHandler.hasData(player, lumberjackStatusMkey)))
		{
			metaHandler.setData(player, lumberjackStatusMkey, false);
		}
	}

	/**
	 * Perform the auto-lumberjack option if the player is a lumberjack, has an axe equipped, and 
	 * breaks a log part. <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param event
	 */
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		// Get the player involved
		Player player = event.getPlayer();

		// Get the material in the player's hand
		ItemStack equippedItem = player.getItemInHand();
		// Get the block hit
		Block block = event.getBlock();

		// If the player is a lumberjack and has an axe equipped and hit a wood block
		// remove the tree.
		if (checkIfLumberjack(player) && isAxe(equippedItem)
				&& isWoodBlock(block)) {
			removeTree(player, block);
		}
	}

	/**
	 * Turns lumberjack status off or on. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param player - player to change the status of
	 * @param isLumberjack - True if lumberjack. False otherwise.
	 */
	public void setLumberjack(Player player, boolean isLumberjack)
	{
		metaHandler.setData(player, lumberjackStatusMkey, isLumberjack);
	}

	/**
	 * Determines whether the specified player has lumberjack metadata <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param player - the player to check
	 * @return True if the player has data, false otherwise
	 */
	public boolean hasLumberjackKey(Player player)
	{
		return metaHandler.hasData(player, lumberjackStatusMkey);
	}

	/**
	 * Tells the player if he or she has lumberjack status. <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @param player - The player to notify.
	 * @param isLumberjack - True if player is a lumberjack, false otherwise
	 */
	public void notifyLumberjack(Player player) {
		player.sendMessage(ChatColor.GREEN + String.format(
				"You are%sa lumberjack", 
				(checkIfLumberjack(player)? " " : " not ")));
	}

	/**
	 * Determines whether or not the player is in lumberjack mode. <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @param player
	 * @return true if player is a lumberjack, false otherwise.
	 */
	public boolean checkIfLumberjack(Player player) {
		boolean isLumberjack = false;
		// Make sure he has lumberjack metadata before we read it
		if (metaHandler.hasData(player, lumberjackStatusMkey)) {
			// Get the status of the lumberjack metadata
			isLumberjack = metaHandler.getData(player, lumberjackStatusMkey).get(0).asBoolean();
		}
		else {
			throw new IllegalStateException(String.format("Player %s has no lumberjack metadata!",
					player.getName()));
		}

		return isLumberjack;
	}

	/**
	 * Determines whether or not the player is in lumberjack mode. <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @param player - player to check
	 * @return true if player is a lumberjack, false otherwise.
	 */
	public boolean isLumberjack(Player player) {
		//Get whether the player is a lumberjack or not
		boolean isLumberjack = checkIfLumberjack(player);

		return isLumberjack;
	}


	private void removeTree(Player player, Block block)
	{
		LinkedList<Block> blockQueue = new LinkedList<Block>();
		blockQueue.add(block);
		
		// While the player still has an axe and there are blocks
		// left, keep removing stuff.
		while (isAxe(player.getItemInHand()) &&
				!blockQueue.isEmpty()) {
			// Get the block in question
			block = blockQueue.pop();
			
			// Make sure it's a wood/leaf block before tearing it up
			if (isWoodBlock(block) ||
					block.getType() == Material.LEAVES) {
				// Break the block
				block.breakNaturally();
				// Weaken the axe if player is in survival mode
				if (player.getGameMode() == GameMode.SURVIVAL) {
					if (block.getType() == Material.LEAVES) {
						leafCount += leafCost;
						if (leafCount >= 1) {
							Utility.weakenToolInHand(
									player,
									(short) (autoLoggerItemDurabilityCost * (int) leafCount));
							leafCount -= (int) leafCount;
						}
					} else {
						Utility.weakenToolInHand(player,
								(short) autoLoggerItemDurabilityCost);
					}
				}
				// Add the adjacent blocks to the queue
				blockQueue.add(block.getRelative(BlockFace.UP));
				blockQueue.add(block.getRelative(BlockFace.DOWN));
				blockQueue.add(block.getRelative(BlockFace.NORTH));
				blockQueue.add(block.getRelative(BlockFace.SOUTH));
				blockQueue.add(block.getRelative(BlockFace.EAST));
				blockQueue.add(block.getRelative(BlockFace.WEST));
			}
		}
	}

	/**
	 * Checks whether the item in question is an axe or not <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @param item - item to check
	 * @return true if item is an axe, false otherwise
	 */
	private boolean isAxe(ItemStack item)
	{
		// Get the material that the item is made of
		Material itemMaterial = item.getType();

		// Return true if the item is an axe, false otherwise
		return itemMaterial == Material.DIAMOND_AXE ||
				itemMaterial == Material.IRON_AXE ||
				itemMaterial == Material.GOLD_AXE ||
				itemMaterial == Material.STONE_AXE ||
				itemMaterial == Material.WOOD_AXE;
	}

	/**
	 * Checks whether the block in question is wood <br>        
	 *
	 * <hr>
	 * Date created: Jan 1, 2013 <br>
	 * Date last modified: Jan 1, 2013 <br>
	 *
	 * <hr>
	 * @param block - block to check
	 * @return True if the block is wood. False otherwise.
	 */
	private boolean isWoodBlock(Block block)
	{

		// Get the material that the block is
		Material mat = block.getType();

		// Return if the block is wood or not
		return mat == Material.LOG; 
	}
}

