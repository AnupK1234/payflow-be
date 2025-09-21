package com.payflow.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSalaryStructureResponseDTO {

    private Long id;
    private BigDecimal basic; // Base salary
    private BigDecimal hra;   // House rent allowance
    private BigDecimal da;    // Dearness allowance
    private BigDecimal pf;    // PF
    private String otherAllowances; // Keep as String to match entity

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private Long employeeId;
    private String employeeName;
}
