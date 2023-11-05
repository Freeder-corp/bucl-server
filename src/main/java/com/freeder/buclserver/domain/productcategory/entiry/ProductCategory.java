package com.freeder.buclserver.domain.productcategory.entiry;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.product.entity.Product;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private String category_name;

    @Column(name = "product_desc")
    private String productDesc;

    @OneToMany(mappedBy = "productCategory")
    private List<Product> products = new ArrayList<>();
}
