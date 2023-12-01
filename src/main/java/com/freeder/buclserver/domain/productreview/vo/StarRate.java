package com.freeder.buclserver.domain.productreview.vo;

import java.util.Comparator;

public enum StarRate {
	ZERO(0.0),
	HALF(0.5),
	ONE(1.0),
	ONE_AND_HALF(1.5),
	TWO(2.0),
	TWO_AND_HALF(2.5),
	THREE(3.0),
	THREE_AND_HALF(3.5),
	FOUR(4.0),
	FOUR_AND_HALF(4.5),
	FIVE(5.0);

	private final double value;

	StarRate(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public static Comparator<StarRate> comparator() {
		return Comparator.comparingDouble(StarRate::getValue);
	}
}