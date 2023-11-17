package com.freeder.buclserver.app.wishes;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/wishes")
@Tag(name = "wishes 관련 API", description = "찜 관련 API")
public class WishesController {
}
