package me.Yukun.RankQuests.MultiSupport;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.Yukun.RankQuests.Main;

public class WorldGuard {
	static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

	public static boolean allowsPVP(Entity en) {
		if (plugin != null) {
			Location loc = en.getLocation();
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(loc);
			if (set.queryState(null, DefaultFlag.PVP) == StateFlag.State.DENY) {
				return false;
			}
		}
		return true;
	}

	public static boolean allowsBreak(Entity en) {
		if (plugin != null) {
			Location loc = en.getLocation();
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(loc);
			if (set.queryState(null, DefaultFlag.BLOCK_BREAK) == StateFlag.State.DENY) {
				return false;
			}
		}
		return true;
	}

	public static boolean allowsExplotions(Entity en) {
		if (plugin != null) {
			Location loc = en.getLocation();
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(loc);
			if (set.queryState(null, DefaultFlag.OTHER_EXPLOSION) == StateFlag.State.DENY) {
				return false;
			}
		}
		return true;
	}

	public static boolean isInRegion(Player player) {
		if (plugin != null) {
			List<String> id = Main.settings.getConfig().getStringList("RankQuestOptions.Regions");
			for (ProtectedRegion set : WGBukkit.getPlugin().getRegionManager(player.getWorld())
					.getApplicableRegions(player.getLocation())) {
				if (set != null) {
					String ids = set.getId();
					if (id.contains(ids)) {
						return true;
					} else {
						continue;
					}
				}
				return false;
			}
		}
		return false;
	}

	public static boolean notInRegion(Player player) {
		if (plugin != null) {
			List<String> blacklist = (ArrayList<String>) Main.settings.getConfig()
					.getStringList("RankQuestOptions.RegionBlacklist");
			for (ProtectedRegion set : WGBukkit.getPlugin().getRegionManager(player.getWorld())
					.getApplicableRegions(player.getLocation())) {
				String id = set.getId();
				if (blacklist.contains(id)) {
					return false;
				} else {
					continue;
				}
			}
			return true;
		}
		return false;
	}
}