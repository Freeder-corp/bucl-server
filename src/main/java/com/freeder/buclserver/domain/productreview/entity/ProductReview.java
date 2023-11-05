package com.freeder.buclserver.domain.productreview.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.user.entity.User;
import javax.persistence.*;
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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(length = 300)
    private String content;
}
