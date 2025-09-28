package com.payflow.app.controller;

import com.payflow.app.dto.request.ClientPaymentRequestDTO;
import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.service.ClientPaymentRequestService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-requests")
@RequiredArgsConstructor
public class ClientPaymentRequestController {

    private final ClientPaymentRequestService requestService;

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('ORGANIZATION')")
    public ResponseEntity<ClientPaymentRequest> sendPaymentRequest(
            @RequestBody ClientPaymentRequestDTO requestDTO) {
        ClientPaymentRequest request = requestService.sendPaymentRequest(requestDTO);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/client/{clientId}/pending")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<ClientPaymentRequest>> getPendingRequests(@PathVariable Long clientId) {
        List<ClientPaymentRequest> requests = requestService.getPendingRequestsForClient(clientId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{requestId}/accept/{clientBankAccountId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<ClientPaymentRequest> acceptRequest(
            @PathVariable Long requestId,
            @PathVariable Long clientBankAccountId) {
        ClientPaymentRequest acceptedRequest = requestService.acceptPaymentRequest(requestId, clientBankAccountId);
        return ResponseEntity.ok(acceptedRequest);
    }
}
