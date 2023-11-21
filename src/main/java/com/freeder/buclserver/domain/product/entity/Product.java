package com.freeder.buclserver.domain.product.entity;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.freeder.buclserver.domain.product.vo.ProductStatus;
import com.freeder.buclserver.domain.product.vo.TaxStatus;
import com.freeder.buclserver.domain.productcategory.entity.ProductCategory;
import com.freeder.buclserver.domain.productoption.entity.ProductOption;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product")
public class Product extends TimestampMixin {
	@Id
	@Column(name = "product_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ProductCategory productCategory;

	@ManyToOne
	@JoinColumn(name = "shipping_info_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ShippingInfo shippingInfo;

	@OneToMany(mappedBy = "product")
	private List<ProductOption> productOptions = new ArrayList<>();

	@OneToMany(mappedBy = "product")
	private List<ProductReview> productReviews = new ArrayList<>();

	private String name;

	@Column(name = "product_code", unique = true)
	private String productCode;

	@Column(name = "brand_name")
	private String brandName;

	@Column(name = "manufacturer_name")
	private String manufacturerName;

	@Column(name = "supplier_name")
	private String supplierName;

	@Column(name = "supply_price")
	private int supplyPrice;
	@Column(name = "consumer_price")
	private int consumerPrice;
	@Column(name = "sale_price")
	private int salePrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "tax_status")
	private TaxStatus taxStatus;

	@Column(name = "margin_rate")
	private int marginRate;
	@Column(name = "tax_rate")
	private int taxRate;
	@Column(name = "discount_rate")
	private int discountRate;

	@Column(name = "consumer_reward_rate")
	private int consumerRewardRate;

	@Column(name = "business_reward_rate")
	private int businessRewardRate;

	@Column(name = "image_path", length = 5000)
	private String imagePath;

	@Column(name = "detail_image_path", length = 5000)
	private String detailImagePath;

	@Column(name = "product_state")
	@Enumerated(EnumType.STRING)
	private ProductStatus productStatus;

	@Column(name = "sku_code", unique = true)
	private String skuCode;

	@Column(name = "product_priority")
	private int productPriority;

	@ColumnDefault("true")
	@Column(name = "is_exposed")
	private boolean isExposed;

	@Column(name = "is_available_multiple_option")
	private boolean isAvailableMultipleOption;

	@Column(name = "sale_alternatives")
	private String saleAlternatives;
}
