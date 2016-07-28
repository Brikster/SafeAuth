package ru.mrbrikster.safeauth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PluginManager {

	private static List<Character> allowedSymbols = Main.getMainConfig().getCharacterList("allowedSymbols");
	private static HashMap<Player, BukkitTask> authTasks = new HashMap<>();

	public static boolean containsBlockedChars(char[] charArray) {
		return !allowedSymbols.containsAll(Arrays.asList(charArray));
	}

	public static boolean isTooBig(String name) {
		return Main.getMainConfig().getInt("maxNameLength") < name.length();
	}

	public static boolean isRegistred(Player player) {
		// TODO
		return false;
	}

	public static void startTask(Player player, TaskType taskType) {
		authTasks.put(player, (new BukkitRunnable() {

			@Override
			public void run() {
				player.sendMessage(format(Main.getLocConfig().getString(taskType == TaskType.LOGIN ? "loginMessage" : "registerMessage")));
			}

			private String format(String string) {
				return ChatColor.translateAlternateColorCodes('&', string);
			}
			
		}.runTaskTimerAsynchronously(Main.getPlugin(), 20L, 40L)));
	}
	
	public static void stopTask(Player player) {
		authTasks.get(player).cancel();
		authTasks.remove(player);
	}

	public static boolean authCommand(String message) {
		return Main.getLocConfig().getStringList("allowedCommands").contains(message);
	}

	public static String createHash(String string) {
		return Utils.toSha256(Utils.toSha256(string) + Utils.toMd5(string));
	}

	public static void register(Player sender, String passwordHash) {
		
	}

	public static void login(Player sender, String passwordHash) {
		
		
	}

}
