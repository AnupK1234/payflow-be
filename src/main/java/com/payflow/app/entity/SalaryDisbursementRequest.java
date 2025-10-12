package com.payflow.app.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import com.payflow.app.enums.DisbursementStatus;

@Entity
@Table(name = "salary_disbursement_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryDisbursementRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    private DisbursementStatus status;

    private LocalDateTime requestDate;
    private LocalDateTime approvedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}
