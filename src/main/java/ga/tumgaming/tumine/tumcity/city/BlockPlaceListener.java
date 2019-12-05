package ga.tumgaming.tumine.tumcity.city;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import ga.tumgaming.tumine.tumcity.TUMineCity;

public class BlockPlaceListener implements Listener {

	private HashMap<Player, Location[]> plLoc;
	private TUMineCity plugin;

	public BlockPlaceListener(TUMineCity pl, HashMap<Player, Location[]> hm) {
		plugin = pl;
		plLoc = hm;
	}

	private ArrayList<String> edit = new ArrayList<String>();

	@EventHandler
	private void onBlockPlace(BlockPlaceEvent e) {

		Block block = e.getBlock();
		Player player = e.getPlayer();
		player.sendMessage("test1");
		if (e.getItemInHand().getItemMeta().getLore().get(0).equals("City building Block")) {
			player.sendMessage("test2");
			if (plLoc.containsKey(player)) {
				player.sendMessage("test3");
				Location[] locs = plLoc.get(player);
				if (locs[0] == null) {
					locs[0] = block.getLocation();
					player.sendMessage("First block set at: X: " + block.getX() + " Z: " + block.getZ());
					plLoc.replace(player, locs);
					if (locs[0] != null && locs[1] != null) {
						// city can be created
					}
				} else if (locs[1] == null) {
					locs[1] = block.getLocation();
					player.sendMessage("Second block set at: X: " + block.getX() + " Z: " + block.getZ());
					plLoc.replace(player, locs);
					if (locs[0] != null && locs[1] != null) {
						// city can be created
					}
				} else if (locs[0] != null && locs[1] != null) {
					player.sendMessage("You already placed enough Blocks");
					e.setCancelled(true);
				}

			} else {
				player.sendMessage("test4");
				Location[] locs = new Location[2];
				locs[0] = block.getLocation();
				plLoc.put(player, locs);
				player.sendMessage("First block set at: X: " + block.getX() + " Z: " + block.getZ());
			}
		} 
	}

	@EventHandler
	private void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Player player = e.getPlayer();

		if (plLoc.containsValue(block.getLocation())) {
			if (plLoc.containsKey(player)) {
				Location[] locs = plLoc.get(player);
				if (locs[0] != null && locs[0] == block.getLocation()) {
					locs[0] = null;
					plLoc.replace(player, locs);
				} else if (locs[1] != null && locs[1] == block.getLocation()) {
					locs[1] = null;
					plLoc.replace(player, locs);
				} else {
					e.setCancelled(true);
				}
			} else {
				player.sendMessage("This is not your City building Block");
				e.setCancelled(true);
			}
		}
	}
}