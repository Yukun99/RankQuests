package me.Yukun.RankQuests;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Yukun.RankQuests.Api;

public class RankQuest implements CommandExecutor {
	String SIname = "noitem";

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		int amount = 0;
		String header = "&b&l==========RankQuests v1.0 by Yukun==========";
		String footer = "&b&l============================================";
		String usage3 = "/rankquest redeem";
		String usage2 = "/rankquest reload";
		String usage = "/rankquest give <player> <rank> <amount>";
		String prefix = Api.getMessageString("Messages.Prefix");
		String invalid = "&cInvalid argument!";
		String rank = "";
		String questreceive = Api.getMessageString("Messages.QuestReceive");
		String voucherreceive = Api.getMessageString("Messages.VoucherReceive");
		String fullinvenquest = Api.getMessageString("Messages.QuestInventoryFull");
		String fullinvenvoucher = Api.getMessageString("Messages.VoucherInventoryFull");

		if (args.length == 0) {
			sender.sendMessage(Api.color(header));
			sender.sendMessage(Api.color("&fThanks for downloading my plugin!"));
			sender.sendMessage(Api.color("&bCommands: "));
			sender.sendMessage(Api.color("&c/rankquest give: " + usage));
			sender.sendMessage(Api.color("&c/rankquest reload: " + usage2));
			sender.sendMessage(Api.color("&c/rankquest redeem: " + usage3));
			sender.sendMessage(Api.color(footer));
			return true;
		}
		if (args.length == 1) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args[0].equalsIgnoreCase("redeem")) {
					if (Api.getRedeemString("Redeem." + player.getName()) != null) {
						HashMap<String, Integer> redeems = new HashMap<String, Integer>();
						if (Main.settings.getRedeems().getConfigurationSection("Redeem." + player.getName())
								.getKeys(false) != null) {
							for (String line : Main.settings.getRedeems()
									.getConfigurationSection("Redeem." + player.getName()).getKeys(false)) {
								redeems.put(line, Integer.parseInt(
										Api.getRedeemString("Redeem." + player.getName() + "." + line + ".Amount")));
							}
							if (redeems != null) {
								for (String ranks : Main.settings.getConfig()
										.getConfigurationSection("RankQuestOptions.Ranks").getKeys(false)) {
									if (redeems.get(ranks) != null) {
										ItemStack item = new ItemStack(
												Material.getMaterial(
														Api.getConfigString("RankQuestOptions.QuestItemType")),
												redeems.get(ranks));
										ItemMeta itemMeta = item.getItemMeta();
										String dname = Api.color(Api.replacePHolders(
												Api.getConfigString("RankQuestOptions.Name"), player, ranks));
										ArrayList<String> lore = new ArrayList<String>();
										for (String line : Main.settings.getConfig()
												.getStringList("RankQuestOptions.Lore")) {
											lore.add(Api.color(Api.replacePHolders(line, player, ranks)));
										}
										itemMeta.setDisplayName(dname);
										itemMeta.setLore(lore);
										item.setItemMeta(itemMeta);
										if (player.getInventory().firstEmpty() != -1) {
											player.getInventory().addItem(item);
											redeems.remove(ranks);
											Api.setRedeemsInt("Redeem." + player.getName() + "." + ranks + ".Amount",
													null);
											Main.settings.saveRedeems();
											Api.setRedeemsString("Redeem." + player.getName() + "." + ranks, null);
											Main.settings.saveRedeems();
											Api.setRedeemsString("Redeem." + player.getName(), null);
											Main.settings.saveRedeems();
										} else if (Api.containsItem(player, item)) {
											ItemStack invenitem = Api.getItem(player, item);
											if ((invenitem.getAmount() + item.getAmount()) <= 64) {
												player.getInventory().addItem(item);
												player.sendMessage(Api
														.color(Api.replacePHolders(prefix + questreceive, player, ranks)
																.replace("%amount%", item.getAmount() + "")));
												redeems.remove(ranks);
												Api.setRedeemsInt(
														"Redeem." + player.getName() + "." + ranks + ".Amount", null);
												Main.settings.saveRedeems();
												Api.setRedeemsString("Redeem." + player.getName() + "." + ranks, null);
												Main.settings.saveRedeems();
												Api.setRedeemsString("Redeem." + player.getName(), null);
												Main.settings.saveRedeems();
											} else {
												player.sendMessage(Api.color(prefix + fullinvenquest));
											}
										}
									}
									continue;
								}
							}
						}
					}
				} else if (player.isOp() || player.hasPermission("RankQuest.Reload")) {
					if (args[0].equalsIgnoreCase("reload")) {
						Main.settings.reloadConfig();
						Main.settings.reloadMessages();
						sender.sendMessage(Api.color(prefix + "&aYou have reloaded the plugin!"));
						return true;
					} else if (args[0].equalsIgnoreCase("give")) {
						sender.sendMessage(Api.color(prefix + usage));
						return true;
					} else {
						sender.sendMessage(Api.color(prefix + invalid));
						return true;
					}
				} else {
					player.sendMessage(Api.color(prefix + "&cYou do not have permission to use this command!"));
					return false;
				}
			} else {
				if (args[0].equalsIgnoreCase("reload")) {
					Main.settings.reloadConfig();
					Main.settings.reloadMessages();
					sender.sendMessage(Api.color(prefix + "&aYou have reloaded the plugin!"));
					return true;
				} else if (args[0] == "give") {
					sender.sendMessage(me.Yukun.RankQuests.Api.color(prefix + usage));
					return true;
				} else {
					sender.sendMessage(Api.color(prefix + invalid));
					return true;
				}
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("give")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (player.isOp() || player.hasPermission("RankQuest.Give")) {
						for (String ranks : Main.settings.getConfig().getConfigurationSection("RankQuestOptions.Ranks")
								.getKeys(false)) {
							if (args[1].equalsIgnoreCase(ranks)) {
								rank = ranks;
								ItemStack item = new ItemStack(
										Material.getMaterial(Api.getConfigString("RankQuestOptions.QuestItemType")), 1);
								ItemMeta itemMeta = item.getItemMeta();
								String dname = Api.color(Api
										.replacePHolders(Api.getConfigString("RankQuestOptions.Name"), player, rank));
								ArrayList<String> dlore = new ArrayList<String>();
								for (String line : Main.settings.getConfig().getStringList("RankQuestOptions.Lore")) {
									dlore.add(Api.color(Api.replacePHolders(line, player, rank)));
								}
								itemMeta.setDisplayName(dname);
								itemMeta.setLore(dlore);
								item.setItemMeta(itemMeta);
								if (player.getInventory().firstEmpty() != -1) {
									player.sendMessage(
											Api.color(Api.replacePHolders(prefix + questreceive, player, ranks)
													.replace("%amount%", 1 + "")));
									player.getInventory().addItem(item);
									return true;
								} else {
									if (!Api.containsItem(player, item)) {
										player.sendMessage(Api.color(prefix + fullinvenquest));
										Api.setRedeemsInt("Redeem." + player.getName() + "." + ranks + ".Amount", 1);
										return true;
									} else {
										if (Api.containsItem(player, item)) {
											ItemStack invenitem = Api.getItem(player, item);
											if ((invenitem.getAmount() + 1) <= 64) {
												player.sendMessage(Api
														.color(Api.replacePHolders(prefix + questreceive, player, ranks)
																.replace("%amount%", 1 + "")));
												player.getInventory().addItem(item);
												return true;
											} else {
												Api.setRedeemsInt(
														"Redeem." + player.getName() + "." + ranks + ".Amount", 1);
												player.sendMessage(Api.color(prefix + fullinvenquest));
											}
										}
									}
								}
							} else if (args[1].equalsIgnoreCase(ranks + "Voucher")) {
								rank = ranks + ".Voucher";
								ItemStack item = new ItemStack(
										Material.getMaterial(
												Api.getConfigString("RankQuestOptions.Ranks." + rank + ".VoucherItemType")),
										1);
								ItemMeta itemMeta = item.getItemMeta();
								String dname = Api.color(Api.replacePHolders(
										Api.getConfigString("RankQuestOptions.Ranks." + rank + ".Name"), player, ranks));
								ArrayList<String> dlore = new ArrayList<String>();
								for (String line : Main.settings.getConfig()
										.getStringList("RankQuestOptions.Ranks." + rank + ".Lore")) {
									dlore.add(Api.color(Api.replacePHolders(line, player, ranks)));
								}
								itemMeta.setDisplayName(dname);
								itemMeta.setLore(dlore);
								item.setItemMeta(itemMeta);
								if (player.getInventory().firstEmpty() != -1) {
									player.sendMessage(
											Api.color(Api.replacePHolders(prefix + voucherreceive, player, ranks)
													.replace("%amount%", 1 + "")));
									player.getInventory().addItem(item);
									return true;
								} else {
									if (!Api.containsItem(player, item)) {
										player.sendMessage(Api.color(prefix + fullinvenvoucher));
										return true;
									} else {
										if (Api.containsItem(player, item)) {
											ItemStack invenitem = Api.getItem(player, item);
											if ((invenitem.getAmount() + 1) <= 64) {
												player.sendMessage(Api.color(
														Api.replacePHolders(prefix + voucherreceive, player, ranks)
																.replace("%amount%", 1 + "")));
												player.getInventory().addItem(item);
											} else {
												player.sendMessage(Api.color(prefix + fullinvenvoucher));
											}
										}
									}
								}
							}
						}
					} else {
						sender.sendMessage(Api.color(prefix + "&cYou do not have permission to use this command!"));
					}
				} else {
					sender.sendMessage(Api.color(prefix + "&cPlease specify who to send the item to!"));
				}
			} else {
				sender.sendMessage(Api.color(prefix + invalid));
				return false;
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("give")) {
				if (sender instanceof Player) {
					if (sender.isOp() || sender.hasPermission("RankQuest.Give")) {
						if (Bukkit.getPlayer(args[1]) != null) {
							Player player = Bukkit.getPlayer(args[1]);
							for (String ranks : Main.settings.getConfig()
									.getConfigurationSection("RankQuestOptions.Ranks").getKeys(false)) {
								if (args[2].equalsIgnoreCase(ranks)) {
									rank = ranks;
									ItemStack item = new ItemStack(
											Material.getMaterial(Api.getConfigString("RankQuestOptions.QuestItemType")),
											1);
									ItemMeta itemMeta = item.getItemMeta();
									String dname = Api.color(Api.replacePHolders(
											Api.getConfigString("RankQuestOptions.Name"), player, rank));
									ArrayList<String> dlore = new ArrayList<String>();
									for (String line : Main.settings.getConfig()
											.getStringList("RankQuestOptions.Lore")) {
										dlore.add(Api.color(Api.replacePHolders(line, player, rank)));
									}
									itemMeta.setDisplayName(dname);
									itemMeta.setLore(dlore);
									item.setItemMeta(itemMeta);
									if (player.getInventory().firstEmpty() != -1) {
										player.sendMessage(
												Api.color(Api.replacePHolders(prefix + questreceive, player, ranks)
														.replace("%amount%", 1 + "")));
										player.getInventory().addItem(item);
										return true;
									} else {
										if (!Api.containsItem(player, item)) {
											player.sendMessage(Api.color(prefix + fullinvenquest));
											Api.setRedeemsInt("Redeem." + player.getName() + "." + ranks + ".Amount",
													1);
											return true;
										} else {
											if (Api.containsItem(player, item)) {
												ItemStack invenitem = Api.getItem(player, item);
												if ((invenitem.getAmount() + 1) <= 64) {
													player.sendMessage(Api.color(
															Api.replacePHolders(prefix + questreceive, player, ranks)
																	.replace("%amount%", 1 + "")));
													player.getInventory().addItem(item);
													return true;
												} else {
													Api.setRedeemsInt(
															"Redeem." + player.getName() + "." + ranks + ".Amount", 1);
													player.sendMessage(Api.color(prefix + fullinvenquest));
												}
											}
										}
									}
								} else if (args[2].equalsIgnoreCase(ranks + "Voucher")) {
									rank = ranks + ".Voucher";
									ItemStack item = new ItemStack(
											Material.getMaterial(Api
													.getConfigString("RankQuestOptions.Ranks." + rank + ".VoucherItemType")),
											1);
									ItemMeta itemMeta = item.getItemMeta();
									String dname = Api.color(Api.replacePHolders(
											Api.getConfigString("RankQuestOptions.Ranks." + rank + ".Name"), player, ranks));
									ArrayList<String> dlore = new ArrayList<String>();
									for (String line : Main.settings.getConfig()
											.getStringList("RankQuestOptions.Ranks." + rank + ".Lore")) {
										dlore.add(Api.color(Api.replacePHolders(line, player, ranks)));
									}
									itemMeta.setDisplayName(dname);
									itemMeta.setLore(dlore);
									item.setItemMeta(itemMeta);
									if (player.getInventory().firstEmpty() != -1) {
										player.sendMessage(
												Api.color(Api.replacePHolders(prefix + voucherreceive, player, ranks)
														.replace("%amount%", 1 + "")));
										player.getInventory().addItem(item);
										return true;
									} else {
										if (!Api.containsItem(player, item)) {
											player.sendMessage(Api.color(prefix + fullinvenvoucher));
											return true;
										} else {
											if (Api.containsItem(player, item)) {
												ItemStack invenitem = Api.getItem(player, item);
												if ((invenitem.getAmount() + 1) <= 64) {
													player.sendMessage(Api.color(
															Api.replacePHolders(prefix + voucherreceive, player, ranks)
																	.replace("%amount%", 1 + "")));
													player.getInventory().addItem(item);
													return true;
												} else {
													player.sendMessage(Api.color(prefix + fullinvenvoucher));
													return true;
												}
											}
										}
									}
								}
							}
						} else {
							sender.sendMessage(Api.color(prefix + invalid));
							return false;
						}
					} else {
						sender.sendMessage(Api.color(prefix + "&cYou do not have permission to use this command!"));
					}
				}
				if (!(sender instanceof Player)) {
					if (Bukkit.getPlayer(args[1]) != null) {
						Player player = Bukkit.getPlayer(args[1]);
						for (String ranks : Main.settings.getConfig().getConfigurationSection("RankQuestOptions.Ranks")
								.getKeys(false)) {
							if (args[2].equalsIgnoreCase(ranks)) {
								rank = ranks;
								ItemStack item = new ItemStack(
										Material.getMaterial(Api.getConfigString("RankQuestOptions.QuestItemType")), 1);
								ItemMeta itemMeta = item.getItemMeta();
								String dname = Api.color(Api
										.replacePHolders(Api.getConfigString("RankQuestOptions.Name"), player, rank));
								ArrayList<String> dlore = new ArrayList<String>();
								for (String line : Main.settings.getConfig().getStringList("RankQuestOptions.Lore")) {
									dlore.add(Api.color(Api.replacePHolders(line, player, rank)));
								}
								itemMeta.setDisplayName(dname);
								itemMeta.setLore(dlore);
								item.setItemMeta(itemMeta);
								if (player.getInventory().firstEmpty() != -1) {
									player.sendMessage(
											Api.color(Api.replacePHolders(prefix + questreceive, player, ranks)
													.replace("%amount%", 1 + "")));
									player.getInventory().addItem(item);
									return true;
								} else {
									if (!Api.containsItem(player, item)) {
										Api.setRedeemsInt("Redeem." + player.getName() + "." + ranks + ".Amount", 1);
										player.sendMessage(Api.color(prefix + fullinvenquest));
										return true;
									} else {
										if (Api.containsItem(player, item)) {
											ItemStack invenitem = Api.getItem(player, item);
											if ((invenitem.getAmount() + 1) <= 64) {
												player.sendMessage(Api
														.color(Api.replacePHolders(prefix + questreceive, player, ranks)
																.replace("%amount%", 1 + "")));
												player.getInventory().addItem(item);
												return true;
											} else {
												Api.setRedeemsInt(
														"Redeem." + player.getName() + "." + ranks + ".Amount", 1);
												player.sendMessage(Api.color(prefix + fullinvenquest));
											}
										}
									}
								}
							} else if (args[2].equalsIgnoreCase(ranks + "Voucher")) {
								rank = ranks + ".Voucher";
								ItemStack item = new ItemStack(
										Material.getMaterial(
												Api.getConfigString("RankQuestOptions.Ranks." + rank + ".VoucherItemType")),
										1);
								ItemMeta itemMeta = item.getItemMeta();
								String dname = Api.color(Api.replacePHolders(
										Api.getConfigString("RankQuestOptions.Ranks." + rank + ".Name"), player, ranks));
								ArrayList<String> dlore = new ArrayList<String>();
								for (String line : Main.settings.getConfig()
										.getStringList("RankQuestOptions.Ranks." + rank + ".Lore")) {
									dlore.add(Api.color(Api.replacePHolders(line, player, ranks)));
								}
								itemMeta.setDisplayName(dname);
								itemMeta.setLore(dlore);
								item.setItemMeta(itemMeta);
								if (player.getInventory().firstEmpty() != -1) {
									player.sendMessage(
											Api.color(Api.replacePHolders(prefix + voucherreceive, player, ranks)
													.replace("%amount%", 1 + "")));
									player.getInventory().addItem(item);
									return true;
								} else {
									if (!Api.containsItem(player, item)) {
										player.sendMessage(Api.color(prefix + fullinvenvoucher));
										return true;
									} else {
										if (Api.containsItem(player, item)) {
											ItemStack invenitem = Api.getItem(player, item);
											if ((invenitem.getAmount() + 1) <= 64) {
												player.sendMessage(Api.color(
														Api.replacePHolders(prefix + voucherreceive, player, ranks)
																.replace("%amount%", 1 + "")));
												player.getInventory().addItem(item);
											} else {
												player.sendMessage(Api.color(prefix + fullinvenvoucher));
											}
										}
									}
								}
							}
						}
					} else {
						sender.sendMessage(Api.color(prefix + invalid));
						return false;
					}
				}
			} else {
				sender.sendMessage(Api.color(prefix + invalid));
				return false;
			}
		}
		if (args.length == 4) {
			if (args[0].equalsIgnoreCase("give")) {
				if (sender instanceof Player) {
					if (sender.isOp() || sender.hasPermission("RankQuest.Give")) {
						if (Bukkit.getPlayer(args[1]) != null) {
							Player player = Bukkit.getPlayer(args[1]);
							for (String ranks : Main.settings.getConfig()
									.getConfigurationSection("RankQuestOptions.Ranks").getKeys(false)) {
								if (args[2].equalsIgnoreCase(ranks)) {
									if (Api.isInt(args[3])) {
										amount = Integer.parseInt(args[3]);
										if (amount != 0 && amount <= 64) {
											rank = ranks;
											ItemStack item = new ItemStack(
													Material.getMaterial(
															Api.getConfigString("RankQuestOptions.QuestItemType")),
													amount);
											ItemMeta itemMeta = item.getItemMeta();
											String dname = Api.color(Api.replacePHolders(
													Api.getConfigString("RankQuestOptions.Name"), player, ranks));
											ArrayList<String> dlore = new ArrayList<String>();
											for (String line : Main.settings.getConfig()
													.getStringList("RankQuestOptions.Lore")) {
												dlore.add(Api.color(Api.replacePHolders(line, player, ranks)));
											}
											itemMeta.setDisplayName(dname);
											itemMeta.setLore(dlore);
											item.setItemMeta(itemMeta);
											if (player.getInventory().firstEmpty() != -1) {
												player.getInventory().addItem(item);
												player.sendMessage(Api
														.color(Api.replacePHolders(prefix + questreceive, player, ranks)
																.replace("%amount%", amount + "")));
												return true;
											} else {
												if (!Api.containsItem(player, item)) {
													player.sendMessage(Api.color(prefix + fullinvenquest));
													Api.setRedeemsInt(
															"Redeem." + player.getName() + "." + ranks + ".Amount",
															amount);
													return true;
												} else {
													if (Api.containsItem(player, item)) {
														ItemStack invenitem = Api.getItem(player, item);
														if ((invenitem.getAmount() + amount) <= 64) {
															player.sendMessage(
																	Api.color(Api
																			.replacePHolders(prefix + questreceive,
																					player, ranks)
																			.replace("%amount%", amount + "")));
															player.getInventory().addItem(item);
														} else {
															player.sendMessage(Api.color(prefix + fullinvenquest));
															Api.setRedeemsInt("Redeem." + player.getName() + "." + ranks
																	+ ".Amount", amount);
															return true;
														}
													}
												}
											}
										} else {
											player.sendMessage(Api.color(prefix
													+ "&cEither you are trying to spawn in 0 items, or more than a stack of items. I'm sorry but you can't do that. (yet)"));
											return false;
										}
									} else {
										sender.sendMessage(Api.color(prefix + invalid));
									}
								} else if (args[2].equalsIgnoreCase(ranks + "Voucher")) {
									if (Api.isInt(args[3])) {
										amount = Integer.parseInt(args[3]);
										if (amount != 0 && amount <= 64) {
											rank = ranks + ".Voucher";
											ItemStack item = new ItemStack(Material.getMaterial(Api
													.getConfigString("RankQuestOptions.Ranks." + rank + ".VoucherItemType")),
													amount);
											ItemMeta itemMeta = item.getItemMeta();
											String dname = Api.color(Api.replacePHolders(
													Api.getConfigString("RankQuestOptions.Ranks." + rank + ".Name"), player,
													ranks));
											ArrayList<String> dlore = new ArrayList<String>();
											for (String line : Main.settings.getConfig()
													.getStringList("RankQuestOptions.Ranks." + rank + ".Lore")) {
												dlore.add(Api.color(Api.replacePHolders(line, player, ranks)));
											}
											itemMeta.setDisplayName(dname);
											itemMeta.setLore(dlore);
											item.setItemMeta(itemMeta);
											if (player.getInventory().firstEmpty() != -1) {
												player.getInventory().addItem(item);
												player.sendMessage(Api.color(
														Api.replacePHolders(prefix + voucherreceive, player, ranks)
																.replace("%amount%", amount + "")));
												return true;
											} else {
												if (!Api.containsItem(player, item)) {
													player.sendMessage(Api.color(prefix + fullinvenvoucher));
													return true;
												} else {
													if (Api.containsItem(player, item)) {
														ItemStack invenitem = Api.getItem(player, item);
														if ((invenitem.getAmount() + amount) <= 64) {
															player.sendMessage(
																	Api.color(Api
																			.replacePHolders(prefix + voucherreceive,
																					player, ranks)
																			.replace("%amount%", amount + "")));
															player.getInventory().addItem(item);
														} else {
															player.sendMessage(Api.color(prefix + fullinvenvoucher));
															return true;
														}
													}
												}
											}
										}
									}
								}
							}
						} else {
							sender.sendMessage(Api.color(prefix + invalid));
							return false;
						}
					} else {
						sender.sendMessage(Api.color(prefix + "&cYou do not have permission to use this command!"));
					}
				}
				if (!(sender instanceof Player)) {
					if (Bukkit.getPlayer(args[1]) != null) {
						Player player = Bukkit.getPlayer(args[1]);
						for (String ranks : Main.settings.getConfig().getConfigurationSection("RankQuestOptions.Ranks")
								.getKeys(false)) {
							if (args[2].equalsIgnoreCase(ranks)) {
								if (Api.isInt(args[3])) {
									amount = Integer.parseInt(args[3]);
									if (amount != 0 && amount <= 64) {
										rank = ranks;
										ItemStack item = new ItemStack(Material.getMaterial(
												Api.getConfigString("RankQuestOptions.QuestItemType")), amount);
										ItemMeta itemMeta = item.getItemMeta();
										String dname = Api.color(Api.replacePHolders(
												Api.getConfigString("RankQuestOptions.Name"), player, ranks));
										ArrayList<String> dlore = new ArrayList<String>();
										for (String line : Main.settings.getConfig()
												.getStringList("RankQuestOptions.Lore")) {
											dlore.add(Api.color(Api.replacePHolders(line, player, ranks)));
										}
										itemMeta.setDisplayName(dname);
										itemMeta.setLore(dlore);
										item.setItemMeta(itemMeta);
										if (player.getInventory().firstEmpty() != -1) {
											player.getInventory().addItem(item);
											player.sendMessage(
													Api.color(Api.replacePHolders(prefix + questreceive, player, ranks)
															.replace("%amount%", amount + "")));
											return true;
										} else {
											if (!Api.containsItem(player, item)) {
												player.sendMessage(Api.color(prefix + fullinvenquest));
												return true;
											} else {
												if (Api.containsItem(player, item)) {
													ItemStack invenitem = Api.getItem(player, item);
													if ((invenitem.getAmount() + amount) <= 64) {
														player.sendMessage(Api.color(Api
																.replacePHolders(prefix + questreceive, player, ranks)
																.replace("%amount%", amount + "")));
														player.getInventory().addItem(item);
													} else {
														player.sendMessage(Api.color(prefix + fullinvenquest));
														return true;
													}
												}
											}
										}
									} else {
										player.sendMessage(Api.color(prefix
												+ "&cEither you are trying to spawn in 0 items, or more than a stack of items. I'm sorry but you can't do that. (yet)"));
										return false;
									}
								} else {
									sender.sendMessage(Api.color(prefix + invalid));
								}
							} else if (args[2].equalsIgnoreCase(ranks + "Voucher")) {
								if (Api.isInt(args[3])) {
									amount = Integer.parseInt(args[3]);
									if (amount != 0 && amount <= 64) {
										rank = ranks + ".Voucher";
										ItemStack item = new ItemStack(Material.getMaterial(
												Api.getConfigString("RankQuestOptions.Ranks." + rank + ".VoucherItemType")),
												amount);
										ItemMeta itemMeta = item.getItemMeta();
										String dname = Api.color(Api.replacePHolders(
												Api.getConfigString("RankQuestOptions.Ranks." + rank + ".Name"), player,
												ranks));
										ArrayList<String> dlore = new ArrayList<String>();
										for (String line : Main.settings.getConfig()
												.getStringList("RankQuestOptions.Ranks." + rank + ".Lore")) {
											dlore.add(Api.color(Api.replacePHolders(line, player, ranks)));
										}
										itemMeta.setDisplayName(dname);
										itemMeta.setLore(dlore);
										item.setItemMeta(itemMeta);
										if (player.getInventory().firstEmpty() != -1) {
											player.getInventory().addItem(item);
											player.sendMessage(Api
													.color(Api.replacePHolders(prefix + voucherreceive, player, ranks)
															.replace("%amount%", amount + "")));
											return true;
										} else {
											if (!Api.containsItem(player, item)) {
												player.sendMessage(Api.color(prefix + fullinvenvoucher));
												return true;
											} else {
												if (Api.containsItem(player, item)) {
													ItemStack invenitem = Api.getItem(player, item);
													if ((invenitem.getAmount() + amount) <= 64) {
														player.sendMessage(Api.color(Api
																.replacePHolders(prefix + voucherreceive, player, ranks)
																.replace("%amount%", amount + "")));
														player.getInventory().addItem(item);
													} else {
														player.sendMessage(Api.color(prefix + fullinvenvoucher));
														return true;
													}
												}
											}
										}
									}
								}
							}
						}
					} else {
						sender.sendMessage(Api.color(prefix + invalid));
						return false;
					}
				}
			} else {
				sender.sendMessage(Api.color(prefix + invalid));
				return false;
			}
		}

		if (args.length > 4) {
			sender.sendMessage(Api.color(prefix + invalid));
			return false;
		}
		return false;
	}
}
