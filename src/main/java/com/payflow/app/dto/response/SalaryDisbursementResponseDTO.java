package com.payflow.app.dto.response;

import java.time.LocalDateTime;
import com.payflow.app.enums.DisbursementStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalaryDisbursementResponseDTO {
    private Long id;
    private DisbursementStatus status;
    private LocalDateTime requestDate;
    private Long organizationId;
    private Long createdById;
    private Long approvedById;
    private LocalDateTime approvedAt;
}
