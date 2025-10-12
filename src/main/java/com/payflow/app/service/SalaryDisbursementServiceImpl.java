package com.payflow.app.service;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.payflow.app.dto.request.SalaryDisbursementRequestActionDTO;
import com.payflow.app.dto.response.SalaryDisbursementResponseDTO;
import com.payflow.app.dto.response.UserResponse;
import com.payflow.app.entity.Organization;
import com.payflow.app.entity.SalaryDisbursementRequest;
import com.payflow.app.enums.DisbursementStatus;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.SalaryDisbursementRequestRepository;
import com.payflow.app.security.util.CurrentUserUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalaryDisbursementServiceImpl implements SalaryDisbursementService {

    private final SalaryDisbursementRequestRepository requestRepo;
    private final OrganizationRepository orgRepo;
    private final JobLauncher jobLauncher;
    private final Job salaryDisbursementJob;
    private final CurrentUserUtil currentUserUtil;

    @Override
    public SalaryDisbursementResponseDTO createRequest(HttpServletRequest request) {
    	UserResponse user = currentUserUtil.getCurrentUser(request);
    	Long orgId = user.getOrganizationId();
        Organization org = orgRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        SalaryDisbursementRequest req = SalaryDisbursementRequest.builder()
                .organization(org)
                .status(DisbursementStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .build();

        SalaryDisbursementRequest saved = requestRepo.save(req);
        return mapToDTO(saved);
    }

    @Override
    public void approveRequest(Long requestId) throws Exception {
        SalaryDisbursementRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        req.setStatus(DisbursementStatus.APPROVED);
        req.setApprovedAt(LocalDateTime.now());
        requestRepo.save(req);

        JobParameters params = new JobParametersBuilder()
                .addLong("orgId", req.getOrganization().getId())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(salaryDisbursementJob, params);
    }

    @Override
    public void rejectRequest(Long requestId) {
        SalaryDisbursementRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        req.setStatus(DisbursementStatus.REJECTED);
        req.setApprovedAt(LocalDateTime.now());
        requestRepo.save(req);
    }

    @Override
    public void takeAction(Long requestId, SalaryDisbursementRequestActionDTO action) throws Exception {
        if ("approve".equalsIgnoreCase(action.getAction())) {
            approveRequest(requestId);
        } else if ("reject".equalsIgnoreCase(action.getAction())) {
            rejectRequest(requestId);
        } else {
            throw new RuntimeException("Invalid action. Use 'approve' or 'reject'.");
        }
    }

    @Override
    public Page<SalaryDisbursementResponseDTO> listRequests(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<SalaryDisbursementRequest> pageResult;

        if ("ALL".equalsIgnoreCase(status)) {
            pageResult = requestRepo.findAll(pageable);
        } else {
            DisbursementStatus filterStatus;
            try {
                filterStatus = DisbursementStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status");
            }
            pageResult = requestRepo.findByStatus(filterStatus, pageable);
        }

        return pageResult.map(this::mapToDTO);
    }
    
    @Override
    public Page<SalaryDisbursementResponseDTO> listRequestsByOrganization(String status, int page, int size, HttpServletRequest request) {
        // Extract the current user from token
        UserResponse currentUser = currentUserUtil.getCurrentUser(request);
        Long orgId = currentUser.getOrganizationId();

        Organization org = orgRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<SalaryDisbursementRequest> pageResult;

        if ("ALL".equalsIgnoreCase(status)) {
            pageResult = requestRepo.findByOrganization(org, pageable);
        } else {
            DisbursementStatus filterStatus;
            try {
                filterStatus = DisbursementStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status");
            }
            pageResult = requestRepo.findByOrganizationAndStatus(org, filterStatus, pageable);
        }

        return pageResult.map(this::mapToDTO);
    }


    private SalaryDisbursementResponseDTO mapToDTO(SalaryDisbursementRequest req) {
        return SalaryDisbursementResponseDTO.builder()
                .id(req.getId())
                .status(req.getStatus())
                .requestDate(req.getRequestDate())
                .organizationId(req.getOrganization().getId())
                .createdById(req.getCreatedBy() != null ? req.getCreatedBy().getId() : null)
                .approvedById(req.getApprovedBy() != null ? req.getApprovedBy().getId() : null)
                .approvedAt(req.getApprovedAt())
                .build();
    }
}
