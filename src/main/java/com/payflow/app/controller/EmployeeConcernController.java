package com.payflow.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.payflow.app.dto.request.UpdateConcernStatusRequestDTO;
import com.payflow.app.dto.response.ConcernResponseDTO;
import com.payflow.app.service.EmployeeConcernService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/concerns")
@RequiredArgsConstructor
@Tag(name = "Employee Concerns", description = "APIs to manage employee concerns in the organization")
public class EmployeeConcernController {

	private final EmployeeConcernService concernService;

	@PostMapping(value = "/raise", consumes = "multipart/form-data")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@Operation(summary = "Raise a concern", description = "This endpoint allows an EMPLOYEE to raise a new concern with optional attachment.")
	public ResponseEntity<ConcernResponseDTO> raiseConcern(@RequestPart("data") String dataJson,
			@RequestPart(value = "attachment", required = false) MultipartFile attachment) {

		return ResponseEntity.ok(concernService.raiseConcern(dataJson, attachment));
	}

	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@PutMapping("/{concernId}/status")
	@Operation(summary = "Update the status of a concern", description = "This endpoint allows ORG_ADMIN to update the status of a concern identified by its ID.")
	public ResponseEntity<ConcernResponseDTO> updateStatus(@PathVariable Long concernId,
			@RequestBody UpdateConcernStatusRequestDTO requestDTO) {
		return ResponseEntity.ok(concernService.updateConcernStatus(concernId, requestDTO));
	}

	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@GetMapping("/employee/{employeeId}")
	@Operation(summary = "Get concerns by employee", description = "This endpoint returns a list of all concerns raised by a specific employee, identified by their ID.")

	public ResponseEntity<List<ConcernResponseDTO>> getConcernsByEmployee(@PathVariable Long employeeId) {
		return ResponseEntity.ok(concernService.getConcernsByEmployee(employeeId));
	}

	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@GetMapping("/organization/{organizationId}")
	@Operation(summary = "Get concerns by organization", description = "This endpoint returns a list of all concerns raised within a specific organization, identified by its ID.")
	public ResponseEntity<List<ConcernResponseDTO>> getConcernsByOrganization(@PathVariable Long organizationId) {
		return ResponseEntity.ok(concernService.getConcernsByOrganization(organizationId));
	}
}
