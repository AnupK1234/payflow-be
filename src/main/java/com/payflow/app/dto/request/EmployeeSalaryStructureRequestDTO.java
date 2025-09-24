package com.payflow.app.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSalaryStructureRequestDTO {

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal basic;
    private BigDecimal hra;
    private BigDecimal da;
    private BigDecimal pf;
    private String otherAllowances;
    private Boolean isCurrent;
}
