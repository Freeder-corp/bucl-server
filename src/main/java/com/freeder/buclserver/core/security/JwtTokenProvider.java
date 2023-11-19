package com.freeder.buclserver.core.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.freeder.buclserver.domain.user.vo.JoinType;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private final UserDetailsService userDetailsService;

	private static final Long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 1; // 1시간
	private static final Long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 24 * 30; // 30일

	@Value("${jwt.secret-key}")
	private String secretKey;
	private Key encodeKey;

	@PostConstruct
	protected void init() {
		encodeKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	public String createAccessToken(Long memberId, JoinType joinType) {
		return createToken(memberId, joinType, ACCESS_TOKEN_EXPIRED_TIME);
	}

	public String createRefreshToken(Long memberId, JoinType joinType) {
		return createToken(memberId, joinType, REFRESH_TOKEN_EXPIRED_TIME);
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public void validateToken(String token) {
		Jwts.parserBuilder()
			.setSigningKey(encodeKey)
			.build()
			.parseClaimsJws(token);
	}

	private String createToken(Long memberId, JoinType joinType, Long tokenExpiredTime) {
		Date now = new Date();
		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setSubject(String.valueOf(memberId))
			.claim("joinType", joinType.name())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + tokenExpiredTime))
			.signWith(encodeKey, SignatureAlgorithm.HS256)
			.compact();
	}

	private String getUsername(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(encodeKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}
}
