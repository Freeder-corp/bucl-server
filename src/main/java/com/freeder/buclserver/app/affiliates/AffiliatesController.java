package com.freeder.buclserver.app.affiliates;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/affiliates")
@Tag(name = "affiliates 관련 API", description = "판매 링크 관련 API")
public class AffiliatesController {
}
