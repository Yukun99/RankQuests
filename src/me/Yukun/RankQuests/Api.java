package me.Yukun.RankQuests;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.Yukun.RankQuests.MultiSupport.FactionsSupport;
import me.Yukun.RankQuests.MultiSupport.FactionsUUID;
import me.Yukun.RankQuests.MultiSupport.NMS_v1_10_R1;
import me.Yukun.RankQuests.MultiSupport.NMS_v1_7_R4;
import me.Yukun.RankQuests.MultiSupport.NMS_v1_8_R1;
import me.Yukun.RankQuests.MultiSupport.NMS_v1_8_R2;
import me.Yukun.RankQuests.MultiSupport.NMS_v1_8_R3;
import me.Yukun.RankQuests.MultiSupport.NMS_v1_9_R1;
import me.Yukun.RankQuests.MultiSupport.NMS_v1_9_R2;
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

	public static ItemStack addGlow(ItemStack item) {
		if (getVersion() == 1101) {
			return NMS_v1_10_R1.addGlow(item);
		}
		if (getVersion() == 192) {
			return NMS_v1_9_R2.addGlow(item);
		}
		if (getVersion() == 191) {
			return NMS_v1_9_R1.addGlow(item);
		}
		if (getVersion() == 183) {
			return NMS_v1_8_R3.addGlow(item);
		}
		if (getVersion() == 182) {
			return NMS_v1_8_R2.addGlow(item);
		}
		if (getVersion() == 181) {
			return NMS_v1_8_R1.addGlow(item);
		}
		if (getVersion() == 174) {
			return NMS_v1_7_R4.addGlow(item);
		} else {
			Bukkit.getLogger().log(Level.SEVERE, "&bRank&eQuests&f >> &cYour server is too far out of date. "
					+ "Please update or remove this plugin to stop further Errors.");
			return item;
		}
	}

	public static ItemStack addGlow(ItemStack item, boolean toggle) {
		if (toggle) {
			if (getVersion() == 1101) {
				return NMS_v1_10_R1.addGlow(item);
			}
			if (getVersion() == 192) {
				return NMS_v1_9_R2.addGlow(item);
			}
			if (getVersion() == 191) {
				return NMS_v1_9_R1.addGlow(item);
			}
			if (getVersion() == 183) {
				return NMS_v1_8_R3.addGlow(item);
			}
			if (getVersion() == 182) {
				return NMS_v1_8_R2.addGlow(item);
			}
			if (getVersion() == 181) {
				return NMS_v1_8_R1.addGlow(item);
			}
			if (getVersion() == 174) {
				return NMS_v1_7_R4.addGlow(item);
			} else {
				Bukkit.getLogger().log(Level.SEVERE, "&bSpace&eFlares&f >> &cYour server is too far out of date. "
						+ "Please update or remove this plugin to stop further Errors.");
				return item;
			}
		} else {
			return item;
		}
	}

	public static boolean hasFactions() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Factions") != null)
			return true;
		return false;
	}

	public static boolean isInWarzone(Player player) {
		Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
		Plugin worldguard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		String f = Api.getConfigString("RankQuestOptions.CheckWarzone");
		String wg = Api.getConfigString("RankQuestOptions.CheckWorldGuard");
		String bl = Api.getConfigString("RankQuestOptions.CheckBlacklist");
		String pvp = Api.getConfigString("RankQuestOptions.PvPFlag");
		if (wg.equalsIgnoreCase("true") && f.equalsIgnoreCase("true")) {
			if (factions != null && worldguard != null) {
				if (factions.getDescription().getAuthors().contains("drtshock")) {
					if (bl.equalsIgnoreCase("true")) {
						if ((WorldGuard.isInRegion(player) || FactionsUUID.isInWarzone(player))
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
						if (WorldGuard.isInRegion(player) && FactionsUUID.isInWarzone(player)) {
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
			if (factions != null) {
				if (factions.getDescription().getAuthors().contains("drtshock")) {
					if (FactionsUUID.isInWarzone(player)) {
						if (pvp.equalsIgnoreCase("true")) {
							if (WorldGuard.allowsPVP(player)) {
								return true;
							}
						}
						return true;
					}
					if (!FactionsUUID.inTerritory(player)) {
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
			}
		}
		if (wg.equalsIgnoreCase("false") && f.equalsIgnoreCase("false")) {
			return true;
		}
		return false;
	}

	public static boolean canBreakBlock(Player player, Block block) {
		Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
		if (factions != null) {
			if (player != null) {
				if (factions.getDescription().getAuthors().contains("drtshock")) {
					if (FactionsUUID.canBreakBlock(player, block))
						return true;
					if (!FactionsUUID.canBreakBlock(player, block))
						return false;
				}
				if (factions.getDescription().getWebsite() != null) {
					if (factions.getDescription().getWebsite()
							.equalsIgnoreCase("https://www.massivecraft.com/factions")) {
						if (FactionsSupport.canBreakBlock(player, block))
							return true;
						if (!FactionsSupport.canBreakBlock(player, block))
							return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean inTerritory(Player player) {
		Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
		if (factions != null) {
			if (factions.getDescription().getAuthors().contains("drtshock")) {
				if (FactionsUUID.inTerritory(player))
					return true;
				if (!FactionsUUID.inTerritory(player))
					return false;
			}
			if (factions.getDescription().getWebsite() != null) {
				if (factions.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions")) {
					if (FactionsSupport.inTerritory(player))
						return true;
					if (!FactionsSupport.inTerritory(player))
						return false;
				}
			}
		}
		return false;
	}

	public static boolean isFriendly(Entity P, Entity O) {
		if (P instanceof Player && O instanceof Player) {
			Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
			if (factions != null) {
				Player player = (Player) P;
				Player other = (Player) O;
				if (factions.getDescription().getAuthors().contains("drtshock")) {
					if (FactionsUUID.isFriendly(player, other))
						return true;
					if (!FactionsUUID.isFriendly(player, other))
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