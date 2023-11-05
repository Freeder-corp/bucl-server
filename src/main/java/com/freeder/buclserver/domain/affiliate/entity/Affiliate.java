package com.freeder.buclserver.domain.affiliate.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "affiliate")
public class Affiliate extends TimestampMixin {
    @Id
    @Column(name = "affiliate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "affiliate_url")
    private String affiliateUrl;
}
