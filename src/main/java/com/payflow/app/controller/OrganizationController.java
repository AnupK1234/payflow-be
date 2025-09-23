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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

	private final OrganizationService organizationService;

	// Public: Organization self-registration
	@PostMapping(value = "/register", consumes = "multipart/form-data")
	public ResponseEntity<OrganizationResponse> register(@Valid @RequestPart("data") String dataJson,
			@RequestPart("documents") List<MultipartFile> documents) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
	    CreateOrganizationRequest req = mapper.readValue(dataJson, CreateOrganizationRequest.class);
		
		return ResponseEntity.ok(organizationService.registerOrganization(req, documents));
	}

	// Bank Admin: list all organizations
	@GetMapping
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	public ResponseEntity<List<OrganizationResponse>> listAll() {
		return ResponseEntity.ok(organizationService.listAll());
	}

	// Bank Admin: get single org
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	public ResponseEntity<OrganizationResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(organizationService.getById(id));
	}

	// Bank Admin: verify organization
	@PostMapping("/{id}/verify")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	public ResponseEntity<OrganizationResponse> verify(@PathVariable Long id,
			@RequestParam(defaultValue = "true") boolean approve) {
		return ResponseEntity.ok(organizationService.verifyOrganization(id, approve));
	}

	// Bank Admin: update organization
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	public ResponseEntity<OrganizationResponse> update(@PathVariable Long id,
			@RequestBody UpdateOrganizationRequest req) {
		return ResponseEntity.ok(organizationService.updateOrganization(id, req));
	}

	// Bank Admin: delete organization
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('BANK_ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		organizationService.deleteOrganization(id);
		return ResponseEntity.noContent().build();
	}
}
