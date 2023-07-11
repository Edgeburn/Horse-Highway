/*
 * Copyright (c) 2023 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighway;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

public class HorseManager {

	private final AbstractHorse horse;
	private final HorseHighway plugin;
	private final Player rider;
	private double speed;

	HorseManager(AbstractHorse horse, Player rider, HorseHighway plugin) {
		this.horse = horse;
		this.plugin = plugin;
		this.rider = rider;
	}

	public void updateSpeed(Material material) {
		if (rider.hasPermission("horsehighway.use")) {
			HorseSpeedChangeEvent speedChangeEvent = new HorseSpeedChangeEvent(
				this,
				material,
				speed
			);
			Bukkit.getPluginManager().callEvent(new HorseSpeedChangeEvent(this, material, speed));
			if (!speedChangeEvent.isCancelled()) {
				speed = speedChangeEvent.setHorseSpeed();
			}
		}
	}

	public AbstractHorse getHorse() {
		return horse;
	}

	public HorseHighway getPlugin() {
		return plugin;
	}

	public Player getRider() {
		return rider;
	}

	public double getDefaultSpeed() {
		return SpeedConversionUtil.calculateGenericMovementSpeedFromKph(
			getPlugin().getHorseHighwayConfig().getDefaultSpeedInKph()
		);
	}
}
