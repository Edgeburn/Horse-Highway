package com.edgeburnmedia.horsehighway;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class SpeedMapper {
    private HorseHighway plugin;
    private FileConfiguration speedMapperConfig;
    private File speedMapperConfigFile;

    SpeedMapper(HorseHighway plugin) {
        this.plugin = plugin;
        createSpeedMapConfig();
        reloadSpeedMap();
    }

    public void reloadSpeedMap() {
        Set<String> speedMapKeys = getSpeedMapperConfig().getKeys(false);
        Material material;
        plugin.clearSpeedMap();
        for (String key : speedMapKeys) {
            material = Material.valueOf(key);
            plugin.addToSpeedMap(material, getSpeedMapperConfig().getDouble(key));
        }
    }

    public HashMap<String, Double> getConfiguredBlocks() {
        Set<String> speedMapKeys = getSpeedMapperConfig().getKeys(false);
        HashMap<String, Double> speedHashMap = new HashMap<>();

        for (String key : speedMapKeys) {
            speedHashMap.put(key, getSpeedMapperConfig().getDouble(key));
        }

        return speedHashMap;
    }

    public boolean isInMap(String material) throws IllegalArgumentException{
        if (Material.matchMaterial(material) != null) {
            return isInMap(Material.matchMaterial(material));
        } else {
            throw new IllegalArgumentException("No such material " + material + " exists.");
        }
    }

    public boolean isInMap(Material material) {
        return getSpeedMapperConfig().contains(material.name());
    }

    public void modifyElement(Material material, double speed) {
        getSpeedMapperConfig().set(material.name(), speed);
        try {
            getSpeedMapperConfig().save(speedMapperConfigFile);
        } catch (IOException e) {
            plugin.getServer().getLogger().log(Level.SEVERE, "Failed to save config!", e);
        }
        reloadSpeedMap();
    }

    public void removeElement(Material material) {
        getSpeedMapperConfig().set(material.name(), null);
        try {
            getSpeedMapperConfig().save(speedMapperConfigFile);
        } catch (IOException e) {
            plugin.getServer().getLogger().log(Level.SEVERE, "Failed to save config!", e);
        }
        reloadSpeedMap();
    }

    public void removeElement(String material) throws IllegalArgumentException {
        if (Material.matchMaterial(material) != null) {
            removeElement(Material.matchMaterial(material));
        } else {
            throw new IllegalArgumentException("No such material " + material + " exists.");
        }
    }

    public FileConfiguration getSpeedMapperConfig() {
        return speedMapperConfig;
    }

    private void createSpeedMapConfig() {
        speedMapperConfigFile = new File(plugin.getDataFolder(), "speed_on_blocks.yml");
        if (!speedMapperConfigFile.exists()) {
            speedMapperConfigFile.getParentFile().mkdirs();
            plugin.saveResource("speed_on_blocks.yml", false);
        }

        speedMapperConfig = new YamlConfiguration();
        try {
            speedMapperConfig.load(speedMapperConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().severe("Failed to load speed_on_blocks.yml config!");
        }
    }

}
