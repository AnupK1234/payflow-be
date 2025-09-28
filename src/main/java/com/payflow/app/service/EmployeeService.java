package com.payflow.app.service;

import java.time.LocalDate;
import java.util.List;

import com.payflow.app.dto.request.*;
import com.payflow.app.dto.response.*;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO req);

    List<EmployeeResponseDTO> getAllEmployees();

    EmployeeResponseDTO getEmployeeById(Long id);

    EmployeeResponseDTO updateEmployee(Long id, CreateEmployeeRequestDTO req);

    void deleteEmployee(Long id);

    
    EmployeeSalaryStructureResponseDTO addSalaryStructure(Long employeeId, EmployeeSalaryStructureRequestDTO req);

    List<EmployeeSalaryStructureResponseDTO> getSalaryStructures(Long employeeId);

    
    List<SalaryAccountUpdateRequestResponseDTO> getPendingSalaryAccountRequests(Long orgId);

   
    boolean processSalaryAccountRequest(Long requestId, boolean approve);

    boolean processSalaryAccountRequest(Long requestId, boolean approve, Long adminId);

	List<SalaryAccountUpdateRequestResponseDTO> getSalaryAccountRequestsWithFilters(String employeeName,
			String bankName, String status, LocalDate startDate, LocalDate endDate);
    
}

