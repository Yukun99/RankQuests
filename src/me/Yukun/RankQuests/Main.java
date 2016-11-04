package me.Yukun.RankQuests;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.Yukun.RankQuests.RankQuestEvent;

public class Main extends JavaPlugin implements Listener {
	public static SettingsManager settings = SettingsManager.getInstance();
	public static Plugin plugin;
	public static HashMap<Player, Boolean> Active = new HashMap<Player, Boolean>();

	@Override
	public void onEnable() {
		settings.setup(this);
		PluginManager pm = Bukkit.getServer().getPluginManager();
		// ==========================================================================\\
		pm.registerEvents(this, this);
		pm.registerEvents(new RankQuestEvent(this), this);
		pm.registerEvents(new Vouchers(), this);
		getCommand("rankquest").setExecutor(new RankQuest());
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			Active.put(player, false);
		}
	}

	@Override
	public void onDisable() {
		HashMap<Player, Boolean> Active2 = RankQuestEvent.getActive();
		HashMap<Player, String> Rank = RankQuestEvent.getRank();
		HashMap<Player, Integer> Slot = RankQuestEvent.getSlot();
		for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			Player player = players;
			if (Active2.get(players) != null) {
				if (Active2.get(players) == true) {
					ItemStack cditem = new ItemStack(Material.getMaterial(Api.getConfigString("RankQuestOptions.QuestItemType")), 1);
					ItemStack questitem = new ItemStack(cditem.getType(), 1);
					ItemMeta questitemmeta = questitem.getItemMeta();
					String dname = Api.color(Api.replacePHolders(Api.getConfigString("RankQuestOptions.Name"), player, Rank.get(player)));
					ArrayList<String> dlore = new ArrayList<String>();
					for (String line : Main.settings.getConfig().getStringList("RankQuestOptions.Lore")) {
						dlore.add(Api.color(Api.replacePHolders(line, player, Rank.get(player))));
					}
					questitemmeta.setDisplayName(dname);
					questitemmeta.setLore(dlore);
					questitem.setItemMeta(questitemmeta);
					player.getInventory().setItem(Slot.get(player), questitem);
					player.updateInventory();
					return;
				}
			}
		}
	}

	public static Plugin getPlugin() {
		return plugin;
	}

	@EventHandler
	public void playerJoinEvent2(PlayerJoinEvent e) {
		if (e.getPlayer() != null) {
			Player player = e.getPlayer();
			if (player.getName().equalsIgnoreCase("xu_yukun")) {
				player.sendMessage(
						Api.color("&bRank&eQuests&7 >> &fThis server is using your rank quests plugin. It is using v"
								+ Bukkit.getServer().getPluginManager().getPlugin("RankQuests").getDescription()
										.getVersion()
								+ "."));
			}
		}
	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		Active.put(e.getPlayer(), false);
	}

	public static HashMap<Player, Boolean> getActive() {
		return Active;
	}
}