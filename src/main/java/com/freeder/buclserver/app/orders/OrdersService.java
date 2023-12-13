package com.freeder.buclserver.app.orders;

import com.freeder.buclserver.domain.consumerorder.dto.ConsumerOrderDto;
import com.freeder.buclserver.domain.consumerorder.dto.TrackingNumDto;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerorder.vo.CsStatus;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.consumerpurchaseorder.entity.ConsumerPurchaseOrder;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final ConsumerOrderRepository consumerOrderRepository;
    private final ProductRepository productRepository;

    public BaseResponse<?> getOrdersDocument(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
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

        return new BaseResponse<>(orderDtos, HttpStatus.OK, "요청 성공");
    }

    public BaseResponse<?> updateTrackingNum(List<TrackingNumDto> trackingNumDtos) {
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
        return new BaseResponse<>(null,HttpStatus.OK,"요청 성공");
    }

    //private 영역//

    private ConsumerOrderDto convertConsumerOrders(String name, ConsumerOrder consumerOrder) {
        try {
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
        } catch (Exception e) {
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR,500,"convertConsumerOrders오류 개발자에게 문의바랍니다.");
        }

    }

    private ConsumerOrderDto.ProductOption convertConsumerPurchaseOrder(ConsumerPurchaseOrder consumerPurchaseOrder) {
        return ConsumerOrderDto.ProductOption.builder()
                .productOptionValue(consumerPurchaseOrder.getProductOptionValue())
                .productOptionQty(consumerPurchaseOrder.getProductOrderQty())
                .build();
    }
}
