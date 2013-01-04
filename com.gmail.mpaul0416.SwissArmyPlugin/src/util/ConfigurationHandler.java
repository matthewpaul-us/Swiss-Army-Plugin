/**
 * ---------------------------------------------------------------------------
 * File name: ConfigurationHandler.java<br/>
 * Project name: com.gmail.mpaul0416.SwissArmyPlugin<br/>
 * ---------------------------------------------------------------------------
 * Creator's name and email: Matthew Paul, paulmr@goldmail.etsu.edu<br/>
 * Course:  CSCI ____<br/>
 * Creation Date: Jan 4, 2013<br/>
 * Date of Last Modification: Jan 4, 2013
 * ---------------------------------------------------------------------------
 */

package util;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;


/**
 * Convenience handler for dealing with config files. <br>
 *
 * <hr>
 * Date created: Jan 4, 2013<br>
 * Date last modified: Jan 4, 2013<br>
 * <hr>
 * @author Matthew Paul
 */
public class ConfigurationHandler {
	private FileConfiguration config;
	private Logger logger;
	private Plugin plugin;
	
	/**
	 * Constructor <br>        
	 *
	 * <hr>
	 * Date created: Jan 4, 2013 <br>
	 * Date last modified: Jan 4, 2013 <br>
	 *
	 * <hr>
	 * @param config
	 * @param logger
	 * @param plugin
	 */
	public ConfigurationHandler(FileConfiguration config, Logger logger,
			Plugin plugin) {
		this.config = config;
		this.logger = logger;
		this.plugin = plugin;
	}
	
	/**
	 * Retrieves a value from the configuration,
	 * setting it to a default if it is not there. <br>        
	 *
	 * <hr>
	 * Date created: Jan 4, 2013 <br>
	 * Date last modified: Jan 4, 2013 <br>
	 *
	 * <hr>
	 * @param key - The key to retrieve
	 * @param defaultValue - The value to use if it's not found
	 * @return - The data associated with the key, or the default if not found. Return type
	 * is the same as the default value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T safeRetrieve(String key, T defaultValue)
	{
		// If the config has the key, return it
		if (config.isSet(key)) {
			return (T) config.get(key);
		}
		// Otherwise, warn the server and set the default
		else {
			logger.warning(String.format("Key \"%s\" is missing from %s's config file! Adding it to the config.",
					key, plugin));
			config.set(key, defaultValue);
			return defaultValue;
		}
	}
	
	/**
	 * Sets a configuration if there is no configuration with the same key. <br>        
	 *
	 * <hr>
	 * Date created: Jan 4, 2013 <br>
	 * Date last modified: Jan 4, 2013 <br>
	 *
	 * <hr>
	 * @param key - Key to set
	 * @param newValue - Value to set the key
	 */
	public <T> void safeSet(String key, T newValue)
	{
		if (!config.isSet(key)) {
			config.set(key, newValue);
		}
		else {
			logger.warning(String.format("Key \"%s\" already has data! Ignoring the set.",
					key));
		}
	}
	
	/**
	 * Set the value associated with the key, overriding the old value. <br>        
	 *
	 * <hr>
	 * Date created: Jan 4, 2013 <br>
	 * Date last modified: Jan 4, 2013 <br>
	 *
	 * <hr>
	 * @param key - Key to set
	 * @param newValue - Value to set the key
	 */
	public <T> void set(String key, T newValue)
	{
		config.set(key, newValue);
	}
}
