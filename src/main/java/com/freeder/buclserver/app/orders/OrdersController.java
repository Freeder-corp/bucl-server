package com.freeder.buclserver.app.orders;

import com.freeder.buclserver.domain.consumerorder.dto.TrackingNumDto;
import com.freeder.buclserver.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "orders 관련 API", description = "주문 관련 API")
public class OrdersController {

    private final OrdersService service;
    
    @GetMapping("/document/{product_id}")
    public BaseResponse<?> getOrdersDocument(
            @PathVariable(name = "product_id") Long productId
    ) {
        return service.getOrdersDocument(productId);
    }

    @PutMapping("/document")
    public BaseResponse<?> updateTrackingNum(@Valid @RequestBody List<TrackingNumDto> trackingNumDtos){
        return service.updateTrackingNum(trackingNumDtos);
    }

}
