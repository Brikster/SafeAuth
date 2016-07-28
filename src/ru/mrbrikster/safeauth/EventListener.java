package ru.mrbrikster.safeauth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (PluginManager.containsBlockedChars(e.getPlayer().getName().toCharArray())) {
			e.disallow(Result.KICK_FULL, Main.getLocConfig().getString("blockedSymbols"));
			return;
		}
		
		if (PluginManager.isTooBig(e.getPlayer().getName())) {
			e.disallow(Result.KICK_FULL, Main.getLocConfig().getString("nameTooBig"));
			return;
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		// Start login task
		PluginManager.startTask(e.getPlayer(), PluginManager.isRegistred(e.getPlayer()) ? TaskType.LOGIN : TaskType.REGISTER);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		// Stop login task
		PluginManager.stopTask(e.getPlayer());
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (!PluginManager.authCommand(e.getMessage()) && Main.getMainConfig().getBoolean("blockCommands")) e.setCancelled(true);
	}

}
