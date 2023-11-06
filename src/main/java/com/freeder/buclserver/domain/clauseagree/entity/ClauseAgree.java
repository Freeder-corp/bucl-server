package com.freeder.buclserver.domain.clauseagree.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.clause.entity.Clause;
import com.freeder.buclserver.domain.user.entity.User;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "clause_agree")
public class ClauseAgree extends TimestampMixin {
	@Id
	@Column(name = "clause_agree_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "clause_id")
	private Clause clause;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "is_agreed", nullable = false)
	private boolean isAgreed;

	@CreatedDate
	@Column(name = "agreed_at")
	private LocalDateTime agreedAt;
}
