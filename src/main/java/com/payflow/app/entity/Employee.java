package com.payflow.app.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true, length = 20)
    private String employeeCode;    

    @NotNull
    @Column(nullable = false, length = 100)
    private String fullName;

    @NotNull
    @Email
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateOfJoining;

    @NotNull
    @Column(nullable = false, length = 50)
    private String jobTitle;

    @NotNull
    @Column(nullable = false, length = 50)
    private String department;

    @NotNull
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Size(min = 12, max = 12, message = "Aadhaar must be 12 digits")
    @Column(unique = true, length = 12)
    private String aadhaarNumber;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
    @Column(unique = true, length = 10)
    private String panNumber;

    @NotNull
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeSalaryStructure> salaryStructures = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccount> bankAccounts = new ArrayList<>();
}
