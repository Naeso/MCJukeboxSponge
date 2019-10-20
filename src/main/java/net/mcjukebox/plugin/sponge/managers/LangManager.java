package net.mcjukebox.plugin.sponge.managers;

import org.json.JSONObject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class LangManager {

	private JSONObject keyValues = new JSONObject();

	public LangManager(){
		addDefaults();
	}

	/**
	 * Loads the language file from the data folder, if it exists.
	 */
	public void loadLang(JSONObject langObject){
		keyValues = langObject;
		addDefaults();
	}

	/**
	 * Returns the value associated with a particular key.
	 *
	 * @param key Key to find
	 * @return Value from the config
	 */
	public String get(String key){
		String[] elements = key.split("\\.");
		JSONObject finalParent = keyValues;

		for(int i = 0; i < elements.length - 1; i++){
			if(!finalParent.has(elements[i])) finalParent.put(elements[i], new JSONObject());
			finalParent = finalParent.getJSONObject(elements[i]);
		}

		String value = null;
		if(elements.length == 0 && finalParent.has(key)) value = finalParent.getString(key);
		else if(finalParent.has(elements[elements.length - 1])) value = finalParent.getString(elements[elements.length - 1]);

		return TextColors.RED + "Missing Key: " + key;
	}

	/**
	 * Adds all default configuration values to the JSON Object.
	 */
	private void addDefaults(){
		addDefault("region.registered", "&aRegion registered!");
		addDefault("region.unregistered", "&aRegion unregistered!");
		addDefault("region.notregistered", "&cThat region is not registered!");

		addDefault("user.openLoading", "&aGenerating link...");
		addDefault("user.openClient", "Click here to launch our custom music client.");
		addDefault("user.openHover", "Launch client");
		addDefault("user.openDomain", "https://client.mcjukebox.net");

		addDefault("event.clientConnect", "&aYou connected to our audio server!");
		addDefault("event.clientDisconnect", "&cYou disconnected from our audio server.");

		addDefault("command.notOnline", "&c[user] is not currently online.");
	}

	/**
	 * Adds a particular property to the JSON Object if it is not already present.
	 *
	 * @param key Key to associate the value with
	 * @param value Value to associate the key with
	 */
	private void addDefault(String key, String value){
		String[] elements = key.split("\\.");
		JSONObject finalParent = keyValues;

		for(int i = 0; i < elements.length - 1; i++){
			if(!finalParent.has(elements[i])) finalParent.put(elements[i], new JSONObject());
			finalParent = finalParent.getJSONObject(elements[i]);
		}

		if(!finalParent.has(key) && elements.length == 0) finalParent.put(key, value);
		else if(!finalParent.has(elements[elements.length - 1])) finalParent.put(elements[elements.length - 1], value);
	}

}
