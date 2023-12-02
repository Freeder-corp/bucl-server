package com.freeder.buclserver;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.selling.dto.SellingDto;
import com.freeder.buclserver.global.util.DateUtils;
import com.freeder.buclserver.global.util.ImageParsing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class BuclServerApplicationTests {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ImageParsing imageParsing;

    //	@Test
    void contextLoads() {

    }

    @Test
    void sellingTest() {
        Product product = productRepository.findByIdForAffiliate(1L);
    }

    @Test
    void affiliateUrlTest(){
        String body = String.format("%s,%s,%d",1,1, DateUtils.nowDate());
        System.out.println(body);

    }

    @Test
    void test1(){
        System.out.println();
    }

    private SellingDto convertSellingDto(Product product) {
        float reward = (product.getConsumerPrice() - product.getSalePrice()) / product.getBusinessRewardRate();

        return SellingDto.builder()
                .brandName(product.getBrandName())
                .name(product.getName())
                .imagePath(imageParsing.getImageList(product.getImagePath()))
                .reward(reward)
                .build();
    }

}
