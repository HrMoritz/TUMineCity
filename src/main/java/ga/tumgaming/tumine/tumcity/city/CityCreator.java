package ga.tumgaming.tumine.tumcity.city;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

import ga.tumgaming.tumine.tumcity.TUMineCity;
import ga.tumgaming.tumine.tumcity.util.Config;

public class CityCreator {

	private WorldEditPlugin we;
	private WorldGuardPlugin wg;
	private Config cities;
	private HashMap<Player, Location[]> plLoc;

	public CityCreator(Config cit, WorldGuardPlugin _wg, HashMap<Player, Location[]> hm) {
		this.cities = cit;
		this.wg = _wg;
		plLoc = hm;
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
					LocalPlayer localplayer = wg.wrapPlayer(player);
					if (!regions.overlapsUnownedRegion(region, localplayer)) {

						String path = player.getUniqueId().toString();
						cities.set(path, name);
						regions.addRegion(region);
						plLoc.remove(player);

						// check if city overlaps with other region
						try {
							regions.save();
						} catch (StorageException e) {
							e.printStackTrace();
						}
						return "You created the city " + name + "!";
					} else {
						return ChatColor.RED + "Selection overlaps with another City";
					}
				} else {
					return ChatColor.RED + "There is already a city with this name!";
				}
			} else {
				return ChatColor.RED + "ERROR: 2001! Report this to an Admin!";
			}
		} else {
			return ChatColor.RED + "You are already in a city!";
		}
	}

	public String removeCity(Player player, World world, String name) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));

		if (regions.getRegion(name) != null) {
			if (regions.getRegion(name).getOwners().contains(player.getName())) {
				Set<String> key = cities.getCities();
				for (String s : key) {
					String[] arr = s.split(",");
					if (isUUID(s)) {
						String val = (String) cities.get(s);
						if (val.equalsIgnoreCase(name)) {
							cities.delete(s);
						}
					} else if (arr.length >= 2 && arr[0].equals("invites")) {
						String allInvites = cities.get(s);
						String[] invites = allInvites.split(",");
						String[] newInvites = new String[invites.length-1];
						int index = 0;
						for(int i = 0; i < invites.length; i++ ) {
							if(invites[i].equalsIgnoreCase(name)) {
								
							}else {
								newInvites[index] = invites[i];
								index++;
							}
						}
						if (newInvites.length > 0) {
							player.sendMessage("more than 2 invites");
							String newAllInvites = newInvites[0]; 
							if (newInvites.length > 1) {
								for (int i = 0; i < newInvites.length; i++) {
									newAllInvites = newAllInvites + "," + newInvites[i];
								}
								player.sendMessage("should have deleted " + newAllInvites);
								cities.delete(s);
								cities.set(s, newAllInvites);
							}
						}else {
							cities.delete(s);
						}
					}
				}
				regions.removeRegion(name);
				return "City has been removed";
			} else {
				return ChatColor.RED + "You are not the owner of this city!";
			}
		} else {
			return ChatColor.RED + "This city does not exist!";
		}
	}

	public String addMember(ProtectedRegion region, String name) {
		boolean isOnline = false;
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
				return ChatColor.RED + "This Player has not played on this server yet!";
			}
		} else {
			uuid = Bukkit.getPlayer(name).getUniqueId().toString();
			isOnline = true;
		}

		String checkPath = uuid;
		if (cities.get(checkPath) == null) {
			if (cities.get("invites," + checkPath) != null) {
				String allInvites = cities.get("invites," + checkPath);
				String[] inviteArray = allInvites.split(",");
				ArrayList<String> invites = new ArrayList<String>();
				Collections.addAll(invites, inviteArray);
				if (invites.contains(region.getId())) {
					return ChatColor.RED + name + " has already been invited to this City";
				}
				allInvites = allInvites + "," + region.getId();
				cities.set("invites," + checkPath, allInvites);
			} else {
				String invite = region.getId();
				cities.set("invites," + checkPath, invite);
			}
			if (isOnline) {
				Bukkit.getPlayer(UUID.fromString(uuid))
						.sendMessage(TUMineCity.getPrefix() + "You have been invited to join " + region.getId() + "!");
			}
			return name + " has been invited to the city!";
		} else {
			return ChatColor.RED + name + " is already in a city";
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
				return ChatColor.RED + "This Player has not played on this server yet!";
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
				return ChatColor.RED + name + " is not a member of this city";
			}
		} else {
			return ChatColor.RED + name + " is not a member of this city";
		}
	}

	public String leaveCity(ProtectedRegion region, Player player) {
		String checkPath = player.getUniqueId().toString();
		if (cities.get(checkPath) != null) {
			if (region.getOwners().contains(player.getName())) {

				DefaultDomain members = region.getMembers();
				if (members.getPlayers().isEmpty()) {
					return ChatColor.RED
							+ "There is no other member in this city. You might want to try /city delete [name]";
				}
				String newOwner = members.getPlayers().iterator().next();
				cities.delete(checkPath);
				DefaultDomain owners = region.getOwners();
				owners.removePlayer(player.getName());

				owners.addPlayer(newOwner);
				region.setOwners(owners);
				members.removePlayer(newOwner);
				region.setMembers(members);
				return "You left the city " + region.getId() + "!";
			} else {
				cities.delete(checkPath);
				DefaultDomain members = region.getMembers();
				members.removePlayer(player.getName());
				region.setMembers(members);
				return "You left the city " + region.getId() + "!";
			}

		} else {
			return ChatColor.RED + "You are not in a city!";
		}
	}

	public String joinCity(ProtectedRegion region, Player player) {
		if (region != null) {
			String checkPath = player.getUniqueId().toString();
			if (cities.get(checkPath) == null) {
				if (isInvited(region.getId(), player.getUniqueId().toString())) {
					DefaultDomain members = region.getMembers();
					members.addPlayer(player.getName());
					region.setMembers(members);
					cities.delete("invites," + checkPath);
					cities.set(checkPath, region.getId());
					return "Joined the city " + region.getId() + "!";
				} else {
					return ChatColor.RED + "You are not invited to join" + region.getId() + "!";
				}
			} else {
				return ChatColor.RED + "You are already in a city";
			}
		} else {
			return ChatColor.RED + "This City does not exist!";
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
		String invites = cities.get("invites," + checkPath);

		return invites;
	}

	public boolean isInvited(String region, String uuid) {
		String checkPath = uuid;
		String allInvites = cities.get("invites," + checkPath);
		String[] inviteArray = allInvites.split(",");
		ArrayList<String> invites = new ArrayList<String>();
		Collections.addAll(invites, inviteArray);
		if (invites.contains(region)) {
			return true;
		}
		return false;
	}

	public boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
