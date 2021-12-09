package com.edgeburnmedia.horsehighway;

import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class HorseHighway extends JavaPlugin {
    private HashMap<Horse, HorseManager> horseManagers = new HashMap<>();
    private HashMap<Material, Double> speedMap = new HashMap<>();
    private HorseHighwayConfig horseHighwayConfig;
    private SpeedMapper speedMapper;
    private PluginDescriptionFile pluginDescriptionFile;


    @Override
    public void onEnable() {
        horseHighwayConfig = new HorseHighwayConfig(this);
        speedMapper = new SpeedMapper(this);
        speedMapper.reloadSpeedMap();
        getCommand("horsehighway").setExecutor(new HorseHighwayCommands(this));
        getCommand("horsehighway").setTabCompleter(new HorseHighwayCommandTabCompleter(this));
        getServer().getPluginManager().registerEvents(new HorseHighwayListeners(this), this);
        pluginDescriptionFile = this.getDescription();

        // FIXME add ID
        new UpdateChecker(this, -1).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available.");
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerHorse(Horse horse, Player rider) {
        horseManagers.put(horse, new HorseManager(horse, rider, this));
    }

    public HashMap<Horse, HorseManager> getHorseManagers() {
        return horseManagers;
    }

    public HashMap<Material, Double> getSpeedMap() {
        return this.speedMap;
    }

    public HorseHighwayConfig getHorseHighwayConfig() {
        return horseHighwayConfig;
    }

    public SpeedMapper getSpeedMapper() {
        return speedMapper;
    }

    public void clearSpeedMap() {
        this.getSpeedMap().clear();
    }

    public void addToSpeedMap(Material material, double speed) {
        this.getSpeedMap().put(material, speed);
    }

    public PluginDescriptionFile getPluginDescriptionFile() {
        return pluginDescriptionFile;
    }
}
