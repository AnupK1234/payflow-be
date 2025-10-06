package com.payflow.app.controller;

import com.payflow.app.dto.request.ClientPaymentRequestDTO;
import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.service.ClientPaymentRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-requests")
@RequiredArgsConstructor
@Tag(name = "Client Payment Requests", description = "APIs for managing client payment requests and approvals")
public class ClientPaymentRequestController {

    private final ClientPaymentRequestService requestService;

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(
        summary = "Send a payment request to a client",
        description = "Allows an ORGANIZATION to send a payment request to a specific client."
    )
    public ResponseEntity<ClientPaymentRequest> sendPaymentRequest(
            @RequestBody ClientPaymentRequestDTO requestDTO) {
        ClientPaymentRequest request = requestService.sendPaymentRequest(requestDTO);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/client/{clientId}/pending")
    @PreAuthorize("hasAuthority('CLIENT','ORGANIZATION')")
    @Operation(
        summary = "Get pending payment requests for client and org",
        description = "Allows a CLIENT and ,'ORGANIZATION'  to retrieve all pending payment requests directed to them."
    )
    public ResponseEntity<List<ClientPaymentRequest>> getPendingRequests(@PathVariable Long clientId) {
        List<ClientPaymentRequest> requests = requestService.getPendingRequestsForClient(clientId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{requestId}/accept/{clientBankAccountId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(
        summary = "Accept a payment request",
        description = "Allows a CLIENT to accept a specific payment request"
        		+ " by providing the payment request ID and the client bank account ID to use for the transaction."
    )
    public ResponseEntity<ClientPaymentRequest> acceptRequest(
            @PathVariable Long requestId,
            @PathVariable Long clientBankAccountId) {
        ClientPaymentRequest acceptedRequest = requestService.acceptPaymentRequest(requestId, clientBankAccountId);
        return ResponseEntity.ok(acceptedRequest);
    }
}
