package com.payflow.app.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPaymentRequestDTO {
    private Long organizationId;
    private Long clientId;
    private Double amount;
    private String reason;
    private String metadata;
}
