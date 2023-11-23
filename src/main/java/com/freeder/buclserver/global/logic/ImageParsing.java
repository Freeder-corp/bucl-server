package com.freeder.buclserver.global.logic;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ImageParsing {
	public String getThumbnailUrl(String imagePath) {
		List<String> imageList = getImageList(imagePath);
		return imageList.isEmpty() ? null : imageList.get(0);
	}

	public List<String> getImageList(String imagePath) {
		return Arrays.asList(imagePath.split("\\s+"));
	}
}
