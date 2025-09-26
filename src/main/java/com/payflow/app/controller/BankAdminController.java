package com.payflow.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.OrganizationRequest;
import com.payflow.app.dto.response.OrganizationResponse;
import com.payflow.app.service.BankAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bank-admin")
@RequiredArgsConstructor
@Tag(name = "Bank Admin", description = "APIs for managing bank admin operations")
public class BankAdminController {

	private final BankAdminService bankAdminService;

	// List pending orgs
	@GetMapping("/organizations/pending")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Get a list of pending organizations", description = "Fetches all organizations that are currently pending approval.")
	public ResponseEntity<List<OrganizationResponse>> getPendingOrganizations() {
		return ResponseEntity.ok(bankAdminService.listPendingOrganizations());
	}

	// Approve or reject org
	@PostMapping("/organizations/verify")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Approve or reject an organization", description = "Approve or reject an organization based on the provided verification request.")
	public ResponseEntity<OrganizationResponse> verifyOrganization(@RequestBody OrganizationRequest request) {
		return ResponseEntity.ok(bankAdminService.verifyOrganization(request));
	}

	// Get org details
	@GetMapping("/organizations/{id}")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Get details of a specific organization", description = "Fetches the details of the organization specified by its ID.")
	public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable("id") Long id) {
		return ResponseEntity.ok(bankAdminService.getOrganizationDetails(id));
	}

	// List all organizations
	@GetMapping("/organizations")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Get a list of all organizations", description = "Fetches all the organizations available in the system.")
	public ResponseEntity<List<OrganizationResponse>> listAllOrganizations() {
		return ResponseEntity.ok(bankAdminService.listAllOrganizations());
	}
}
