package ru.mrbrikster.safeauth;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Plugin plugin;
	private static FileConfiguration config;

	public void onEnable() {
		plugin = this;
		config = getConfig();
		
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static FileConfiguration getMainConfig() {
		return config;
	}

	public static FileConfiguration getLocConfig() {
		return null;
	}

}
