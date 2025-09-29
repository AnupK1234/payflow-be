package com.payflow.app.dto.request;

import java.time.LocalDateTime;

import com.payflow.app.entity.Organization;
import com.payflow.app.entity.User;
import com.payflow.app.enums.DisbursementStatus;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	private LocalDateTime requestDate;

	@Enumerated(EnumType.STRING)
	private DisbursementStatus status = DisbursementStatus.PENDING;

	@ManyToOne
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@ManyToOne
	@JoinColumn(name = "created_by")
	private User createdBy; // Org Admin

	@ManyToOne
	@JoinColumn(name = "approved_by")
	private User approvedBy; // Bank Admin

	private LocalDateTime approvedAt;
}
