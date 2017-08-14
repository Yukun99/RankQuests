package me.Yukun.RankQuests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {

	static SettingsManager instance = new SettingsManager();

	public static SettingsManager getInstance() {
		return instance;
	}

	Plugin p;

	FileConfiguration config;
	File cfile;
	
	FileConfiguration messages;
	File mfile;
	
	FileConfiguration redeems;
	File rfile;
	
	FileConfiguration loggers;
	File lfile;

	public void setup(Plugin p) {
		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
		
		cfile = new File(p.getDataFolder(), "config.yml");
		if (!cfile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/config.yml");
         		InputStream E = getClass().getResourceAsStream("/config.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		config = YamlConfiguration.loadConfiguration(cfile);
		
		mfile = new File(p.getDataFolder(), "Messages.yml");
		if (!mfile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/Messages.yml");
         		InputStream E = getClass().getResourceAsStream("/Messages.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		messages = YamlConfiguration.loadConfiguration(mfile);
		
		rfile = new File(p.getDataFolder(), "Redeem.yml");
		if (!rfile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/Redeem.yml");
         		InputStream E = getClass().getResourceAsStream("/Redeem.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		redeems = YamlConfiguration.loadConfiguration(rfile);
		
		lfile = new File(p.getDataFolder(), "Loggers.yml");
		if (!lfile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/Loggers.yml");
         		InputStream E = getClass().getResourceAsStream("/Loggers.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		redeems = YamlConfiguration.loadConfiguration(lfile);
	}

	public FileConfiguration getConfig() {
		return config;
	}
	
	public FileConfiguration getMessages() {
		return messages;
	}
	
	public FileConfiguration getRedeems() {
		return redeems;
	}
	
	public FileConfiguration getLoggers() {
		return loggers;
	}

	public void saveConfig() {
		try {
			config.save(cfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
		}
	}
	
	public void saveMessages() {
		try {
			messages.save(mfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save messages.yml!");
		}
	}
	
	public void saveRedeems() {
		try {
			redeems.save(rfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Redeem.yml!");
		}
	}
	
	public void saveLoggers() {
		try {
			loggers.save(lfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Loggers.yml!");
		}
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(cfile);
	}
	
	public void reloadMessages() {
		messages = YamlConfiguration.loadConfiguration(mfile);
	}
	
	public void reloadRedeems() {
		redeems = YamlConfiguration.loadConfiguration(rfile);
	}
	
	public void reloadLoggers() {
		loggers = YamlConfiguration.loadConfiguration(lfile);
	}

	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}

	public static void copyFile(InputStream in, File out) throws Exception { // https://bukkit.org/threads/extracting-file-from-jar.16962/
		InputStream fis = in;
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}
}
