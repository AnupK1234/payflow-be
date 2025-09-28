package com.payflow.app.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPaymentRequestResponseDTO {

    private Long id;                  // Payment request ID
    private Long organizationId;      // ID of the organization that sent the request
    private Long clientId;            // ID of the client receiving the request
    private Double amount;            // Payment amount
    private String reason;            // Reason for payment
    private String metadata;          // Any additional info
    private String status;            // Status: PENDING, ACCEPTED, COMPLETED
    private LocalDateTime createdAt;  // Timestamp when request was created
    private LocalDateTime acceptedAt; // Timestamp when request was accepted
}
