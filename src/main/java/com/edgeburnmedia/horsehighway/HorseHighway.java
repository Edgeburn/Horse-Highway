/*
 * Copyright (c) 2023 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighway;

import java.util.HashMap;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class HorseHighway extends JavaPlugin {

	// FIXME add actual ID
	private static final int RESOURCE_ID = 100564;
	private final HashMap<AbstractHorse, HorseManager> horseManagers = new HashMap<>();
	private final HashMap<Material, Double> speedMap = new HashMap<>();
	private HorseHighwayConfig horseHighwayConfig;
	private SpeedMapper speedMapper;
	private PluginDescriptionFile pluginDescriptionFile;

	@Override
	public void onEnable() {
		// bstats metrics
		Metrics metrics = new Metrics(this, 14569);

		horseHighwayConfig = new HorseHighwayConfig(this);
		speedMapper = new SpeedMapper(this);
		speedMapper.reloadSpeedMap();
		getCommand("horsehighway").setExecutor(new HorseHighwayCommands(this));
		getCommand("horsehighway").setTabCompleter(new HorseHighwayCommandTabCompleter(this));
		getServer().getPluginManager().registerEvents(new HorseHighwayListeners(this), this);
		pluginDescriptionFile = this.getDescription();

		new UpdateChecker(this, RESOURCE_ID)
			.getVersion(version -> {
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

	public void registerHorse(AbstractHorse horse, Player rider) {
		horseManagers.put(horse, new HorseManager(horse, rider, this));
	}

	public HashMap<AbstractHorse, HorseManager> getHorseManagers() {
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

	public void deregisterHorse(Player player, Entity playerVehicle) {
		if (playerVehicle == null) {
			return;
		}

		if (playerVehicle.getType().equals(EntityType.HORSE)) {
			if (getHorseManagers().containsKey(playerVehicle)) {
				getHorseManagers().remove(playerVehicle);
			} else {
				getLogger().warning("Deregistering horse that is not registered.");
			}
		}
	}
}
