package com.freeder.buclserver.domain.productdetail.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.freeder.buclserver.core.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PRODUCT_DETAIL")
public class ProductDetail extends TimestampMixin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

}
