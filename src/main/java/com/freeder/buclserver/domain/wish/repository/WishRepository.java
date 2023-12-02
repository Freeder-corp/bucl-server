package com.freeder.buclserver.domain.wish.repository;

import com.freeder.buclserver.domain.wish.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish,Long> {
    List<Wish> findByUserId(Long userId);
}
