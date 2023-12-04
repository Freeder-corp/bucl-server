package com.freeder.buclserver.domain.productreview.entity;

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

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.productreview.vo.StarRate;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_review")
public class ProductReview extends TimestampMixin {
	@Id
	@Column(name = "product_review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@ManyToOne
	@JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Product product;

	@Enumerated(EnumType.STRING)
	@Column(name = "star_rate")
	private StarRate starRate;

	@Column(name = "image_path")
	private String imagePath;

	@Column(name = "selected_option")
	private String selectedOption;

	public Product getProduct() {
		return product;
	}
}
