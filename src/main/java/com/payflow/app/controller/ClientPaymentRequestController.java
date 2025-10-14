package com.payflow.app.controller;

import com.payflow.app.dto.request.ClientPaymentRequestDTO;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.enums.PaymentStatus;
import com.payflow.app.enums.Role;
import com.payflow.app.repository.BankAccountRepository;
import com.payflow.app.security.util.CurrentUserUtil;
import com.payflow.app.service.ClientPaymentRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payment-requests")
@RequiredArgsConstructor
@Tag(name = "Client Payment Requests", description = "APIs for managing client payment requests and approvals")
public class ClientPaymentRequestController {

    private final ClientPaymentRequestService requestService;
    private final BankAccountRepository bankAccountRepository;
    private final CurrentUserUtil currentUserUtil;

    
    @PostMapping("/send")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    @Operation(summary = "Send a payment request to a client",
               description = "Allows an organization admin to send a payment request to a specific client.")
    public ResponseEntity<ClientPaymentRequest> sendPaymentRequest(
            @RequestBody ClientPaymentRequestDTO requestDTO,
            HttpServletRequest request) {
        return ResponseEntity.ok(requestService.sendPaymentRequest(requestDTO, request));
    }

    
    @GetMapping("/client/pending")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(summary = "Get pending payment requests",
               description = "Retrieve all pending payment requests for the currently logged-in client.")
    public ResponseEntity<List<ClientPaymentRequest>> getPendingRequests(HttpServletRequest request) {
        return ResponseEntity.ok(requestService.getPendingRequestsForClient(request));
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

   
    @PostMapping("/{requestId}/reject")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(summary = "Reject a payment request",
               description = "Client rejects a payment request by providing request ID.")
    public ResponseEntity<ClientPaymentRequest> rejectRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.rejectPaymentRequest(requestId));
    }

   
    @GetMapping("/history")
    @PreAuthorize("hasAnyAuthority('CLIENT','ORG_ADMIN')")
    @Operation(summary = "Get payment history",
               description = "Retrieves payment history for the logged-in user (client or organization) with optional filters.")
    public ResponseEntity<List<ClientPaymentRequest>> getPaymentHistory(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) PaymentStatus status) {
        return ResponseEntity.ok(requestService.getPaymentHistoryForCurrentUser(request, startDate, endDate, status));
    }

   
    @GetMapping("/client/bank-accounts")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(summary = "Get client's active bank accounts",
               description = "Fetch all active bank accounts associated with the currently logged-in client.")
    public ResponseEntity<List<BankAccount>> getClientBankAccounts(HttpServletRequest request) {
       
        Long clientId = currentUserUtil.getCurrentUser(request).getClientId();
        
        List<BankAccount> accounts = bankAccountRepository
                .findAllByClientIdAndOwnerTypeAndStatusIgnoreCase(clientId, Role.CLIENT, "ACTIVE");

        if (accounts.isEmpty()) {
            System.out.println("No active bank accounts found for client " + clientId);
        } else {
            System.out.println("Bank accounts fetched for client " + clientId + ": " + accounts.size());
        }

        return ResponseEntity.ok(accounts);
    }
}
