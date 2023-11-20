package com.freeder.buclserver.domain.consumerpurchaseorder.entity;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.productoption.entity.ProductOption;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "consumer_purchase_order")
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ConsumerPurchaseOrder extends TimestampMixin {
	@Id
	@Column(name = "consumer_purchase_order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "consumer_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ConsumerOrder consumerOrder;

	@ManyToOne
	@JoinColumn(name = "group_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ProductOption productOption;

	@Column(name = "product_order_code", unique = true)
	private String productOrderCode;

	@Column(name = "product_option_value")
	private String productOptionValue;

	@Column(name = "product_amount")
	private int productAmount;

	@Column(name = "product_order_qty")
	private int productOrderQty;

	@Column(name = "product_order_amount")
	private int productOrderAmount;
}
