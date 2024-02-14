package com.freeder.buclserver.domain.productoption.entity;

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
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.productoption.vo.OptionKey;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_option")
public class ProductOption extends TimestampMixin {
	@Id
	@Column(name = "production_option_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Product product;

	@Column(name = "sku_code", unique = true)
	private Long skuCode;

	@Column(name = "option_key")
	@Enumerated(EnumType.STRING)
	private OptionKey optionKey;

	@Column(name = "option_value")
	private String optionValue;

	@ColumnDefault("0")
	@Column(name = "option_sequence")
	private int optionSequence;

	@Column(name = "product_qty")
	private int productQty;

	@Column(name = "max_order_qty")
	private int maxOrderQty;

	@Column(name = "min_order_qty")
	private int minOrderQty;

	@Column(name = "option_extra_amount")
	private int optionExtraAmount;

	@ColumnDefault("true")
	@Column(name = "is_exposed")
	private boolean isExposed;
}
