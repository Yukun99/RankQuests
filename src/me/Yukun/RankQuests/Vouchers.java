package me.Yukun.RankQuests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Vouchers implements Listener {

	@EventHandler
	public void voucherUseEvent(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		String prefix = Api.getMessageString("Messages.Prefix");
		String voucheruse = Api.getMessageString("Messages.VoucherUse");
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			//player.sendMessage("1");
			if (Api.getItemInHand(player) != null) {
				//player.sendMessage("2");
				ItemStack item = Api.getItemInHand(player);
				if (item.hasItemMeta()) {
					//player.sendMessage("3");
					if (item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
						//player.sendMessage("4");
						for (String ranks : Main.settings.getConfig().getConfigurationSection("RankQuestOptions.Ranks").getKeys(false)) {
							//player.sendMessage("5");
							String dcdname = Api.removeColor(item.getItemMeta().getDisplayName());
							String rankname = Api.color(
									Api.getConfigString("RankQuestOptions.Ranks." + ranks + ".Voucher.Name").replace("%rank%",
											Api.getConfigString("RankQuestOptions.Ranks." + ranks + ".RankName")));
							String dcrankname = Api.removeColor(rankname);
							if (dcdname.equalsIgnoreCase(dcrankname)) {
								//player.sendMessage("6");
								String rank = ranks;
								ItemMeta itemMeta = item.getItemMeta();
								e.setCancelled(true);
								for (String voucherlore : Main.settings.getConfig()
										.getStringList("RankQuestOptions.Ranks." + rank + ".Voucher.Lore")) {
									//player.sendMessage("7");
									String fvoucherlore = Api.color(Api.replacePHolders(voucherlore, player, rank));
									String dcvoucherlore = Api.removeColor(fvoucherlore);
									if (dcvoucherlore.equalsIgnoreCase(Api.removeColor(itemMeta.getLore().get(0)))) {
										//player.sendMessage("8");
										player.sendMessage(
												Api.color(Api.replacePHolders(prefix + voucheruse, player, rank)));
										if (item.getAmount() == 1) {
											Api.setItemInHand(player, null);
											;
										}
										if (item.getAmount() > 1) {
											ItemStack item2 = item;
											item2.setAmount(item.getAmount() - 1);
											Api.setItemInHand(player, item2);
										}
										for (String cmds : Main.settings.getConfig()
												.getStringList("RankQuestOptions.Ranks." + rank + ".Voucher.Commands")) {
											String cmd = cmds.replace("%player%", player.getName());
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
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
