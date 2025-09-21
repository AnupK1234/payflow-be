package com.payflow.app.service;

import java.util.List;

import com.payflow.app.dto.request.*;
import com.payflow.app.dto.response.*;



public interface EmployeeService {

    // Employee CRUD
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO req);

    List<EmployeeResponseDTO> getAllEmployees();

    EmployeeResponseDTO getEmployeeById(Long id);

    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO req);

    void deleteEmployee(Long id);

    // Salary Structure management for Employee
    EmployeeSalaryStructureResponseDTO addSalaryStructure(Long employeeId, EmployeeSalaryStructureRequestDTO req);

    List<EmployeeSalaryStructureResponseDTO> getSalaryStructures(Long employeeId);
}
