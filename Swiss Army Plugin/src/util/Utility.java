/**
 * ---------------------------------------------------------------------------
 * File name: Utility.java<br/>
 * Project name: com.gmail.mpaul0416.SwissArmyPlugin<br/>
 * ---------------------------------------------------------------------------
 * Creator's name and email: Matthew Paul, paulmr@goldmail.etsu.edu<br/>
 * Course:  CSCI ____<br/>
 * Creation Date: Jan 3, 2013<br/>
 * Date of Last Modification: Jan 3, 2013
 * ---------------------------------------------------------------------------
 */

package util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Provides some convenience methods for the swiss army plugin<br>
 *
 * <hr>
 * Date created: Jan 3, 2013<br>
 * Date last modified: Jan 3, 2013<br>
 * <hr>
 * @author Matthew Paul
 */
public class Utility {

	/**
	 * Subtracts one from the item held by the specified player. <br>        
	 *
	 * <hr>
	 * Date created: Jan 3, 2013 <br>
	 * Date last modified: Jan 3, 2013 <br>
	 *
	 * <hr>
	 * @param player - the player to decrement the item from.
	 */
	public static void decrementItemInHandAmount(Player player) {
		// Get the item involved
		ItemStack equippedItem = player.getItemInHand();

		player.sendMessage("Amount before decrement: " + equippedItem.getAmount());

		//Decrement the amount
		equippedItem.setAmount(equippedItem.getAmount() - 1);

		// Check if we need to remove the item because of amount less than one
		if (equippedItem.getAmount() > 0)
		{
			player.setItemInHand(equippedItem);
		}
		else {
			player.setItemInHand(null);
		}
	}

	/**
	 * Weakens the tool in the player's hand by a set amount. <br>        
	 *
	 * <hr>
	 * Date created: Jan 5, 2013 <br>
	 * Date last modified: Jan 5, 2013 <br>
	 *
	 * <hr>
	 * @param player - The player whose tool is to be weakened.
	 * @param amount - The amount to weaken the tool by
	 */
	public static void weakenToolInHand(Player player, short amount)
	{
		ItemStack itemInHand = player.getItemInHand();

		// Adjust the durability accordingly
		itemInHand.setDurability((short) (itemInHand.getDurability() + amount));

		// If the tool is past its breaking point
		if (itemInHand.getDurability() > itemInHand.getType().getMaxDurability()) 
		{
			// Remove the tool from his inventory
			player.setItemInHand(null);
			// Play the tool break sound effect
			player.playSound(player.getLocation(),
					Sound.ITEM_BREAK, 10, 1);
		}
	}
}
