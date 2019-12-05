package ga.tumgaming.tumine.tumcity;

import ga.tumgaming.tumine.tumcity.city.BlockPlaceListener;
import ga.tumgaming.tumine.tumcity.city.CityCreator;
import ga.tumgaming.tumine.tumcity.city.CommandKit;
import ga.tumgaming.tumine.tumcity.util.Config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public class TUMineCity extends JavaPlugin {

	private static Config player;
	private static Config invitations;

	private static Plugin plugin;
	
	private static TUMineCity instance;

	private static CityCreator cityCreator;

	private static CommandKit commandKit;

	private static WorldGuardPlugin worldGuardPlugin;
	
	private static BlockPlaceListener blockPlaceListener;
	
	private static HashMap<Player, Location[]> plLoc = new HashMap<>();

	@Override
	public void onEnable() {
		instance = this;
		this.plugin = this;
		worldGuardPlugin = getWorldGuard();
		player = new Config(this, "player");
		cityCreator = new CityCreator(player, worldGuardPlugin, plLoc);
		getCommand("city").setExecutor(new CommandKit(cityCreator, plLoc));
		registerEvents();

		log("Plugin erfolgreich geladen");
	}

	/**
	 * logs a String in the console
	 *
	 * @param str logged String
	 */
	public void log(String str) {
		Logger.getLogger(str);
	}

	private static void registerEvents() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new BlockPlaceListener(instance, plLoc, cityCreator), plugin);

	}

	public static Config getPlayerConfig() {
		return player;
	}

	public static Config getInvitationsConfig() {
		return invitations;
	}

	public static Plugin getPlugin() {
		return plugin;
	}

	public static WorldGuardPlugin getWorldGuard() {
		Plugin wgp = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		if (wgp == null || !(wgp instanceof WorldGuardPlugin)) {
			return null;
		}
		return (WorldGuardPlugin) wgp;
	}

	public static WorldEditPlugin getWorldEdit() {
		Plugin wep = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
		if (wep == null || wep instanceof WorldGuardPlugin) {
			return null;
		}
		return (WorldEditPlugin) wep;
	}
	
	public static String getPrefix() {
		return new String(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "TUMine" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + " ");
	}

}
