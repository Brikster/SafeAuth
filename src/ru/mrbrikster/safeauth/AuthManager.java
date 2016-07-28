package ru.mrbrikster.safeauth;

import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AuthManager extends Command {
	
	public enum TaskType {
		LOGIN,
		REGISTER;
	}

	protected AuthManager(String name) {
		super(name);
		
		
		try {
			Field commandMapField = Main.getPlugin().getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap commandMap = (CommandMap) commandMapField.get(Main.getPlugin().getServer());
			commandMap.register(name, this);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) return true;
		
		Player player = (Player) sender;
		
		switch (super.getName()) {
		case "login":
			this.performLogin(player, label, args);
			break;
		case "l":
			this.performLogin(player, label, args);
			break;
		case "register":
			this.performRegister(player, label, args);
			break;
		case "reg":
			this.performRegister(player, label, args);
			break;
		}
		return true;
	}

	private void performRegister(Player sender, String label, String[] args) {
		if (args.length != 2) {
			return;
		}
		
		if (!args[0].equals(args[1])) {
			sender.sendMessage(format(Main.getLocConfig().getString("notEquals")));
			return;
		}
		
		String passwordHash = PluginManager.createPasswordHash(args[0]);
		
		PluginManager.register(sender, passwordHash);
	}

	private void performLogin(Player sender, String label, String[] args) {
		if (args.length != 1) {
			return;
		}
		
		String passwordHash = PluginManager.createPasswordHash(args[0]);
		
		PluginManager.login(sender, passwordHash);
	}
	
	private String format(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

}
