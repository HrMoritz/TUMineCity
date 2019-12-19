package ga.tumgaming.tumine.tumcity.city;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import ga.tumgaming.tumine.tumcity.TUMineCity;

public class BlockBreakListener implements Listener {

	private HashMap<Player, Location[]> plLoc;
	public BlockBreakListener(HashMap<Player, Location[]> hm) {
		plLoc = hm;
	}

	@EventHandler
	private void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Player player = e.getPlayer();
		boolean found = false;
		// if (block.getType() == Material.GOLD_BLOCK) {
		for (Location[] value : plLoc.values()) {
			for (int i = 0; i < value.length; i++) {
				if (value[i] != null) {
					if (value[i].equals(block.getLocation())) {
						found = true;
						break;
					}
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
					player.sendMessage(TUMineCity.getPrefix() + "Destroyed City building Block 1");
					// Give back item with lore
					PlayerInventory inventory = player.getInventory();
	                ItemStack item = new ItemStack(Material.GOLD_BLOCK);
	                ItemMeta itemMeta = item.getItemMeta();
	                ArrayList<String> lore = new ArrayList<String>();
	                lore.add("City building Block");
	                itemMeta.setLore(lore);
	                item.setItemMeta(itemMeta);
					inventory.addItem(item);
					e.setDropItems(false);
					plLoc.replace(player, locs);
				} else if (locs[1] != null && locs[1].equals(block.getLocation())) {
					locs[1] = null;
					// Give back item with lore
					PlayerInventory inventory = player.getInventory();
	                ItemStack item = new ItemStack(Material.GOLD_BLOCK);
	                ItemMeta itemMeta = item.getItemMeta();
	                ArrayList<String> lore = new ArrayList<String>();
	                lore.add("City building Block");
	                itemMeta.setLore(lore);
	                item.setItemMeta(itemMeta);
					inventory.addItem(item);
					e.setDropItems(false);
					player.sendMessage(TUMineCity.getPrefix() + "Destroyed City building Block 2");
					plLoc.replace(player, locs);
				} else {
					player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "This is not your City building Block");
					e.setCancelled(true);
				}
			} else {
				player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "This is not your City building Block");
				e.setCancelled(true);
			}
		}
	}
	// }
}