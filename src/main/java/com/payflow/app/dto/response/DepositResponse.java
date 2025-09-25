package com.payflow.app.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DepositResponse {
    private Long id;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String createdBy;
    private String approvedBy;
}
