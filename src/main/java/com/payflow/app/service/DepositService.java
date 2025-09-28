package com.payflow.app.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.payflow.app.dto.request.CreateDepositRequest;
import com.payflow.app.dto.response.DepositResponse;
import com.payflow.app.enums.DepositStatus;

public interface DepositService {

	// Org Admin creates a deposit request
	DepositResponse createDeposit(Long orgId, CreateDepositRequest req);

	// Bank Admin approves or rejects a deposit request
	DepositResponse approveDeposit(Long depositId, boolean approve);

	// Org Admin lists all deposits for their organization
	List<DepositResponse> listByOrg(Long orgId);

	Page<DepositResponse> listForBank(DepositStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
