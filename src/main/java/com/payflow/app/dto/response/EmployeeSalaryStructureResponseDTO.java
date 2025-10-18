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

    private Long employeeId;
    private String employeeName;
    private Long id;
    private BigDecimal basic; 
    private BigDecimal hra;  
    private BigDecimal da;    
    private BigDecimal pf;    

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Boolean isCurrent;
    private BigDecimal netSalary;

    

    public String getMonth() {
        return effectiveFrom != null ? effectiveFrom.getMonth().name() : "";
    }

    public int getYear() {
        return effectiveFrom != null ? effectiveFrom.getYear() : 0;
    }

    public BigDecimal getDeductions() {
        return pf != null ? pf : BigDecimal.ZERO;
    }

}
