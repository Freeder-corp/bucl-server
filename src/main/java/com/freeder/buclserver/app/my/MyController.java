package com.freeder.buclserver.app.my;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/my")
@Tag(name = "my 관련 API", description = "마이페이지 관련 API")
public class MyController {
}
