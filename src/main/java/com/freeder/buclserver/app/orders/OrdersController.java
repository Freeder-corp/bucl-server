package com.freeder.buclserver.app.orders;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/orders")
@Tag(name = "orders 관련 API", description = "주문 관련 API")
public class OrdersController {
}
