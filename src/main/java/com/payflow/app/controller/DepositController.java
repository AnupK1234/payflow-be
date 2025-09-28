package com.payflow.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.CreateDepositRequest;
import com.payflow.app.dto.response.DepositResponse;
import com.payflow.app.enums.DepositStatus;
import com.payflow.app.service.DepositService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
@Tag(name = "Deposits", description = "APIs for managing deposits from organizations")
public class DepositController {

	private final DepositService depositService;

	@PostMapping("/org/{orgId}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Create a deposit request", description = "Used by an organization admin to create a deposit request")
	public ResponseEntity<DepositResponse> createDeposit(@PathVariable Long orgId,
			@Valid @RequestBody CreateDepositRequest request) {

		DepositResponse deposit = depositService.createDeposit(orgId, request);
		return ResponseEntity.ok(deposit);
	}

	@PostMapping("/{depositId}/approve")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Approve or reject a deposit request", description = "Used by bank admin to approve or reject an organization's deposit")
	public ResponseEntity<DepositResponse> approveDeposit(@PathVariable Long depositId, @RequestParam boolean approve) {

		DepositResponse deposit = depositService.approveDeposit(depositId, approve);
		return ResponseEntity.ok(deposit);
	}

	@GetMapping("/org/{orgId}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "List all deposits by organization", description = "Fetches deposits for a given organization by its admin")
	public ResponseEntity<List<DepositResponse>> listDepositsByOrg(@PathVariable Long orgId) {

		List<DepositResponse> deposits = depositService.listByOrg(orgId);
		return ResponseEntity.ok(deposits);
	}

	@GetMapping("/bank")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "List all deposits across organizations", description = "Used by bank admin to view deposits from all organizations, with optional filters (status, date range, pagination)")
	public ResponseEntity<Page<DepositResponse>> listDepositsForBank(
			@Parameter(description = "Filter deposits by status (PENDING, APPROVED, REJECTED). Default = all") @RequestParam(required = false) DepositStatus status,

			@Parameter(description = "Filter deposits created after this date (inclusive)") @RequestParam(required = false) LocalDate startDate,

			@Parameter(description = "Filter deposits created before this date (inclusive)") @RequestParam(required = false) LocalDate endDate,

			@ParameterObject Pageable pageable) {

		return ResponseEntity.ok(depositService.listForBank(status, startDate, endDate, pageable));
	}
}
