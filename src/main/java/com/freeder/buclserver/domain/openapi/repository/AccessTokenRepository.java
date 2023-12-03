package com.freeder.buclserver.domain.openapi.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.openapi.entity.OpenApiAccessToken;

@Repository
public interface AccessTokenRepository extends JpaRepository<OpenApiAccessToken, Long> {

	Optional<OpenApiAccessToken> findFirstByExpireDateAfter(LocalDateTime expireDate);

	OpenApiAccessToken save(OpenApiAccessToken accessToken);
}
