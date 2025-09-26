package com.payflow.app.controller;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.app.dto.request.RaiseConcernRequestDTO;
import com.payflow.app.dto.request.UpdateConcernStatusRequestDTO;
import com.payflow.app.dto.response.ConcernResponseDTO;
import com.payflow.app.service.EmployeeConcernService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/concerns")
@RequiredArgsConstructor
public class EmployeeConcernController {

	private final EmployeeConcernService concernService;

	@PostMapping(value = "/raise", consumes = "multipart/form-data")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public ResponseEntity<ConcernResponseDTO> raiseConcern(@RequestPart("data") String dataJson,
			@RequestPart(value = "attachment", required = false) MultipartFile attachment) {
	
		return ResponseEntity.ok(concernService.raiseConcern(dataJson, attachment));
	}

	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@PutMapping("/{concernId}/status")
	public ResponseEntity<ConcernResponseDTO> updateStatus(@PathVariable Long concernId,
			@RequestBody UpdateConcernStatusRequestDTO requestDTO) {
		return ResponseEntity.ok(concernService.updateConcernStatus(concernId, requestDTO));
	}

	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@GetMapping("/employee/{employeeId}")
	public ResponseEntity<List<ConcernResponseDTO>> getConcernsByEmployee(@PathVariable Long employeeId) {
		return ResponseEntity.ok(concernService.getConcernsByEmployee(employeeId));
	}

	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@GetMapping("/organization/{organizationId}")
	public ResponseEntity<List<ConcernResponseDTO>> getConcernsByOrganization(@PathVariable Long organizationId) {
		return ResponseEntity.ok(concernService.getConcernsByOrganization(organizationId));
	}
}
