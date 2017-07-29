package me.Yukun.RankQuests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.Yukun.RankQuests.Api;
import me.Yukun.RankQuests.Main;

public class RankQuestEvent implements Listener {
	static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("RankQuests");

	@SuppressWarnings("static-access")
	public RankQuestEvent(Plugin plugin) {
		this.plugin = plugin;
	}

	public static HashMap<Player, Integer> getCountDown() {
		return CountDown;
	}

	public static HashMap<Player, Boolean> getActive() {
		return Active;
	}

	public static HashMap<Player, String> getRank() {
		return Rank;
	}

	public static HashMap<Player, Integer> getSlot() {
		return Slot;
	}

	public static HashMap<Player, Integer> CountDown = new HashMap<Player, Integer>();
	public static HashMap<Player, Boolean> Active = Main.getActive();
	public static HashMap<Player, Integer> Slot = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> Time = new HashMap<Player, Integer>();
	public static HashMap<Player, String> Rank = new HashMap<Player, String>();
	String rank = "";
	String prefix = Api.getMessageString("Messages.Prefix");
	String move = Api.getMessageString("Messages.NoMovingQuest");
	String onlystart1 = Api.getMessageString("Messages.OnlyStart1");

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		Active.put(player, false);
		return;
	}

	@EventHandler
	public void startQuestEvent(final PlayerInteractEvent e) {
		Player player = e.getPlayer();
		final ArrayList<String> queststart = (ArrayList<String>) Main.settings.getMessages()
				.getStringList("Messages.BeginMessage");
		final ArrayList<String> questallstart = (ArrayList<String>) Main.settings.getMessages()
				.getStringList("Messages.BeginAllMessage");
		final String questcomplete = Api.getMessageString("Messages.QuestComplete");
		final String questallcomplete = Api.getMessageString("Messages.QuestAllComplete");
		final String notInWarzone = Api.getMessageString("Messages.NotInWarzone");
		String only1RankQuest = Api.getMessageString("Messages.Only1RankQuest");
		final String questitemtype = Api.getConfigString("RankQuestOptions.QuestItemType");
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// player.sendMessage("1");
			if (Api.getItemInHand(player) != null) {
				// player.sendMessage("2");
				final ItemStack item = Api.getItemInHand(player);
				final Material itemtype = Material.getMaterial(questitemtype);
				if (item.hasItemMeta()) {
					// player.sendMessage("3");
					if (item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
						// player.sendMessage("4");
						if (item.getType() == Material.getMaterial(questitemtype)) {
							// player.sendMessage("5");
							for (String ranks : Main.settings.getConfig()
									.getConfigurationSection("RankQuestOptions.Ranks").getKeys(false)) {
								// player.sendMessage("6");
								String itemname = Api.color(Api
										.replacePHolders(Api.getConfigString("RankQuestOptions.Name"), player, ranks)
										.replace("%time%",
												Api.getConfigString("RankQuestOptions.Ranks." + ranks + ".Time")));
								rank = ranks;
								if (Api.removeColor(itemname)
										.contains(Api.removeColor(item.getItemMeta().getDisplayName()))) {
									// player.sendMessage("7");
									ArrayList<String> itemlore = new ArrayList<String>();
									for (String line : Main.settings.getConfig()
											.getStringList("RankQuestOptions.Lore")) {
										itemlore.add(Api.color(Api.replacePHolders(line, player, rank)));
									}
									if (itemlore != null) {
										// player.sendMessage("8");
										ArrayList<String> heldlore = (ArrayList<String>) Api.getItemInHand(player)
												.getItemMeta().getLore();
										if (Api.removeColor(itemlore.get(0))
												.equalsIgnoreCase(Api.removeColor(heldlore.get(0)))) {
											// player.sendMessage("9");
											String stime = Api
													.getConfigString("RankQuestOptions.Ranks." + rank + ".Time");
											if (Api.isInt(stime)) {
												// player.sendMessage("10");
												if (Api.isInWarzone(player)) {
													// player.sendMessage("11");
													if (item.getAmount() == 1) {
														if (Active.get(player) == false) {
															final int maxtime = Integer.parseInt(Api.getConfigString(
																	"RankQuestOptions.Ranks." + rank + ".Time"));
															Rank.put(player, rank);
															Active.put(player, true);
															Slot.put(player, player.getInventory().getHeldItemSlot());
															Time.put(player, maxtime);
															/*
															 * if (Api.
															 * getConfigString(
															 * "RankQuestOptions.TagPlayer")
															 * .equalsIgnoreCase
															 * ("true")) { if
															 * (Bukkit.
															 * getPluginManager(
															 * ) .getPlugin(
															 * "CombatTagPlus")
															 * != null) {
															 * PlayerCombatTagEvent
															 * event = new
															 * PlayerCombatTagEvent(
															 * player, player,
															 * maxtime * 1000);
															 * Bukkit.
															 * getPluginManager(
															 * ).callEvent(event
															 * ); } }
															 */
															CountDown.put(player, Bukkit.getScheduler()
																	.scheduleSyncRepeatingTask(plugin, new Runnable() {
																		Player player = e.getPlayer();
																		int slot = Slot.get(player);

																		@Override
																		public void run() {
																			if (Active.get(player) != null
																					&& Active.get(player) == true) {
																				if (Api.isInWarzone(player)) {
																					int time = Time.get(player);
																					if (time == maxtime) {
																						Time.put(player, (time - 1));
																						String newname = "";
																						ArrayList<String> dlore = new ArrayList<String>();
																						ItemStack newitem = new ItemStack(
																								itemtype, 1);
																						ItemMeta newmeta = newitem
																								.getItemMeta();
																						newname = Api.color(Api
																								.replacePHolders(
																										Api.getConfigString(
																												"RankQuestOptions.CdName"),
																										player,
																										Rank.get(
																												player))
																								.replace("%time%",
																										Api.getConfigString(
																												"RankQuestOptions.Ranks."
																														+ Rank.get(
																																player)
																														+ ".Time")));
																						for (String line : Main.settings
																								.getConfig()
																								.getStringList(
																										"RankQuestOptions.CdLore")) {
																							dlore.add(Api.color(
																									Api.replacePHolders(
																											line,
																											player,
																											Rank.get(
																													player))));
																						}
																						newmeta.setDisplayName(newname);
																						newmeta.setLore(dlore);
																						newitem.setItemMeta(newmeta);
																						player.getInventory()
																								.setItem(slot, newitem);
																						for (String begin : queststart) {
																							player.sendMessage(
																									Api.color(
																											Api.replacePHolders(
																													begin,
																													player,
																													Rank.get(
																															player))));
																						}
																						for (Player online : Bukkit
																								.getServer()
																								.getOnlinePlayers()) {
																							if (online != player) {
																								for (String beginall : questallstart) {
																									online.sendMessage(
																											Api.color(
																													Api.replacePHolders(
																															beginall,
																															player,
																															Rank.get(
																																	player))));
																								}
																							}
																						}
																					}
																					if (time > 0 && time < maxtime) {
																						Time.put(player, (time - 1));
																						ItemStack questitemold = new ItemStack(
																								Material.getMaterial(
																										questitemtype),
																								1);
																						ItemMeta questitemoldmeta = questitemold
																								.getItemMeta();
																						String questitemoldname = Api
																								.color(Api
																										.replacePHolders(
																												Api.getConfigString(
																														"RankQuestOptions.CdName"),
																												player,
																												Rank.get(
																														player))
																										.replace(
																												"%time%",
																												"" + (time
																														+ 1)));
																						ArrayList<String> questitemoldlore = new ArrayList<String>();
																						for (String line : Main.settings
																								.getConfig()
																								.getStringList(
																										"RankQuestOptions.CdLore")) {
																							questitemoldlore
																									.add(Api.color(
																											Api.replacePHolders(
																													line,
																													player,
																													Rank.get(
																															player))));
																						}
																						questitemoldmeta.setDisplayName(
																								questitemoldname);
																						questitemoldmeta.setLore(
																								questitemoldlore);
																						questitemold.setItemMeta(
																								questitemoldmeta);
																						if (player.getInventory()
																								.getItem(
																										slot) != null) {
																							ItemStack held = player
																									.getInventory()
																									.getItem(slot);
																							String dcdname = Api
																									.removeColor(held
																											.getItemMeta()
																											.getDisplayName());
																							String dccname = Api
																									.removeColor(
																											questitemold
																													.getItemMeta()
																													.getDisplayName());
																							List<String> dlore = held
																									.getItemMeta()
																									.getLore();
																							List<String> clore = questitemold
																									.getItemMeta()
																									.getLore();
																							ArrayList<String> dcdlore = new ArrayList<String>();
																							ArrayList<String> dcclore = new ArrayList<String>();
																							for (String line : dlore) {
																								dcdlore.add(
																										Api.removeColor(
																												line));
																							}
																							for (String line : clore) {
																								dcclore.add(
																										Api.removeColor(
																												line));
																							}
																							if (dcdname
																									.equalsIgnoreCase(
																											dccname)) {
																								if (dcdlore.equals(
																										dcclore)) {
																									ItemStack questitemnew = new ItemStack(
																											Material.getMaterial(
																													questitemtype),
																											1);
																									ItemMeta questitemnewmeta = questitemnew
																											.getItemMeta();
																									String questitemnewname = Api
																											.color(Api
																													.replacePHolders(
																															Api.getConfigString(
																																	"RankQuestOptions.CdName"),
																															player,
																															Rank.get(
																																	player))
																													.replace(
																															"%time%",
																															"" + (time)));
																									ArrayList<String> questitemnewlore = new ArrayList<String>();
																									for (String line : Main.settings
																											.getConfig()
																											.getStringList(
																													"RankQuestOptions.CdLore")) {
																										questitemnewlore
																												.add(Api.color(
																														Api.replacePHolders(
																																line,
																																player,
																																Rank.get(
																																		player))));
																									}
																									questitemnewmeta
																											.setDisplayName(
																													questitemnewname);
																									questitemnewmeta
																											.setLore(
																													questitemnewlore);
																									questitemnew
																											.setItemMeta(
																													questitemnewmeta);
																									player.getInventory()
																											.setItem(
																													slot,
																													questitemnew);
																								}
																							}
																						}
																					}
																					if (time <= 0) {
																						Bukkit.getScheduler()
																								.cancelTask(CountDown
																										.get(player));
																						CountDown.remove(player);
																						Active.put(player, false);
																						Slot.remove(player);
																						Time.remove(player);
																						player.getInventory().setItem(
																								slot, new ItemStack(
																										Material.AIR));
																						if (Api.isInt(
																								Api.getConfigString(
																										"RankQuestOptions.Ranks."
																												+ Rank.get(
																														player)
																												+ ".Voucher.Amount"))) {
																							int amount = Integer
																									.parseInt(
																											Api.getConfigString(
																													"RankQuestOptions.Ranks."
																															+ Rank.get(
																																	player)
																															+ ".Voucher.Amount"));
																							if (amount > 0) {
																								ItemStack voucher = new ItemStack(
																										Material.getMaterial(
																												Api.getConfigString(
																														"RankQuestOptions.Ranks."
																																+ Rank.get(
																																		player)
																																+ ".Voucher.VoucherItemType")),
																										amount);
																								ItemMeta vouchermeta = voucher
																										.getItemMeta();
																								vouchermeta
																										.setDisplayName(
																												Api.color(
																														Api.replacePHolders(
																																Api.getConfigString(
																																		"RankQuestOptions.Ranks."
																																				+ Rank.get(
																																						player)
																																				+ ".Voucher.Name"),
																																player,
																																Rank.get(
																																		player))));
																								ArrayList<String> vlore = new ArrayList<String>();
																								for (String line : Main.settings
																										.getConfig()
																										.getStringList(
																												"RankQuestOptions.Ranks."
																														+ Rank.get(
																																player)
																														+ ".Voucher.Lore")) {
																									vlore.add(Api.color(
																											Api.replacePHolders(
																													line,
																													player,
																													Rank.get(
																															player))));
																								}
																								vouchermeta
																										.setLore(vlore);
																								voucher.setItemMeta(
																										vouchermeta);
																								if (voucher != null) {
																									player.getInventory()
																											.setItem(
																													slot,
																													voucher);
																									player.sendMessage(
																											Api.color(
																													Api.replacePHolders(
																															prefix + questcomplete,
																															player,
																															Rank.get(
																																	player))));
																									for (Player players : Bukkit
																											.getServer()
																											.getOnlinePlayers()) {
																										if (players != player) {
																											players.sendMessage(
																													Api.color(
																															Api.replacePHolders(
																																	prefix + questallcomplete,
																																	player,
																																	Rank.get(
																																			player))));
																										}
																									}
																								}
																							}
																						}
																					}
																				} else if (!(Api.isInWarzone(player))) {
																					player.getInventory().setItem(slot,
																							item);
																					player.sendMessage(Api.color(
																							prefix + notInWarzone));
																					Bukkit.getScheduler().cancelTask(
																							CountDown.get(player));
																					CountDown.remove(player);
																					Active.remove(player);
																					Active.put(player, false);
																					Slot.remove(player);
																					Time.remove(player);
																					return;
																				}
																			}
																		}
																	}, 0, 20));
															return;
														} else {
															if (Active.get(player) == true) {
																player.sendMessage(Api.color(prefix + onlystart1));
															}
														}
													} else {
														player.sendMessage(Api.color(prefix + only1RankQuest));
														return;
													}
												} else {
													player.sendMessage(Api.color(prefix + notInWarzone));
													return;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			} else if (Api.getVersion() >= 191) {
				if (player.getInventory().getItemInOffHand() != null) {
					final ItemStack item = player.getInventory().getItemInOffHand();
					final Material itemtype = Material.getMaterial(questitemtype);
					if (item.hasItemMeta()) {
						// player.sendMessage("3");
						if (item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
							// player.sendMessage("4");
							if (item.getType() == Material.getMaterial(questitemtype)) {
								// player.sendMessage("5");
								for (String ranks : Main.settings.getConfig()
										.getConfigurationSection("RankQuestOptions.Ranks").getKeys(false)) {
									// player.sendMessage("6");
									String itemname = Api.color(Api.replacePHolders(
											Api.getConfigString("RankQuestOptions.Name"), player, ranks));
									rank = ranks;
									if (Api.removeColor(itemname)
											.contains(Api.removeColor(item.getItemMeta().getDisplayName()))) {
										// player.sendMessage("7");
										ArrayList<String> itemlore = new ArrayList<String>();
										for (String line : Main.settings.getConfig()
												.getStringList("RankQuestOptions.Lore")) {
											itemlore.add(Api.color(Api.replacePHolders(line, player, rank)));
										}
										if (itemlore != null) {
											// player.sendMessage("8");
											ArrayList<String> heldlore = (ArrayList<String>) Api.getItemInHand(player)
													.getItemMeta().getLore();
											if (Api.removeColor(itemlore.get(0))
													.equalsIgnoreCase(Api.removeColor(heldlore.get(0)))) {
												// player.sendMessage("9");
												String stime = Api
														.getConfigString("RankQuestOptions.Ranks." + rank + ".Time");
												if (Api.isInt(stime)) {
													// player.sendMessage("10");
													if (Api.isInWarzone(player)) {
														// player.sendMessage("11");
														if (item.getAmount() == 1) {
															if (Active.get(player) == false) {
																final int maxtime = Integer.parseInt(
																		Api.getConfigString("RankQuestOptions.Ranks."
																				+ rank + ".Time"));
																Rank.put(player, rank);
																Active.put(player, true);
																Slot.put(player,
																		player.getInventory().getHeldItemSlot());
																Time.put(player, maxtime);
																/*
																 * if (Api.
																 * getConfigString
																 * (
																 * "RankQuestOptions.TagPlayer")
																 * .equalsIgnoreCase
																 * ("true")) {
																 * if (Bukkit.
																 * getPluginManager
																 * ()
																 * .getPlugin(
																 * "CombatTagPlus")
																 * != null) {
																 * PlayerCombatTagEvent
																 * event = new
																 * PlayerCombatTagEvent(
																 * player,
																 * player,
																 * maxtime *
																 * 1000);
																 * Bukkit.
																 * getPluginManager
																 * ().callEvent(
																 * event); } }
																 */
																CountDown.put(player,
																		Bukkit.getScheduler().scheduleSyncRepeatingTask(
																				plugin, new Runnable() {
																					Player player = e.getPlayer();
																					int slot = Slot.get(player);

																					@Override
																					public void run() {
																						if (Active.get(player) != null
																								&& Active.get(
																										player) == true) {
																							if (Api.isInWarzone(
																									player)) {
																								int time = Time
																										.get(player);
																								if (time == maxtime) {
																									Time.put(player,
																											(time - 1));
																									String newname = "";
																									ArrayList<String> dlore = new ArrayList<String>();
																									ItemStack newitem = new ItemStack(
																											itemtype,
																											1);
																									ItemMeta newmeta = newitem
																											.getItemMeta();
																									newname = Api
																											.color(Api
																													.replacePHolders(
																															Api.getConfigString(
																																	"RankQuestOptions.CdName"),
																															player,
																															Rank.get(
																																	player))
																													.replace(
																															"%time%",
																															Api.getConfigString(
																																	"RankQuestOptions.Ranks."
																																			+ Rank.get(
																																					player)
																																			+ ".Time")));
																									for (String line : Main.settings
																											.getConfig()
																											.getStringList(
																													"RankQuestOptions.CdLore")) {
																										dlore.add(
																												Api.color(
																														Api.replacePHolders(
																																line,
																																player,
																																Rank.get(
																																		player))));
																									}
																									newmeta.setDisplayName(
																											newname);
																									newmeta.setLore(
																											dlore);
																									newitem.setItemMeta(
																											newmeta);
																									player.getInventory()
																											.setItem(
																													slot,
																													newitem);
																									for (String begin : queststart) {
																										player.sendMessage(
																												Api.color(
																														Api.replacePHolders(
																																begin,
																																player,
																																Rank.get(
																																		player))));
																									}
																									for (Player online : Bukkit
																											.getServer()
																											.getOnlinePlayers()) {
																										if (online != player) {
																											for (String beginall : questallstart) {
																												online.sendMessage(
																														Api.color(
																																Api.replacePHolders(
																																		beginall,
																																		player,
																																		Rank.get(
																																				player))));
																											}
																										}
																									}
																								}
																								if (time > 0
																										&& time < maxtime) {
																									Time.put(player,
																											(time - 1));
																									ItemStack questitemold = new ItemStack(
																											Material.getMaterial(
																													questitemtype),
																											1);
																									ItemMeta questitemoldmeta = questitemold
																											.getItemMeta();
																									String questitemoldname = Api
																											.color(Api
																													.replacePHolders(
																															Api.getConfigString(
																																	"RankQuestOptions.CdName"),
																															player,
																															Rank.get(
																																	player))
																													.replace(
																															"%time%",
																															"" + (time
																																	+ 1)));
																									ArrayList<String> questitemoldlore = new ArrayList<String>();
																									for (String line : Main.settings
																											.getConfig()
																											.getStringList(
																													"RankQuestOptions.CdLore")) {
																										questitemoldlore
																												.add(Api.color(
																														Api.replacePHolders(
																																line,
																																player,
																																Rank.get(
																																		player))));
																									}
																									questitemoldmeta
																											.setDisplayName(
																													questitemoldname);
																									questitemoldmeta
																											.setLore(
																													questitemoldlore);
																									questitemold
																											.setItemMeta(
																													questitemoldmeta);
																									if (player
																											.getInventory()
																											.getItem(
																													slot) != null) {
																										ItemStack held = player
																												.getInventory()
																												.getItem(
																														slot);
																										String dcdname = Api
																												.removeColor(
																														held.getItemMeta()
																																.getDisplayName());
																										String dccname = Api
																												.removeColor(
																														questitemold
																																.getItemMeta()
																																.getDisplayName());
																										List<String> dlore = held
																												.getItemMeta()
																												.getLore();
																										List<String> clore = questitemold
																												.getItemMeta()
																												.getLore();
																										ArrayList<String> dcdlore = new ArrayList<String>();
																										ArrayList<String> dcclore = new ArrayList<String>();
																										for (String line : dlore) {
																											dcdlore.add(
																													Api.removeColor(
																															line));
																										}
																										for (String line : clore) {
																											dcclore.add(
																													Api.removeColor(
																															line));
																										}
																										if (dcdname
																												.equalsIgnoreCase(
																														dccname)) {
																											if (dcdlore
																													.equals(dcclore)) {
																												ItemStack questitemnew = new ItemStack(
																														Material.getMaterial(
																																questitemtype),
																														1);
																												ItemMeta questitemnewmeta = questitemnew
																														.getItemMeta();
																												String questitemnewname = Api
																														.color(Api
																																.replacePHolders(
																																		Api.getConfigString(
																																				"RankQuestOptions.CdName"),
																																		player,
																																		Rank.get(
																																				player))
																																.replace(
																																		"%time%",
																																		"" + (time)));
																												ArrayList<String> questitemnewlore = new ArrayList<String>();
																												for (String line : Main.settings
																														.getConfig()
																														.getStringList(
																																"RankQuestOptions.CdLore")) {
																													questitemnewlore
																															.add(Api.color(
																																	Api.replacePHolders(
																																			line,
																																			player,
																																			Rank.get(
																																					player))));
																												}
																												questitemnewmeta
																														.setDisplayName(
																																questitemnewname);
																												questitemnewmeta
																														.setLore(
																																questitemnewlore);
																												questitemnew
																														.setItemMeta(
																																questitemnewmeta);
																												player.getInventory()
																														.setItem(
																																slot,
																																questitemnew);
																											}
																										}
																									}
																								}
																								if (time <= 0) {
																									Bukkit.getScheduler()
																											.cancelTask(
																													CountDown
																															.get(player));
																									CountDown.remove(
																											player);
																									Active.put(player,
																											false);
																									Slot.remove(player);
																									Time.remove(player);
																									player.getInventory()
																											.setItem(
																													slot,
																													new ItemStack(
																															Material.AIR));
																									if (Api.isInt(
																											Api.getConfigString(
																													"RankQuestOptions.Ranks."
																															+ Rank.get(
																																	player)
																															+ ".Voucher.Amount"))) {
																										int amount = Integer
																												.parseInt(
																														Api.getConfigString(
																																"RankQuestOptions.Ranks."
																																		+ Rank.get(
																																				player)
																																		+ ".Voucher.Amount"));
																										if (amount > 0) {
																											ItemStack voucher = new ItemStack(
																													Material.getMaterial(
																															Api.getConfigString(
																																	"RankQuestOptions.Ranks."
																																			+ Rank.get(
																																					player)
																																			+ ".Voucher.VoucherItemType")),
																													amount);
																											ItemMeta vouchermeta = voucher
																													.getItemMeta();
																											vouchermeta
																													.setDisplayName(
																															Api.color(
																																	Api.replacePHolders(
																																			Api.getConfigString(
																																					"RankQuestOptions.Ranks."
																																							+ Rank.get(
																																									player)
																																							+ ".Voucher.Name"),
																																			player,
																																			Rank.get(
																																					player))));
																											ArrayList<String> vlore = new ArrayList<String>();
																											for (String line : Main.settings
																													.getConfig()
																													.getStringList(
																															"RankQuestOptions.Ranks."
																																	+ Rank.get(
																																			player)
																																	+ ".Voucher.Lore")) {
																												vlore.add(
																														Api.color(
																																Api.replacePHolders(
																																		line,
																																		player,
																																		Rank.get(
																																				player))));
																											}
																											vouchermeta
																													.setLore(
																															vlore);
																											voucher.setItemMeta(
																													vouchermeta);
																											if (voucher != null) {
																												player.getInventory()
																														.setItem(
																																slot,
																																voucher);
																												player.sendMessage(
																														Api.color(
																																Api.replacePHolders(
																																		prefix + questcomplete,
																																		player,
																																		Rank.get(
																																				player))));
																												for (Player players : Bukkit
																														.getServer()
																														.getOnlinePlayers()) {
																													if (players != player) {
																														players.sendMessage(
																																Api.color(
																																		Api.replacePHolders(
																																				prefix + questallcomplete,
																																				player,
																																				Rank.get(
																																						player))));
																													}
																												}
																											}
																										}
																									}
																								}
																							} else if (!(Api
																									.isInWarzone(
																											player))) {
																								player.getInventory()
																										.setItem(slot,
																												item);
																								player.sendMessage(
																										Api.color(prefix
																												+ notInWarzone));
																								Bukkit.getScheduler()
																										.cancelTask(
																												CountDown
																														.get(player));
																								CountDown
																										.remove(player);
																								Active.remove(player);
																								Active.put(player,
																										false);
																								Slot.remove(player);
																								Time.remove(player);
																								return;
																							}
																						}
																					}
																				}, 0, 20));
																return;
															} else {
																if (Active.get(player) == true) {
																	player.sendMessage(Api.color(prefix + onlystart1));
																}
															}
														} else {
															player.sendMessage(Api.color(prefix + only1RankQuest));
															return;
														}
													} else {
														player.sendMessage(Api.color(prefix + notInWarzone));
														return;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void questItemDropEvent(PlayerDropItemEvent e) {
		if (e.getPlayer() != null) {
			Player player = e.getPlayer();
			if (Active.get(player) != null && Active.get(player) == true) {
				if (Rank.get(player) != null) {
					if (e.getItemDrop() != null) {
						Item idrop = e.getItemDrop();
						ItemStack drop = idrop.getItemStack();
						if (drop.hasItemMeta()) {
							if (drop.getItemMeta().hasDisplayName() && drop.getItemMeta().hasLore()) {
								if (drop.getAmount() != 0) {
									if (drop.getType() != null) {
										if (drop.getType().name() != null) {
											if (drop.getType().name().equalsIgnoreCase(
													Api.getConfigString("RankQuestOptions.QuestItemType"))) {
												String line = Api.removeColor(drop.getItemMeta().getLore().get(0));
												String sline = Main.settings.getConfig()
														.getStringList("RankQuestOptions.CdLore").get(0);
												String fsline = Api
														.color(Api.replacePHolders(sline, player, Rank.get(player)));
												String dcfsline = Api.removeColor(fsline);
												if (line.equalsIgnoreCase(dcfsline)) {
													e.setCancelled(true);
													player.sendMessage(Api.color(
															prefix + Api.getMessageString("Messages.DropItemMessage")));
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e) {
		if (e.getWhoClicked() != null && e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			if (Active.get(player) != null && Active.get(player) == true) {
				if (Rank.get(player) != null) {
					if (e.getCurrentItem() != null) {
						ItemStack questitem = e.getCurrentItem();
						String cname = Api.color(Api.replacePHolders(Api.getConfigString("RankQuestOptions.CdName"),
								player, Rank.get(player)));
						if (questitem.hasItemMeta()) {
							ItemMeta questitemmeta = questitem.getItemMeta();
							if (questitemmeta.getDisplayName() != null && questitemmeta.getLore() != null) {
								if (Api.getArgument("%time%", questitem, cname) != null) {
									if (Api.isInt("" + Api.getArgument("%time%", questitem, cname))) {
										ArrayList<String> dcclore = new ArrayList<String>();
										for (String line : Main.settings.getConfig()
												.getStringList("RankQuestOptions.CdLore")) {
											dcclore.add(Api.removeColor(
													Api.color(Api.replacePHolders(line, player, Rank.get(player)))));
										}
										ArrayList<String> dcdlore = new ArrayList<String>();
										for (String line : questitemmeta.getLore()) {
											dcdlore.add(Api.removeColor(line));
										}
										if (dcclore != null && dcclore != null) {
											if (dcclore.equals(dcdlore)) {
												e.setCancelled(true);
												player.sendMessage(Api.color(prefix + move));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void inventoryMoveItemEvent(InventoryMoveItemEvent e) {
		if (e.getSource().getHolder() instanceof Player) {
			Player player = (Player) e.getSource().getHolder();
			if (e.getItem() != null) {
				ItemStack iitem = e.getItem();
				if (iitem.getType() == Material.getMaterial(Api.getConfigString("RankQuestOptions.QuestItemType"))) {
					if (iitem.hasItemMeta()) {
						ItemMeta iitemMeta = iitem.getItemMeta();
						if (iitemMeta.hasDisplayName() && iitemMeta.hasLore()) {
							String cname = Api.color(Api.replacePHolders(Api.getConfigString("RankQuestOptions.CdName"),
									player, Rank.get(player)));
							if (Api.isInt("" + Api.getArgument("%time%", iitem, cname))) {
								ArrayList<String> dcclore = new ArrayList<String>();
								for (String line : Main.settings.getConfig().getStringList("RankQuestOptions.CdLore")) {
									dcclore.add(Api.removeColor(
											Api.color(Api.replacePHolders(line, player, Rank.get(player)))));
								}
								ArrayList<String> dcdlore = new ArrayList<String>();
								for (String line : iitemMeta.getLore()) {
									dcdlore.add(Api.removeColor(line));
								}
								if (dcclore != null && dcclore != null) {
									if (dcclore.equals(dcdlore)) {
										e.setCancelled(true);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent e) {
		if (Active.get(e.getPlayer()) == true) {
			Player player = e.getPlayer();
			ItemStack questitem = new ItemStack(
					Material.getMaterial(Api.getConfigString("RankQuestOptions.QuestItemType")), 1);
			ItemMeta questitemmeta = questitem.getItemMeta();
			questitemmeta.setDisplayName(Api.color(
					Api.replacePHolders(Api.getConfigString("RankQuestOptions.CdName"), player, Rank.get(player))));
			ArrayList<String> lore = new ArrayList<String>();
			for (String line : Main.settings.getConfig().getStringList("Lore")) {
				lore.add(Api.color(Api.replacePHolders(line, player, Rank.get(player))));
			}
			questitemmeta.setLore(lore);
			questitem.setItemMeta(questitemmeta);
			if (questitem != null) {
				player.getInventory().setItem(Slot.get(player), questitem);
				Bukkit.getScheduler().cancelTask(CountDown.get(player));
				CountDown.remove(player);
				Active.put(player, false);
				Slot.remove(player);
				Time.remove(player);
			}
		}
	}

	@EventHandler
	public void playerDeathEvent(final PlayerDeathEvent e) {
		if (e.getEntity() != null && e.getEntity() instanceof Player) {
			final Player player = e.getEntity();
			if (Active.get(player) != null && Active.get(player) == true) {
				if (player != null) {
					if (Rank.get(player) != null) {
						for (ItemStack drop : e.getDrops()) {
							if (drop != null) {
								if (drop.hasItemMeta()) {
									if (drop.getItemMeta().hasDisplayName() && drop.getItemMeta().hasLore()) {
										if (drop.getAmount() != 0) {
											if (drop.getType().name().equalsIgnoreCase(
													Api.getConfigString("RankQuestOptions.QuestItemType"))) {
												String line = Api.removeColor(drop.getItemMeta().getLore().get(0));
												String sline = Main.settings.getConfig()
														.getStringList("RankQuestOptions.CdLore").get(0);
												String fsline = Api
														.color(Api.replacePHolders(sline, player, Rank.get(player)));
												String dcfsline = Api.removeColor(fsline);
												if (line.equalsIgnoreCase(dcfsline)) {
													String dname = Api.color(Api.replacePHolders(
															Api.getConfigString("RankQuestOptions.Name"), player,
															Rank.get(player)));
													ArrayList<String> dlore = new ArrayList<String>();
													for (String lore : Main.settings.getConfig()
															.getStringList("RankQuestOptions.Lore")) {
														dlore.add(Api.color(
																Api.replacePHolders(lore, player, Rank.get(player))));
													}
													ItemMeta dropMeta = drop.getItemMeta();
													dropMeta.setDisplayName(dname);
													dropMeta.setLore(dlore);
													drop.setItemMeta(dropMeta);
													drop.setAmount(1);
													Bukkit.getScheduler().cancelTask(CountDown.get(player));
													CountDown.remove(player);
													Active.put(player, false);
													Slot.remove(player);
													Time.remove(player);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
