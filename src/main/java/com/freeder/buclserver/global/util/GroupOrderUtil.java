package com.freeder.buclserver.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Component;

@Component
public class GroupOrderUtil {
	public static LocalDateTime getStartGroupOrderDateTime() {
		LocalDate currentDate = LocalDate.now();

		LocalTime desiredTime = LocalTime.of(0, 0, 0, 0);

		return LocalDateTime.of(currentDate, desiredTime);
	}

	public static LocalDateTime getEndGroupOrderDateTime() {
		LocalDate currentDate = LocalDate.now();

		LocalTime desiredTime = LocalTime.of(23, 59, 0, 0);

		return LocalDateTime.of(currentDate, desiredTime);
	}
}
