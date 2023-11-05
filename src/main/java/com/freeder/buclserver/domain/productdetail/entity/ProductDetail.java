package com.freeder.buclserver.domain.productdetail.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import javax.persistence.*;
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
