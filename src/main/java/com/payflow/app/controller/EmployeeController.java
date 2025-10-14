package com.payflow.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;
import com.payflow.app.dto.request.UpdateEmployeeRequestDTO;
import com.payflow.app.dto.response.EmployeeResponseDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.dto.response.SalaryAccountUpdateRequestResponseDTO;
import com.payflow.app.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "APIs for managing employee data and salary structures")
public class EmployeeController {

	private final EmployeeService employeeService;

	@PostMapping
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Create a new employee", description = "Allows an ORG_ADMIN to create a new employee by providing their personal and organizational details.")
	public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody CreateEmployeeRequestDTO req) {
		return ResponseEntity.ok(employeeService.createEmployee(req));
	}

	@GetMapping
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "List all employees", description = "Fetches a list of all employees registered under the current organization.")
	public ResponseEntity<List<EmployeeResponseDTO>> listAll() {
		return ResponseEntity.ok(employeeService.getAllEmployees());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Get employee by ID", description = "Fetches detailed information about a specific employee using their ID.")
	public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.getEmployeeById(id));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Update employee details", description = "Updates the information of an existing employee identified by their ID.")
	public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Long id,
			@Valid @RequestBody UpdateEmployeeRequestDTO req) {
		return ResponseEntity.ok(employeeService.updateEmployee(id, req));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Delete an employee", description = "Removes an employee from the organization using their ID.")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}

	// ------------------ Salary Structures ------------------

	@PostMapping("/{id}/salary-structures")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Add salary structure for employee", description = "Adds a salary structure for a specific employee. Useful for tracking salary components and configurations.")
	public ResponseEntity<EmployeeSalaryStructureResponseDTO> addSalaryStructure(@PathVariable Long id,
			@Valid @RequestBody EmployeeSalaryStructureRequestDTO req) {
		return ResponseEntity.ok(employeeService.addSalaryStructure(id, req));
	}

	@GetMapping("/{id}/salary-structures")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Get employee's salary structures", description = "Retrieves all salary structures associated with a given employee.")
	public ResponseEntity<List<EmployeeSalaryStructureResponseDTO>> getSalaryStructures(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.getSalaryStructures(id));
	}

	@GetMapping("/salary-account/requests")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Get salary account update requests", description = "Fetches salary account update requests with optional filters like employee name, bank name, request status, and date range.")
	public ResponseEntity<List<SalaryAccountUpdateRequestResponseDTO>> getSalaryAccountRequests(
			@RequestParam(required = false) String employeeName, @RequestParam(required = false) String bankName,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		return ResponseEntity.ok(employeeService.getSalaryAccountRequestsWithFilters(employeeName, bankName, status,
				startDate, endDate));
	}

	@PostMapping("/salary-account/requests/{requestId}/process")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Approve or reject salary account request", description = "Processes a salary account update request by approving or rejecting it based on the request ID.")
	public ResponseEntity<String> processSalaryAccountRequest(@PathVariable Long requestId,
			@RequestParam boolean approve) {

		boolean result = employeeService.processSalaryAccountRequest(requestId, approve);

		if (result) {
			return ResponseEntity.ok(approve ? "Request approved successfully" : "Request rejected successfully");
		} else {
			return ResponseEntity.badRequest().body("Unable to process the request");
		}
	}

	@GetMapping("/salary-account-requests")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Get all salary account update requests for current organization", description = "Fetches all salary account update requests for the organization associated with the logged-in ORG_ADMIN.")
	public ResponseEntity<List<SalaryAccountUpdateRequestResponseDTO>> getRequestsForOrganization(
			HttpServletRequest request) {

		List<SalaryAccountUpdateRequestResponseDTO> requests = employeeService.getRequestsByOrganizationId(request);

		return ResponseEntity.ok(requests);
	}
}
