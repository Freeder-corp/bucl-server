package com.freeder.buclserver.domain.userlog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.user.entity.User;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "USER_LOG")
public class UserLog extends TimestampMixin {
	@Id
	@Column(name = "user_log_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "total_actual_order_amount")
	private int totalActualOrderAmount;

	@Column(name = "total_order_count")
	private int totalOrderCount;

	@Column(name = "total_actual_order_count")
	private int totalActualOrderCount;
}
