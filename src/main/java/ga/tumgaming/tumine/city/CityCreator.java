package ga.tumgaming.tumine.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import ga.tumgaming.tumine.util.Config;

public class CityCreator {

	private WorldEditPlugin we;
	private WorldGuardPlugin wg;
	private Config cities;

	public CityCreator(Config cit) {
		cities = cit;
	}

	public String createCity(Player player, World world, String name, BlockVector3 _min, BlockVector3 _max) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));

		String checkPath = player.getUniqueId().toString();
		if (cities.get(checkPath) == null) {
			if (regions != null) {
				if (regions.getRegion(name) == null) {

					// Create Region
					BlockVector3 min = BlockVector3.at(_min.getBlockX(), 0, _min.getBlockZ());
					BlockVector3 max = BlockVector3.at(_max.getBlockX(), 255, _max.getBlockZ());
					ProtectedRegion region = new ProtectedCuboidRegion(name, min, max);
					DefaultDomain owners = region.getMembers();
					owners.addPlayer(player.getName());
					region.setOwners(owners);
					String path = player.getUniqueId().toString();
					cities.set(path, name);
					regions.addRegion(region);
					try {
						regions.save();
					} catch (StorageException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "City has been created!";
				} else {
					return "A city with this name already exists!";
				}
			} else {
				return "ERROR: 2001! Report this to an Admin!";
			}
		} else {
			return "You are already in a city!";
		}
	}

	public String removeCity(Player player, World world) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		String name = getRegionFromPlayer(player.getUniqueId().toString(), world).getId();
		player.sendMessage("Start");
		if (regions.getRegion(name).getOwners().contains(player.getName())) {
			player.sendMessage("you owner");
			if (regions.getRegion(name) != null) {
				Set <String> key = cities.getCities();
				for (String s : key) {
				    if(cities.get(s) == name) {
				    	cities.set(s, null);
				    }
				}
				
				regions.removeRegion(name);
				return "City has been removed";
			} else {
				return "This city does not exist!";
			}
		} else {
			return "You are not an owner of this city!";
		}
	}

	public String addMember(ProtectedRegion region, String uuid) {
		String checkPath = uuid;
		if (cities.get(checkPath) == null) {
			if (cities.get("invites" + checkPath) != null) {
				List<String> list = cities.get("invites" + checkPath);
				if (list.contains(region.getId())) {
					return "Player has already been invited to the City";
				}
				list.add(region.getId());
				cities.set("invites" + checkPath, list);
			} else {
				List<String> list = new ArrayList<String>();
				list.add(region.getId());
				cities.set("invites" + checkPath, list);
			}
			return "Player has been invited to the city!";
		} else {
			return "Player is already in a city";
		}
	}

	public String removeMember(ProtectedRegion region, String name) {
		String uuid = "";
		if (Bukkit.getPlayer(name) == null) {
			boolean played = false;
			OfflinePlayer[] offPlayers = Bukkit.getOfflinePlayers();
			for (OfflinePlayer offlinePlayer : offPlayers) {
				if (offlinePlayer.getName() == name) {
					uuid = offlinePlayer.getUniqueId().toString();
					played = true;
					break;
				}
			}
			if (!played) {
				return "This Player has not played on this server yet!";
			}
		} else {
			uuid = Bukkit.getPlayer(name).getUniqueId().toString();
		}
		String checkPath = uuid;
		if (cities.get(checkPath) == region.getId()) {
			DefaultDomain members = region.getMembers();
			if (members.contains(uuid)) {
				members.removePlayer(uuid);
				region.setMembers(members);
				return "Removed member!";
			} else {
				return "That player is not a member of this city";
			}
		} else {
			return "This city does not exist";
		}
	}

	public String leaveCity(ProtectedRegion region, String uuid) {
		String checkPath = UUID.fromString(uuid).toString();
		if (cities.get(checkPath) != null) {
			cities.set(checkPath, null);
			return "You left the city!";
		} else {
			return "You are not in a city!";
		}
	}

	public String joinCity(ProtectedRegion region, String uuid) {
		String checkPath = uuid;
		if (cities.get(checkPath) == null) {
			if (isInvited(region.getId(), uuid)) {
				cities.set("invites" + checkPath, null);
				cities.set(checkPath, region.getId());
				return "Joined city!";
			} else {
				return "You are not invited to that city!";
			}
		} else {
			return "That city does not exist!";
		}
	}

	public ProtectedRegion getRegionFromPlayer(String uuid, World world) {
		String name = cities.get(uuid);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		Bukkit.getServer().getConsoleSender().sendMessage(name);
		return regions.getRegion(name);
	}

	public ProtectedRegion getRegionFromName(String name, World world) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));

		return regions.getRegion(name);
	}

	public boolean isOwner(Player player, ProtectedRegion region) {
		return region.getOwners().contains(player.getName());
	}

	public String getInvites(Player player) {
		String checkPath = player.getUniqueId().toString();
		List<String> list = cities.get("invites" + checkPath);
		String result = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			result += ", " + list.get(i);
		}
		return result;
	}

	public boolean isInvited(String region, String uuid) {
		String checkPath = uuid;
		List<String> list = cities.get("invites" + checkPath);
		return list.contains(region);
	}
}
