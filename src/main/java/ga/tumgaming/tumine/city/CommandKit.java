package ga.tumgaming.tumine.city;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;

import ga.tumgaming.tumine.util.Config;

public class CommandKit implements CommandExecutor {

	private static Config player;

	private static Config invitations;

	private static CityCreator cityCreator;

	public CommandKit(Config pl, Config inv, CityCreator cit) {
		player = pl;
		invitations = inv;
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
		if ((Player) sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage("test0");
			if (command.getName().equalsIgnoreCase("city")) {
			player.sendMessage("test1");
				if (args[0].equalsIgnoreCase("create")) {
					int xmin, xmax, zmin, zmax;
					player.sendMessage("test2");
					String name;
					name = args[1];
					if (isNumeric(args[2]) && isNumeric(args[3]) && isNumeric(args[4]) && isNumeric(args[5])) {
					player.sendMessage("test3");
						BlockVector3 min = BlockVector3.at(Integer.parseInt(args[2]), 0, Integer.parseInt(args[3]));
						BlockVector3 max = BlockVector3.at(Integer.parseInt(args[4]), 0, Integer.parseInt(args[5]));
						cityCreator.createCity(player, player.getWorld(), name, min, max);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("delete")) {
					cityCreator.removeCity(player.getName(), player.getWorld());
					return true;
				} else if (args[0].equalsIgnoreCase("add")) {
					if (cityCreator.isOwner(player,cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()))) {
						cityCreator.addMember(cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()),	args[1]);
					}
					return true;
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (cityCreator.isOwner(player,cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()))) {
						cityCreator.removeMember(cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()),args[1]);
					}
					return true;
				} else if (args[0].equalsIgnoreCase("join")) {
					cityCreator.joinCity(cityCreator.getRegionFromName(args[1], player.getWorld()),
							player.getUniqueId().toString());
							return true;
				} else if (args[0].equalsIgnoreCase("leave")) {
					if (cityCreator.isOwner(player,cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()))) {
						cityCreator.leaveCity(cityCreator.getRegionFromPlayer(player.getUniqueId().toString(), player.getWorld()),player.getUniqueId().toString());
					return true;
					}
				} else if (args[0].equalsIgnoreCase("invites")) {
					cityCreator.getInvites(player);
							return true;
				}
			}
		}
		return false;
	}
}