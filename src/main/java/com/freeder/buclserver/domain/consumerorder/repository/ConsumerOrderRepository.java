package com.freeder.buclserver.domain.consumerorder.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.consumerorder.entity.ConsumerOrder;
import com.freeder.buclserver.domain.user.entity.User;

@Repository
public interface ConsumerOrderRepository extends JpaRepository<ConsumerOrder, Long> {

	Page<ConsumerOrder> findAllByConsumerOrderByCreatedAtDesc(User consumer, Pageable pageable);

	Optional<ConsumerOrder> findByOrderCodeAndConsumer(String orderCode, User consumer);

	Optional<ConsumerOrder> findByOrderCode(String orderCode);

	@Query("SELECT COUNT(co) FROM ConsumerOrder co " + "WHERE co.product.productCode = :productCode "
		+ "AND co.createdAt between :startAt AND :endedAt "
		+ "AND co.csStatus = com.freeder.buclserver.domain.consumerorder.vo.CsStatus.NONE")
	Integer countByProductCodeWithGroupOrderCondition(@Param("productCode") Long productCode,
		@Param("startAt") LocalDateTime startAt,
		@Param("endedAt") LocalDateTime endedAt);
}
