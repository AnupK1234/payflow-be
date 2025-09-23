package com.payflow.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.payflow.app.enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false, unique = true)
	private String registrationNumber;

	@Column(nullable = false, unique = true)
	private String address;

	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;

	@OneToOne(mappedBy = "organization", cascade = CascadeType.ALL)
	private User adminUser;

	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	private List<Employee> employees = new ArrayList<>();

	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	private List<Vendor> vendors = new ArrayList<>();

	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	private List<Client> clients = new ArrayList<>();

	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	private List<Document> documents = new ArrayList<>();

}
