package com.freeder.buclserver.domain.shipping.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "shipping")
public class Shipping extends TimestampMixin {
	@Id
	@Column(name = "shipping_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "consumser_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ConsumerOrder consumerOrder;

	@ManyToOne
	@JoinColumn(name = "shipping_info_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ShippingInfo shippingInfo;

	@OneToOne(mappedBy = "shipping")
	private ShippingAddress shippingAddress;

	@Column(name = "shipping_co_name")
	private String shippingCoName;

	@Column(name = "shipping_num", unique = true)
	private String shippingNum;

	@Column(name = "tracking_num")
	private String trackingNum;

	@Column(name = "shipping_status")
	@Enumerated(EnumType.STRING)
	private ShippingStatus shippingStatus;

	@Column(name = "is_active")
	private boolean isActive;

	@Column(name = "purchase_order_input_date")
	private LocalDateTime purchaseOrderInputDate;

	@Column(name = "tracking_num_input_date")
	private LocalDateTime trackingNumInputDate;

	@Column(name = "shipped_date")
	private LocalDateTime shippedDate;
}