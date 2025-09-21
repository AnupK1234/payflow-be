package com.payflow.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.payflow.app.dto.request.EmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;
import com.payflow.app.dto.response.EmployeeResponseDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // ------------------ Employee CRUD ------------------

    // ORG_ADMIN: Create employee
    @PostMapping
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO req) {
        return ResponseEntity.ok(employeeService.createEmployee(req));
    }

    // ORG_ADMIN: Get all employees
    @GetMapping
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<List<EmployeeResponseDTO>> listAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // ORG_ADMIN: Get employee by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // ORG_ADMIN: Update employee
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody EmployeeRequestDTO req) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, req));
    }

    // ORG_ADMIN: Delete employee
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    
    @PostMapping("/{id}/salary-structures")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<EmployeeSalaryStructureResponseDTO> addSalaryStructure(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeSalaryStructureRequestDTO req) {
        return ResponseEntity.ok(employeeService.addSalaryStructure(id, req));
    }

    // ORG_ADMIN: Get all salary structures for employee
    @GetMapping("/{id}/salary-structures")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<List<EmployeeSalaryStructureResponseDTO>> getSalaryStructures(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getSalaryStructures(id));
    }
}
