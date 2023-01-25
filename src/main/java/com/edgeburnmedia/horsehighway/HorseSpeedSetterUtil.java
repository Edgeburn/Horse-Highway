package com.edgeburnmedia.horsehighway;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;

public class HorseSpeedSetterUtil {

	public static void setHorseSpeedFromKph(Horse horse, double speedInKph) {
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
			.setBaseValue(SpeedConversionUtil.calculateGenericMovementSpeedFromKph(speedInKph));
	}

	public static void setHorseSpeedFromGenericMovementSped(Horse horse, double speed) {
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
	}
}
