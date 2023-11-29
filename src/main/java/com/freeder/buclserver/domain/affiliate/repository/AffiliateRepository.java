package com.freeder.buclserver.domain.affiliate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.affiliate.entity.Affiliate;

public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {

	@EntityGraph(attributePaths = {"user", "product"})
	List<Affiliate> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
}
