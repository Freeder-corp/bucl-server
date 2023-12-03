package com.freeder.buclserver.sellingwishaffiliate;

import com.freeder.buclserver.app.wishes.WishesService;
import com.freeder.buclserver.domain.grouporder.repository.GroupOrderRepository;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.wish.dto.WishDto;
import com.freeder.buclserver.domain.wish.repository.WishRepository;
import com.freeder.buclserver.global.util.DateUtils;
import com.freeder.buclserver.global.util.ImageParsing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FeatTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ImageParsing imageParsing;

    @Autowired
    WishesService wishesService;

    @Autowired
    WishRepository wishRepository;

    @Autowired
    GroupOrderRepository groupOrderRepository;

    @Test
    void sellingTest() {
        Product product = productRepository.findByIdForAffiliate(1L);
    }

    @Test
    void affiliateUrlTest() {
        String body = String.format("%s,%s,%d", 1, 1, DateUtils.nowDate());
        System.out.println(body);

    }

//    @Test
    void insertWishTest() {
        /*System.out.println(wishesService.WishCreateRes(WishDto.WishCreateReq.builder()
                .userId(1L)
                .productId(2L)
                .build()));*/
    }

//    @Test
    void deleteWishTest(){
//        wishesService.deleteWish(3L);
    }

//    @Test
    void getWishesTest(){
//        System.out.println(wishRepository.findByUserId(1L));
    }

    @Test
    void groupOrderTest(){
        System.out.println(groupOrderRepository.findByProduct_Id(1L));
    }
}
