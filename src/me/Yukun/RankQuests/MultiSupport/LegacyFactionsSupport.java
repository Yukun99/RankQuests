package me.Yukun.RankQuests.MultiSupport;

import org.bukkit.entity.Player;

import me.Yukun.RankQuests.Api;
import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;

public class LegacyFactionsSupport {
	public static boolean isFriendly(Player player, Player other){
		Faction p = FPlayerColl.get(player).getFaction();
		Faction o = FPlayerColl.get(other).getFaction();
		Relation r = FPlayerColl.get(player).getRelationTo(FPlayerColl.get(other));
		if(Api.removeColor(o.getTag()).equalsIgnoreCase("Wilderness"))return false;
		if(p==o)return true;
		if(!r.isAlly())return false;
		if(r.isAlly())return true;
		return false;
	}
	public static boolean inTerritory(Player P){
		if(Api.removeColor(FPlayerColl.get(P).getFaction().getTag()).equalsIgnoreCase("Wilderness"))return false;
		if(FPlayerColl.get(P).isInOwnTerritory()){
			return true;
		}
		if(FPlayerColl.get(P).isInAllyTerritory()){
			return true;
		}
		return false;
	}
	public static boolean isInWarzone(Player player) {
		FLocation loc = new FLocation(player.getLocation());
		Faction B = Board.get().getFactionAt(loc);
		if (Api.removeColor(B.getTag()).equalsIgnoreCase(Api.getConfigString("RankQuestOptions.WarzoneName"))) {
			return true;
		}
		return false;
	}
}
