package com.freeder.buclserver.domain.consumerorder.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.freeder.buclserver.domain.consumerorder.vo.CsStatus;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.consumerpayment.entity.ConsumerPayment;
import com.freeder.buclserver.domain.consumerpurchaseorder.entity.ConsumerPurchaseOrder;
import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "consumer_order")
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ConsumerOrder extends TimestampMixin {
	@Id
	@Column(name = "consumer_order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consumer_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User consumer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User business;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private GroupOrder groupOrder;

	@OneToMany(mappedBy = "consumerOrder")
	private List<Shipping> shippings = new ArrayList<>();

	@OneToMany(mappedBy = "consumerOrder")
	private List<ConsumerPayment> consumerPayments = new ArrayList<>();

	@OneToMany(mappedBy = "consumerOrder")
	private List<ConsumerPurchaseOrder> consumerPurchaseOrders = new ArrayList<>();

	@Column(name = "order_code", unique = true)
	private String orderCode;

	@Column(name = "product_amount")
	private Integer productAmount;

	@Column(name = "order_num")
	private int orderNum;

	@Column(name = "shipping_fee")
	private int shippingFee;

	@Column(name = "total_order_amount")
	private int totalOrderAmount;

	@Column(name = "reward_use_amount")
	private int rewardUseAmount;

	@Column(name = "spent_amount")
	private int spentAmount;

	@ColumnDefault("false")
	@Column(name = "is_rewarded", nullable = false)
	private boolean isRewarded;

	@ColumnDefault("false")
	@Column(name = "is_confirmed")
	private boolean isConfirmed;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status")
	private OrderStatus orderStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "cs_status")
	private CsStatus csStatus;

	@Builder
	private ConsumerOrder(
		User consumer, User business, Product product, GroupOrder groupOrder, String orderCode, int orderNum,
		int shippingFee, int totalOrderAmount, int rewardUseAmount, int spentAmount, boolean isRewarded,
		boolean isConfirmed, OrderStatus orderStatus, CsStatus csStatus
	) {
		this.consumer = consumer;
		this.business = business;
		this.product = product;
		this.groupOrder = groupOrder;
		this.orderCode = orderCode;
		this.orderNum = orderNum;
		this.shippingFee = shippingFee;
		this.totalOrderAmount = totalOrderAmount;
		this.rewardUseAmount = rewardUseAmount;
		this.spentAmount = spentAmount;
		this.isRewarded = isRewarded;
		this.isConfirmed = isConfirmed;
		this.orderStatus = orderStatus;
		this.csStatus = csStatus;
	}

	public void setCsStatus(CsStatus csStatus) {
		this.csStatus = csStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void setConfirmed() {
		this.isConfirmed = true;
	}
}
