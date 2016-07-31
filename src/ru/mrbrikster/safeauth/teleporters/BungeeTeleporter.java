package ru.mrbrikster.safeauth.teleporters;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.ChannelNotRegisteredException;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import ru.mrbrikster.safeauth.Main;

public class BungeeTeleporter {

	public static void send(Player player) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
		    out.writeUTF("Connect");
			out.writeUTF(Main.getMainConfig().getString("mainServer"));
			player.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable(){

				@Override
				public void run() {
					if (player.isOnline()) {
						player.kickPlayer(format(Main.getLocConfig().getString("errorConnectServer")));
					}
				}
				
			}, 60L);
		} catch (ChannelNotRegisteredException e) {
			if (player.isOnline()) {
				player.kickPlayer(format(Main.getLocConfig().getString("errorConnectServer")));
			}
		}
	}
	
	private static String format(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

}
