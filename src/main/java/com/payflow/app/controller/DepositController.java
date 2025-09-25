package com.payflow.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.payflow.app.dto.request.CreateDepositRequest;
import com.payflow.app.dto.response.DepositResponse;
import com.payflow.app.service.DepositService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    // -----------------------------
    // Org Admin creates a deposit request
    // -----------------------------
    @PostMapping("/org/{orgId}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<DepositResponse> createDeposit(
            @PathVariable Long orgId,
            @Valid @RequestBody CreateDepositRequest request) {

        DepositResponse deposit = depositService.createDeposit(orgId, request);
        return ResponseEntity.ok(deposit);
    }

    // -----------------------------
    // Bank Admin approves or rejects a deposit
    // -----------------------------
    @PostMapping("/{depositId}/approve")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<DepositResponse> approveDeposit(
            @PathVariable Long depositId,
            @RequestParam boolean approve) {

        DepositResponse deposit = depositService.approveDeposit(depositId, approve);
        return ResponseEntity.ok(deposit);
    }

    // -----------------------------
    // Org Admin lists all deposits for their organization
    // -----------------------------
    @GetMapping("/org/{orgId}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<List<DepositResponse>> listDepositsByOrg(
            @PathVariable Long orgId) {

        List<DepositResponse> deposits = depositService.listByOrg(orgId);
        return ResponseEntity.ok(deposits);
    }
}
