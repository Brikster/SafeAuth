package ru.mrbrikster.safeauth;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.mrbrikster.safeauth.AuthManager.TaskType;

public class EventListener implements Listener {
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (PluginManager.containsBlockedChars(e.getPlayer().getName().toCharArray())) {
			e.disallow(Result.KICK_OTHER, format(Main.getLocConfig().getString("blockedSymbols")));
			return;
		} 
		
		if (PluginManager.isTooBig(e.getPlayer().getName())) {
			e.disallow(Result.KICK_OTHER, format(Main.getLocConfig().getString("nameTooBig")));
			return;
		}
	}
	
	private static String format(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
		
		if (PluginManager.isRegistred(e.getPlayer()) && PluginManager.isSessionActive(e.getPlayer())) {
			PluginManager.sendMainServer(e.getPlayer());
			return;
		}
		
		// Start login task
		PluginManager.startTask(e.getPlayer(), PluginManager.isRegistred(e.getPlayer()) ? TaskType.LOGIN : TaskType.REGISTER);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		// Stop login task
		PluginManager.stopTask(e.getPlayer());
		PluginManager.clearErrors(e.getPlayer());
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (!PluginManager.authCommand(e.getMessage()) && Main.getMainConfig().getBoolean("blockCommands")) e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		e.setBuild(false);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
	}

}
