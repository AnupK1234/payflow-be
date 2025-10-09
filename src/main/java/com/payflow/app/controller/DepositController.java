package com.payflow.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "Deposits", description = "APIs for managing deposits from organizations and clients")
public class DepositController {

    private final DepositService depositService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN','CLIENT')")
    @Operation(summary = "Create a deposit request")
    public ResponseEntity<DepositResponse> createDeposit(
            @Valid @RequestBody CreateDepositRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Long orgId = depositService.getOrgIdForUsername(username);
        Long clientId = depositService.getClientIdForUsername(username);

        if (orgId != null) {
            return ResponseEntity.ok(depositService.createDepositForOrg(orgId, request));
        } else if (clientId != null) {
            return ResponseEntity.ok(depositService.createDepositForClient(clientId, request));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    
    @PostMapping("/{depositId}/approve")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    @Operation(summary = "Approve or reject a deposit request")
    public ResponseEntity<DepositResponse> approveDeposit(
            @PathVariable Long depositId,
            @RequestParam boolean approve
    ) {
        return ResponseEntity.ok(depositService.approveDeposit(depositId, approve));
    }

    
    @GetMapping("/org")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    @Operation(summary = "List all deposits by organization")
    public ResponseEntity<List<DepositResponse>> listDepositsByOrg(Authentication authentication) {
        Long orgId = depositService.getOrgIdForUsername(authentication.getName());
        return ResponseEntity.ok(depositService.listByOrg(orgId));
    }

    
    @GetMapping("/client")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(summary = "List all deposits by client")
    public ResponseEntity<List<DepositResponse>> listDepositsByClient(Authentication authentication) {
        Long clientId = depositService.getClientIdForUsername(authentication.getName());
        return ResponseEntity.ok(depositService.listByClient(clientId));
    }

 
    @GetMapping("/bank")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    @Operation(summary = "List all deposits across organizations and clients")
    public ResponseEntity<Page<DepositResponse>> listDepositsForBank(
            @Parameter(description = "Filter by status") @RequestParam(required = false) DepositStatus status,
            @Parameter(description = "Start date filter") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "End date filter") @RequestParam(required = false) LocalDate endDate,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(depositService.listForBank(status, startDate, endDate, pageable));
    }
}
