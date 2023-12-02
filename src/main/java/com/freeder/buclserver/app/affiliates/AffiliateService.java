package com.freeder.buclserver.app.affiliates;

import com.freeder.buclserver.domain.affiliate.dto.AffiliateDto;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.response.BaseResponse;
import com.freeder.buclserver.global.util.CryptoAes256;
import com.freeder.buclserver.global.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AffiliateService {
    private final CryptoAes256 cryptoAes256;

    public BaseResponse<?> getAffiliateUrl(String affiliateEncrypt) throws Exception {

        String[] body = validUrl(affiliateEncrypt);

        return new BaseResponse<>(
                convertAffiliateDto(body[0], body[1]),
                HttpStatus.OK,
                "요청 성공"
        );
    }


    ////////////////////////////////////////private영역/////////////////////////////////////////


    private String[] validUrl(String affiliateEncrypt) {
        try {

            String[] body = cryptoAes256.decrypt(affiliateEncrypt).split(",");

            if (!DateUtils.isOneWeekPassed(Long.parseLong(body[body.length - 1]))) {
                throw new BaseException(HttpStatus.BAD_REQUEST, 400, "만료된 링크입니다.");
            }

            return body;

        } catch (Exception e) {
            throw new BaseException(HttpStatus.BAD_REQUEST, 400, "잘못된 URL입니다.");
        }

    }


    private AffiliateDto convertAffiliateDto(String productId, String userId) {
        return AffiliateDto.builder()
                .productId(Long.valueOf(productId))
                .userId(Long.valueOf(userId))
                .build();
    }
}
