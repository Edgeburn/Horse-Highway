package com.edgeburnmedia.horsehighway;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class HorseManager {

	private final Horse horse;
	private final HorseHighway plugin;
	private final Player rider;
	private double speed;

	HorseManager(Horse horse, Player rider, HorseHighway plugin) {
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

	public Horse getHorse() {
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
