package com.payflow.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class SalaryAccountUpdateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization org;

    private String bankName;
    private String accountNumber;
    private String ifscCode;

   
    private String additionalInfo;

    private String status; 
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private Long approvedBy; 
}

