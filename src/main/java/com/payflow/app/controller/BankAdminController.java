package com.payflow.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.OrganizationRequest;
import com.payflow.app.dto.response.OrganizationResponse;
import com.payflow.app.service.BankAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bank-admin")
@RequiredArgsConstructor
public class BankAdminController {

	private final BankAdminService bankAdminService;

	// List pending orgs
	@GetMapping("/organizations/pending")
	public ResponseEntity<List<OrganizationResponse>> getPendingOrganizations() {
		return ResponseEntity.ok(bankAdminService.listPendingOrganizations());
	}

	// Approve or reject org
	@PostMapping("/organizations/verify")
	public ResponseEntity<OrganizationResponse> verifyOrganization(@RequestBody OrganizationRequest request) {
		return ResponseEntity.ok(bankAdminService.verifyOrganization(request));
	}

	// Get org details
	@GetMapping("/organizations/{id}")
	public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable("id") Long id) {
		return ResponseEntity.ok(bankAdminService.getOrganizationDetails(id));
	}

	// List all organizations
	@GetMapping("/organizations")
	public ResponseEntity<List<OrganizationResponse>> listAllOrganizations() {
		return ResponseEntity.ok(bankAdminService.listAllOrganizations());
	}
}
