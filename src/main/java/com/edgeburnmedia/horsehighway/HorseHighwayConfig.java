package com.edgeburnmedia.horsehighway;

public class HorseHighwayConfig {

	private final HorseHighway plugin;

	HorseHighwayConfig(HorseHighway plugin) {
		this.plugin = plugin;

		plugin.saveDefaultConfig();
	}

	public boolean showSpeedometer() {
		return plugin.getConfig().getBoolean("showSpeedometer");
	}

	public double getDefaultSpeedInKph() {
		return plugin.getConfig().getDouble("defaultSpeed");
	}
}
