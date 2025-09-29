package com.payflow.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.dto.request.SalaryDisbursementRequest;

public interface SalaryDisbursementRequestRepository extends JpaRepository<SalaryDisbursementRequest, Long> {
}
