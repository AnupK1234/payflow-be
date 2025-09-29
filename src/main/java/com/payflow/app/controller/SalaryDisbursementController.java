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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/salary-disbursement")
@RequiredArgsConstructor
public class SalaryDisbursementController {
	private final SalaryDisbursementRequestRepository requestRepo;
	private final OrganizationRepository orgRepo;
	private final JobLauncher jobLauncher;
	private final Job salaryDisbursementJob;

	@PostMapping("/{orgId}/request")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
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
