package ga.tumgaming.tumine.tumcity.city;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
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
	private CityCreator cityCreator;

	public BlockPlaceListener(TUMineCity pl, HashMap<Player, Location[]> hm, CityCreator cc) {
		plugin = pl;
		plLoc = hm;
		cityCreator = cc;
	}

	private ArrayList<String> edit = new ArrayList<String>();

	@EventHandler
	private void onBlockPlace(BlockPlaceEvent e) {

		Block block = e.getBlock();
		Player player = e.getPlayer();
		if (block.getType().equals(Material.GOLD_BLOCK)) {
			if (e.getItemInHand() != null && e.getItemInHand().getItemMeta() != null
					&& e.getItemInHand().getItemMeta().getLore() != null
					&& e.getItemInHand().getItemMeta().getLore().get(0) != null) {
				if (e.getItemInHand().getItemMeta().getLore().get(0).equals("City building Block")) {
					if (cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()) == null) {
						if (plLoc.containsKey(player)) {
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
							Location[] locs = new Location[2];
							locs[0] = block.getLocation();
							plLoc.put(player, locs);
							player.sendMessage("First block set at: X: " + block.getX() + " Z: " + block.getZ());
						}
					} else {
						player.sendMessage("You are already in a city");
						e.setCancelled(true);
					}
				}
			}
		}
	}

}