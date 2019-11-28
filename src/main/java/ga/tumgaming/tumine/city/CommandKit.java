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
			if (command.getName().equalsIgnoreCase("city")) {
				if (args[1].equalsIgnoreCase("create")) {
					int xmin, xmax, zmin, zmax;
					String name;
					name = args[2];
					if (isNumeric(args[3]) && isNumeric(args[4]) && isNumeric(args[5]) && isNumeric(args[6])) {
						BlockVector3 min = BlockVector3.at(Integer.parseInt(args[3]), 0, Integer.parseInt(args[4]));
						BlockVector3 max = BlockVector3.at(Integer.parseInt(args[5]), 0, Integer.parseInt(args[6]));
						;
						cityCreator.createCity(player, player.getWorld(), name, min, max);
					}
				} else if (args[1].equalsIgnoreCase("delete")) {
					cityCreator.removeCity(player.getName(), args[3], player.getWorld());
				} else if (args[1].equalsIgnoreCase("add")) {
					if (cityCreator.isOwner(player,
							cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()))) {
						cityCreator.addMember(cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()),
								args[3]);
					}
				} else if (args[1].equalsIgnoreCase("remove")) {
					if (cityCreator.isOwner(player,
							cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()))) {
						cityCreator.removeMember(cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()),
								args[3]);
					}
				} else if (args[1].equalsIgnoreCase("join")) {
					cityCreator.joinCity(cityCreator.getRegionFromName(args[1], player.getWorld()), player.getName());
				} else if (args[1].equalsIgnoreCase("leave")) {
					if (cityCreator.isOwner(player,
							cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()))) {
						cityCreator.removeMember(cityCreator.getRegionFromPlayer(player.getName(), player.getWorld()),
								args[3]);
					}
				} else if (args[1].equalsIgnoreCase("invites")) {
					cityCreator.joinCity(cityCreator.getRegionFromName(args[1], player.getWorld()), player.getName());
				}
			}
		}                       
		return false;
	}
}