package com.freeder.buclserver.domain.shippingaddress.entity;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.user.entity.User;
import com.freeder.buclserver.global.mixin.TimestampMixin;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "shipping_address")
public class ShippingAddress extends TimestampMixin {
	@Id
	@Column(name = "shipping_address_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shipping_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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

	@Builder
	private ShippingAddress(
		User user, Shipping shipping, String recipientName, String zipCode, String address,
		String addressDetail, String contactNumber, String memoContent
	) {
		this.user = user;
		this.shipping = shipping;
		this.recipientName = recipientName;
		this.zipCode = zipCode;
		this.address = address;
		this.addressDetail = addressDetail;
		this.contactNumber = contactNumber;
		this.memoContent = memoContent;
	}
}
