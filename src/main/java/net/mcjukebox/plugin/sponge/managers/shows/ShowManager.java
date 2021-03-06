package net.mcjukebox.plugin.sponge.managers.shows;

import net.mcjukebox.plugin.sponge.MCJukebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ShowManager {

	private MCJukebox currentInstance;
	public ShowManager(MCJukebox instance) {
		this.currentInstance = instance;
	}

	public HashMap<String, Show> getShows() {
		return shows;
	}

	public void setShows(HashMap<String, Show> shows) {
		this.shows = shows;
	}

	private HashMap<String, Show> shows = new HashMap<String, Show>();

	public Show getShow(String name) {
		name = name.replace("@", "");
		name = name.toLowerCase();
		if(shows.containsKey(name)) return shows.get(name);

		Show show = new Show(currentInstance);
		show.setChannel(name);
		shows.put(name, show);
		return show;
	}

	public List<Show> getShowsByPlayer(UUID UUID) {
		ArrayList<Show> inShows = new ArrayList<Show>();
		for(Show show : shows.values()) {
			if(show.getMembers().containsKey(UUID)) inShows.add(show);
		}
		return inShows;
	}

	public boolean inInShow(UUID UUID) {
		for(Show show : shows.values()) {
			if(show.getMembers().containsKey(UUID)) return true;
		}
		return false;
	}

	public boolean showExists(String name) {
		name = name.replace("@", "");
		return shows.containsKey(name);
	}

}
