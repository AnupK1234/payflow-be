package com.payflow.app.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.payflow.app.dto.request.SalaryDisbursementRequestActionDTO;
import com.payflow.app.dto.response.SalaryDisbursementResponseDTO;
import com.payflow.app.service.SalaryDisbursementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/salary-disbursement")
@RequiredArgsConstructor
@Tag(name = "Salary Disbursement Management", description = "APIs for initiating and approving salary disbursement requests and triggering batch jobs.")
public class SalaryDisbursementController {

    private final SalaryDisbursementService salaryDisbursementService;

    @PostMapping("/{orgId}/request")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    @Operation(summary = "Create Salary Disbursement Request", description = "An ORG_ADMIN submits a request to initiate salary disbursement for a specific organization.")
    public ResponseEntity<SalaryDisbursementResponseDTO> createRequest(@PathVariable Long orgId) {
        SalaryDisbursementResponseDTO response = salaryDisbursementService.createRequest(orgId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/approve")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    @Operation(summary = "Approve Disbursement Request and Start Job", description = "A BANK_ADMIN approves a PENDING disbursement request and triggers the Spring Batch job.")
    public ResponseEntity<String> approveRequest(@PathVariable Long requestId) throws Exception {
        salaryDisbursementService.approveRequest(requestId);
        return ResponseEntity.ok("Disbursement Job Started!");
    }

    @PutMapping("/{requestId}/action")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    @Operation(summary = "Approve or Reject a Disbursement Request", description = "A BANK_ADMIN can approve or reject a salary disbursement request.")
    public ResponseEntity<String> takeAction(@PathVariable Long requestId,
                                             @RequestBody SalaryDisbursementRequestActionDTO action) throws Exception {
        salaryDisbursementService.takeAction(requestId, action);
        return ResponseEntity.ok("Action completed successfully!");
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('BANK_ADMIN')")
    @Operation(summary = "List all disbursement requests (paginated)", description = "Fetch a paginated list of all disbursement requests, optionally filtered by status.")
    public ResponseEntity<Page<SalaryDisbursementResponseDTO>> listAllRequests(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<SalaryDisbursementResponseDTO> responsePage = salaryDisbursementService.listRequests(status, page, size);
        return ResponseEntity.ok(responsePage);
    }
}
