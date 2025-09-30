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
    private String otherAllowances; 

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Boolean isCurrent;
    

    public String getMonth() {
        return effectiveFrom != null ? effectiveFrom.getMonth().name() : "";
    }

    public int getYear() {
        return effectiveFrom != null ? effectiveFrom.getYear() : 0;
    }

    public BigDecimal getAllowances() {
        try {
            return otherAllowances != null && !otherAllowances.isEmpty() 
                   ? new BigDecimal(otherAllowances) : BigDecimal.ZERO;
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getDeductions() {
        return pf != null ? pf : BigDecimal.ZERO;
    }

    public BigDecimal getNetSalary() {
        BigDecimal total = BigDecimal.ZERO;
        if (basic != null) total = total.add(basic);
        if (hra != null) total = total.add(hra);
        if (da != null) total = total.add(da);
        total = total.add(getAllowances());
        total = total.subtract(getDeductions());
        return total;
    }
}
