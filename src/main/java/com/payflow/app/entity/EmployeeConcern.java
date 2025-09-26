package com.payflow.app.entity;

import java.time.LocalDateTime;

import com.payflow.app.enums.ConcernStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_concerns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeConcern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(nullable = false, length = 1000)
    private String description;

    private String attachmentUrl; // Optional

    @Enumerated(EnumType.STRING)
    private ConcernStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
