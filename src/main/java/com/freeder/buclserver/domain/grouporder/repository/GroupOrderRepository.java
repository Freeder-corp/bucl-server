package com.freeder.buclserver.domain.grouporder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;

@Repository
public interface GroupOrderRepository extends JpaRepository<GroupOrder, Long> {

	Optional<GroupOrder> findByProductIdAndIsEndedFalse(Long productId);
}
