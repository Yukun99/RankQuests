package me.Yukun.RankQuests;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.Yukun.RankQuests.MultiSupport.FactionsSupport;
import me.Yukun.RankQuests.MultiSupport.FactionsUUIDSupport;
import me.Yukun.RankQuests.MultiSupport.LegacyFactionsSupport;
import me.Yukun.RankQuests.MultiSupport.WorldGuard;
import net.minelink.ctplus.CombatTagPlus;

public class Api {
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("RankQuests");

	@SuppressWarnings("static-access")
	public Api(Plugin plugin) {
		this.plugin = plugin;
	}

	public static Boolean isTagged(Player player) {
		if (Bukkit.getPluginManager().getPlugin("CombatTagPlus") != null) {
			CombatTagPlus ct = CombatTagPlus.getPlugin(CombatTagPlus.class);
			if (ct.getTagManager().isTagged(player.getUniqueId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static void tagPlayer(Player player) {
		if (Bukkit.getPluginManager().getPlugin("CombatTagPlus") != null) {
			CombatTagPlus ct = CombatTagPlus.getPlugin(CombatTagPlus.class);
			if (ct.getTagManager().isTagged(player.getUniqueId())) {
				return;
			} else {
				ct.getTagManager().tag(player, player);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player) {
		if (getVersion() >= 191) {
			return player.getInventory().getItemInMainHand();
		} else {
			return player.getItemInHand();
		}
	}

	public static void setLoggersString(String path, String msg) {
		Main.settings.getLoggers().set(path, msg);
		Main.settings.saveLoggers();
	}
	
	public static void setConfigString(String path, String msg) {
		Main.settings.getConfig().set(path, msg);
		Main.settings.saveConfig();
	}

	public static void setRedeemsInt(String path, Integer i) {
		if (i != null) {
			if (Main.settings.getRedeems().contains(path)) {
				Main.settings.getRedeems().set(path, i + Integer.parseInt(Api.getRedeemString(path)));
				Main.settings.saveRedeems();
			} else if (!Main.settings.getRedeems().contains(path)) {
				Main.settings.getRedeems().set(path, i);
				Main.settings.saveRedeems();
			}
		} else {
			Main.settings.getRedeems().set(path, null);
			Main.settings.saveRedeems();
		}
	}

	public static void setRedeemsString(String path, String msg) {
		Main.settings.getRedeems().set(path, msg);
		Main.settings.saveRedeems();
	}

	public static boolean containsItem(Player player, ItemStack item) {
		for (ItemStack items : player.getInventory().getContents()) {
			if (items.isSimilar(item) && (items.getAmount() + item.getAmount()) <= 64) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	public static ItemStack getItem(Player player, ItemStack item) {
		if (Api.containsItem(player, item)) {
			for (ItemStack items : player.getInventory().getContents()) {
				if (items.isSimilar(item) && (items.getAmount() + item.getAmount()) <= 64) {
					return items;
				} else {
					continue;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static void setItemInHand(Player player, ItemStack item) {
		if (getVersion() >= 191) {
			player.getInventory().setItemInMainHand(item);
		} else {
			player.setItemInHand(item);
		}
	}

	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String removeColor(String msg) {
		msg = ChatColor.stripColor(msg);
		return msg;
	}

	public static String getConfigString(String path) {
		String msg = Main.settings.getConfig().getString(path);
		return msg;
	}

	public static String getMessageString(String path) {
		String msg = Main.settings.getMessages().getString(path);
		return msg;
	}

	public static String getRedeemString(String path) {
		String msg = Main.settings.getRedeems().getString(path);
		return msg;
	}
	
	public static String getLoggersString(String path) {
		String msg = Main.settings.getLoggers().getString(path);
		return msg;
	}

	public static String replacePHolders(String msg, Player player, String rank) {
		return msg
				.replace("%rank%", Main.settings.getConfig().getString("RankQuestOptions.Ranks." + rank + ".RankName"))
				.replace("%player%", player.getDisplayName());
	}

	public static Integer getVersion() {
		String ver = Bukkit.getServer().getClass().getPackage().getName();
		ver = ver.substring(ver.lastIndexOf('.') + 1);
		ver = ver.replaceAll("_", "").replaceAll("R", "").replaceAll("v", "");
		return Integer.parseInt(ver);
	}

	public static Integer getArgument(String Argument, ItemStack item, String Msg) {
		String arg = "0";
		Msg = Api.color(Msg).toLowerCase();
		String name = item.getItemMeta().getDisplayName().toLowerCase();
		if (Msg.contains(Argument.toLowerCase())) {
			String[] b = Msg.split(Argument.toLowerCase());
			if (b.length >= 1)
				arg = name.replace(b[0], "");
			if (b.length >= 2)
				arg = arg.replace(b[1], "");
		}
		if (isInt(arg)) {
			return Integer.parseInt(arg);
		} else {
			return null;
		}
	}

	public static String getString(String Argument, ItemStack item, String Msg) {
		String arg = "0";
		Msg = Api.color(Msg).toLowerCase();
		String name = item.getItemMeta().getDisplayName().toLowerCase();
		if (Msg.contains(Argument.toLowerCase())) {
			String[] b = Msg.split(Argument.toLowerCase());
			if (b.length >= 1)
				arg = name.replace(b[0], "");
			if (b.length >= 2)
				arg = arg.replace(b[1], "");
		}
		return arg;
	}

	public static boolean hasLegacyFactions() {
		if (Bukkit.getServer().getPluginManager().getPlugin("LegacyFactions") != null)
			return true;
		return false;
	}

	public static boolean hasFactions() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Factions") != null)
			return true;
		return false;
	}

	public static boolean isInWarzone(Player player) {
		Plugin factions = null;
		if (hasFactions()) {
			factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
		}
		if (hasLegacyFactions()) {
			factions = Bukkit.getServer().getPluginManager().getPlugin("LegacyFactions");
		}
		Plugin worldguard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		String f = Api.getConfigString("RankQuestOptions.CheckWarzone");
		String wg = Api.getConfigString("RankQuestOptions.CheckWorldGuard");
		String bl = Api.getConfigString("RankQuestOptions.CheckBlacklist");
		String pvp = Api.getConfigString("RankQuestOptions.PvPFlag");
		if (wg.equalsIgnoreCase("true") && f.equalsIgnoreCase("true")) {
			if ((hasFactions() || hasLegacyFactions()) && worldguard != null) {
				if (hasFactions()) {
					if (factions.getDescription().getAuthors().contains("drtshock")) {
						if (bl.equalsIgnoreCase("true")) {
							if ((WorldGuard.isInRegion(player) || FactionsUUIDSupport.isInWarzone(player))
									&& WorldGuard.notInRegion(player)) {
								if (pvp.equalsIgnoreCase("true")) {
									if (WorldGuard.allowsPVP(player)) {
										return true;
									} else {
										return false;
									}
								}
								return true;
							}
						}
						if (bl.equalsIgnoreCase("false")) {
							if (WorldGuard.isInRegion(player) && FactionsUUIDSupport.isInWarzone(player)) {
								if (pvp.equalsIgnoreCase("true")) {
									if (WorldGuard.allowsPVP(player)) {
										return true;
									} else {
										return false;
									}
								}
								return true;
							}
						}
					}
					if (factions.getDescription().getWebsite() != null) {
						if (factions.getDescription().getWebsite()
								.equalsIgnoreCase("https://www.massivecraft.com/factions")) {
							if (bl.equalsIgnoreCase("true")) {
								if ((WorldGuard.isInRegion(player) || FactionsSupport.isInWarzone(player))
										&& WorldGuard.notInRegion(player)) {
									if (pvp.equalsIgnoreCase("true")) {
										if (WorldGuard.allowsPVP(player)) {
											return true;
										} else {
											return false;
										}
									}
									return true;
								}
							}
							if (bl.equalsIgnoreCase("false")) {
								if (WorldGuard.isInRegion(player) || FactionsSupport.isInWarzone(player)) {
									if (pvp.equalsIgnoreCase("true")) {
										if (WorldGuard.allowsPVP(player)) {
											return true;
										} else {
											return false;
										}
									}
									return true;
								}
							}
						}
					}
				} else if (hasLegacyFactions()) {
					if (bl.equalsIgnoreCase("true")) {
						if ((WorldGuard.isInRegion(player) || LegacyFactionsSupport.isInWarzone(player))
								&& WorldGuard.notInRegion(player)) {
							if (pvp.equalsIgnoreCase("true")) {
								if (WorldGuard.allowsPVP(player)) {
									return true;
								} else {
									return false;
								}
							}
							return true;
						}
					}
					if (bl.equalsIgnoreCase("false")) {
						if (WorldGuard.isInRegion(player) || LegacyFactionsSupport.isInWarzone(player)) {
							if (pvp.equalsIgnoreCase("true")) {
								if (WorldGuard.allowsPVP(player)) {
									return true;
								} else {
									return false;
								}
							}
							return true;
						}
					}
				}
			}
		}
		if (wg.equalsIgnoreCase("true") && f.equalsIgnoreCase("false")) {
			if (bl.equalsIgnoreCase("true")) {
				if (WorldGuard.isInRegion(player) && WorldGuard.notInRegion(player)) {
					if (pvp.equalsIgnoreCase("true")) {
						if (WorldGuard.allowsPVP(player)) {
							return true;
						} else {
							return false;
						}
					}
					return true;
				}
			}
			if (bl.equalsIgnoreCase("false")) {
				if (WorldGuard.isInRegion(player)) {
					if (pvp.equalsIgnoreCase("true")) {
						if (WorldGuard.allowsPVP(player)) {
							return true;
						} else {
							return false;
						}
					}
					return true;
				}
			}
		}
		if (wg.equalsIgnoreCase("false") && f.equalsIgnoreCase("true")) {
			if (hasFactions() || hasLegacyFactions()) {
				if (hasFactions()) {
					if (factions.getDescription().getAuthors().contains("drtshock")) {
						if (FactionsUUIDSupport.isInWarzone(player)) {
							if (pvp.equalsIgnoreCase("true")) {
								if (WorldGuard.allowsPVP(player)) {
									return true;
								}
							}
							return true;
						}
						if (!FactionsUUIDSupport.inTerritory(player)) {
							return false;
						}
					}
					if (factions.getDescription().getWebsite() != null) {
						if (factions.getDescription().getWebsite()
								.equalsIgnoreCase("https://www.massivecraft.com/factions")) {
							if (FactionsSupport.isInWarzone(player)) {
								if (pvp.equalsIgnoreCase("true")) {
									if (WorldGuard.allowsPVP(player)) {
										return true;
									}
								}
								return true;
							}
							if (!FactionsSupport.inTerritory(player)) {
								return false;
							}
						}
					}
				} else if (hasLegacyFactions()) {
					if (LegacyFactionsSupport.isInWarzone(player)) {
						if (pvp.equalsIgnoreCase("true")) {
							if (WorldGuard.allowsPVP(player)) {
								return true;
							}
						}
						return true;
					}
					if (!LegacyFactionsSupport.inTerritory(player)) {
						return false;
					}
				}
			}
		}
		if (wg.equalsIgnoreCase("false") && f.equalsIgnoreCase("false")) {
			return true;
		}
		return false;
	}

	public static boolean inTerritory(Player player) {
		Plugin factions = null;
		if (hasFactions()) {
			factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
		}
		if (hasLegacyFactions()) {
			factions = Bukkit.getServer().getPluginManager().getPlugin("LegacyFactions");
		}
		if (hasFactions() || hasLegacyFactions()) {
			if (hasFactions()) {
				if (factions.getDescription().getAuthors().contains("drtshock")) {
					if (FactionsUUIDSupport.inTerritory(player))
						return true;
					if (!FactionsUUIDSupport.inTerritory(player))
						return false;
				}
				if (factions.getDescription().getWebsite() != null) {
					if (factions.getDescription().getWebsite()
							.equalsIgnoreCase("https://www.massivecraft.com/factions")) {
						if (FactionsSupport.inTerritory(player))
							return true;
						if (!FactionsSupport.inTerritory(player))
							return false;
					}
				}
			} else if (hasLegacyFactions()) {
				if (LegacyFactionsSupport.inTerritory(player))
					return true;
				if (!LegacyFactionsSupport.inTerritory(player))
					return false;
			}
		}
		return false;
	}

	public static boolean isFriendly(Entity P, Entity O) {
		if (P instanceof Player && O instanceof Player) {
			Plugin factions = null;
			if (hasFactions()) {
				factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
			}
			if (hasLegacyFactions()) {
				factions = Bukkit.getServer().getPluginManager().getPlugin("LegacyFactions");
			}
			if (hasFactions() || hasLegacyFactions()) {
				Player player = (Player) P;
				Player other = (Player) O;
				if (hasFactions()) {
					if (factions.getDescription().getAuthors().contains("drtshock")) {
						if (FactionsUUIDSupport.isFriendly(player, other))
							return true;
						if (!FactionsUUIDSupport.isFriendly(player, other))
							return false;
					}
					if (factions.getDescription().getWebsite() != null) {
						if (factions.getDescription().getWebsite()
								.equalsIgnoreCase("https://www.massivecraft.com/factions")) {
							if (FactionsSupport.isFriendly(player, other))
								return true;
							if (!FactionsSupport.isFriendly(player, other))
								return false;
						}
					}
				} else if (hasLegacyFactions()) {
					if (LegacyFactionsSupport.isFriendly(player, other))
						return true;
					if (!LegacyFactionsSupport.isFriendly(player, other))
						return false;
				}
			}
		}
		return false;
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static Player getPlayer(String name) {
		return Bukkit.getServer().getPlayer(name);
	}

	public static Boolean hasArgument(String Argument, List<String> Msg) {
		for (String l : Msg) {
			l = Api.color(l).toLowerCase();
			if (l.contains(Argument.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}