package com.payflow.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.dto.request.SalaryDisbursementRequest;
import com.payflow.app.enums.DisbursementStatus;

public interface SalaryDisbursementRequestRepository extends JpaRepository<SalaryDisbursementRequest, Long> {
	Page<SalaryDisbursementRequest> findByStatus(DisbursementStatus status, Pageable pageable);
}
