package com.payflow.app.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.app.dto.request.CreateOrganizationRequest;
import com.payflow.app.dto.request.UpdateOrganizationRequest;
import com.payflow.app.dto.response.OrganizationResponse;
import com.payflow.app.service.OrganizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@Tag(name = "Organizations", description = "APIs for organization registration and management")
public class OrganizationController {

	private final OrganizationService organizationService;

	// Public: Organization self-registration
	@PostMapping(value = "/register", consumes = "multipart/form-data")
	@Operation(summary = "Register a new organization", description = "Allows a public user to self-register a new organization with required documents.")
	public ResponseEntity<OrganizationResponse> register(@Valid @RequestPart("data") String dataJson,
			@RequestPart("documents") List<MultipartFile> documents) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		CreateOrganizationRequest req = mapper.readValue(dataJson, CreateOrganizationRequest.class);

		return ResponseEntity.ok(organizationService.registerOrganization(req, documents));
	}

	@GetMapping
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "List all organizations", description = "Fetches all organizations registered in the system. Accessible to BANK_ADMIN.")
	public ResponseEntity<List<OrganizationResponse>> listAll() {
		return ResponseEntity.ok(organizationService.listAll());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Get organization by ID", description = "Returns the details of a specific organization by its ID. Accessible to BANK_ADMIN.")
	public ResponseEntity<OrganizationResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(organizationService.getById(id));
	}

	@PostMapping("/{id}/verify")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Verify or reject an organization", description = "Used by BANK_ADMIN to verify or reject an organization's registration based on approval status.")
	public ResponseEntity<OrganizationResponse> verify(@PathVariable Long id,
			@RequestParam(defaultValue = "true") boolean approve) {
		return ResponseEntity.ok(organizationService.verifyOrganization(id, approve));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Update organization details", description = "Allows BANK_ADMIN to update the details of an existing organization.")
	public ResponseEntity<OrganizationResponse> update(@PathVariable Long id,
			@RequestBody UpdateOrganizationRequest req) {
		return ResponseEntity.ok(organizationService.updateOrganization(id, req));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	@Operation(summary = "Delete an organization", description = "Deletes an organization from the system based on the given ID. Accessible to BANK_ADMIN.")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		organizationService.deleteOrganization(id);
		return ResponseEntity.noContent().build();
	}
}
