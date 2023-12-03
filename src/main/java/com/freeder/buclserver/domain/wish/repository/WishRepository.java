package com.freeder.buclserver.domain.wish.repository;

import com.freeder.buclserver.domain.wish.entity.Wish;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish,Long> {
    Optional<List<Wish>> findByUserId(Long userId, Pageable pageable);
}
