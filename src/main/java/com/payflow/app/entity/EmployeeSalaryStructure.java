package com.payflow.app.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "employee_salary_structure")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate effectiveFrom;

    @Column(nullable = true)
    private LocalDate effectiveTo;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basic;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hra;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal da;   

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pf;   

    @Column(columnDefinition = "TEXT")
    private String otherAllowances;   

  
    @NotNull
    @Column(nullable = false)
    private Boolean isCurrent = true;

    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
