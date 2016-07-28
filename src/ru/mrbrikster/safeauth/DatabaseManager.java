package ru.mrbrikster.safeauth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	
	private static String userName = "root";
	
	private static String password = "";
	
	private static String hostName = "localhost";
	
	private static String databaseName = "safeauth";
	
	private static int port = 3306;
	
	private static Connection sqlConnection;
	
	public static void connect() {
		// Getting SQL auth-data
		userName = Main.getPlugin().getConfig().getString("sql.username");
		password = Main.getPlugin().getConfig().getString("sql.password");
		hostName = Main.getPlugin().getConfig().getString("sql.hostname");
		databaseName = Main.getPlugin().getConfig().getString("sql.database");
		port = Main.getPlugin().getConfig().getInt("sql.port");
		
		String url = "jdbc:mysql://" + hostName + ":" + port + "/" + databaseName + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
		try {
			sqlConnection = DriverManager.getConnection(url, userName, password);
		} catch (Exception e) {
			sqlConnection = null;
			PluginManager.printLn("Ошибка при подключении к MySQL. Плагин отключен.");
			if (PluginManager.isDebug()) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isConnected() {
		return (sqlConnection == null ? false : true);
	}
	
	public static void disconnect() {
		try {
			sqlConnection.close();
			sqlConnection = null;
		} catch (SQLException e) {
			PluginManager.printLn("Ошибка при отключении от MySQL.");
			if (PluginManager.isDebug()) {
				e.printStackTrace();
			}
		}
	}
	
	public static ResultSet executeQuery(String query) {
		try {
			resumeConnect();
			PreparedStatement statement = sqlConnection.prepareStatement(query);
			return statement.executeQuery();
		} catch (SQLException e) {
			PluginManager.printLn("Не удалось совершить запрос к MySQL.");
			if (PluginManager.isDebug()) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public static void executeUpdate(String query) {
		try {
			resumeConnect();
			Statement statement = sqlConnection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			PluginManager.printLn("Не удалось совершить запрос к MySQL.");
			if (PluginManager.isDebug()) {
				e.printStackTrace();
			}
		}
	}
	
	private static void resumeConnect() {
		try {
			if (sqlConnection.isClosed()) {
				connect();
			}
		} catch (SQLException e) {
			PluginManager.printLn("Не удалось соединиться с MySQL.");
			if (PluginManager.isDebug()) {
				e.printStackTrace();
			}
		}
	}
}
