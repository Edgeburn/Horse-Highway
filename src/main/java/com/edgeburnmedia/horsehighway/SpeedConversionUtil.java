package com.edgeburnmedia.horsehighway;

public class SpeedConversionUtil {

	private static final double GENERIC_MOVEMENT_SPEED_MAGIC_NUMBER = 43.17;

	public static double calculateGenericMovementSpeedFromKph(double speedInKph) {
		return calculateMetresPerSecondFromKph(speedInKph) / GENERIC_MOVEMENT_SPEED_MAGIC_NUMBER;
	}

	public static double calculateGenericMovementSpeedFromMetresPerSecond(
		double speedInMetresPerSecond) {
		return speedInMetresPerSecond / GENERIC_MOVEMENT_SPEED_MAGIC_NUMBER;
	}

	public static double calculateKphFromGenericMovementSpeed(double speedInGenericMovementSpeed) {
		return round(calculateKphFromMetresPerSecond(
				speedInGenericMovementSpeed * GENERIC_MOVEMENT_SPEED_MAGIC_NUMBER),
			1);

	}

	private static double calculateKphFromMetresPerSecond(double speedInMetresPerSecond) {
		return speedInMetresPerSecond * 3.6;
	}

	private static double calculateMetresPerSecondFromKph(double speedInKph) {
		return speedInKph / 3.6;
	}

	public static double round(double value, int places) {
		/*
		 * "Borrowed" from here: https://stackoverflow.com/a/2808648
		 * Original version should work fine here since we're only returning 1 or
		 * decimal places in all places it's used
		 * If in the future the safer version is required, can easily be swapped in
		 * place
		 */
		if (places < 0) {
			throw new IllegalArgumentException();
		}

		long factor = (long) Math.pow(10, places);
		value *= factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

}
