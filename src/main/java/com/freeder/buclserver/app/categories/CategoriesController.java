package com.freeder.buclserver.app.categories;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/categories")
@Tag(name = "categories 관련 API", description = "카테고리 관련 API")
public class CategoriesController {
}
