package com.freeder.buclserver.domain.wish.dto;

import com.freeder.buclserver.domain.productreview.vo.StarRate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishDto {
    private String brandName;
    private String name;
    private int consumerPrice;
    private float starRate;
    private int consumerOrdersNumber;
    private boolean isEnded;

    @Getter
    @Builder
    @ToString
    public static class WishCreateRes{
        private Long wishId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishCreateReq{
        private Long productId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishDeleteReq{
        private Long wishId;
    }
}
