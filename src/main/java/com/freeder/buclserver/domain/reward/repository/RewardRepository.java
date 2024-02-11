package com.freeder.buclserver.domain.reward.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.freeder.buclserver.domain.reward.entity.Reward;
import com.freeder.buclserver.domain.user.entity.User;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

	Optional<List<Reward>> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

	@Query(value = "SELECT r.reward_sum FROM Reward r WHERE r.user_id = :userId ORDER BY r.created_at DESC LIMIT 1",
		nativeQuery = true)
	Optional<Integer> findFirstByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT * FROM Reward WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
	Optional<Reward> findRewardsByUserId(@Param("userId") Long userId);

	Optional<Reward> findFirstByUserOrderByCreatedAtDesc(User user);
}
