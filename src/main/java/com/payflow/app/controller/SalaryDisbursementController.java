package com.payflow.app.controller;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.SalaryDisbursementRequest;
import com.payflow.app.dto.request.SalaryDisbursementRequestAction;
import com.payflow.app.dto.response.SalaryDisbursementResponse;
import com.payflow.app.entity.Organization;
import com.payflow.app.enums.DisbursementStatus;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.SalaryDisbursementRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/salary-disbursement")
@RequiredArgsConstructor
@Tag(name = "Salary Disbursement Management", description = "APIs for initiating and approving salary disbursement requests and triggering batch jobs.")
public class SalaryDisbursementController {
	private final SalaryDisbursementRequestRepository requestRepo;
	private final OrganizationRepository orgRepo;
	private final JobLauncher jobLauncher;
	private final Job salaryDisbursementJob;

	@PostMapping("/{orgId}/request")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Create Salary Disbursement Request", description = "An ORG_ADMIN submits a request to initiate salary disbursement for a specific organization. The request starts in PENDING status.", responses = {
			@ApiResponse(responseCode = "200", description = "Disbursement request created successfully and returned."),
			@ApiResponse(responseCode = "403", description = "Forbidden: User does not have 'ORG_ADMIN' authority."),
			@ApiResponse(responseCode = "404", description = "Organization not found.") })
	public SalaryDisbursementResponse createRequest(@PathVariable Long orgId) {
		Organization org = orgRepo.findById(orgId).orElseThrow(() -> new RuntimeException("Organization not found"));

		SalaryDisbursementRequest req = SalaryDisbursementRequest.builder().organization(org)

				.status(DisbursementStatus.PENDING).requestDate(LocalDateTime.now()).build();
		SalaryDisbursementRequest saved = requestRepo.save(req);

		return SalaryDisbursementResponse.builder().id(saved.getId()).requestDate(saved.getRequestDate())
				.status(saved.getStatus()).organizationId(saved.getOrganization().getId())
				.createdById(saved.getCreatedBy() != null ? saved.getCreatedBy().getId() : null)
				.approvedById(saved.getApprovedBy() != null ? saved.getApprovedBy().getId() : null)
				.approvedAt(saved.getApprovedAt()).build();
	}

	@PostMapping("/{requestId}/approve")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Approve Disbursement Request and Start Job", description = "A BANK_ADMIN approves a PENDING disbursement request, updates its status to APPROVED, and triggers the Spring Batch job for processing the payroll.", responses = {
			@ApiResponse(responseCode = "200", description = "Request approved and Disbursement Batch Job successfully started."),
			@ApiResponse(responseCode = "403", description = "Forbidden: User does not have 'BANK_ADMIN' authority."),
			@ApiResponse(responseCode = "404", description = "Disbursement Request not found."),
			@ApiResponse(responseCode = "500", description = "Internal server error, possibly due to job execution failure.") })
	public String approveRequest(@PathVariable Long requestId) throws Exception {
		SalaryDisbursementRequest req = requestRepo.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		req.setStatus(DisbursementStatus.APPROVED);
		req.setApprovedAt(LocalDateTime.now());
		requestRepo.save(req);

		JobParameters params = new JobParametersBuilder().addLong("orgId", req.getOrganization().getId())
				.addLong("time", System.currentTimeMillis()).toJobParameters();

		jobLauncher.run(salaryDisbursementJob, params);
		return "Disbursement Job Started!";
	}

	@PutMapping("/{requestId}/action")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Approve or Reject a Disbursement Request", description = "A BANK_ADMIN can approve or reject a salary disbursement request. If approved, the batch job is triggered.", responses = {
			@ApiResponse(responseCode = "200", description = "Request updated successfully."),
			@ApiResponse(responseCode = "404", description = "Request not found.") })
	public ResponseEntity<String> takeAction(@PathVariable Long requestId,
			@RequestBody SalaryDisbursementRequestAction action) throws Exception {

		var req = requestRepo.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Disbursement request not found"));

		if (req.getStatus() != DisbursementStatus.PENDING) {
			return ResponseEntity.badRequest().body("Action not allowed. Request is already processed.");
		}

		if (action.getAction().equalsIgnoreCase("approve")) {
			req.setStatus(DisbursementStatus.APPROVED);
			req.setApprovedAt(LocalDateTime.now());
			requestRepo.save(req);

			// Trigger Spring Batch Job
			JobParameters params = new JobParametersBuilder().addLong("orgId", req.getOrganization().getId())
					.addLong("time", System.currentTimeMillis()).toJobParameters();

			jobLauncher.run(salaryDisbursementJob, params);

			return ResponseEntity.ok("Disbursement request approved and batch job started.");
		} else if (action.getAction().equalsIgnoreCase("reject")) {
			req.setStatus(DisbursementStatus.REJECTED);
			req.setApprovedAt(LocalDateTime.now());
			requestRepo.save(req);
			return ResponseEntity.ok("Disbursement request rejected successfully.");
		} else {
			return ResponseEntity.badRequest().body("Invalid action. Use 'approve' or 'reject'.");
		}
	}

	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('BANK_ADMIN')")
	@Operation(summary = "List all disbursement requests (paginated)", description = "Fetch a paginated list of all disbursement requests, optionally filtered by status. Default is ALL.", responses = {
			@ApiResponse(responseCode = "200", description = "Requests fetched successfully.") })
	public ResponseEntity<Page<SalaryDisbursementResponse>> listAllRequests(
			@Parameter(description = "Filter by status (PENDING, APPROVED, REJECTED, COMPLETED, or ALL). Default is ALL.") @RequestParam(defaultValue = "ALL") String status,

			@Parameter(description = "Page number (0-indexed). Default = 0") @RequestParam(defaultValue = "0") int page,

			@Parameter(description = "Page size. Default = 10") @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
		Page<SalaryDisbursementRequest> pageResult;

		if (status.equalsIgnoreCase("ALL")) {
			pageResult = requestRepo.findAll(pageable);
		} else {
			DisbursementStatus filterStatus;
			try {
				filterStatus = DisbursementStatus.valueOf(status.toUpperCase());
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().build();
			}
			pageResult = requestRepo.findByStatus(filterStatus, pageable);
		}

		Page<SalaryDisbursementResponse> responsePage = pageResult
				.map(req -> SalaryDisbursementResponse.builder().id(req.getId()).status(req.getStatus())
						.requestDate(req.getRequestDate()).organizationId(req.getOrganization().getId())
						.createdById(req.getCreatedBy() != null ? req.getCreatedBy().getId() : null)
						.approvedById(req.getApprovedBy() != null ? req.getApprovedBy().getId() : null)
						.approvedAt(req.getApprovedAt()).build());

		return ResponseEntity.ok(responsePage);
	}
}
