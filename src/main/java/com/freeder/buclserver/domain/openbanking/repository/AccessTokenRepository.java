package com.freeder.buclserver.domain.openbanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.openbanking.entity.OpenBankingAccessToken;

@Repository
public interface AccessTokenRepository extends JpaRepository<OpenBankingAccessToken, Long> {

	OpenBankingAccessToken findFirstByExpireDateAfter(String expireDate);

	OpenBankingAccessToken save(OpenBankingAccessToken accessToken);
}
