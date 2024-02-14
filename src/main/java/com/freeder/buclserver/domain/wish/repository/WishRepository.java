package com.freeder.buclserver.domain.wish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.wish.entity.Wish;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

	boolean existsByUser_IdAndProduct_IdAndDeletedAtIsNull(Long userId, Long productId);
}
