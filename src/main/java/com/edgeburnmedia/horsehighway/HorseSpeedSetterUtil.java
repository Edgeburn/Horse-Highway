/*
 * Copyright (c) 2023 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighway;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;

public class HorseSpeedSetterUtil {

	public static void setHorseSpeedFromKph(AbstractHorse horse, double speedInKph) {
		horse
			.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
			.setBaseValue(SpeedConversionUtil.calculateGenericMovementSpeedFromKph(speedInKph));
	}

	public static void setHorseSpeedFromGenericMovementSped(AbstractHorse horse, double speed) {
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
	}
}
