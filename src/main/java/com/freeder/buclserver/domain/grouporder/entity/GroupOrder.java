package com.freeder.buclserver.domain.grouporder.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GROUP_ORDER")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class GroupOrder extends TimestampMixin {
	@Id
	@Column(name = "group_order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "product_id", unique = true,
		foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Product product;

	// @OneToOne(mappedBy = "groupOrder")
	// private List<ConsumerOrder> consumerOrders = new ArrayList<>();

	// @ColumnDefault("false")
	// @Column(name = "is_ended")
	// private boolean isEnded;

	@Column(name = "actl_num")
	private int actlNum;

	@Column(name = "ctrl_num")
	private int ctrlNum;

	@Column(name = "is_active")
	private boolean isActive;

	@Column(name = "is_deadline")
	private boolean isDeadline;

	@Column(name = "needed_update")
	private boolean neededUpdate;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

}
