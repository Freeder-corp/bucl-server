package com.freeder.buclserver.domain.openbanking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.openbanking.entity.OpenBankingAccessToken;

@Repository
public interface AccessTokenRepository extends JpaRepository<OpenBankingAccessToken, Long> {

	Optional<OpenBankingAccessToken> findFirstByExpireDateAfter(String expireDate);

	Optional<OpenBankingAccessToken> findFirstByExpireDateBefore(String expireDate);

	OpenBankingAccessToken save(OpenBankingAccessToken accessToken);

	@Query("SELECT o.clientUseCode FROM OpenBankingAccessToken o WHERE o.accessToken = :accessToken")
	String findClientUseCodeByAccessToken(@Param("accessToken") String accessToken);
}
