package com.payflow.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import com.payflow.app.enums.Role;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner type: EMPLOYEE, ORG_ADMIN, VENDOR, etc.
    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false)
    private Role ownerType;

    // Link to Employee (nullable for organization accounts)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

    // Link to Organization (nullable for employee accounts)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = true)
    private Organization organization;

    @Column(name = "account_number_enc", nullable = false, length = 100)
    private String accountNumberEnc;

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code")
    @Column(name = "ifsc", nullable = false, length = 11)
    private String ifsc;

    // Status: ACTIVE, INACTIVE
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";
    
    private Integer balance = 0;
    
    
}
