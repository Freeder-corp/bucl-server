package com.freeder.buclserver.app.wishes;

import com.freeder.buclserver.app.products.ProductsCategoryService;
import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.grouporder.repository.GroupOrderRepository;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.domain.wish.dto.WishDto;
import com.freeder.buclserver.domain.wish.entity.Wish;
import com.freeder.buclserver.domain.wish.repository.WishRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.freeder.buclserver.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishesService {
    private final WishRepository wishRepository;
    private final GroupOrderRepository groupOrderRepository;
    private final ProductsCategoryService productsCategoryService;

    public BaseResponse<?> getWishesList(Long userId) {

        List<Wish> wishes = wishRepository.findByUserId(userId);

        List<WishDto> list = wishes.stream()
                .map(this::convertWishDto)
                .toList();

        return new BaseResponse<>(
                list,
                HttpStatus.OK,
                "요청 성공"
        );
    }

    @Transactional
    public BaseResponse<?> saveWish(WishDto.WishCreateReq wishCreateReq) {

        return new BaseResponse<>(
                WishCreateRes(wishCreateReq)
                , HttpStatus.OK,
                "요청 성공"
        );
    }

    @Transactional
    public BaseResponse<?> deleteWish(Long wishId) {

        wishRepository.deleteById(wishId);

        return new BaseResponse<>(
                null,
                HttpStatus.OK,
                "요청 성공"
        );
    }


    //private 영역//


    private WishDto convertWishDto(Wish wish) {
        try {
            GroupOrder groupOrder = groupOrderRepository.findByProduct_Id(wish.getProduct().getId());

            return WishDto.builder()
                    .brandName(wish.getProduct().getBrandName())
                    .name(wish.getProduct().getName())
                    .consumerPrice(wish.getProduct().getConsumerPrice())
                    .starRate(productsCategoryService.calculateAverageRating(wish.getProduct().getProductReviews()))
                    .consumerOrdersNumber(groupOrder.getConsumerOrders().size())
                    .isEnded(groupOrder.isEnded())
                    .build();

        } catch (Exception e) {
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 500, "convertWishDto에러 : 개발자에게 문의 바랍니다.");
        }
    }

    private WishDto.WishCreateRes WishCreateRes(WishDto.WishCreateReq wishCreateReq) {

        return WishDto.WishCreateRes.builder()
                .wishId(
                        wishRepository.save(
                                Wish.builder()
                                        .user(
                                                User.builder()
                                                        .id(wishCreateReq.getUserId())
                                                        .build()
                                        )
                                        .product(
                                                Product.builder()
                                                        .id(wishCreateReq.getUserId())
                                                        .build()
                                        )
                                        .build()
                        ).getId()
                )
                .build();
    }
}
