package com.freeder.buclserver.app.selling;

import com.freeder.buclserver.domain.affiliate.dto.AffiliateDto;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.selling.dto.SellingDto;
import com.freeder.buclserver.global.response.BaseResponse;
import com.freeder.buclserver.global.util.CryptoAes256;
import com.freeder.buclserver.global.util.DateUtils;
import com.freeder.buclserver.global.util.ImageParsing;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellingService {
    private final ProductRepository productRepository;
    private final ImageParsing imageParsing;
    private final CryptoAes256 cryptoAes256;

    private final String FRONTURL = "https://bucl.co.kr/";   //예시

    public BaseResponse<?> getSellingPage(AffiliateDto affiliateDto) throws Exception {

        return new BaseResponse<>(
                convertSellingDto(
                        productRepository.findByIdForAffiliate(affiliateDto.getProductId()),
                        createAffiliateUrl(affiliateDto)
                ),
                HttpStatus.OK,
                "요청 성공"
        );
    }


    //////////////////////////PRIVATE영역//////////////////////////


    private SellingDto convertSellingDto(Product product, String url) {

        return SellingDto.builder()
                .brandName(product.getBrandName())
                .name(product.getName())
                .imagePath(imageParsing.getImageList(product.getImagePath()))
                .reward((product.getConsumerPrice() - product.getSalePrice()) / product.getBusinessRewardRate())
                .affiliateUrl(FRONTURL + url)
                .build();
    }

    private String createAffiliateUrl(AffiliateDto affiliateDto) throws Exception {
        return cryptoAes256.encrypt(
                String.format(
                        "%s,%s,%d",
                        affiliateDto.getProductId(),
                        affiliateDto.getUserId(),
                        DateUtils.nowDate())
        );
    }
}
