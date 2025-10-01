package com.payflow.app.controller;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.SalaryDisbursementRequest;
import com.payflow.app.dto.response.SalaryDisbursementResponse;
import com.payflow.app.entity.Organization;
import com.payflow.app.enums.DisbursementStatus;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.SalaryDisbursementRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}
