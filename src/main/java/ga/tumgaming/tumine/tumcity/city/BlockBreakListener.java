package ga.tumgaming.tumine.tumcity.city;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import ga.tumgaming.tumine.tumcity.TUMineCity;

public class BlockBreakListener implements Listener {

	private HashMap<Player, Location[]> plLoc;
	private TUMineCity plugin;
	private CityCreator cityCreator;

	public BlockBreakListener(TUMineCity pl, HashMap<Player, Location[]> hm, CityCreator cc) {
		plugin = pl;
		plLoc = hm;
		cityCreator = cc;
	}

	@EventHandler
	private void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Player player = e.getPlayer();
		boolean found = false;
		//if (block.getType() == Material.GOLD_BLOCK) {
			for (Location[] value : plLoc.values()) {
				for (int i = 0; i < value.length; i++) {
					String s = Boolean.toString(value[i].equals(block.getLocation()));
					if (value[i].equals(block.getLocation())) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
			if (found) {
				if (plLoc.containsKey(player)) {
					Location[] locs = plLoc.get(player);
					if (locs[0] != null && locs[0].equals(block.getLocation())) {
						locs[0] = null;
						player.sendMessage("Destroyed City building Block 1");
						plLoc.replace(player, locs);
					} else if (locs[1] != null && locs[1].equals(block.getLocation())) {
						locs[1] = null;
						player.sendMessage("Destroyed City building Block 2");
						plLoc.replace(player, locs);
					} else {
						player.sendMessage("This is not your City building Block");
						e.setCancelled(true);
					}
				} else {
					player.sendMessage("This is not your City building Block");
					e.setCancelled(true);
				}
			}
		}
	//}
}