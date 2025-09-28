package com.payflow.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.DepositRequest;
import com.payflow.app.enums.DepositStatus;

public interface DepositRequestRepository extends JpaRepository<DepositRequest, Long> {
	List<DepositRequest> findByOrganizationId(Long orgId);

	Page<DepositRequest> findByStatus(DepositStatus status, Pageable pageable);

	Page<DepositRequest> findByStatusAndCreatedAtBetween(DepositStatus status, LocalDateTime start, LocalDateTime end,
			Pageable pageable);

	Page<DepositRequest> findByStatusAndCreatedAtAfter(DepositStatus status, LocalDateTime start, Pageable pageable);

	Page<DepositRequest> findByStatusAndCreatedAtBefore(DepositStatus status, LocalDateTime end, Pageable pageable);

	Page<DepositRequest> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

	Page<DepositRequest> findByCreatedAtAfter(LocalDateTime start, Pageable pageable);

	Page<DepositRequest> findByCreatedAtBefore(LocalDateTime end, Pageable pageable);
}
