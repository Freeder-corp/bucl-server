package com.freeder.buclserver.app.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freeder.buclserver.app.payment.dto.PaymentPrepareDto;
import com.freeder.buclserver.app.payment.dto.PaymentVerifyDto;
import com.freeder.buclserver.app.payment.dto.ProductOptionDto;
import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.consumerorder.repository.ConsumerOrderRepository;
import com.freeder.buclserver.domain.consumerorder.vo.CsStatus;
import com.freeder.buclserver.domain.consumerorder.vo.OrderStatus;
import com.freeder.buclserver.domain.consumerpayment.entity.ConsumerPayment;
import com.freeder.buclserver.domain.consumerpayment.repository.ConsumerPaymentRepository;
import com.freeder.buclserver.domain.consumerpayment.vo.PaymentMethod;
import com.freeder.buclserver.domain.consumerpayment.vo.PaymentStatus;
import com.freeder.buclserver.domain.consumerpayment.vo.PgProvider;
import com.freeder.buclserver.domain.consumerpurchaseorder.entity.ConsumerPurchaseOrder;
import com.freeder.buclserver.domain.consumerpurchaseorder.repository.ConsumerPurchaseOrderRepository;
import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import com.freeder.buclserver.domain.grouporder.repository.GroupOrderRepository;
import com.freeder.buclserver.domain.member.entity.Member;
import com.freeder.buclserver.domain.member.repository.MemberRepository;
import com.freeder.buclserver.domain.product.entity.Product;
import com.freeder.buclserver.domain.product.repository.ProductRepository;
import com.freeder.buclserver.domain.productoption.entity.ProductOption;
import com.freeder.buclserver.domain.productoption.repository.ProductOptionRepository;
import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.reward.repository.RewardRepository;
import com.freeder.buclserver.domain.reward.vo.RewardType;
import com.freeder.buclserver.domain.shipping.entity.Shipping;
import com.freeder.buclserver.domain.shipping.repository.ShippingRepository;
import com.freeder.buclserver.domain.shipping.vo.ShippingStatus;
import com.freeder.buclserver.domain.shippingaddress.entity.ShippingAddress;
import com.freeder.buclserver.domain.shippingaddress.repository.ShippingAddressRepository;
import com.freeder.buclserver.domain.shippinginfo.entity.ShippingInfo;
import com.freeder.buclserver.domain.shippinginfo.repository.ShippingInfoRepository;
import com.freeder.buclserver.global.exception.BaseException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private int minimumAmount = 500;

	private String testSocialId = "sjfdlkwjlkj149202";

	@Value("${iamport.key}")
	private String restApiKey;
	@Value("${iamport.secret}")
	private String restApiSecret;

	private IamportClient iamportClient;

	private final ConsumerOrderRepository consumerOrderRepository;
	private final ConsumerPurchaseOrderRepository consumerPurchaseOrderRepository;
	private final ConsumerPaymentRepository consumerPaymentRepository;
	private final ShippingRepository shippingRepository;
	private final ShippingAddressRepository shippingAddressRepository;

	private final MemberRepository memberRepository;
	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;
	private final ShippingInfoRepository shippingInfoRepository;
	private final GroupOrderRepository groupOrderRepository;
	private final RewardRepository rewardRepository;

	@PostConstruct
	public void init() {
		this.iamportClient = new IamportClient(restApiKey, restApiSecret);
	}

	public boolean preparePayment(PaymentPrepareDto paymentPrepareDto) {
		PrepareData prepareData = new PrepareData(paymentPrepareDto.getMerchantUid(),
			BigDecimal.valueOf(paymentPrepareDto.getAmount()));
		try {
			iamportClient.postPrepare(prepareData);
			return true;
		} catch (Exception error) {
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "사전 검증 요청 실패");
		}
	}

	@Transactional()
	public IamportResponse<Payment> verifyPayment(
		PaymentVerifyDto paymentVerifyDto) throws
		IamportResponseException,
		IOException, Exception {

		String impUid = paymentVerifyDto.getImpUid();

		Member member = memberRepository.findBySocialId(testSocialId).orElseThrow();
		Product product = productRepository.findByProductCode(paymentVerifyDto.getProductCode()).orElseThrow();
		ShippingInfo shippingInfo = shippingInfoRepository.findById(product.getShippingInfo().getId())
			.orElseThrow();
		List<ProductOptionDto> productOptionDtos = paymentVerifyDto.getProductOptionList();
		IamportResponse<Payment> irsp = iamportClient.paymentByImpUid(impUid);

		int requestPaymentAmount = paymentVerifyDto.getAmount();
		int rewardAmt = paymentVerifyDto.getRewardAmt();
		int actualPaymentAmount = irsp.getResponse().getAmount().intValue();

		verifyReward(member, rewardAmt, iamportClient, irsp);

		if (actualPaymentAmount != requestPaymentAmount) {
			cancelPayment(iamportClient, irsp);
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
				"요청 결제 금액과 결제 금액이 다릅니다.");
		}

		int spendAmount = calcSpendAmount(productOptionDtos, product, shippingInfo, rewardAmt);

		if (spendAmount != requestPaymentAmount) {
			cancelPayment(iamportClient, irsp);
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
				"결제 금액과 실제 결제 해야될 금액과 일치 하지 않습니다.");
		}

		if (spendAmount < minimumAmount) {
			cancelPayment(iamportClient, irsp);
			throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
				"최소 금액은 " + minimumAmount + "원 이상 입니다.");
		}
		try {
			GroupOrder groupOrder = groupOrderRepository.findByProductAndIsEnded(product, false).orElseThrow();
			ConsumerOrder orderReturn = createConsumerOrder(groupOrder, member, product, paymentVerifyDto);

			for (ProductOptionDto productOptionDto : productOptionDtos) {
				String orderCode = paymentVerifyDto.getImpUid() + UUID.randomUUID();
				createConsumerPurchaseOrder(orderReturn, productOptionDto, orderCode);
			}

			if (rewardAmt != 0) {
				Reward memberReward = rewardRepository.findFirstByMemberOrderByCreatedAtDesc(member).orElseThrow();
				createSpendedReward(member, product, orderReturn, rewardAmt, memberReward);
			}

			createConsumerPayment(member, orderReturn, paymentVerifyDto);
			Shipping shippingReturn = createShipping(orderReturn, shippingInfo);
			createShippingAddres(member, shippingReturn, paymentVerifyDto);

		} catch (Exception e) {
			log.info("{'status':'error', 'msg':'" + e.getMessage() + "' 'cause':'" + e.getMessage() + "'}");
			CancelData cancelData = cancelPayment(iamportClient, irsp);
			throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"결체 처리 중 오류가 발생해서 결체 취소 됐습니다.");
		}

		return irsp;
	}

	public CancelData cancelPayment(IamportClient iamportClient, IamportResponse<Payment> irsp) throws
		IamportResponseException,
		IOException {
		CancelData cancelData = new CancelData(irsp.getResponse().getImpUid(), true);
		iamportClient.cancelPaymentByImpUid(cancelData);
		return cancelData;
	}

	public void verifyReward(Member member, int rewardAmt, IamportClient iamportClient,
		IamportResponse<Payment> irsp) throws
		IamportResponseException,
		IOException {
		if (rewardAmt != 0) {
			Reward memberReward = rewardRepository.findFirstByMemberOrderByCreatedAtDesc(member).orElseThrow();
			int memberCurrentRewardSum = memberReward.getRewardSum();
			if (memberCurrentRewardSum < rewardAmt) {
				CancelData cancelData = new CancelData(irsp.getResponse().getImpUid(), true);
				iamportClient.cancelPaymentByImpUid(cancelData);
				throw new BaseException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
					"현재 가지고 계신 리워드 금액이 부족 합니다.");
			}
		}
	}

	public int calcSpendAmount(
		List<ProductOptionDto> productOptionDtos,
		Product product, ShippingInfo shippingInfo, int rewardAmt) {
		int totalAmount = 0;
		int spendAmount = 0;

		for (ProductOptionDto productOptionDto : productOptionDtos) {
			ProductOption productOption = productOptionRepository.findBySkuCode(productOptionDto.getSkuCode())
				.orElseThrow();
			int optionAmount =
				(product.getSalePrice() + productOption.getOptionExtraAmount()) * productOptionDto.getProductOrderQty();
			totalAmount += optionAmount;

		}
		totalAmount += shippingInfo.getShippingFee();

		spendAmount = totalAmount - rewardAmt;

		return spendAmount;
	}

	public ConsumerOrder createConsumerOrder(
		GroupOrder groupOrder, Member member, Product product, PaymentVerifyDto paymentVerifyDto) {
		ConsumerOrder order = ConsumerOrder
			.builder()
			.groupOrder(groupOrder)
			.consumer(member)
			.orderCode(paymentVerifyDto.getOrdCode())
			.isConfirmed(false)
			.isRewarded(false)
			.totalOrderAmount(paymentVerifyDto.getTotalOrdAmt())
			.spentAmount(paymentVerifyDto.getAmount())
			.shippingFee(paymentVerifyDto.getShpFee())
			.rewardUseAmount(paymentVerifyDto.getRewardAmt())
			.orderStatus(OrderStatus.ORDERED)
			.csStatus(CsStatus.NONE)
			.product(product)
			.build();

		return consumerOrderRepository.save(order);
	}

	public void createConsumerPurchaseOrder(
		ConsumerOrder consumerOrder,
		ProductOptionDto productOptionDto, String orderCode) {
		ProductOption productOption = productOptionRepository.findBySkuCode(productOptionDto.getSkuCode())
			.orElseThrow();
		ConsumerPurchaseOrder purchaseOrder = ConsumerPurchaseOrder
			.builder()
			.consumerOrder(consumerOrder)
			.productOption(productOption)
			.productOrderCode(orderCode)
			.productAmount(productOptionDto.getProductOrderAmt())
			.productOptionValue(productOption.getOptionValue())
			.productOrderQty(productOptionDto.getProductOrderQty())
			.productOrderAmount(productOptionDto.getProductOrderAmt() * productOptionDto.getProductOrderQty())
			.build();
		consumerPurchaseOrderRepository.save(purchaseOrder);
	}

	public Reward createSpendedReward(Member member, Product product,
		ConsumerOrder consumerOrder, int rewardAmt, Reward memberReward) {
		Reward spendReward = Reward
			.builder()
			.member(member)
			.product(product)
			.consumerOrder(consumerOrder)
			.productName(product.getName())
			.productBrandName(product.getBrandName())
			.rewardType(RewardType.SPEND)
			.spentRewardAmount(rewardAmt)
			.previousRewardSum(memberReward.getRewardSum())
			.rewardSum(memberReward.getRewardSum() - rewardAmt)
			.build();

		return rewardRepository.save(spendReward);
	}

	public ConsumerPayment createConsumerPayment(
		Member member, ConsumerOrder consumerOrder, PaymentVerifyDto paymentVerifyDto) {
		ConsumerPayment consumerPayment = ConsumerPayment
			.builder()
			.consumerOrder(consumerOrder)
			.pgTid(paymentVerifyDto.getPgTid())
			.pgProvider(PgProvider.KAKAOPAY)
			.paymentCode(paymentVerifyDto.getOrdCode())
			.paymentAmount(paymentVerifyDto.getTotalOrdAmt())
			.consumerName(paymentVerifyDto.getRecipientName())
			.consumerEmail(member.getEmail())
			.consumerAddress(paymentVerifyDto.getAddr() + " " + paymentVerifyDto.getAddrDetail())
			.paymentStatus(PaymentStatus.PAID)
			.paymentMethod(PaymentMethod.CARD)
			.paidAt(LocalDateTime.now())
			.build();

		return consumerPaymentRepository.save(consumerPayment);
	}

	public Shipping createShipping(
		ConsumerOrder consumerOrder, ShippingInfo shippingInfo) {
		Shipping shipping = Shipping
			.builder()
			.consumerOrder(consumerOrder)
			.shippingInfo(shippingInfo)
			.shippingStatus(ShippingStatus.PROCESSING)
			.isActive(true)
			.build();

		return shippingRepository.save(shipping);
	}

	public ShippingAddress createShippingAddres(
		Member member, Shipping shipping, PaymentVerifyDto paymentVerifyDto) {
		ShippingAddress shippingAddress = ShippingAddress
			.builder()
			.shipping(shipping)
			.member(member)
			.recipientName(paymentVerifyDto.getRecipientName())
			.zipCode(paymentVerifyDto.getZipCode())
			.address(paymentVerifyDto.getAddr())
			.addressDetail(paymentVerifyDto.getAddrDetail())
			.contactNumber(paymentVerifyDto.getContactNum())
			.memoContent(paymentVerifyDto.getMemoCnt())
			.build();

		return shippingAddressRepository.save(shippingAddress);
	}
}

