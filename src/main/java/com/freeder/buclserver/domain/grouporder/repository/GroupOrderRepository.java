package com.freeder.buclserver.domain.grouporder.repository;

import com.freeder.buclserver.domain.grouporder.entity.GroupOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupOrderRepository extends JpaRepository<GroupOrder,Long> {
    GroupOrder findByProduct_Id(Long productId);
}
