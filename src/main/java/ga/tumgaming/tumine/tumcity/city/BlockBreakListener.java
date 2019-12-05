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
		player.sendMessage("Test0");

		boolean found = false;
		if (block.getType() == Material.GOLD_BLOCK) {
			for (Location[] value : plLoc.values()) {
				player.sendMessage(value[0].toString());
				player.sendMessage(value[1].toString());
				player.sendMessage(block.getLocation().toString());
				for (int i = 0; i < value.length; i++) {
					player.sendMessage("Checking");
					if (value[i] == block.getLocation()) {
						player.sendMessage("Found");
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}

			}

			if (found) {
				player.sendMessage("Test1");
				if (plLoc.containsKey(player)) {
					player.sendMessage("Test2");
					Location[] locs = plLoc.get(player);
					if (locs[0] != null && locs[0] == block.getLocation()) {
						locs[0] = null;
						plLoc.replace(player, locs);
						player.sendMessage("Test3");
					} else if (locs[1] != null && locs[1] == block.getLocation()) {
						locs[1] = null;
						plLoc.replace(player, locs);
						player.sendMessage("Test4");
					} else {
						player.sendMessage("This is not your City building Block");
						player.sendMessage("Test5");
						e.setCancelled(true);
					}
				} else {
					player.sendMessage("This is not your City building Block");
					player.sendMessage("Test6");
					e.setCancelled(true);
				}
			}
		}
	}
}