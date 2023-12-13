package com.freeder.buclserver.domain.productreview.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewPhotoDTO {
	private List<String> imagePath;
}
