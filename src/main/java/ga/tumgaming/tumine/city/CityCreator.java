package ga.tumgaming.tumine.city;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import ga.tumgaming.tumine.util.Config;

public class CityCreator {

	private WorldEditPlugin we;
	private WorldGuardPlugin wg;
	private Config cities;
	private Config invitations;

	public CityCreator(Config cit, Config inv) {
		cities = cit;
		invitations = inv;
	}

	public void createCity(Player player, World world, String name, BlockVector3 _min, BlockVector3 _max) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get((com.sk89q.worldedit.world.World) world);

		String checkPath = player.getUniqueId().toString();
		if (cities.get(checkPath) == null) {
			if (regions == null || regions.getRegion(name) == null) {
				// Create Region
				BlockVector3 min = BlockVector3.at(_min.getBlockX(), 0, _min.getBlockZ());
				BlockVector3 max = BlockVector3.at(_max.getBlockX(), 255, _max.getBlockZ());
				ProtectedRegion region = new ProtectedCuboidRegion("spawn", min, max);
				regions.addRegion(region);
				DefaultDomain owners = region.getMembers();
				owners.addPlayer(player.getUniqueId());
				region.setOwners(owners);
				String path = player.getUniqueId().toString();
				cities.set(path, name);
				path = "Cities." + name;
				cities.set(path, player);
			} else {
				// The world has no region support or region data failed to load
			}
		}
	}

	public void removeCity(String player, String name, World world) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get((com.sk89q.worldedit.world.World) world);

		if (regions.getRegion(name).getOwners().contains(player)) {
			String[] key = cities.getKeys();
			for (int i = 0; i < key.length; i++) {
				if (cities.get(key[i]) == name) {
					cities.set(key[i], null);
				}
			}

			if (regions.getRegion(name) != null) {
				regions.removeRegion(name);
			}
		}

	}

	public void addMember(ProtectedRegion region, String newMember) {
		String checkPath = UUID.fromString(newMember).toString();
		if (cities.get(checkPath) == null) {
			if(invitations.get(checkPath) != null){
				List<String> list = invitations.get(checkPath);
				list.add(region.getId());
				invitations.set(checkPath, list);
			}else {
				List<String> list = new ArrayList<String>();
				list.add(region.getId());
				invitations.set(checkPath, list);
			}
		}
	}

	public void removeMember(ProtectedRegion region, String member) {
		String checkPath = UUID.fromString(member).toString();
		if (cities.get(checkPath) == region.getId()) {
			DefaultDomain members = region.getMembers();
			if (members.contains(member)) {
				members.removePlayer(member);
				region.setMembers(members);
			}
		}
	}

	public void leaveCity(ProtectedRegion region, String member) {
		String checkPath = UUID.fromString(member).toString();
		if (cities.get(checkPath) != null) {
			cities.set(checkPath, null);
		}
	}

	public void joinCity(ProtectedRegion region, String newMember) {
		String checkPath = UUID.fromString(newMember).toString() + region.getId();
		if (cities.get(checkPath) == null) {
			if (isInvited(region.getId(), newMember)) {
				invitations.set(checkPath, null);
				cities.set(checkPath, region.getId());
			}
		}
	}

	public ProtectedRegion getRegionFromPlayer(String player, World world) {
		String name = cities.get(UUID.fromString(player).toString());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get((com.sk89q.worldedit.world.World) world);

		return regions.getRegion(name);
	}
	
	public ProtectedRegion getRegionFromName(String name, World world) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get((com.sk89q.worldedit.world.World) world);

		return regions.getRegion(name);
	}
	
	public boolean isOwner(Player player, ProtectedRegion region) {
		return region.getOwners().contains(player.getUniqueId());
	}
	
	public void getInvites(Player player) {
		String checkPath = player.getUniqueId().toString();
		List<String> list = invitations.get(checkPath);
	}
	
	public boolean isInvited(String region, String player) {
		String checkPath = UUID.fromString(player).toString();
		List<String> list = invitations.get(checkPath);
		return list.contains(region);
	}
}
