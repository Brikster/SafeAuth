package ru.mrbrikster.safeauth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import ru.mrbrikster.safeauth.AuthManager.TaskType;
import ru.mrbrikster.safeauth.teleporters.BungeeTeleporter;
import ru.mrbrikster.safeauth.teleporters.LilyPadTeleporter;

public class PluginManager {

	private static String allowedSymbols = Main.getMainConfig().getString("allowedSymbols");
	private static HashMap<Player, BukkitTask> authTasks = new HashMap<>();
	private static HashMap<Player, Integer> authErrors = new HashMap<>();

	public static boolean containsBlockedChars(String playerName) {
		return !playerName.matches("^" + allowedSymbols);
	}

	public static boolean isTooBig(String name) {
		return Main.getMainConfig().getInt("maxNameLength") < name.length();
	}

	public static boolean isRegistred(Player player) {
		ResultSet resultSet = DatabaseManager.executeQuery("SELECT password FROM safeauth WHERE player='" + player.getName().toLowerCase() + "'");
	
		try {
			if (resultSet == null || !resultSet.first()) return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	public static void startTask(Player player, TaskType taskType) {
		authTasks.put(player, (new BukkitRunnable() {

			@Override
			public void run() {
				if (!player.isOnline()) return;
				player.sendMessage(format(Main.getLocConfig().getString(taskType == TaskType.LOGIN ? "loginMessage" : "registerMessage")));
			}

			private String format(String string) {
				return ChatColor.translateAlternateColorCodes('&', string);
			}
			
		}.runTaskTimer(Main.getPlugin(), 20L, 40L)));
	}
	
	public static void stopTask(Player player) {
		if (!authTasks.containsKey(player)) return;
		
		authTasks.get(player).cancel();
		authTasks.remove(player);
	}

	public static boolean authCommand(String message) {
		if (message.contains("")) message = message.split(" ")[0];
		
		return Main.getMainConfig().getStringList("allowedCommands").contains(message.replace("/", ""));
	}

	public static String createPasswordHash(String password) {
		if (Main.getMainConfig().getBoolean("useSha256")) {
			String salt = Utils.getRandomString(16);
			return "$SHA$" + salt + "$" + Utils.toSha256(Utils.toSha256(password) + salt);
		} else {
			return Utils.toSha256(Utils.toSha256(password) + Utils.toMd5(password));
		}
	}

	public static void register(Player player, String passwordHash) {
		DatabaseManager.executeUpdate("INSERT INTO safeauth (player, ip, password, session, realname) VALUES ('" + player.getName().toLowerCase() + "', '" + player.getAddress().getAddress().getHostAddress() + "', '" + passwordHash  + "', from_unixtime(" + new Date().getTime()/1000 + "), '" + player.getName() + "')");
		player.sendMessage(format(Main.getLocConfig().getString("successfulRegister")));
		
		stopTask(player);
		sendMainServer(player);
	}

	public static void login(Player player, String password) {
		if (!validPassword(player, password)) {
			player.sendMessage(format(Main.getLocConfig().getString("wrongPassword")));
			addError(player);
			if (getErrors(player) >= Main.getMainConfig().getInt("maxErrors")) player.kickPlayer(format(Main.getLocConfig().getString("manyErrors")));
			return;
		}
		
		DatabaseManager.executeUpdate("UPDATE safeauth SET session=from_unixtime(" + new Date().getTime()/1000 + ") WHERE player='" + player.getName().toLowerCase() + "'");
		
		player.sendMessage(format(Main.getLocConfig().getString("loggedIn")));
		stopTask(player);
		sendMainServer(player);
	}
	

	public static void sendMainServer(Player player) {
		if (Main.getMainConfig().getString("proxy").equalsIgnoreCase("BUNGEE")) {
			BungeeTeleporter.send(player);
		} else if (Main.getMainConfig().getString("proxy").equalsIgnoreCase("LILYPAD")) {
			LilyPadTeleporter.send(player);
		}
	}

	public static boolean validPassword(Player player, String password) {
		ResultSet resultSet = DatabaseManager.executeQuery("SELECT password FROM safeauth WHERE player='" + player.getName().toLowerCase() + "'");
		try {
			resultSet.first();
			if (Main.getMainConfig().getBoolean("useSha256")) {
				String string = resultSet.getString("password");
				String salt = string.split("\\$")[2];
				return string.equals("$SHA$" + salt + "$" + Utils.toSha256(Utils.toSha256(password) + salt));
			} else {
				return (resultSet.getString("password").equals(createPasswordHash(password)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isDebug() {
		return Main.getMainConfig().getBoolean("debug");
	}

	public static void printLn(String string) {
		Bukkit.getConsoleSender().sendMessage("[SafeAuth] " + string);
	}
	
	public static void createTable() {
		DatabaseManager.executeUpdate("CREATE TABLE IF NOT EXISTS safeauth ("
				+ "id INT AUTO_INCREMENT, "
				+ "player VARCHAR(255) NOT NULL, "
				+ "realname VARCHAR(255) NOT NULL, "
				+ "password VARCHAR(255) NOT NULL, "
				+ "ip VARCHAR(255) NOT NULL, "
				+ "email VARCHAR(255), "
				+ "session TIMESTAMP NOT NULL, "
				+ "PRIMARY KEY (id)"
				+ ")");
	}
	
	private static String format(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static boolean isSessionActive(Player player) {
		ResultSet resultSet = DatabaseManager.executeQuery("SELECT session, ip FROM safeauth WHERE player='" + player.getName().toLowerCase() + "'");
		
		try {
			if (resultSet == null || !resultSet.first()) return false;
			long time = new Date().getTime();
			if ((time - (Main.getMainConfig().getInt("sessionTime") * 1000)) <= resultSet.getTimestamp("session").getTime() 
					&& resultSet.getString("ip").equals(player.getAddress().getAddress().getHostAddress())) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static Integer getErrors(Player player) {
		return authErrors.get(player);
	}
	
	public static void addError(Player player) {
		if (!authErrors.containsKey(player)) {
			authErrors.put(player, 1); 
			return;
		}
		
		Integer old = authErrors.get(player);
		authErrors.remove(player);
		authErrors.put(player, ++old);
	}

	public static void clearErrors(Player player) {
		if (authErrors.containsKey(player)) authErrors.remove(player);
	}

	public static boolean validEmail(String string) {
		// TODO in next versions
		return false;
	}

	public static void sendPasswordByEmail(Player player) {
		// TODO in next versions
	}

}
