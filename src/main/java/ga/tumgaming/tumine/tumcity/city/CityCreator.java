package ga.tumgaming.tumine.tumcity.city;

import java.util.ArrayList;
import java.util.Collections;
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
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import ga.tumgaming.tumine.tumcity.util.Config;

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
					DefaultDomain owners = region.getOwners();
					owners.addPlayer(player.getName());
					region.setOwners(owners);
					if(!regions.overlapsUnownedRegion(region, (LocalPlayer) player)){
					
					String path = player.getUniqueId().toString();
					cities.set(path, name);
					regions.addRegion(region);
					
					//check if city overlaps with other region
					try {
						regions.save();
					} catch (StorageException e) {
						e.printStackTrace();
					}
					return "City has been created!";
					}else {
						return "City overlaps with another City";
					}
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

	public String removeCity(Player player, World world, String name) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		
			if (regions.getRegion(name) != null) {
				if (regions.getRegion(name).getOwners().contains(player.getName())) {
				Set<String> key = cities.getCities();
				for (String s : key) {
					String val = (String) cities.get(s);
					if (val.equalsIgnoreCase(name)) {
						cities.delete(s);
					}
				}
				
				regions.removeRegion(name);
				return "City has been removed";
			} else {
				return "You are not an owner of this city!";
			}
		} else {
			return "This city does not exist!";
		}
	}

	public String addMember(ProtectedRegion region, String name) {

		String uuid = "";
		if (Bukkit.getPlayer(name) == null) {
			boolean played = false;
			OfflinePlayer[] offPlayers = Bukkit.getOfflinePlayers();
			for (OfflinePlayer offlinePlayer : offPlayers) {
				if (offlinePlayer.getName().equals( name)) {
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
		if (cities.get(checkPath) == null) {
			if (cities.get("invites." + checkPath) != null) {
				String allInvites = cities.get("invites." + checkPath);
				String[] inviteArray = allInvites.split(",");
				ArrayList<String> invites = new ArrayList<String>();
				Collections.addAll(invites, inviteArray);
				if (invites.contains(region.getId())) {
					return "Player has already been invited to the City";
				}
				allInvites = allInvites + "," + region.getId();
				cities.set("invites." + checkPath, allInvites);
			} else {
				String invite = region.getId();
				cities.set("invites." + checkPath, invite);
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
				if (offlinePlayer.getName().equals(name)) {
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
		String val = (String) cities.get(checkPath);
		if (val.equalsIgnoreCase(region.getId())) {
			DefaultDomain members = region.getMembers();
			cities.delete(checkPath);
			if (members.contains(name)) {
				members.removePlayer(name);
				region.setMembers(members);
				return "Removed member!";
			} else {
				return "That player is not a member of this city";
			}
		} else {
			return "That player is not a member of this city";
		}
	}

	public String leaveCity(ProtectedRegion region, Player player) {
		String checkPath = player.getUniqueId().toString();
		if (cities.get(checkPath) != null) {
			cities.delete(checkPath);
			DefaultDomain members = region.getMembers();
			members.removePlayer(player.getName());
			region.setMembers(members);
			return "You left the city!";
		} else {
			return "You are not in a city!";
		}
	}

	public String joinCity(ProtectedRegion region, Player player) {
		if(region != null) {
		String checkPath = player.getUniqueId().toString();
		if (cities.get(checkPath) == null) {
			if (isInvited(region.getId(), player.getUniqueId().toString())) {
				DefaultDomain members = region.getMembers();
				members.addPlayer(player.getName());
				region.setMembers(members);
				cities.delete("invites." + checkPath);
				cities.set(checkPath, region.getId());
				return "Joined city!";
			} else {
				return "You are not invited to that city!";
			}
		} else {
			return "You are already in a city";
		}
	}else{
		return "This City does not exist!";
		}
	}

	public ProtectedRegion getRegionFromPlayer(String uuid, World world) {
		String name = cities.get(uuid);
		if (name != (null)) {
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			if (container != null) {
				RegionManager regions = container.get(BukkitAdapter.adapt(world));
				if (regions != null) {
					ProtectedRegion region = regions.getRegion(name);
					if (region == null) {
						return null;
					}
					return region;
				}
			}
		}
		return null;
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
		String invites = cities.get("invites." + checkPath);

		return invites;
	}

	public boolean isInvited(String region, String uuid) {
		String checkPath = uuid;
		String allInvites = cities.get("invites." + checkPath);
		String[] inviteArray = allInvites.split(",");
		ArrayList<String> invites = new ArrayList<String>();
		Collections.addAll(invites, inviteArray);
		if (invites.contains(region)) {
			return true;
		}
		return false;
	}
}
