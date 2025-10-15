package com.payflow.app.controller;

import com.payflow.app.dto.request.ClientPaymentRequestDTO;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.enums.PaymentStatus;
import com.payflow.app.enums.Role;
import com.payflow.app.repository.BankAccountRepository;
import com.payflow.app.service.ClientPaymentRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/payment-requests")
@Tag(name = "Client Payment Requests", description = "APIs for managing client payment requests and approvals")
public class ClientPaymentRequestController {

    private final ClientPaymentRequestService requestService;
    private final BankAccountRepository bankAccountRepository;

    public ClientPaymentRequestController(
            @Qualifier("clientPaymentRequestService") ClientPaymentRequestService requestService,
            BankAccountRepository bankAccountRepository) {
        this.requestService = requestService;
        this.bankAccountRepository = bankAccountRepository;
    }

    
    @PostMapping("/send")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    @Operation(summary = "Send a payment request to a client",
               description = "Allows an organization admin to send a payment request to a specific client.")
    public ResponseEntity<ClientPaymentRequest> sendPaymentRequest(
            @RequestBody ClientPaymentRequestDTO requestDTO, HttpServletRequest request) {
        return ResponseEntity.ok(requestService.sendPaymentRequest(requestDTO, request));
    }

   
    @GetMapping("/client/{clientId}/pending")
    @PreAuthorize("hasAnyAuthority('CLIENT','ORG_ADMIN')")
    @Operation(summary = "Get pending payment requests",
               description = "Retrieve all pending payment requests for a client.")
    public ResponseEntity<List<ClientPaymentRequest>> getPendingRequests(@PathVariable Long clientId) {
        return ResponseEntity.ok(requestService.getPendingRequestsForClient(clientId));
    }

    @PostMapping("/{requestId}/accept/{clientBankAccountId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(summary = "Accept a payment request",
               description = "Client accepts a payment request by providing request ID and bank account ID.")
    public ResponseEntity<ClientPaymentRequest> acceptRequest(
            @PathVariable Long requestId,
            @PathVariable Long clientBankAccountId) {
        return ResponseEntity.ok(requestService.acceptPaymentRequest(requestId, clientBankAccountId));
    }

    
    @GetMapping("/client/{clientId}/history")
    @PreAuthorize("hasAnyAuthority('CLIENT','ORG_ADMIN')")
    @Operation(summary = "Get client payment history",
               description = "Retrieves client payment history with optional filters.")
    public ResponseEntity<List<ClientPaymentRequest>> getPaymentHistory(
            @PathVariable Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) PaymentStatus status) {
        return ResponseEntity.ok(requestService.getPaymentHistoryForClient(clientId, startDate, endDate, status));
    }

   
    @GetMapping("/client/{clientId}/bank-accounts")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(summary = "Get client's active bank accounts",
               description = "Fetch all active bank accounts associated with a client.")
    public ResponseEntity<List<BankAccount>> getClientBankAccounts(@PathVariable Long clientId) {
        
        List<BankAccount> accounts = bankAccountRepository
                .findAllByClientIdAndOwnerTypeAndStatusIgnoreCase(clientId, Role.CLIENT, "ACTIVE");

        if (accounts.isEmpty()) {
            System.out.println("No active bank accounts found for client " + clientId);
        } else {
            System.out.println("Bank accounts fetched for client " + clientId + ": " + accounts.size());
        }

        return ResponseEntity.ok(accounts);
    }
    
    @PostMapping("/{requestId}/reject")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(summary = "Reject a payment request",
               description = "Client rejects a payment request by providing request ID.")
    public ResponseEntity<ClientPaymentRequest> rejectRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.rejectPaymentRequest(requestId));
    }

}
