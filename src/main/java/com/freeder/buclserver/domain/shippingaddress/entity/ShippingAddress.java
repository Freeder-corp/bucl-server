package com.freeder.buclserver.domain.shippingaddress.entity;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.user.entity.User;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "shipping_address")
public class ShippingAddress extends TimestampMixin {
    @Id
    @Column(name = "shipping_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "shipping_address_name")
    private String shippingAddressName;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "zip_code")
    private String zipCode;

    private String address;

    @Column(name = "address_detail",length = 500)
    private String addressDetail;

    @Column(name = "contact_num")
    private String contactNum;

    @Column(name = "tel_num",nullable = true)
    private String telNum;

    @Column(name = "is_default_address")
    private boolean isDefaultAddress;
}
