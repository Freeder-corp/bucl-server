package com.freeder.buclserver.domain.productreview.vo;

public enum StarRate {
	ZERO(0.0f),
	HALF(0.5f),
	ONE(1.0f),
	ONE_AND_HALF(1.5f),
	TWO(2.0f),
	TWO_AND_HALF(2.5f),
	THREE(3.0f),
	THREE_AND_HALF(3.5f),
	FOUR(4.0f),
	FOUR_AND_HALF(4.5f),
	FIVE(5.0f);

	private final float value;

	StarRate(float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	public static StarRate findByValue(Float value) {
		for (StarRate starRate : values()) {
			if (starRate.value == (value)) {
				return starRate;
			}
		}

		throw new IllegalArgumentException("별점 범위를 벗어났습니다: " + value);
	}
}
