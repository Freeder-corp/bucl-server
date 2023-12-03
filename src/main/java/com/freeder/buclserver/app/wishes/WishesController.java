package com.freeder.buclserver.app.wishes;

import com.freeder.buclserver.domain.wish.dto.WishDto;
import com.freeder.buclserver.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/wishes")
@RequiredArgsConstructor
@Tag(name = "wishes 관련 API", description = "찜 관련 API")
public class WishesController {
    private final WishesService service;

    @GetMapping("/{user_id}")
    public BaseResponse<?> getWishesList(
            @PathVariable(name = "user_id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return service.getWishesList(userId, page, pageSize);
    }

    @PostMapping()
    public BaseResponse<?> saveWish(@RequestBody WishDto.WishCreateReq wishCreateReq) {
        return service.saveWish(wishCreateReq);
    }

    @DeleteMapping("/{wish_id}")
    public BaseResponse<?> deleteWish(
            @PathVariable(name = "wish_id") Long wishId
    ) {
        return service.deleteWish(wishId);
    }
}
