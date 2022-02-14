package com.edgeburnmedia.horsehighway;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Array;
import java.util.*;

public class HorseHighwayCommandTabCompleter implements TabCompleter {
    private HorseHighway plugin;

    HorseHighwayCommandTabCompleter(HorseHighway plugin) {
        this.plugin = plugin;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command. For players tab-completing a
     *                command inside of a command block, this will be the player,
     *                not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     *         to default to the command executor
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // plugin.getServer().broadcastMessage("args: " + Arrays.toString(args) + "
        // length " + args.length);
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("horsehighway.reload")) {
                commands.add("reload");
                commands.add("version");
            }
            // FIXME uncomment me when ready
            // if (sender instanceof Player && sender.hasPermission("horsehighway.editor"))
            // {
            // commands.add("editor");
            // }
            if (sender.hasPermission("horsehighway.modify")) {
                commands.add("add");
                commands.add("remove");
                commands.add("modify");
            }
            if (sender.hasPermission("horsehighway.list")) {
                commands.add("list");
            }
            commands.add("help");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (sender.hasPermission("horsehighway.modify")) {
                if (args[0].equals("add")) {
                    commands = getMaterialBlocksAsString();
                } else if (args[0].equals("remove") || args[0].equals("modify")) {
                    Set<String> keySet = plugin.getSpeedMapper().getConfiguredBlocks().keySet();
                    ArrayList<String> commandsArrayList = new ArrayList<>(keySet);

                    // set all to be lowercase as it looks better in autofill
                    for (int i = 0; i < commandsArrayList.size(); i++) {
                        commandsArrayList.set(i, commandsArrayList.get(i).toLowerCase(Locale.ROOT));
                    }

                    commands = commandsArrayList;
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }

    private static ArrayList<String> getMaterialBlocksAsString() {
        ArrayList<Material> materialsList = new ArrayList<>();
        ArrayList<String> materials = new ArrayList<>();

        for (Material material : Material.values()) {
            if (material.isBlock() && material != Material.AIR) {
                materialsList.add(material);
            }
        }

        for (Material material : materialsList) {
            materials.add(material.name().toLowerCase(Locale.ROOT));
        }

        return materials;
    }

    public static boolean isValidBlock(Material material) {
        String materialName = material.name().toLowerCase(Locale.ROOT);
        return getMaterialBlocksAsString().contains(materialName);

    }

    public static boolean isValidBlock(String material) {
        return getMaterialBlocksAsString().contains(material);
    }

}
