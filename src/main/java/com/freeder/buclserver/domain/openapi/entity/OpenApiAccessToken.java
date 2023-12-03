package com.freeder.buclserver.domain.openapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;

@Entity
@Builder
@Table(name = "openapi")
public class OpenApiAccessToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq", nullable = false)
	private Long seq;

	@Column(nullable = false, length = 400)
	private String accessToken;

	@Column(nullable = false)
	private String tokenType;

	@Column(nullable = false)
	private String expireDate;

	@Column(nullable = false)
	private String scope;

	@Column(nullable = false)
	private String clientUseCode;

	public OpenApiAccessToken(Long seq, String accessToken, String tokenType, String expireDate, String scope,
		String clientUseCode) {
		this.seq = seq;
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expireDate = expireDate;
		this.scope = scope;
		this.clientUseCode = clientUseCode;
	}

	public OpenApiAccessToken() {

	}
}
