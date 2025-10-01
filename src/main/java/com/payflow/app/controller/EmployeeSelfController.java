package com.payflow.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.SalaryAccountUpdateRequestDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.service.EmployeeSelfService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employee/self")
@RequiredArgsConstructor
@Tag(name = "Employee Self Service", description = "API for employees to access and manage their own data.")
public class EmployeeSelfController {

	private final EmployeeSelfService employeeSelfService;

	@GetMapping("/salary-history")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@Operation(summary = "Get Employee's Own Salary History", description = "Retrieves a list of the employee's past and current salary structure details.", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved salary history"),
			@ApiResponse(responseCode = "403", description = "Forbidden: User does not have 'EMPLOYEE' authority") })
	public ResponseEntity<List<EmployeeSalaryStructureResponseDTO>> getSalaryHistory() {
		return ResponseEntity.ok(employeeSelfService.getSalaryHistory());
	}

	@GetMapping("/salary-history/pdf")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@Operation(summary = "Download Employee's Salary History as PDF", description = "Downloads the employee's salary history as a PDF file, optionally filtered by a date range.", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully generated and returned PDF file"),
			@ApiResponse(responseCode = "403", description = "Forbidden: User does not have 'EMPLOYEE' authority") })
	public void downloadSalaryHistoryPdf(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			HttpServletResponse response) {

		employeeSelfService.downloadSalaryHistoryPdf(startDate, endDate, response);
	}

//    // Employee: Download salary history CSV
//    @GetMapping("/salary-history/csv")
//    @PreAuthorize("hasAuthority('EMPLOYEE')")
//    public void downloadSalaryHistoryCsv(
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            HttpServletResponse response) {
//
//        employeeSelfService.downloadSalaryHistoryCsv(startDate, endDate, response);
//    }

	@PostMapping("/salary-account/update-request")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@Operation(summary = "Request Salary Account Update", description = "Submits a request to update the employee's salary bank account details.", responses = {
			@ApiResponse(responseCode = "200", description = "Salary account update request submitted successfully"),
			@ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data"),
			@ApiResponse(responseCode = "403", description = "Forbidden: User does not have 'EMPLOYEE' authority") })
	public ResponseEntity<String> requestSalaryAccountUpdate(
			@RequestBody @Valid SalaryAccountUpdateRequestDTO requestDTO) {

		employeeSelfService.requestSalaryAccountUpdate(requestDTO);
		return ResponseEntity.ok("Salary account update request submitted successfully");
	}
}
