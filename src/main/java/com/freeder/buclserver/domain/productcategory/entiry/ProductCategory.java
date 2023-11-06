package com.freeder.buclserver.domain.productcategory.entiry;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.product.entity.Product;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_category")
public class ProductCategory extends TimestampMixin {
	@Id
	@Column(name = "product_category_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "category_name")
	private String categoryName;

	@Column(name = "product_desc")
	private String productDesc;

	@OneToMany(mappedBy = "productCategory")
	private List<Product> products = new ArrayList<>();
}
