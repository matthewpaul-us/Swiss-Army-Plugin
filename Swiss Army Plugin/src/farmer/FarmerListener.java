/**
 * ---------------------------------------------------------------------------
 * File name: FarmerListener.java<br/>
 * Project name: com.gmail.mpaul0416.SwissArmyPlugin<br/>
 * ---------------------------------------------------------------------------
 * Creator's name and email: Matthew Paul, paulmr@goldmail.etsu.edu<br/>
 * Course:  CSCI ____<br/>
 * Creation Date: Jan 2, 2013<br/>
 * Date of Last Modification: Jan 2, 2013
 * ---------------------------------------------------------------------------
 */

package farmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import util.ConfigurationHandler;
import util.MetadataHandler;



/**
 * Listener for the auto-farmer portion<br>
 *
 * <hr>
 * Date created: Jan 2, 2013<br>
 * Date last modified: Jan 2, 2013<br>
 * <hr>
 * @author Matthew Paul
 */
public class FarmerListener implements Listener {

	
	private  String farmerStatusMkey;
	private MetadataHandler metaHandler;
	private ConfigurationHandler configHandler;

	// Config file keys
	private final String MKEY_STRING = "farmer.metadataKey";
	
	// Default Values
	private static final String FARMER_STATUS = "farmerStatus";
	
	// Mapping for crops
	private HashMap<Material, Material> seedCropMap;

	/**
	 * Constructor <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 4, 2013 <br>
	 *
	 * <hr>
	 * @param metaHandler - the Metadata handler to use.
	 * @param configHandler - The configuration handler to use.
	 */
	public FarmerListener(MetadataHandler metaHandler, 
			ConfigurationHandler configHandler)
	{
		this.metaHandler = metaHandler;
		this.configHandler = configHandler;

		// Read in the file configuration
		loadSettingsFromConfigFile();
		
		// Initialize the hashmap
		initializeSeedCropMap();
		
	}

	/**
	 * Initializes the mapping between the seeds and the crops. <br>        
	 *
	 * <hr>
	 * Date created: Jan 5, 2013 <br>
	 * Date last modified: Jan 5, 2013 <br>
	 *
	 * <hr>
	 */
	private void initializeSeedCropMap() {
		seedCropMap = new HashMap<Material, Material>();
		
		//Add the values
		seedCropMap.put(Material.SEEDS, Material.CROPS);
		seedCropMap.put(Material.POTATO_ITEM, Material.POTATO);
		seedCropMap.put(Material.CARROT_ITEM, Material.CARROT);
	}

	/**
	 * Look for the farmer portion of the plugin.yml <br>        
	 *
	 * <hr>
	 * Date created: Jan 4, 2013 <br>
	 * Date last modified: Jan 4, 2013 <br>
	 *
	 * <hr>
	 */
	private void loadSettingsFromConfigFile() {
		// Load the metadata key from the config
		farmerStatusMkey = configHandler.safeRetrieve(MKEY_STRING, FARMER_STATUS);
	}

	/**
	 * Makes sure the player has farmer metadata when the login <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if (!((boolean) metaHandler.hasData(player, farmerStatusMkey)))
		{
			metaHandler.setData(player, farmerStatusMkey, false);
		}
	}

	/**
	 * Causes tilled soil to be filled with seed from the player. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param event
	 */
	@EventHandler
	public void onPlayerPlantSeed(PlayerInteractEvent event)
	{
		// Check to make sure it has a block
		if (event.hasBlock()) {
			// Get the specified player
			Player player = event.getPlayer();

			// Get the material in hand
			ItemStack equippedItem = player.getItemInHand();
			// Get the block hit
			Block block = event.getClickedBlock();

			// If the player is a farmer, has seeds equipped, and right-clicks the soil
			// auto-plant the seeds.
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
					isFarmer(player) &&
					isCropSeed(equippedItem) &&
					isSoil(block)) {
				recursivePlant(player, block);
			}
		}
	}

	@EventHandler
	public void onPlayerHarvestWheat(BlockBreakEvent event)
	{
		Block block = event.getBlock();

		// If we broke wheat that is ready to be harvested...
		if (isCropInState(block, CropState.RIPE)) {

			// Get the block below
			block = block.getRelative(BlockFace.DOWN);

			// If the player is a farmer, harvest the wheat
			if (isFarmer(event.getPlayer())) {
				// Recursively harvest the wheat
				recursiveHarvest(block, new ArrayList<Block>());
			}
		}
	}
	
	/**
	 * Determine whether crop is in a certain crop state. <br>        
	 *
	 * <hr>
	 * Date created: Jan 3, 2013 <br>
	 * Date last modified: Jan 3, 2013 <br>
	 *
	 * <hr>
	 * @param block - The block to examine
	 * @param cropState - The crop state to match. If null, crop state will not matter.
	 */
	private boolean isCropInState(Block block, CropState cropState){
		return isCropBlock(block) && 
				(cropState != null? block.getData() == cropState.getData():
					true);
	}

	@EventHandler
	public void onPlayerApplyBoneMeal(PlayerInteractEvent event)
	{
		// Make sure the event has a block associated with it
		if (event.hasBlock()) {
			// Get the player
			Player player = event.getPlayer();

			// Get the held item
			ItemStack equippedItem = player.getItemInHand();

			// Get the block hit
			Block block = event.getClickedBlock();

			// if seeds were clicked
			if (isFarmer(player) &&
					isPlayerGrowingWheatWithBonemeal(block, equippedItem, event.getAction())) {
				recursiveBonemeal(player, block, new ArrayList<Block>());
			}
		}
	}

	/**
	 * Recursively Apply bonemeal to all non-ripe wheat in a contiguous area. <br>        
	 *
	 * <hr>
	 * Date created: Jan 3, 2013 <br>
	 * Date last modified: Jan 3, 2013 <br>
	 *
	 * <hr>
	 * @param player - player to remove bonemeal from
	 * @param block - block to apply bonemeal to
	 * @param visitedBlocks - list containing already visited blocks
	 */
	private void recursiveBonemeal(Player player, Block block,
			ArrayList<Block> visitedBlocks) {

		// if it is crops and has not been visited before, handle it
		if(isCropInState(block, null) && !visitedBlocks.contains(block))
		{
			// Add the block to prevent double counting
			visitedBlocks.add(block);

			// Get the item equipped
			ItemStack equippedItem = player.getItemInHand();

			// If the player has bonemeal and the block isn't ripe, it needs bonemeal
			if (isBoneMeal(equippedItem) &&
					!isCropInState(block, CropState.RIPE)) {
				
				// If the player is in survival mode, decrement bonemeal
				if (player.getGameMode() == GameMode.SURVIVAL) {
					util.Utility.decrementItemInHandAmount(player);
				}
//				block.setTypeIdAndData(block.getTypeId(), CropState.RIPE.getData(), true);
				block.setData(CropState.RIPE.getData());

			}

			// Recurse through each cardinal direction

			Block targetBlock = block.getRelative(BlockFace.NORTH);
			recursiveBonemeal(player, targetBlock, visitedBlocks);

			targetBlock = block.getRelative(BlockFace.SOUTH);
			recursiveBonemeal(player, targetBlock, visitedBlocks);

			targetBlock = block.getRelative(BlockFace.EAST);
			recursiveBonemeal(player, targetBlock, visitedBlocks);

			targetBlock = block.getRelative(BlockFace.WEST);
			recursiveBonemeal(player, targetBlock, visitedBlocks);
		}
	}

	private boolean isBoneMeal(ItemStack equippedItem) {
		return equippedItem.getType() == Material.INK_SACK && 	// Check if it's bonemeal like
				equippedItem.getData().equals(new MaterialData(Material.INK_SACK, (byte) 15)); // Check if its bonemeal;
	}

	private boolean isPlayerGrowingWheatWithBonemeal(Block block,
			ItemStack equippedItem, Action action)
	{
		return isCropInState(block, null) &&	// Check if its crops
				!isCropInState(block, CropState.RIPE) &&	// Check if it's not ripe
				isBoneMeal(equippedItem) &&	// Check if its bonemeal
				action.equals(Action.RIGHT_CLICK_BLOCK);	// Check if the player right clicked it
	}

	/**
	 * Recursively harvest wheat that is ready. Checks all attached farm land. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param block - The block below the crops to harvest.
	 * @param visitedBlocks - list containing the blocks already visited.
	 */
	private void recursiveHarvest(Block block, List<Block> visitedBlocks) {
		// if the block is a piece of tilled soil
		if (isSoil(block) && !(visitedBlocks.contains(block))) {
			// Add the block so it doesn't get counted twice
			visitedBlocks.add(block);
			// Get the block above
			Block blockAbove = block.getRelative(BlockFace.UP);

			// See if it is a crop that needs harvesting
			if (isCropInState(blockAbove, CropState.RIPE)) {
				// Break the blocks above
				blockAbove.breakNaturally();
			}

			// Recursively look at the other blocks
			Block targetBlock = block.getRelative(BlockFace.NORTH);
			recursiveHarvest(targetBlock, visitedBlocks);

			targetBlock = block.getRelative(BlockFace.SOUTH);
			recursiveHarvest(targetBlock, visitedBlocks);

			targetBlock = block.getRelative(BlockFace.EAST);
			recursiveHarvest(targetBlock, visitedBlocks);

			targetBlock = block.getRelative(BlockFace.WEST);
			recursiveHarvest(targetBlock, visitedBlocks);
		}
	}

	/**
	 * Auto-plants seeds on tilled ground recursively. It continues until there is
	 * no more seed, or the adjoining spaces do not have tilled soil. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param player - the player to take the seeds from
	 * @param block - the block to recursively plant on
	 */
	private void recursivePlant(Player player, Block block) {
		
		// Get the seeds in the players hand
		ItemStack seedsInHand = player.getItemInHand();
		
		// Get the material to plant with
		Material cropToPlant = seedCropMap.get(seedsInHand.getType());

		
		// If the block is soil
		if (isSoil(block) && 
				block.getRelative(BlockFace.UP).getType() == Material.AIR &&
				isCropSeed(seedsInHand)) {

			// plant the seeds 
			
			block.getRelative(BlockFace.UP).setType(cropToPlant);
			
			// reduce the amount of seeds by one, if the player is in survival mode
			if (player.getGameMode() == GameMode.SURVIVAL) {
				player.sendMessage("Decrementing item in hand");
				util.Utility.decrementItemInHandAmount(player);
			}


			// Recursively plant
			Block targetBlock = block.getRelative(BlockFace.NORTH);
			recursivePlant(player, targetBlock);

			targetBlock = block.getRelative(BlockFace.SOUTH);
			recursivePlant(player, targetBlock);

			targetBlock = block.getRelative(BlockFace.EAST);
			recursivePlant(player, targetBlock);

			targetBlock = block.getRelative(BlockFace.WEST);
			recursivePlant(player, targetBlock);
		}
	}

	/**
	 * Determines whether the player has an entry for the farmer plugin. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param player - player to check
	 * @return true if player has farmer entery, false otherwise.
	 */
	public boolean hasFarmerKey(Player player)
	{
		return metaHandler.hasData(player, farmerStatusMkey);
	}

	/**
	 * Sets the status of the specified player <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param player - the player to set the farmer status of
	 * @param farmerStatus - true if player is a farmer, false otherwise.
	 */
	public void setFarmer(Player player, boolean farmerStatus)
	{
		metaHandler.setData(player, farmerStatusMkey, farmerStatus);
	}

	/**
	 * Determines whether the specified player is a farmer. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param player - the player to check
	 * @return true if player is farmer, false otherwise.
	 */
	public boolean isFarmer(Player player)
	{
		boolean isFarmer = false;
		// Make sure he has farmer metadata before we read it
		if (metaHandler.hasData(player, farmerStatusMkey)) {
			// Get the status of the farmer metadata
			isFarmer = metaHandler.getData(player, farmerStatusMkey).get(0).asBoolean();
		}
		else {
			throw new IllegalStateException(String.format("Player %s has no farmer metadata!",
					player.getName()));
		}

		return isFarmer;
	}

	public void notifyFarmer(Player player)
	{
		player.sendMessage(ChatColor.GREEN + String.format(
				"You are%sa farmer", 
				(isFarmer(player)? " " : " not ")));
	}

	/**
	 * Determines whether the block clicked is tilled or not. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param block - the block to check
	 * @return true if it is tilled, false otherwise.
	 */
	private boolean isSoil(Block block)
	{
		return block.getType() == Material.SOIL;
	}

	/**
	 * Determines whether the specified item is plantable. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 4, 2013 <br>
	 *
	 * <hr>
	 * @param item - item to check
	 * @return true if it is a crop, false otherwise.
	 */
	private boolean isCropSeed(ItemStack item)
	{
		// Make sure there is an item, if not, the player's not holding anything
		if (item != null) {
			// Return true if the item is a crop, false otherwise
			return seedCropMap.keySet().contains(item.getType());
		}
		else {
			return false;
		}
	}
	
	/**
	 * Determines whether it is a crop dealt with in this plugin <br>        
	 *
	 * <hr>
	 * Date created: Jan 5, 2013 <br>
	 * Date last modified: Jan 5, 2013 <br>
	 *
	 * <hr>
	 * @param block
	 * @return
	 */
	private boolean isCropBlock(Block block)
	{
		// See if the crop is in the seedCropMap's values
		return seedCropMap.values().contains(block.getType());
	}
	
	
}
