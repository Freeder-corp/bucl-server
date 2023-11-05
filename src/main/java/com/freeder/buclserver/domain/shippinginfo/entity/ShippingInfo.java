package com.freeder.buclserver.domain.shippinginfo.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.shippingextrafee.entity.ShippingExtraFee;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "shipping_info")
public class ShippingInfo extends TimestampMixin {
    @Id
    @Column(name = "shipping_info_id",nullable = false, unique = true)
    private int id;

    @OneToMany(mappedBy = "shippingInfo")
    private List<ShippingExtraFee> shippingExtraFees = new ArrayList<>();

    @Column(name = "shipping_co_name")
    private String shippingCoName;

    @Column(name = "info_content")
    private String infoContent;

    @Column(name = "shipping_fee")
    private int shippingFee;

    @Column(name = "shipping_fee_phrase")
    private String shippingFeePhrase;
}
