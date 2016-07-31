package ru.mrbrikster.safeauth;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Plugin plugin;
	private static FileConfiguration config;
	private static File locFile;
	private static FileConfiguration locConfig;

	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		config = getConfig();
		
		DatabaseManager.connect();
		
		if (!DatabaseManager.isConnected()) return;
		
		// Load commands
		new AuthManager("login");
		new AuthManager("l");
		new AuthManager("register");
		new AuthManager("reg");
		
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		PluginManager.createTable();
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static FileConfiguration getMainConfig() {
		return config;
	}

	@SuppressWarnings("deprecation")
	public static FileConfiguration getLocConfig() {
		if (locFile == null) {
			locFile = new File(getPlugin().getDataFolder().getAbsolutePath() + "/messages.yml");
			if (!locFile.exists()) {
				try {
					locFile.createNewFile();
					FileConfiguration temp = YamlConfiguration.loadConfiguration(Main.class.getResourceAsStream("/messages.yml"));
					temp.save(locFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (locConfig == null) {	
			locConfig = YamlConfiguration.loadConfiguration(locFile);
		}

		return locConfig;
	}

}
