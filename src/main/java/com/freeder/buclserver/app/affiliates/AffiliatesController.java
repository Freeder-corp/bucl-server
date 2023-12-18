package com.freeder.buclserver.app.affiliates;

import com.freeder.buclserver.domain.affiliate.dto.AffiliateDto;
import com.freeder.buclserver.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/affiliates")
@Tag(name = "affiliates 관련 API", description = "판매 링크 관련 API")
@RequiredArgsConstructor
public class AffiliatesController {
    private final AffiliateService service;

    @PostMapping
    public BaseResponse<?> getSellingPage(
            Authentication authentication,
            @RequestBody AffiliateDto affiliateDto
    ) throws Exception {
        return service.getSellingPage(authentication, affiliateDto);
    }

    @GetMapping("/{AffiliateEncrypt}")
    public BaseResponse<?> getAffiliateUrl(
            @PathVariable(name = "AffiliateEncrypt") String affiliateEncrypt
    ) throws Exception {
        return service.getAffiliateUrl(affiliateEncrypt);
    }
}
