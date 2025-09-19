package com.payflow.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private String role; // BANK_ADMIN, ORG_ADMIN, EMPLOYEE, VENDOR, CLIENT

	private Boolean mustResetPassword = false;
	private Boolean enabled = true;

	@OneToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@OneToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@OneToOne
	@JoinColumn(name = "vendor_id")
	private Vendor vendor;

	@OneToOne
	@JoinColumn(name = "client_id")
	private Client client;
}
