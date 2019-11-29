package ga.tumgaming.tumine.tumcity.city;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;

import ga.tumgaming.tumine.tumcity.util.Config;

public class CommandKit implements CommandExecutor {

	private static Config player;


	private static CityCreator cityCreator;

	public CommandKit(Config pl, CityCreator cit) {
		player = pl;
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
						player.sendMessage("Please type in integers!");
						return false;
					}

					BlockVector3 min = BlockVector3.at(Integer.parseInt(args[2]), 0, Integer.parseInt(args[3]));
					BlockVector3 max = BlockVector3.at(Integer.parseInt(args[4]), 0, Integer.parseInt(args[5]));
					player.sendMessage(cityCreator.createCity(player, player.getWorld(), name, min, max));
					return true;
				} else if (args[0].equalsIgnoreCase("delete")) {
					player.sendMessage(cityCreator.removeCity(player, player.getWorld()));
					return true;
				} else if (args[0].equalsIgnoreCase("add")) {
					if (cityCreator.isOwner(player,
							cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()))) {
						player.sendMessage(cityCreator.addMember(
								cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()), args[1]));
					} else {
						player.sendMessage("You are not the owner of this city!");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (cityCreator.isOwner(player,
							cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()))) {
						player.sendMessage(cityCreator.removeMember(
								cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()),
								args[1]));
					} else {
						player.sendMessage("You are not the owner of this city!");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("join")) {
					player.sendMessage(cityCreator.joinCity(cityCreator.getRegionFromName(args[1], player.getWorld()),
							player.getUniqueId().toString()));
					return true;
				} else if (args[0].equalsIgnoreCase("leave")) {

					player.sendMessage(cityCreator.leaveCity(
							cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()),
							player.getUniqueId().toString()));
					return true;
				} else if (args[0].equalsIgnoreCase("invites")) {
					player.sendMessage(cityCreator.getInvites(player));
					return true;
				}
			}
		}
		return false;
	}
}