package com.freeder.buclserver.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PatchMyProfileResponse {
	private String profilePath;
}
