package com.freeder.buclserver.domain.grouporder.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.product.entity.Product;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "GROUP_ORDER")
public class GroupOrder extends TimestampMixin {
    @Id
    @Column(name = "group_order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "groupOrder")
    private List<ConsumerOrder> consumerOrders = new ArrayList<>();

    @ColumnDefault("false")
    @Column(name = "is_ended")
    private boolean isEnded;
}
