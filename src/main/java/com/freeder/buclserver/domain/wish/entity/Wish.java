package com.freeder.buclserver.domain.wish.entity;

import javax.persistence.*;

import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.*;

@Entity
@Getter
@Setter
@Builder
//@Table(name = "wish", indexes = @Index(name = "idx_wish_userId",columnList = "user_id"))
@Table(name = "wish")
@NoArgsConstructor
@AllArgsConstructor
public class Wish extends TimestampMixin {
	@Id
	@Column(name = "wish_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@ManyToOne
	@JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Product product;
}
