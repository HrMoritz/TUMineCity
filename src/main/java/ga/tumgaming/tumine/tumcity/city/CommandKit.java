package ga.tumgaming.tumine.tumcity.city;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;

import ga.tumgaming.tumine.tumcity.TUMineCity;
import ga.tumgaming.tumine.tumcity.util.Config;

public class CommandKit implements CommandExecutor {

	private static CityCreator cityCreator;

	public CommandKit(CityCreator cit) {
		cityCreator = cit;
	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (command.getName().equalsIgnoreCase("city")) {
				if (args[0].equalsIgnoreCase("create")) {
					String name;
					if (args.length != 6) {
						return false;
					}
					name = args[1];
					int xmin = 0;
					int xmax = 0;
					int zmin = 0;
					int zmax = 0;
					try {
						xmin = Integer.parseInt(args[2]);
						xmax = Integer.parseInt(args[3]);
						zmin = Integer.parseInt(args[4]);
						zmax = Integer.parseInt(args[5]);
					} catch (NumberFormatException nfe) {
						player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "Please type in integers!");
						return false;
					}

					BlockVector3 min = BlockVector3.at(Integer.parseInt(args[2]), 0, Integer.parseInt(args[3]));
					BlockVector3 max = BlockVector3.at(Integer.parseInt(args[4]), 0, Integer.parseInt(args[5]));
					player.sendMessage(TUMineCity.getPrefix() + cityCreator.createCity(player, player.getWorld(), name, min, max));
					return true;
				} else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
					player.sendMessage(TUMineCity.getPrefix() + cityCreator.removeCity(player, player.getWorld(), args[1]));
					return true;
				} else if (args[0].equalsIgnoreCase("add")) {
					if (cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()) != null) {
						if (cityCreator.isOwner(player,
								cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()))) {
							player.sendMessage(TUMineCity.getPrefix() + cityCreator.addMember(
									cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()),
									args[1]));
						} else {
							player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "You are not the owner of this city!");
						}
						return true;
					} else {
						player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "You are not in a city!");
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()) != null) {
						if (cityCreator.isOwner(player,
								cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()))) {
							player.sendMessage(TUMineCity.getPrefix() + cityCreator.removeMember(
									cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()),
									args[1]));
						} else {
							player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "You are not the owner of this city!");
						}
						return true;
					} else {
						player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "You are not in a city");
					}
				} else if (args[0].equalsIgnoreCase("join")) {
					player.sendMessage(TUMineCity.getPrefix() + cityCreator.joinCity(cityCreator.getRegionFromName(args[1], player.getWorld()),
							player));
					return true;
				} else if (args[0].equalsIgnoreCase("leave")) {
					if (cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()) != null) {
						player.sendMessage(TUMineCity.getPrefix() + cityCreator.leaveCity(
								cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()),
								player));
						return true;
					} else {
						player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "You are not in a city");
					}
				} else if (args[0].equalsIgnoreCase("invites")) {

					player.sendMessage(TUMineCity.getPrefix() + cityCreator.getInvites(player));
					return true;
				} else if (args[0].equalsIgnoreCase("info")) {
					if (cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()) != null) {
					player.sendMessage(TUMineCity.getPrefix() + cityCreator
							.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()).getId());
				}else{
					player.sendMessage(TUMineCity.getPrefix() + ChatColor.RED + "You are not in a city!");}
				}
			}
		}
		return false;
	}
}