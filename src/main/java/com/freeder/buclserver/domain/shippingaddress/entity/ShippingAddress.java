package com.freeder.buclserver.domain.shippingaddress.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.freeder.buclserver.core.mixin.TimestampMixin;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.user.entity.User;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "shipping_address")
public class ShippingAddress extends TimestampMixin {
	@Id
	@Column(name = "shipping_address_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne
	@JoinColumn(name = "shipping_id")
	private Shipping shipping;

	@Column(name = "recipient_name")
	private String recipientName;

	@Column(name = "zip_code")
	private String zipCode;

	private String address;

	@Column(name = "address_detail", length = 500)
	private String addressDetail;

	@Column(name = "contact_number")
	private String contactNumber;

	@Column(name = "memo_content")
	private String memoContent;
}
