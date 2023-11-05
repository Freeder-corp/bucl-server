package com.freeder.buclserver.domain.product.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.productcategory.entiry.ProductCategory;
import com.freeder.buclserver.domain.productdetail.entity.ProductDetail;
import com.freeder.buclserver.domain.productoption.entity.ProductOption;
import com.freeder.buclserver.domain.product.vo.TaxStatus;
import com.freeder.buclserver.domain.product.vo.ProductStatus;
import com.freeder.buclserver.domain.productreview.entity.ProductReview;
import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "shipping_info_id")
    private ShippingInfo shippingInfo;

    @OneToOne
    @JoinColumn(name = "product_detail_id")
    private ProductDetail productDetail;

    @OneToMany(mappedBy = "product")
    private List<ProductOption> productOptions = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductReview> productReviews = new ArrayList<>();

    private String name;

    @Column(name = "product_code",unique = true)
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

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "product_state")
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @Column(name = "sku_code",unique = true)
    private String skuCode;

    @Column(name = "product_priority")
    private int productPriority;

    @ColumnDefault("true")
    @Column(name = "is_exposed")
    private boolean isExposed;

    @Column(name = "is_available_multiple_option")
    private boolean is_available_multiple_option;

    @Column(name = "sale_alternatives")
    private String saleAlternatives;
}
