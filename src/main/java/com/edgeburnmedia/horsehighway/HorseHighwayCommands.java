package com.edgeburnmedia.horsehighway;

import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HorseHighwayCommands implements CommandExecutor {

	private final HorseHighway plugin;
	private final String help = """
		§6Horse Highway: Commands
		- help: Show this help documentation
		- reload: Reload speed_on_blocks.yml. Does not reload main config!
		- modify: Modify a block's speed (Usage: /horsehighway modify <block> <new speed>)
		- add: Add a new block and set it's speed (Usage: /horsehighway add <block> <speed>)
		- remove: Remove a block from speed_on_blocks.yml (Usage: /horsehighway remove <block>)
		- list: List blocks and their speeds
		""";

	/**
	 * Executes the given command, returning its success.
	 * <br>
	 * If false is returned, then the "usage" plugin.yml entry for this command (if defined) will be
	 * sent to the player.
	 *
	 * @param sender  Source of the command
	 * @param command Command which was executed
	 * @param label   Alias of the command which was used
	 * @param args    Passed command arguments
	 * @return true if a valid command, otherwise false
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(help);
			return false;
		}

		switch (args[0]) {
			case "help":
				sender.sendMessage(help);
				return true;
			case "reload":
				if (sender.hasPermission("horsehighway.reload")) {
					plugin.getSpeedMapper().reloadSpeedMap();
					sender.sendMessage("§6§lReloaded!");
					return true;
				} else {
					sender.sendMessage("§c§lNo permission for horsehighway.reload!");
					return true;
				}
			case "add", "modify":
				double speed;
				if (sender.hasPermission("horsehighway.modify") && args.length == 3) {
					Material material = Material.matchMaterial(args[1]);
					if (material == null) {
						sender.sendMessage("§c§lNo such material called " + args[1] + " exists.");
						return true;
					} else if (!HorseHighwayCommandTabCompleter.isValidBlock(args[1])) {
						sender.sendMessage("§c§l\"" + args[1] + "\" is not a valid block.");
						return true;
					}
					try {
						speed = Double.parseDouble(args[2]);
					} catch (NumberFormatException e) {
						sender.sendMessage("§c§lCouldn't recognize \"" + args[2]
							+ "\" as a number. Check server logs for details.");
						plugin.getServer().getLogger().log(Level.WARNING,
							"Couldn't recognize \"" + args[2] + "\" as a number.", e);
						return true;
					}
					if (speed > 200.0) {
						sender.sendMessage(
							"§6Speeds over 200 can cause glitches, please be wary!\nI won't stop you from using such speeds, but it isn't my fault if something breaks!");
					}

					plugin.getSpeedMapper().modifyElement(material, speed);
					return true;
				}
			case "remove":
				if (sender.hasPermission("horsehighway.modify") && args.length == 2) {
					try {
						plugin.getSpeedMapper().removeElement(args[1]);
					} catch (IllegalArgumentException e) {
						sender.sendMessage("§c§lNo such material " + args[1] + " exists.");
					}
					return true;
				} else if (sender.hasPermission("horsehighway.modify") && args.length != 2) {
					return false;
				}
			case "list":
				if (sender.hasPermission("horsehighway.list")) {
					HashMap<String, Double> configuredBlocksMap = plugin.getSpeedMapper()
						.getConfiguredBlocks();
					StringBuilder stringBuilder = new StringBuilder();

					stringBuilder.append("§6-----<Horse Highway Blocks>-----\n");

					stringBuilder.append(
						"§3Default: " + plugin.getHorseHighwayConfig().getDefaultSpeedInKph()
							+ "km/h\n");

					for (String key : configuredBlocksMap.keySet()) {
						stringBuilder.append(
							"§3" + key + ": " + configuredBlocksMap.get(key) + " km/h\n");
					}

					stringBuilder.append("§6------------------------------");

					sender.sendMessage(stringBuilder.toString());
					return true;
				}
			case "version":
				String version = plugin.getPluginDescriptionFile().getVersion();
				sender.sendMessage("§3Horse Highway " + version);
				return true;
			default:
				return false;
		}
	}

	HorseHighwayCommands(HorseHighway plugin) {
		this.plugin = plugin;
	}

}
