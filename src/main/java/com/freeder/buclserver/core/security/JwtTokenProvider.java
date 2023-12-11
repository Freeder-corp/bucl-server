package com.freeder.buclserver.core.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.freeder.buclserver.domain.user.vo.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private static final Long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 1; // 1시간
	private static final Long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 24 * 30; // 30일

	@Value("${jwt.secret-key}")
	private String secretKey;
	private Key encodeKey;

	@PostConstruct
	protected void init() {
		encodeKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	// TODO: 토큰을 cookie에 넣어서 보내줘야 하는지에 대해 프론트와 의논 필요
	public String createAccessToken(Long memberId, Role role) {
		return createToken(memberId, role, ACCESS_TOKEN_EXPIRED_TIME);
	}

	public String createRefreshToken(Long memberId, Role role) {
		return createToken(memberId, role, REFRESH_TOKEN_EXPIRED_TIME);
	}

	public Authentication getAuthentication(String token) {
		CustomUserDetails userDetails = CustomUserDetails.of(getUsername(token), getUserRole(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public void validateToken(String token) {
		Jwts.parserBuilder()
			.setSigningKey(encodeKey)
			.build()
			.parseClaimsJws(token);
	}

	public String getUserRole(String token) {
		return getClaims(token).get("role").toString();
	}

	private String createToken(Long memberId, Role role, Long tokenExpiredTime) {
		Date now = new Date();
		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setSubject(String.valueOf(memberId))
			.claim("role", role.name())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + tokenExpiredTime))
			.signWith(encodeKey, SignatureAlgorithm.HS256)
			.compact();
	}

	private String getUsername(String token) {
		return getClaims(token).getSubject();
	}

	private Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(encodeKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}