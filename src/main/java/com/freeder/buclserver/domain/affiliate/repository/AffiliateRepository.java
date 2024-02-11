package com.freeder.buclserver.domain.affiliate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freeder.buclserver.domain.affiliate.entity.Affiliate;

public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {

}
