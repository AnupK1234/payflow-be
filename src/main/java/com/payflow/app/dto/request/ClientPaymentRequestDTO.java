package com.payflow.app.dto.request;
import com.payflow.app.enums.PaymentStatus;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPaymentRequestDTO {
    private Long clientId;
    private Double amount;
    private String reason;
    private String metadata;
}

