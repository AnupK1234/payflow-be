package com.payflow.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.payflow.app.dto.request.EmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;
import com.payflow.app.dto.response.EmployeeResponseDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.dto.response.SalaryAccountUpdateRequestResponseDTO;
import com.payflow.app.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // ------------------ Employee CRUD ------------------

    @PostMapping
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO req) {
        return ResponseEntity.ok(employeeService.createEmployee(req));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<List<EmployeeResponseDTO>> listAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody EmployeeRequestDTO req) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ Salary Structures ------------------

    @PostMapping("/{id}/salary-structures")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<EmployeeSalaryStructureResponseDTO> addSalaryStructure(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeSalaryStructureRequestDTO req) {
        return ResponseEntity.ok(employeeService.addSalaryStructure(id, req));
    }

    @GetMapping("/{id}/salary-structures")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<List<EmployeeSalaryStructureResponseDTO>> getSalaryStructures(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getSalaryStructures(id));
    }

    @GetMapping("/salary-account/requests")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<List<SalaryAccountUpdateRequestResponseDTO>> getSalaryAccountRequests(
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(employeeService.getSalaryAccountRequestsWithFilters(
                employeeName, bankName, status, startDate, endDate));
    }

    // Process (approve/reject) a salary account update request using a boolean flag
    @PostMapping("/salary-account/requests/{requestId}/process")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<String> processSalaryAccountRequest(
            @PathVariable Long requestId,
            @RequestParam boolean approve) {

        boolean result = employeeService.processSalaryAccountRequest(requestId, approve);

        if (result) {
            return ResponseEntity.ok(approve ? "Request approved successfully" : "Request rejected successfully");
        } else {
            return ResponseEntity.badRequest().body("Unable to process the request");
        }
    }
}
