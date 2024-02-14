package com.freeder.buclserver.app.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TrackingDetailDto {
	String kind;
	String level;
	String manName;
	String manPic;
	String telno;
	String telno2;
	long time;
	String timeString;
	String where;
	String code;
	String remark;
}
