package com.freeder.buclserver.sellingwishaffiliate;

import com.freeder.buclserver.app.wishes.WishesService;
import com.freeder.buclserver.domain.consumerorder.dto.ConsumerOrderDto;
import com.freeder.buclserver.domain.consumerorder.dto.TrackingNumDto;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerorder.vo.CsStatus;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.consumerpurchaseorder.entity.ConsumerPurchaseOrder;
import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.grouporder.repository.GroupOrderRepository;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;
import com.freeder.buclserver.domain.wish.dto.WishDto;
import com.freeder.buclserver.domain.wish.repository.WishRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.response.ErrorResponse;
import com.freeder.buclserver.global.util.DateUtils;
import com.freeder.buclserver.global.util.ImageParsing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    ConsumerOrderRepository consumerOrderRepository;

    @Test
    void sellingTest() {
        Product product = productRepository.findByIdForAffiliate(1L).get();
        System.out.println(product);
    }

    //    @Test
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
    void deleteWishTest() {
//        wishesService.deleteWish(3L);
    }

    //    @Test
    void getWishesTest() {
//        System.out.println(wishRepository.findByUserId(1L));
    }

    //    @Test
    void groupOrderTest() {
        System.out.println(groupOrderRepository.findByProduct_Id(1L));
    }

    //    @Test
    @Transactional
    void excelupdate(List<TrackingNumDto> trackingNumDtos) {
//        List<TrackingNumDto> trackingNumDtos = new ArrayList<>();
        for (TrackingNumDto i : trackingNumDtos) {
            ConsumerOrder order = consumerOrderRepository.findByOrderCode(i.getOrderCode()).orElseThrow(() ->
                    new BaseException(HttpStatus.BAD_REQUEST, 400, "잘못된필드")
            );

            Shipping shipping = order.getShippings().stream().filter(Shipping::isActive).findFirst().orElseThrow(() ->
                    new BaseException(HttpStatus.BAD_REQUEST, 400, "활성화된 배송정보가 없습니다.")
            );
            ShippingInfo shippingInfo = shipping.getShippingInfo();
            shipping.setShippingStatus(ShippingStatus.IN_DELIVERY);
            shipping.setTrackingNum(i.getTrakingNum());
            shippingInfo.setShippingCoName(i.getShippingCoName());
        }
    }

    //    @Test
    @Transactional(readOnly = true)
    void excelTest() {
        Product product = productRepository.findById(1L).orElseThrow(() ->
                new BaseException(HttpStatus.BAD_REQUEST, 400, "잘못된필드")
        );
        List<ConsumerOrder> consumerOrders = product.getConsumerOrders().stream()
                .filter(consumerOrder ->
                        consumerOrder.getOrderStatus().equals(OrderStatus.ORDERED) && consumerOrder.getCsStatus().equals(CsStatus.NONE)
                )
                .toList();

        List<ConsumerOrderDto> orderDtos = consumerOrders.stream()
                .map(consumerOrder -> convertConsumerOrders(product.getName(), consumerOrder))
                .toList();

    }

    private ConsumerOrderDto convertConsumerOrders(String name, ConsumerOrder consumerOrder) {

        List<ConsumerOrderDto.ProductOption> list = consumerOrder.getConsumerPurchaseOrders().stream()
                .map(this::convertConsumerPurchaseOrder)
                .toList();

        Shipping shipping = consumerOrder.getShippings().stream()
                .filter(Shipping::isActive)
                .findFirst()
                .orElseThrow(() ->
                        new BaseException(HttpStatus.BAD_REQUEST, 400, "활성화된 배송이 없습니다.")
                );

        ShippingAddress shippingAddress = shipping.getShippingAddress();
        ShippingInfo shippingInfo = shipping.getShippingInfo();

        return ConsumerOrderDto.builder()
                .orderCode(consumerOrder.getOrderCode())
                .name(name)
                .productOptions(list)
                .recipientName(shippingAddress.getRecipientName())
                .zipCode(shippingAddress.getZipCode())
                .address(shippingAddress.getAddress())
                .addressDetail(shippingAddress.getAddressDetail())
                .contactNumber(shippingAddress.getContactNumber())
                .memoContent(shippingAddress.getMemoContent())
                .shippingFee(shippingInfo.getShippingFee())
                .shippingFeePhrase(shippingInfo.getShippingFeePhrase())
                .build();
    }

    private ConsumerOrderDto.ProductOption convertConsumerPurchaseOrder(ConsumerPurchaseOrder consumerPurchaseOrder) {
        return ConsumerOrderDto.ProductOption.builder()
                .productOptionValue(consumerPurchaseOrder.getProductOptionValue())
                .productOptionQty(consumerPurchaseOrder.getProductOrderQty())
                .build();
    }

}
