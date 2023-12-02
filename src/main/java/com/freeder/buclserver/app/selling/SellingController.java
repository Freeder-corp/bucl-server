package com.freeder.buclserver.app.selling;

import com.freeder.buclserver.domain.affiliate.dto.AffiliateDto;
import com.freeder.buclserver.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/selling")
@Tag(name = "Selling 관련 API", description = "판매 관련 API")
public class SellingController {
    private final SellingService service;

    @PostMapping
    public BaseResponse<?> getSellingPage(@RequestBody AffiliateDto affiliateDto) throws Exception {
        return service.getSellingPage(affiliateDto);
    }

}
