package com.freeder.buclserver.app.orders.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDto {
	@NotEmpty
	private String orderCode;            //consumer_order
	@NotEmpty
	private String name;                 //product
	@NotEmpty
	private String productOptionValue;    //consumer_purchase_order
	@NotEmpty
	private Integer productOptionQty;     //consumer_purchase_order
	@NotEmpty
	private String recipientName;       //shipping_address
	@NotEmpty
	private String zipCode;             //shipping_address
	@NotEmpty
	private String address;             //shipping_address
	@NotEmpty
	private String addressDetail;       //shipping_address
	@NotEmpty
	private String contactNumber;       //shipping_address
	@NotEmpty
	private String memoContent;         //shipping_address
	@NotEmpty
	private Integer shippingFee;         //shipping_info
	@NotEmpty
	private String shippingFeePhrase;   //shipping_info
}