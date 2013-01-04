/**
 * ---------------------------------------------------------------------------
 * File name: MetadataHandler.java<br/>
 * Project name: com.gmail.mpaul0416.SwissArmyPlugin<br/>
 * ---------------------------------------------------------------------------
 * Creator's name and email: Matthew Paul, mpaul0416@gmail.com<br/>
 * Creation Date: Jan 2, 2013<br/>
 * Date of Last Modification: Jan 2, 2013
 * ---------------------------------------------------------------------------
 */

package util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Wraps up the metadata handling for a speicic plugin.<br>
 *
 * <hr>
 * Date created: Jan 2, 2013<br>
 * Date last modified: Jan 2, 2013<br>
 * <hr>
 * @author Matthew Paul
 */
public class MetadataHandler {
	/**
	 * plugin the metadata handler has responsibility over.
	 */
	private JavaPlugin plugin;

	
	public JavaPlugin getPlugin() {
		return plugin;
	}

	/**
	 * Constructor <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param plugin - plugin the handler is responsible for.
	 */
	public MetadataHandler(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}

	/**
	 * Determine whether the object has a specified key. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param object - The object to check
	 * @param key - The metadata key to check
	 * @return true if it has the key, false otherwise.
	 */
	public <T extends Metadatable> boolean hasData(T object, String key)
	{
		return object.hasMetadata(key);
	}

	/**
	 * Sets the metadata value associated with the object and key. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param object - the object to attach the value to
	 * @param key - the key to use
	 * @param value - the value to use
	 */
	public <T extends Metadatable> void setData(T object, String key, Object value)
	{
		object.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	/**
	 * Return a list of all metadata values associated with the key and plugin. <br>        
	 *
	 * <hr>
	 * Date created: Jan 2, 2013 <br>
	 * Date last modified: Jan 2, 2013 <br>
	 *
	 * <hr>
	 * @param object
	 * @param key
	 * @return a list of the values used by the plugin
	 */
	public <T extends Metadatable> List<MetadataValue> getData(T object, String key)
	{
		// Get the list and make a list that we can add plugins that we don't need
		List<MetadataValue> list = object.getMetadata(key);
		ArrayList<MetadataValue> listToReturn = new ArrayList<MetadataValue>();
		
		// Find every value that the plugin owns
		for (MetadataValue metadataValue : list) {
			if (metadataValue.getOwningPlugin().getName().equals(plugin.getName())) {
				listToReturn.add(metadataValue);
			}
		}
		
		// return the constructed list
		return listToReturn;
	}

}
